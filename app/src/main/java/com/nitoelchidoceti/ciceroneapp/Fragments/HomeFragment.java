package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeLugar;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.nitoelchidoceti.ciceroneapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment  {
    Context contexto;
    /*public HomeFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        contexto=context;
    }*/

    RecyclerView myRcView;
    AdapterDeLugar adapter;
    ArrayList<PojoLugar> lugares;
    public PojoLugar lugar;
    JSONObject objeto;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home,container,false);

        final Spinner spinner = myView.findViewById(R.id.spinnerCategory);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                myView.getContext(),R.array.categorias,android.R.layout.simple_spinner_item);//CREACION DE ADAPTER

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //AGREGAR LA ANIMACION DE VISTA DE LOS ITEMS DEL SPINNER

        spinner.setAdapter(spinnerAdapter);//ASIGNAR ADAPTER AL SPINNER

        myRcView = myView.findViewById(R.id.recycle_home);
        myRcView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        lugares = new ArrayList<>();
        contexto = this.getContext();
        spinner.setSelection(4);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 4) {
                    final String url = "http://ec2-52-25-238-53.us-west-2.compute.amazonaws.com/Cicerone/PHP/lugarRecycle.php?Categoria=" + (position + 1);
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {


                                        try {
                                            mostrar(response);
                                        } catch (Exception e) {
                                            Toast.makeText(contexto, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(contexto, "Volley Error:" + error + "" + url, Toast.LENGTH_LONG).show();

                        }
                    });
                    RequestQueue ejecutaHome = Volley.newRequestQueue(contexto);
                    ejecutaHome.add(jsonArrayRequest);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(4);
            }
        });

        return myView;
    }

    public void mostrar(JSONArray info) throws JSONException {


        for (int i = 0; i < info.length(); i++) {
            lugar = new PojoLugar();
            objeto = new JSONObject();
            objeto = info.getJSONObject(i);
            lugar.setNombre(objeto.getString("Nombre"));
            lugar.setDescripcion(objeto.getString("Descripcion"));
            lugares.add(lugar);
        }
        adapter = new AdapterDeLugar(lugares);
        adapter.contexto = contexto;
        myRcView.setAdapter(adapter);
    }
}
