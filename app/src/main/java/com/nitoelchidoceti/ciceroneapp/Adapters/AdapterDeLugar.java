package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class AdapterDeLugar extends RecyclerView.Adapter<AdapterDeLugar.FichaHolder>{
    public AdapterDeLugar(ArrayList<PojoLugar> datos) {
        this.datos = datos;
    }

    public ArrayList<PojoLugar> datos;
    public Context contexto;

    @NonNull
    @Override
    public AdapterDeLugar.FichaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = View.inflate(parent.getContext(), R.layout.home_recycle_view,null);
        FichaHolder respect = new FichaHolder(vista);
        contexto=parent.getContext();
        return respect;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeLugar.FichaHolder holder, int position) {

        holder.asignarDatos(datos.get(position));

    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class FichaHolder extends RecyclerView.ViewHolder{
        TextView nombre, descripcion;
        public FichaHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtPlaceNameHome);
            descripcion = itemView.findViewById(R.id.txtPlaceDescricpionHome);
        }

        public void asignarDatos(PojoLugar pojoLugar) {
            nombre.setText(pojoLugar.getNombre());
            descripcion.setText(pojoLugar.getDescripcion());
        }
    }
}
