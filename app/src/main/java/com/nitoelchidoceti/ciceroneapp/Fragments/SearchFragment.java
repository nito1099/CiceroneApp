package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeBusqueda;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.nitoelchidoceti.ciceroneapp.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView myRcView;
    private AdapterDeBusqueda adapter;
    private ArrayList<PojoLugar> lugares;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        lugares = new ArrayList<>();
        myRcView =view.findViewById(R.id.recycle_search);
        myRcView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        setHasOptionsMenu(true);
        llenarpojo();
        return view;
    }

    private void llenarpojo() {
        String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/lugares.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            llenarLugaresAlPojo(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void llenarLugaresAlPojo(JSONArray info) throws JSONException {
        lugares.clear();
        for (int i = 0; i < info.length(); i++) {
            PojoLugar lugar = new PojoLugar();
            JSONObject objeto = new JSONObject();
            objeto = info.getJSONObject(i);
            lugar.setNombre(objeto.getString("Nombre"));
            lugar.setDescripcion(objeto.getString("Descripcion"));
            lugares.add(lugar);
        }
        adapter = new AdapterDeBusqueda(lugares);
        myRcView.setAdapter(adapter);
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

                adapter.setFilter(lugares);
                return true;
            }
        });
    }

    /*@Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_buscador, menu);
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

                adapter.setFilter(lugares);
                return true;
            }
        });
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }



    @Override
    public boolean onQueryTextChange(String newText) {// nunca entra a esta funcion

        try {
            if (!isVisible()) {
                // The fragment was replaced so ignore
                return true;
            }
            ArrayList<PojoLugar> filteredList = filterLugares(lugares, newText);
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
        return lugaresFiltrados;
    }
}
