package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeBusquedaGuias;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.InfoGuiaActivity;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GuidesFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView myRcView;
    private AdapterDeBusquedaGuias adapter;
    private ArrayList<PojoGuia> guiascompletos;
    private View view;
    ArrayList<String> idiomas, titulos;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guides,container,false);
        idiomas = new ArrayList<>();

        titulos = new ArrayList<>();
        guiascompletos = Global.getObject().getGuias();
        myRcView = view.findViewById(R.id.recycle_guides);
        myRcView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        setHasOptionsMenu(true);
        adapter = new AdapterDeBusquedaGuias(view.getContext(),guiascompletos, new AdapterDeBusquedaGuias.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                launchInfoGuiasActivity(position);
            }
        });
        myRcView.setAdapter(adapter);

        return view;
    }


    private void launchInfoGuiasActivity(int position) {
        Intent intentGuia = new Intent(view.getContext(), InfoGuiaActivity.class);
        intentGuia.putExtra("Guia", this.guiascompletos.get(position));
        startActivity(intentGuia);
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

                if (guiascompletos!=null){
                    adapter.setFilter(Global.getObject().getGuias());
                }
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
            ArrayList<PojoGuia> filteredList = filterGuias(Global.getObject().getGuias(), newText);
            adapter.setFilter(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private ArrayList<PojoGuia> filterGuias(ArrayList<PojoGuia> guias, String textoAFiltrar) {

        ArrayList<PojoGuia> guiasFiltrados = new ArrayList<>();

        try {
            textoAFiltrar = textoAFiltrar.toLowerCase();

            for (PojoGuia guia : guias) {
                String nombre = guia.getNombre().toLowerCase();
                if (nombre.contains(textoAFiltrar)) {
                    guiasFiltrados.add(guia);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.guiascompletos=guiasFiltrados;
        return guiasFiltrados;
    }

}
