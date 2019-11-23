package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class AdapterDeBusquedaGuias extends RecyclerView.Adapter<AdapterDeBusquedaGuias.FichaHolderSearchGuias>{

    private ArrayList<PojoGuia> guiasParaAgregar;

    public AdapterDeBusquedaGuias(ArrayList<PojoGuia> lugaresParaAgregar) {
        this.guiasParaAgregar = lugaresParaAgregar;
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
        holder.llenarDatos(guiasParaAgregar.get(position));
    }

    @Override
    public int getItemCount() {
        return guiasParaAgregar.size();
    }

    class FichaHolderSearchGuias extends RecyclerView.ViewHolder {

        TextView nombre, descripcion;

        public FichaHolderSearchGuias(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtPlaceNameHome);
            descripcion = itemView.findViewById(R.id.txtPlaceDescricpionHome);
        }

        public void llenarDatos(PojoGuia guia) {
            nombre.setText(guia.getNombre());
            descripcion.setText("Correo: "+guia.getCorreo()+"\n"+"Sitio: "+guia.getSitio());
        }
    }

    public void setFilter(ArrayList<PojoGuia> guias) {
        this.guiasParaAgregar = new ArrayList<>();
        this.guiasParaAgregar.addAll(guias);
        notifyDataSetChanged();
    }
}
