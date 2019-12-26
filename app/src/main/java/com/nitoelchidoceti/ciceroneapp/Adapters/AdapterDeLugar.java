package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class AdapterDeLugar extends RecyclerView.Adapter<AdapterDeLugar.FichaHolder>{
    public ArrayList<PojoLugar> datos;
    private Context context;
    private  OnItemClickListener listener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    public AdapterDeLugar(ArrayList<PojoLugar> datos, Context context, OnItemClickListener listener) {
        this.datos = datos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterDeLugar.FichaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = View.inflate(parent.getContext(), R.layout.home_recycle_view,null);
        FichaHolder respect = new FichaHolder(vista);
        return respect;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeLugar.FichaHolder holder, int position) {
        holder.asignarDatos(datos.get(position),position,listener);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class FichaHolder extends RecyclerView.ViewHolder{
        TextView nombre, descripcion;
        ImageView fotoPerfil;
        public FichaHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtPlaceNameHome);
            descripcion = itemView.findViewById(R.id.txtPlaceDescricpionHome);
            fotoPerfil = itemView.findViewById(R.id.imgPlace);
        }

        public void asignarDatos(PojoLugar pojoLugar, final int posicion, final OnItemClickListener listener) {
            nombre.setText(pojoLugar.getNombre());
            descripcion.setText(pojoLugar.getDescripcion());
            if (!pojoLugar.getFotografia().equals("null")){
                fotoPerfil.setBackground(null);
                Glide.with(context).load(pojoLugar.getFotografia()).into(fotoPerfil);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(posicion);
                }
            });
        }
    }
}
