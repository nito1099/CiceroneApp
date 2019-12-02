package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeLugar;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView myRcView;
    AdapterDeLugar adapter;
    ArrayList<PojoLugar> lugares;
    public PojoLugar lugar;
    JSONObject objeto;
    Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);
        myRcView = findViewById(R.id.recycle_favs);
        myRcView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lugares = new ArrayList<>();
        contexto = this;

        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/lugaresFavoritos.php?ID="+ Global.getObject().getId();//cambair php
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            mostrar(response);
                        } catch (Exception e) {
                            Toast.makeText(contexto, "No cuentas con lugares Favoritos" , Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.qr_code){
            launchQr();
        }
        return true;
    }

    private void launchQr() {//CODIGO QR ACTIVITY
        Intent launchQRActivity = new Intent(FavoritesActivity.this,QrCodeActivity.class);
        startActivity(launchQRActivity);
    }

    public void mostrar(JSONArray info) throws JSONException {
        lugares.clear();

        for (int i = 0; i < info.length(); i++) {
            lugar = new PojoLugar();
            objeto = new JSONObject();
            objeto = info.getJSONObject(i);
            lugar.setNombre(objeto.getString("Nombre"));
            lugar.setDescripcion(objeto.getString("Descripcion"));
            lugar.setPK_ID(objeto.getInt("PK_ID"));
            lugar.setTelefono(objeto.getString("Telefono"));
            lugar.setDireccion(objeto.getString("Direccion"));
            lugar.setHorario_Inicio(objeto.getString("Horario_Inicio"));
            lugar.setHorario_Final(objeto.getString("Horario_Final"));
            lugar.setFK_Categoria(objeto.getInt("FK_Categoria"));
            Double [] array = new Double[3];
            array[0]=objeto.getDouble("Ninos");
            array[1]=objeto.getDouble("Especial");
            array[2]=objeto.getDouble("Adultos");
            lugar.setCostos(array);
            lugares.add(lugar);
        }

        adapter = new AdapterDeLugar(lugares, contexto, new AdapterDeLugar.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                launchInfoLugarCompleto(position);
            }
        });
        myRcView.setAdapter(adapter);
    }
    private void launchInfoLugarCompleto(int position) {
        Intent intent = new Intent(FavoritesActivity.this, InfoLugarActivity.class);
        intent.putExtra("Lugar", lugares.get(position));
        startActivity(intent);
    }
}
