package com.nitoelchidoceti.ciceroneapp;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeComentarios;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeViewPager;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoComentario;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
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
    ImageView fotoPerfil;
    ArrayList<String> imagenes = new ArrayList<>();
    ViewPager viewPager;
    Button btnPagarTour, btnNecestiaGuia;

    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Global.PAYPAL_CLIENT_ID);

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lugar);
        inicializacion();
        comprobarTourPagado();

        btnPagarTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnPagarTour.getText().equals("TOUR PAGADO")){
                    Toast.makeText(InfoLugarActivity.this, "Usted ya ha pagado el tour, gracias ! :) ", Toast.LENGTH_SHORT).show();
                }else {
                    existeTour();
                }
            }
        });

        btnNecestiaGuia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoLugarActivity.this,BottomNav.class);
                intent.putExtra("guia",pojoLugar.getPK_ID());
                startActivity(intent);
            }
        });

        consultaComentarios();
        llenarInformacion();
        comprobarFav();
        calcularCalificacion();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        comprobarTourPagado();
    }

    private void comprobarTourPagado() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/comprobarTourPagado.php?sitio="+pojoLugar.getPK_ID()+"&turista="+Global.getObject().getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            if (jsonObject.getString("success").equals("false")){
                                btnPagarTour.setText("TOUR PAGADO");
                            }
                        } catch (JSONException e) {
                            Toast.makeText(InfoLugarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(InfoLugarActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void existeTour() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/" +
                "PHP/comprobarTour.php?lugar="+pojoLugar.getPK_ID();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            if (jsonObject.getString("success").equals("true")){
                                iniciarServicioPaypal();
                                procesarPago();
                            }else {
                                Toast.makeText(InfoLugarActivity.this, "Lo sentimos pero aun no tenemos tour para este sitio :(", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(InfoLugarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(InfoLugarActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    tourPagado();
                }
            }else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Compra cancelada", Toast.LENGTH_SHORT).show();


        }else if(requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Compra invalida", Toast.LENGTH_SHORT).show();
    }

    private void tourPagado() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/tourPagado.php?sitio="+pojoLugar.getPK_ID()+"&turista="+Global.getObject().getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            if (jsonObject.getString("success").equals("true")){
                                Toast.makeText(InfoLugarActivity.this, "Se ha realizado el pago correctamente", Toast.LENGTH_SHORT).show();
                                btnPagarTour.setText("TOUR PAGADO");
                            }else {
                                Toast.makeText(InfoLugarActivity.this, "No se pudo realizar el pago correctamente", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(InfoLugarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(InfoLugarActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void iniciarServicioPaypal() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivity(intent);
    }

    private void procesarPago() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(30)), "MXN", "Tour guiado del " +
                pojoLugar.getNombre(), PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    /**
     * inicializa todas la variables de la actividad
     * y los configura
     */
    private void  inicializacion(){
        Toolbar toolbar = findViewById(R.id.toolbar_inf_lugar);
        addFav = findViewById(R.id.imgbtnFav);
        fotoPerfil = findViewById(R.id.imgFotoPerfilLugar);
        escribirComentario = findViewById(R.id.inTxtEscComentario);
        escribirComentario.setCounterMaxLength(255);
        escribirComentario.setNextFocusDownId(R.id.btnPublicarComentario);
        btnNecestiaGuia = findViewById(R.id.btnNecesitasGuia);
        oneSelected = false;
        twoSelected = false;
        threeSelected = false;
        fourSelected = false;
        fiveSelected = false;
        btnPagarTour = findViewById(R.id.btnPagarTour);
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
        if (!pojoLugar.getFotografia().equals("null")){
            fotoPerfil.setBackground(null);
            Glide.with(this).load(pojoLugar.getFotografia()).into(fotoPerfil);
        }
        viewPager = findViewById(R.id.viewPagerLugar);
        obtenerImagenes();
        setSupportActionBar(toolbar);
    }

    private void obtenerImagenes( ) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/consultaMultimedia.php?esGuia=false&id=" + pojoLugar.getPK_ID();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            llenarImagenes(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ImageView imageView = findViewById(R.id.viewPagerLugar);
                imageView.setImageResource(R.drawable.img_catedral_premium);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(InfoLugarActivity.this);
        queue.add(jsonArrayRequest);
    }

    private void llenarImagenes(JSONArray response) throws JSONException {
        imagenes.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject objeto;
                objeto = response.getJSONObject(i);
                imagenes.add(objeto.getString("Imagen"));
            }
        AdapterDeViewPager adapterDeViewPager = new AdapterDeViewPager(this,imagenes);
        viewPager.setAdapter(adapterDeViewPager);
    }

    /**
     * calcula el promedio del lugar
     */
    private void calcularCalificacion() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/promedioCalificacion.php?lugar=" + pojoLugar.getPK_ID()+
                "&esSitio=true";
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

    /**
     * obtiene los comentarios de la DB
     */
    private void consultaComentarios() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/comentariosLugar.php?id_place=" + pojoLugar.getPK_ID()+
                "&esSitio=true";
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
     * Agrega el sitio o si no lo tiene lo elimina
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

    /**
     * Dependiendo del numero de estrella de calificacion presionado cambia la vista
     * de la calificacion del lugar y registra que ya se ha asignado una calificacion
     * @param numero numero de estrella clickeada
     * @param isSelected si la estrella ya ha sido seleccionada anteriormente
     */
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
        if (chicoMalo() == false){
            if (calificacion != null || comentario.getText() != null ) {
                String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/" +
                        "agregarReviewSitio.php?nombre="+Global.getObject().getId()+"&sitio="+ pojoLugar.getPK_ID()+
                        "&calificacion="+calificacion+"&comentario="+comentario.getText()+"&esSitio=true";
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
        }else {
            Toast.makeText(this, "Su comentario cuenta con Palabras malsonantes", Toast.LENGTH_LONG).show();
            comentario.setText("");
        }

    }

    private boolean chicoMalo() {
        String sentence = comentario.getText().toString().toLowerCase();
        for (String malaPalabra : Global.getObject().getMalasPalabras()) {
            if (sentence.indexOf(malaPalabra.toLowerCase()) != -1) {
                return true;//chico malo
            }
        }
        return false;//chico Bueno
    }
}
