package com.example.tsdmh_7s5_proyecto2_lfrm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ResultFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup contenedor,
                             Bundle estadoGuardado) {
        return inflater.inflate(R.layout.fragment_result, contenedor, false);
    }

    void mostrarCategoria(int categoria) {
        View vista = requireView();
        View espera = vista.findViewById(R.id.txtEspera);
        View detalle = vista.findViewById(R.id.panelDetalle);
        espera.setVisibility(categoria < 0 ? View.VISIBLE : View.GONE);
        detalle.setVisibility(categoria < 0 ? View.GONE : View.VISIBLE);

        if (categoria >= 0) {
            int[] productosPorCategoria = {
                    R.array.productos_oficina, R.array.productos_tecnologia,
                    R.array.productos_limpieza, R.array.productos_cafeteria
            };
            String[] productos = getResources().getStringArray(productosPorCategoria[categoria]);
            TextView lista = vista.findViewById(R.id.txtProductos);
            lista.setText(android.text.TextUtils.join("\n", productos));
        }
    }
}
