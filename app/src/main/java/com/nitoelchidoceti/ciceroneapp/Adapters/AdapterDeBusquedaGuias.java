package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

import static com.nitoelchidoceti.ciceroneapp.R.drawable.img_catedral_premium;

public class AdapterDeBusquedaGuias extends RecyclerView.Adapter<AdapterDeBusquedaGuias.FichaHolderSearchGuias>{

    private ArrayList<PojoGuia> guiasParaAgregar;
    private AdapterDeBusquedaGuias.OnItemClickListener listener;
    Context context;
     public interface  OnItemClickListener{
         void OnItemClick(int position);
     }
    public AdapterDeBusquedaGuias(Context context,ArrayList<PojoGuia> lugaresParaAgregar, AdapterDeBusquedaGuias.OnItemClickListener listener) {
        this.guiasParaAgregar = lugaresParaAgregar;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public FichaHolderSearchGuias onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = View.inflate(parent.getContext(), R.layout.home_recycle_view, null);
        FichaHolderSearchGuias holderSearch = new FichaHolderSearchGuias(vista);
        return holderSearch;
    }

    @Override
    public void onBindViewHolder(@NonNull FichaHolderSearchGuias holder, int position) {
        holder.llenarDatos(guiasParaAgregar.get(position),position,listener);
    }

    @Override
    public int getItemCount() {
        return guiasParaAgregar.size();
    }

    class FichaHolderSearchGuias extends RecyclerView.ViewHolder {

        TextView nombre, descripcion;
        ImageView fotoPerfil;

        public FichaHolderSearchGuias(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtPlaceNameHome);
            descripcion = itemView.findViewById(R.id.txtPlaceDescricpionHome);
            fotoPerfil= itemView.findViewById(R.id.imgPlace);
        }

        public void llenarDatos(PojoGuia guia, final  int posicion, final AdapterDeBusquedaGuias.OnItemClickListener listener) {
            nombre.setText(guia.getNombre());
            descripcion.setText("Correo: "+guia.getCorreo()+"\n"+"Sitio: "+guia.getNombreDelSitio());
            if (!guia.getFotografia().equals("null")){
                fotoPerfil.setBackground(null);
                Glide.with(context).load(guia.getFotografia()).into(fotoPerfil);
            }else {
                fotoPerfil.setImageResource(img_catedral_premium);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(posicion);
                }
            });
        }
    }

    public void setFilter(ArrayList<PojoGuia> guias) {
        this.guiasParaAgregar = new ArrayList<>();
        this.guiasParaAgregar.addAll(guias);
        notifyDataSetChanged();
    }
}
