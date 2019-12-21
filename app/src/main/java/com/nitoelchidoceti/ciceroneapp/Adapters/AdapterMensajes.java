package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoMensaje;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterMensajes  extends RecyclerView.Adapter<AdapterMensajes.HolderMensajes>{

    private List<PojoMensaje> mensajes = new ArrayList<>();
    private Context context;

    public AdapterMensajes( Context context) {
        this.context = context;
    }

    public void addMensaje(PojoMensaje mensaje){
        mensajes.add(mensaje);
        notifyItemInserted(mensajes.size());
    }
    @NonNull
    @Override
    public HolderMensajes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_mensajes,parent,false);
        return new HolderMensajes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMensajes holder, int position) {
        holder.getNombre().setText(mensajes.get(position).getNombre());
        holder.getMensaje().setText(mensajes.get(position).getMensaje());
        holder.getHora().setText(mensajes.get(position).getFecha());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class HolderMensajes extends RecyclerView.ViewHolder {
        private TextView nombre, hora, mensaje;

        public HolderMensajes(@NonNull View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.txtNombreCardView);
            hora=itemView.findViewById(R.id.txtFechaCardView);
            mensaje=itemView.findViewById(R.id.txtMensajeCardView);
        }

        public TextView getNombre() {
            return nombre;
        }

        public void setNombre(TextView nombre) {
            this.nombre = nombre;
        }

        public TextView getHora() {
            return hora;
        }

        public void setHora(TextView hora) {
            this.hora = hora;
        }

        public TextView getMensaje() {
            return mensaje;
        }

        public void setMensaje(TextView mensaje) {
            this.mensaje = mensaje;
        }

    }
}
