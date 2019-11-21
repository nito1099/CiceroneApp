package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class AdapterDeBusqueda extends RecyclerView.Adapter<AdapterDeBusqueda.FichaHolderSearch> {

    private ArrayList<PojoLugar> lugaresParaAgregar;

    public AdapterDeBusqueda(ArrayList<PojoLugar> lugaresParaAgregar) {
        this.lugaresParaAgregar = lugaresParaAgregar;
    }

    @NonNull
    @Override
    public FichaHolderSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = View.inflate(parent.getContext(), R.layout.home_recycle_view, null);
        FichaHolderSearch holderSearch = new FichaHolderSearch(vista);
        return holderSearch;
    }

    @Override
    public void onBindViewHolder(@NonNull FichaHolderSearch holder, int position) {
        holder.llenarDatos(lugaresParaAgregar.get(position));
    }

    @Override
    public int getItemCount() {
        return lugaresParaAgregar.size();
    }


    class FichaHolderSearch extends RecyclerView.ViewHolder {

        TextView nombre, descripcion;

        public FichaHolderSearch(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtPlaceNameHome);
            descripcion = itemView.findViewById(R.id.txtPlaceDescricpionHome);
        }

        public void llenarDatos(PojoLugar lugar) {
            nombre.setText(lugar.getNombre());
            descripcion.setText(lugar.getDescripcion());
        }
    }

    public void setFilter(ArrayList<PojoLugar> lugares) {
        this.lugaresParaAgregar = new ArrayList<>();
        this.lugaresParaAgregar.addAll(lugares);
        notifyDataSetChanged();
    }


}
