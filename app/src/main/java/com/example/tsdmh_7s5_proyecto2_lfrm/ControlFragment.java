package com.example.tsdmh_7s5_proyecto2_lfrm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ControlFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup contenedor,
                             Bundle estadoGuardado) {
        return inflater.inflate(R.layout.fragment_control, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);
        PrincipalActivity principal = (PrincipalActivity) requireActivity();
        vista.findViewById(R.id.btnSolicitar).setOnClickListener(v -> principal.solicitarDatos());

        Spinner selector = vista.findViewById(R.id.spinnerCategorias);
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                requireContext(), R.array.categorias, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selector.setAdapter(adaptador);
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> padre, View elemento, int posicion, long id) {
                // La primera opción es la indicación para elegir, no una categoría.
                principal.seleccionarCategoria(posicion - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> padre) {
                principal.seleccionarCategoria(-1);
            }
        });
    }

    void mostrarEstado(boolean cargados, boolean cargando, int avance, int categoria) {
        View vista = requireView();
        View solicitud = vista.findViewById(R.id.panelSolicitud);
        View seleccion = vista.findViewById(R.id.panelSeleccion);
        if (cargados && seleccion.getVisibility() != View.VISIBLE) {
            Spinner selector = vista.findViewById(R.id.spinnerCategorias);
            selector.setSelection(categoria + 1, false);
        }
        solicitud.setVisibility(cargados ? View.GONE : View.VISIBLE);
        seleccion.setVisibility(cargados ? View.VISIBLE : View.GONE);

        Button solicitar = vista.findViewById(R.id.btnSolicitar);
        ProgressBar barra = vista.findViewById(R.id.barraProgreso);
        TextView estado = vista.findViewById(R.id.txtCarga);
        solicitar.setEnabled(!cargando);
        barra.setProgress(avance);
        barra.setVisibility(cargando ? View.VISIBLE : View.INVISIBLE);
        estado.setVisibility(cargando ? View.VISIBLE : View.INVISIBLE);
        estado.setText(getString(R.string.cargando, avance));
    }
}
