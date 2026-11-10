package com.example.tsdmh_7s5_proyecto2_lfrm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.ref.WeakReference;

@SuppressWarnings("deprecation") // AsyncTask forma parte de la evaluación.
public class PrincipalActivity extends AppCompatActivity {

    private ControlFragment controles;
    private ResultFragment panelResultado;
    private SolicitarDatos tarea;
    private boolean datosCargados;
    private int categoria = -1;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        if (estadoGuardado == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentcontainer, new ControlFragment())
                    .replace(R.id.resultcontainer, new ResultFragment())
                    .setReorderingAllowed(true)
                    .commitNow();
        } else {
            datosCargados = estadoGuardado.getBoolean("datosCargados");
            categoria = estadoGuardado.getInt("categoria", -1);
        }

        controles = (ControlFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentcontainer);
        panelResultado = (ResultFragment) getSupportFragmentManager()
                .findFragmentById(R.id.resultcontainer);

        tarea = (SolicitarDatos) getLastCustomNonConfigurationInstance();
        if (tarea != null) {
            tarea.conectar(this);
            if (tarea.terminada) {
                datosCargados = tarea.datosCargados;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        actualizarPantalla();
    }

    void solicitarDatos() {
        if (datosCargados || (tarea != null && !tarea.terminada)) {
            return;
        }
        tarea = new SolicitarDatos();
        tarea.conectar(this);
        tarea.execute();
        actualizarPantalla();
    }

    void seleccionarCategoria(int posicion) {
        if (datosCargados) {
            categoria = posicion;
            panelResultado.mostrarCategoria(categoria);
        }
    }

    private void actualizarPantalla() {
        boolean cargando = tarea != null && !tarea.terminada;
        controles.mostrarEstado(datosCargados, cargando, cargando ? tarea.avance : 0, categoria);
        findViewById(R.id.resultcontainer).setVisibility(datosCargados ? View.VISIBLE : View.GONE);
        if (datosCargados) {
            panelResultado.mostrarCategoria(categoria);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        estado.putBoolean("datosCargados", datosCargados);
        estado.putInt("categoria", categoria);
        super.onSaveInstanceState(estado);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return tarea;
    }

    @Override
    protected void onDestroy() {
        if (tarea != null) {
            tarea.actividad.clear();
            if (!isChangingConfigurations()) {
                tarea.cancel(true);
            }
        }
        super.onDestroy();
    }

    private static class SolicitarDatos extends AsyncTask<Void, Integer, Boolean> {

        private WeakReference<PrincipalActivity> actividad;
        private int avance;
        private boolean terminada;
        private boolean datosCargados;

        void conectar(PrincipalActivity principal) {
            actividad = new WeakReference<>(principal);
        }

        @Override
        protected Boolean doInBackground(Void... parametros) {
            for (int progreso = 10; progreso <= 100; progreso += 10) {
                if (isCancelled()) {
                    return false;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                publishProgress(progreso);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... valores) {
            avance = valores[0];
            PrincipalActivity principal = actividad.get();
            if (principal != null) {
                principal.actualizarPantalla();
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            terminada = true;
            datosCargados = resultado;
            PrincipalActivity principal = actividad.get();
            if (principal != null) {
                principal.datosCargados = resultado;
                principal.actualizarPantalla();
            }
        }
    }
}