package com.nitoelchidoceti.ciceroneapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;

import org.json.JSONArray;
import org.json.JSONObject;

public class InfoLugarActivity extends AppCompatActivity {
    ImageButton addFav;
    Boolean selected;
    PojoLugar pojoLugar;
    TextView nombreDelSitio,descripcionDeLugarCompleto,direccion,telefono,horario, costos;
    String previous;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lugar);
        Toolbar toolbar = findViewById(R.id.toolbar_inf_lugar);
        addFav = findViewById(R.id.imgbtnFav);

        if ( (getIntent().getSerializableExtra("previusActivity"))==null){
            previous="nada";
        }else{
            previous=(String) getIntent().getSerializableExtra("previusActivity");
        }
        nombreDelSitio=findViewById(R.id.txtNombreLugar);
        descripcionDeLugarCompleto=findViewById(R.id.txtDescripcion);
        direccion=findViewById(R.id.txtDireccionSitio);
        telefono=findViewById(R.id.txtTelefonoSitio);
        horario=findViewById(R.id.txtHorarioSitio);
        costos=findViewById(R.id.txtCostosSitio);
        pojoLugar =(PojoLugar) getIntent().getSerializableExtra("Lugar");
        setSupportActionBar(toolbar);
        llenarInformacion();
        comprobarFav();

    }

    /**
     * Llena la inf de los lugares
     */
    private void llenarInformacion() {

        nombreDelSitio.setText(pojoLugar.getNombre());
        descripcionDeLugarCompleto.setText(pojoLugar.getDescripcion());
        direccion.setText(pojoLugar.getDireccion());
        telefono.setText(pojoLugar.getTelefono());
        horario.setText(pojoLugar.getHorario_Inicio()+ " hrs a "+
                pojoLugar.getHorario_Final()+" hrs");
        Double []array;
        array=pojoLugar.getCostos();
        costos.setText("Niños: "+ array[0]+" MXN"+ "\n" +
                "Estudiantes o 3ra Edad: "+ array[1]+" MXN"+"\n" +
                "Adultos: "+ array[2]+" MXN");
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

    /**
     * Comprueba si ya tiene el sitio como favorito
     */
    private void comprobarFav(){
        final String url="http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/comprobarFavoritos.php" +
                "?user="+ Global.getObject().getId()+"&lugar="+pojoLugar.getPK_ID();//**************FALTA ENVIAR ID DE LUGAR
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,//*************COMPROBAR SI TIENE EL SITIO COMO FAV
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            JSONObject success = response.getJSONObject(0);
                            if (success.getString("success").equals("true")){//afirmar que si hay fav
                                addFav.setBackgroundResource(R.drawable.ic_favoritos_selected);
                                selected=true;
                            }else{
                                addFav.setBackgroundResource(R.drawable.ic_favoritos);
                                selected=false;
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InfoLugarActivity.this,"Error: "+ error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(InfoLugarActivity.this);
        queue.add(jsonArrayRequest);
    }


    private void launchQr() {//CODIGO QR ACTIVITY
        Intent launchQRActivity = new Intent(InfoLugarActivity.this,QrCodeActivity.class);
        startActivity(launchQRActivity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (previous.equals("favoritos")){
            Intent intent = new Intent(InfoLugarActivity.this,FavoritesActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Cuando se presiona el boton de favoritos
     * Agrega el sitio si no lo tiene o lo elimina
     * @param view vista de xml
     */
    public void addFavPlace(View view) {//AGREGAR O ELIMINAR LUGAR DE FAVORITOS
        if (selected==false){
            final String url="http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/agregarFavoritos.php" +
                    "?user="+ Global.getObject().getId()+"&lugar="+pojoLugar.getPK_ID();//**************FALTA ENVIAR ID DE LUGAR
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try{
                                addFav.setBackgroundResource(R.drawable.ic_favoritos_selected);
                                selected=true;
                                Toast.makeText(InfoLugarActivity.this,"Se ha agregado a favoritos correctamente ",
                                        Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(InfoLugarActivity.this,""+url,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(InfoLugarActivity.this,"Error: "+ error.getMessage(),
                            Toast.LENGTH_LONG).show();
                    //Toast.makeText(InfoLugarActivity.this,""+url,
                    //        Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue queue = Volley.newRequestQueue(InfoLugarActivity.this);
            queue.add(jsonArrayRequest);
        }else{
            final String url="http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/eliminarFavoritos.php" +
                    "?user="+ Global.getObject().getId()+"&lugar="+pojoLugar.getPK_ID();//**************FALTA ENVIAR ID DE LUGAR
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try{
                                addFav.setBackgroundResource(R.drawable.ic_favoritos);
                                selected=false;
                                Toast.makeText(InfoLugarActivity.this,"Se ha elminado a favoritos correctamente ",
                                        Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(InfoLugarActivity.this,""+url,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(InfoLugarActivity.this,"Error: "+ error.getMessage(),
                            Toast.LENGTH_LONG).show();

                }
            });
            RequestQueue queue = Volley.newRequestQueue(InfoLugarActivity.this);
            queue.add(jsonArrayRequest);

        }
    }
}
