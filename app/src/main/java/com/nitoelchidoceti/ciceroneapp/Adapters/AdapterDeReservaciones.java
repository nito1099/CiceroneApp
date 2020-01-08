package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoReservacion;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class AdapterDeReservaciones extends RecyclerView.Adapter<AdapterDeReservaciones.FichaHolder> {
    public ArrayList<String> datos;

    public AdapterDeReservaciones(ArrayList<String> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public AdapterDeReservaciones.FichaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = View.inflate(parent.getContext(), R.layout.recycle_reservaciones, null);
        FichaHolder respecto = new FichaHolder(vista);
        return respecto;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeReservaciones.FichaHolder holder, int position) {
        holder.asignarDatos(datos.get(position));
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class FichaHolder extends RecyclerView.ViewHolder {
        TextView reservacion;

        public FichaHolder(@NonNull View itemView) {
            super(itemView);
            reservacion = itemView.findViewById(R.id.txtReservacion);
        }

        public void asignarDatos(String reservacion) {
            this.reservacion.setText("Fecha: "+reservacion);
            //max 23
        }
    }
}
