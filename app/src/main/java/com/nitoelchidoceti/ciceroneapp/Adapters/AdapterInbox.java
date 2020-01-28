package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.MensajeRecibir;
import com.nitoelchidoceti.ciceroneapp.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterInbox extends RecyclerView.Adapter<AdapterInbox.HolderMensajes> {

    private List<MensajeRecibir> mensajes = new ArrayList<>();
    private Context context;
    private AdapterInbox.OnItemClickListener listener;
    private ArrayList<String> urlsFoto = new ArrayList<>();

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public AdapterInbox(Context context, AdapterInbox.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void addMensaje(MensajeRecibir mensaje) {
        mensajes.add(mensaje);
        urlsFoto.add(mensaje.getFotoPerfilDestinatario());
        notifyItemInserted(mensajes.size());
    }

    public void addMensaje(MensajeRecibir mensaje,String urlFoto) {
        mensajes.add(mensaje);
        urlsFoto.add(urlFoto);
        notifyItemInserted(mensajes.size());
    }

    public void clearList(){
        int size = mensajes.size();
        mensajes.clear();
        urlsFoto.clear();
        notifyItemRangeRemoved(0,size);
    }

    @NonNull
    @Override
    public HolderMensajes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.recycle_inbox, parent, false);
        return new HolderMensajes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMensajes holder, int position) {
        holder.getNombre().setText(mensajes.get(position).getNombreDestinatario());
        holder.getMensaje().setText(mensajes.get(position).getMensaje());
        if (mensajes.get(position).getFotoPerfilDestinatario()!=null){
            Glide.with(context).load(mensajes.get(position).getFotoPerfilDestinatario()).into(holder.getFotoPerfil());
        }
        if(mensajes.get(position).getIdUsuario().equals("turista"+ Global.getObject().getId())){
            holder.getNombre().setText(mensajes.get(position).getNombreDestinatario());
        }else {
            holder.getNombre().setText(mensajes.get(position).getNombre());
        }
        Long codigoHora = mensajes.get(position).getHora();
        Date date = new Date(codigoHora);
        PrettyTime prettyTime = new PrettyTime(new Date(), Locale.getDefault());
        holder.getHora().setText(prettyTime.format(date));
        holder.onClickFake(position,listener);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class HolderMensajes extends RecyclerView.ViewHolder {
        private TextView nombre, hora, mensaje;
        private ImageView fotoPerfil;
        private ImageView imgMensaje;


        public HolderMensajes(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombreInbox);
            hora = itemView.findViewById(R.id.txtFechaInbox);
            mensaje = itemView.findViewById(R.id.txtMsgInbox);
            fotoPerfil = itemView.findViewById(R.id.imgPerfil);
            imgMensaje= itemView.findViewById(R.id.imagenChat);
        }

        public void onClickFake(final int posicion, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(posicion);
                }
            });
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

        public ImageView getFotoPerfil() {
            return fotoPerfil;
        }

        public void setFotoPerfil(ImageView fotoPerfil) {
            this.fotoPerfil = fotoPerfil;
        }

        public ImageView getImgMensaje() {
            return imgMensaje;
        }

        public void setImgMensaje(ImageView imgMensaje) {
            this.imgMensaje = imgMensaje;
        }
    }
}
