package com.nitoelchidoceti.ciceroneapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeComentarios;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoComentario;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoLugarActivity extends AppCompatActivity {
    ImageButton addFav;
    Boolean selected, oneSelected, twoSelected, threeSelected, fourSelected, fiveSelected;
    PojoLugar pojoLugar;
    ArrayList<PojoComentario> comentarios;
    TextView nombreDelSitio,descripcionDeLugarCompleto,direccion,telefono,horario, costos, calificacionLugar;
    String previous;
    AdapterDeComentarios adapterDeComentario;
    RecyclerView recycleComentario;
    TextInputLayout escribirComentario;
    ImageButton oneStar, twoStar, threeStar, fourStar, fiveStar;
    EditText comentario;
    String calificacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lugar);
        Toolbar toolbar = findViewById(R.id.toolbar_inf_lugar);
        addFav = findViewById(R.id.imgbtnFav);
        escribirComentario = findViewById(R.id.inTxtEscComentario);
        escribirComentario.setCounterMaxLength(255);
        escribirComentario.setNextFocusDownId(R.id.btnPublicarComentario);
        oneSelected = false;
        twoSelected = false;
        threeSelected = false;
        fourSelected = false;
        fiveSelected = false;
        oneStar=findViewById(R.id.UnaEstrella);
        twoStar=findViewById(R.id.DosEstrellas);
        threeStar=findViewById(R.id.TresEstrellas);
        fourStar=findViewById(R.id.CuatroEstrellas);
        fiveStar=findViewById(R.id.CincoEstrellas);
        comentario=findViewById(R.id.txtComentarioNuevo);
        calificacionLugar=findViewById(R.id.txtCalificacionNumero);
        comentarios = new ArrayList<>();
        recycleComentario = findViewById(R.id.recycle_comentarios);
        recycleComentario.setLayoutManager(new LinearLayoutManager(InfoLugarActivity.this,
                LinearLayoutManager.VERTICAL, false));
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
        consultaComentarios();
        llenarInformacion();
        comprobarFav();
        calcularCalificacion();
    }

    private void calcularCalificacion() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/promedioCalificacion.php?lugar=" + pojoLugar.getPK_ID();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject;
                            jsonObject = response.getJSONObject(0);
                            calificacionLugar.setText(jsonObject.getString("Calificacion"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(InfoLugarActivity.this);
        queue.add(jsonArrayRequest);
    }

    private void consultaComentarios() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/comentariosLugar.php?id_place=" + pojoLugar.getPK_ID();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            mostrarComentarios(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(InfoLugarActivity.this);
        queue.add(jsonArrayRequest);
    }


    /**
     * Obtiene los datos y los agrega al arreglo de comentarios
     * para despues mandarles el array
     *
     * @param response
     * @throws JSONException
     */
    private void mostrarComentarios(JSONArray response) throws JSONException {
        comentarios.clear();
        for (int i = 0; i < response.length(); i++) {
            PojoComentario comentario = new PojoComentario();
            JSONObject objeto;
            objeto = response.getJSONObject(i);
            comentario.setComentario(objeto.getString("Descripcion"));
            comentario.setFecha(objeto.getString("Fecha"));
            comentario.setUserName(objeto.getString("Nombre"));
            comentario.setFK_lugar(objeto.getString("FK_Sitios"));
            comentarios.add(comentario);
        }
        adapterDeComentario = new AdapterDeComentarios(comentarios);
        recycleComentario.setAdapter(adapterDeComentario);

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
                "?user=" + Global.getObject().getId() + "&lugar=" + pojoLugar.getPK_ID();
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

    public void selectedStar(View view) {
        stars(1,(oneSelected==true)?true:false);

    }

    public void selected2Stars(View view) {
        stars(2,(twoSelected==true)?true:false);
    }

    public void selectedThreeStars(View view) {
        stars(3,(threeSelected==true)?true:false);

    }

    public void selectedFourStars(View view) {
        stars(4,(fourSelected==true)?true:false);

    }

    public void selected5FiveStars(View view) {

        stars(5,(fiveSelected==true)?true:false);
    }

    private void stars(int numero, boolean isSelected){

        if (isSelected==false){
            switch (numero){
                case 1:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic__empty_star);
                    threeStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=false;
                    threeSelected=false;
                    fourSelected=false;
                    fiveSelected=false;

                    calificacion ="1";
                    break;
                case 2:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=false;
                    fourSelected=false;
                    fiveSelected=false;
                    calificacion ="2";

                    break;
                case 3:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic_one_star);
                    fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=true;
                    fourSelected=false;
                    fiveSelected=false;

                    calificacion ="3";
                    break;
                case 4:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic_one_star);
                    fourStar.setBackgroundResource(R.drawable.ic_one_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=true;
                    fourSelected=true;
                    fiveSelected=false;

                    calificacion ="4";
                    break;
                case 5:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic_one_star);
                    fourStar.setBackgroundResource(R.drawable.ic_one_star);
                    fiveStar.setBackgroundResource(R.drawable.ic_one_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=true;
                    fourSelected=true;
                    fiveSelected=true;

                    calificacion ="5";
                    break;
            }
        }else{
            switch (numero){
                case 1:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic__empty_star);
                    threeStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=false;
                    threeSelected=false;
                    fourSelected=false;
                    fiveSelected=false;
                    calificacion ="1";
                    break;
                case 2:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=false;
                    fourSelected=false;
                    fiveSelected=false;

                    calificacion ="2";
                    break;
                case 3:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic_one_star);
                    fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=true;
                    fourSelected=false;
                    fiveSelected=false;

                    calificacion ="3";
                    break;
                case 4:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic_one_star);
                    fourStar.setBackgroundResource(R.drawable.ic_one_star);
                    fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=true;
                    fourSelected=true;
                    fiveSelected=false;

                    calificacion ="4";
                    break;
                case 5:
                    oneStar.setBackgroundResource(R.drawable.ic_one_star);
                    twoStar.setBackgroundResource(R.drawable.ic_one_star);
                    threeStar.setBackgroundResource(R.drawable.ic_one_star);
                    fourStar.setBackgroundResource(R.drawable.ic_one_star);
                    fiveStar.setBackgroundResource(R.drawable.ic_one_star);

                    oneSelected=true;
                    twoSelected=true;
                    threeSelected=true;
                    fourSelected=true;
                    fiveSelected=true;

                    calificacion ="5";

                    break;
            }
        }

    }

    /**
     * Se agrega el review a la DB
     * @param view
     */
    public void agregarReview(View view) {
        if (calificacion!=null||comentario.getText()==null){
            String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/" +
                    "agregarReviewSitio.php?nombre="+Global.getObject().getId()+"&sitio="+ pojoLugar.getPK_ID()+
                    "&calificacion="+calificacion+"&comentario="+comentario.getText();
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jsonObject ;
                            try {
                                jsonObject = response.getJSONObject(0);
                                if (jsonObject.getString("success").equals("true")){
                                    Toast.makeText(InfoLugarActivity.this,"Se ha " +
                                            "publicado su comentario correctamente",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(InfoLugarActivity.this,"Ya ha agregado " +
                                            "un comentario previamente.",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(InfoLugarActivity.this);
            requestQueue.add(jsonArrayRequest);
        }else {
            Toast.makeText(InfoLugarActivity.this,"Porfavor no deje campos vacios.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
