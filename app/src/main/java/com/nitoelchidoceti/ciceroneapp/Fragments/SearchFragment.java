package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeBusqueda;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.InfoLugarActivity;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.nitoelchidoceti.ciceroneapp.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    public RecyclerView searchRecycle;
    private AdapterDeBusqueda adapter;
    private View view;
    private ArrayList<PojoLugar> lugares;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchRecycle =view.findViewById(R.id.recycle_search);
        searchRecycle.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        setHasOptionsMenu(true);
        lugares= Global.getObject().getLugares();
        adapter = new AdapterDeBusqueda(lugares, view.getContext(), new AdapterDeBusqueda.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                launchInfoLugarCompleto(position);
            }
        });
        searchRecycle.setAdapter(adapter);
        return view;
    }

    private void launchInfoLugarCompleto(int position) {
        Intent intent = new Intent(view.getContext(), InfoLugarActivity.class);
        intent.putExtra("Lugar", lugares.get(position));
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_buscador, menu);
        MenuItem item = menu.findItem(R.id.buscador);

        androidx.appcompat.widget.SearchView searchView;
         searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                adapter.setFilter(Global.getObject().getLugares());
                return true;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }



    @Override
    public boolean onQueryTextChange(String newText) {

        try {
            if (!isVisible()) {
                // The fragment was replaced so ignore
                return true;
            }
            ArrayList<PojoLugar> filteredList = filterLugares(Global.getObject().getLugares(), newText);
            adapter.setFilter(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private ArrayList<PojoLugar> filterLugares(ArrayList<PojoLugar> lugares, String textoAFiltrar) {

        ArrayList<PojoLugar> lugaresFiltrados = new ArrayList<>();

        try {
            textoAFiltrar = textoAFiltrar.toLowerCase();

            for (PojoLugar lugar : lugares) {
                String nombre = lugar.getNombre().toLowerCase();
                if (nombre.contains(textoAFiltrar)) {
                    lugaresFiltrados.add(lugar);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.lugares=lugaresFiltrados;
        return lugaresFiltrados;
    }
}
