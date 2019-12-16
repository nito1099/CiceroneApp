package com.nitoelchidoceti.ciceroneapp.Adapters;

import android.app.ActionBar;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class AdapterDeAyudaAlUsuario extends RecyclerView.Adapter<AdapterDeAyudaAlUsuario.FichaHolderUserHelp> {

    private ArrayList<String> problemas,problemasCompletos;
    private AdapterDeAyudaAlUsuario.OnItemClickListener listener;



    public interface  OnItemClickListener{
        void OnItemClick(int position);
    }

    public AdapterDeAyudaAlUsuario(ArrayList<String> problemas,ArrayList<String> problemasCompletos, AdapterDeAyudaAlUsuario.OnItemClickListener listener) {
        this.problemas = problemas;
        this.listener = listener;
        this.problemasCompletos=problemasCompletos;
    }

    @NonNull
    @Override
    public AdapterDeAyudaAlUsuario.FichaHolderUserHelp onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = View.inflate(parent.getContext(), R.layout.recycle_user_help, null);
        AdapterDeAyudaAlUsuario.FichaHolderUserHelp holderSearch = new AdapterDeAyudaAlUsuario.FichaHolderUserHelp(vista);
        return holderSearch;
    }

    @Override
    public void onBindViewHolder(@NonNull FichaHolderUserHelp holder, int position) {
        holder.llenarDatos(problemas.get(position),position,listener);
    }


    @Override
    public int getItemCount() {
        return problemas.size();
    }

    class FichaHolderUserHelp extends RecyclerView.ViewHolder {

        TextView descripcion;

        public FichaHolderUserHelp(@NonNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.txtProblemaUserHelp);
        }

        public void llenarDatos(final String problems, final  int posicion, final AdapterDeAyudaAlUsuario.OnItemClickListener listener) {
            descripcion.setText(problems);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(posicion);

                    ViewGroup.LayoutParams params = descripcion.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    descripcion.setLayoutParams(params);
                    //descripcion.setHeight(350);
                    descripcion.setText(problemasCompletos.get(posicion));
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setFilter(ArrayList<String> problemas2) {
        this.problemas = new ArrayList<>();
        this.problemas.addAll(problemas2);
        notifyDataSetChanged();
    }
}
