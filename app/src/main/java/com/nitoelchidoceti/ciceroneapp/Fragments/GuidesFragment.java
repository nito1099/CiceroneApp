package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class GuidesFragment extends Fragment {
    View view;
    RecyclerView myRcView;
    AdapterDeBusqueda adapter;
    ArrayList<PojoLugar> lugares;
    PojoLugar lugar;
    JSONObject objeto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guides,container,false);

        lugares = new ArrayList<>();
        myRcView = myRcView.findViewById(R.id.recycle_guides);
        myRcView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        llenarpojo();

        return view;
    }

    private void llenarpojo() {
        String url = "";
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
            lugar = new PojoLugar();
            objeto = new JSONObject();
            objeto = info.getJSONObject(i);
            lugar.setNombre(objeto.getString("Nombre"));
            lugar.setDescripcion(objeto.getString("Descripcion"));
            lugares.add(lugar);
        }

        adapter = new AdapterDeBusqueda(lugares);
        myRcView.setAdapter(adapter);
    }


}
