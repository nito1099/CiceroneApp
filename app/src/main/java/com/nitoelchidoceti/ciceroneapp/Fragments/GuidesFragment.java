package com.nitoelchidoceti.ciceroneapp.Fragments;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeBusquedaGuias;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GuidesFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView myRcView;
    private AdapterDeBusquedaGuias adapter;
    private ArrayList<PojoGuia> guias;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guides,container,false);

        guias = new ArrayList<>();
        myRcView = view.findViewById(R.id.recycle_guides);
        myRcView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        setHasOptionsMenu(true);
        llenarpojo();
        return view;
    }

    private void llenarpojo() {
        String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/guias.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            llenarGuiasAlPojo(response);

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

    private void llenarGuiasAlPojo(JSONArray info) throws JSONException {
        guias.clear();
        for (int i = 0; i < info.length(); i++) {
            PojoGuia guia = new PojoGuia();
            JSONObject objeto = new JSONObject();
            objeto = info.getJSONObject(i);
            guia.setId(Integer.parseInt(objeto.getString("PK_Registro")));
            guia.setNombre(objeto.getString("Nombre"));
            guia.setTelefono(objeto.getString("Telefono"));
            guia.setDuracion(objeto.getString("Duracion"));
            guia.setCorreo(objeto.getString("Correo"));
            guia.setSitio(objeto.getString("Sitio"));
            //FALTA LA FOTOGRAFÍA********
            Double[] aux = new Double[3];
            aux[0] = Double.valueOf(objeto.getString("Ninos"));
            aux[1] = Double.valueOf(objeto.getString("Especial"));
            aux[2] = Double.valueOf(objeto.getString("Adultos"));
            guia.setCostos(aux);
            guias.add(guia);
        }
        adapter = new AdapterDeBusquedaGuias(guias);
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

                adapter.setFilter(guias);
                return true;
            }
        });
    }

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
            ArrayList<PojoGuia> filteredList = filterGuias(guias, newText);
            adapter.setFilter(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private ArrayList<PojoGuia> filterGuias(ArrayList<PojoGuia> guias, String textoAFiltrar) {

        ArrayList<PojoGuia> lugaresFiltrados = new ArrayList<>();

        try {
            textoAFiltrar = textoAFiltrar.toLowerCase();

            for (PojoGuia guia : guias) {
                String nombre = guia.getNombre().toLowerCase();
                if (nombre.contains(textoAFiltrar)) {
                    lugaresFiltrados.add(guia);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lugaresFiltrados;
    }

}
