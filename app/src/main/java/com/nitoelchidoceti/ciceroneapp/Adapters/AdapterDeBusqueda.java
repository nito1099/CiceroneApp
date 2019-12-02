package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.content.Context;
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
    private Context context;
    private AdapterDeBusqueda.OnItemClickListener listener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    public AdapterDeBusqueda(ArrayList<PojoLugar> lugaresParaAgregar,Context context, AdapterDeBusqueda.OnItemClickListener listener) {
        this.lugaresParaAgregar = lugaresParaAgregar;
        this.context = context;
        this.listener = listener;
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
        holder.llenarDatos(lugaresParaAgregar.get(position),position,listener);
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

        public void llenarDatos(PojoLugar lugar , final int posicion, final AdapterDeBusqueda.OnItemClickListener listener) {
            nombre.setText(lugar.getNombre());
            descripcion.setText(lugar.getDescripcion());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(posicion);
                }
            });
        }
    }

    public void setFilter(ArrayList<PojoLugar> lugares ) {
        this.lugaresParaAgregar = new ArrayList<>();
        this.lugaresParaAgregar.addAll(lugares);
        notifyDataSetChanged();

    }
}
