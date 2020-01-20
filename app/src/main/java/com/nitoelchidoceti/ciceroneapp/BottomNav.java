package com.nitoelchidoceti.ciceroneapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nitoelchidoceti.ciceroneapp.Fragments.AccountFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.GuidesFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.HomeFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.MapFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.SearchFragment;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BottomNav extends AppCompatActivity {

    private EditText txtNombre,txtEmail,txtPlace,txtCell,txtBirthday;
    TextView btnListo;
    public ArrayList<PojoLugar> lugaresDeAqui;
    private  ArrayList<PojoGuia> guias;
    FloatingActionButton btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        BottomNavigationView bottomNav = findViewById(R.id.menu_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        guias=new ArrayList<>();
        lugaresDeAqui=new ArrayList<>();
        if (getIntent().getSerializableExtra("guia")!= null){//si la actividad anterior fue infolugar act
            launchGuidesFragment();
            bottomNav.setSelectedItemId(R.id.nav_guides);
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
        Toolbar toolbar = findViewById(R.id.toolbarBottom);
        setSupportActionBar(toolbar);
        txtBirthday = findViewById(R.id.txt_birthday_account);
        llenarpojo();//lugares
        llenarpojo2();//guias
        chicoMalo();
    }



    private void chicoMalo() {
        String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/malasPalabras.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> aux = new ArrayList<>();
                            for (int i=0;i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                aux.add(jsonObject.getString("BadWord"));
                            }
                            Global.getObject().setMalasPalabras(aux);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BottomNav.this, "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("valeverga"+error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(BottomNav.this);
        requestQueue.add(jsonArrayRequest);
    }

    private void llenarpojo2() {
        String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/guias.php";
        JsonArrayRequest jsonArrayRequestGuia = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            llenarGuiasAlPojo(response);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BottomNav.this, "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        RequestQueue requestQueueGuia = Volley.newRequestQueue(BottomNav.this);
        requestQueueGuia.add(jsonArrayRequestGuia);
    }

    private void llenarGuiasAlPojo(JSONArray info) throws JSONException {
        guias.clear();
        for (int i = 0; i < info.length(); i++) {
            PojoGuia guia = new PojoGuia();
            JSONObject objeto ;
            objeto = info.getJSONObject(i);
            guia.setId(Integer.parseInt(objeto.getString("PK_Registro")));
            guia.setNombre("Guia "+objeto.getString("Nombre"));
            guia.setTelefono(objeto.getString("Telefono"));
            guia.setDuracion(objeto.getString("Duracion"));
            guia.setCorreo(objeto.getString("Correo"));
            guia.setFotografia(objeto.getString("Fotografia"));
            guia.setNombreDelSitio(objeto.getString("Sitio"));
            guia.setHorario("De " + objeto.getString("Horario_Inicio") + " a " + objeto.getString("Horario_Final"));
            guia.setToken(objeto.getString("Token"));
            guia.setFK_Sitio(objeto.getString("FK_Sitio"));
            //FALTA LA FOTOGRAFÍA********
            Double[] aux = new Double[3];
            aux[0] = Double.valueOf(objeto.getString("Ninos"));
            aux[1] = Double.valueOf(objeto.getString("Especial"));
            aux[2] = Double.valueOf(objeto.getString("Adultos"));
            guia.setCostos(aux);
            guia.setIdiomas(consultaIdiomas(guia.getId()));  //agrego los idiomas que me regresa mi funcion
            guia.setTitulos(consultaTitulos(guia.getId()));  //lo mismo de arriba pero con titulos
            guias.add(guia);
        }
        Global.getObject().setGuias(guias);
    }

    private ArrayList<String> consultaTitulos(int guia) {
        final ArrayList<String> titulos = new ArrayList<>();
        String url2 = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/titulosGuia.php?FK_guia="+guia;
        System.out.println(url2);
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET,
                url2,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                titulos.add(jsonObject.getString("Especialidad")+" en " +
                                        jsonObject.getString("Carrera")+" en el "+
                                        jsonObject.getString("Universidad"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BottomNav.this, "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue2 = Volley.newRequestQueue(BottomNav.this);
        requestQueue2.add(jsonArrayRequest2);
        return  titulos;
    }

    private ArrayList<String> consultaIdiomas(int  guia) {
        final ArrayList<String> idiomas = new ArrayList<>();
        String url2 = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/idiomasGuias.php?FK_guia="+guia;
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET,
                url2,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int j=0;j<response.length();j++){
                            JSONObject jsonObject = response.getJSONObject(j);
                            idiomas.add(jsonObject.getString("Idioma"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BottomNav.this, "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue2 = Volley.newRequestQueue(BottomNav.this);
        requestQueue2.add(jsonArrayRequest2);
        return idiomas;
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
                Toast.makeText(BottomNav.this, "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(BottomNav.this);
        requestQueue.add(jsonArrayRequest);
    }

    private void llenarLugaresAlPojo(JSONArray info) throws JSONException {
        if (lugaresDeAqui.isEmpty()){

        }else{
            lugaresDeAqui.clear();
        }
        for (int i = 0; i < info.length(); i++) {
            PojoLugar lugar = new PojoLugar();
            JSONObject objeto;
            objeto = info.getJSONObject(i);
            lugar.setNombre(objeto.getString("Nombre"));
            lugar.setDescripcion(objeto.getString("Descripcion"));
            lugar.setPK_ID(objeto.getInt("PK_ID"));
            lugar.setTelefono(objeto.getString("Telefono"));
            lugar.setDireccion(objeto.getString("Direccion"));
            lugar.setFotografia(objeto.getString("Fotografia"));
            lugar.setHorario_Inicio(objeto.getString("Horario_Inicio"));
            lugar.setHorario_Final(objeto.getString("Horario_Final"));
            lugar.setFK_Categoria(objeto.getInt("FK_Categoria"));
            Double [] array = new Double[3];
            array[0]=objeto.getDouble("Ninos");
            array[1]=objeto.getDouble("Especial");
            array[2]=objeto.getDouble("Adultos");
            lugar.setCostos(array);
            lugaresDeAqui.add(lugar);
        }
        Global.getObject().setLugares(lugaresDeAqui);
    }


    private void launchQr() {//CODIGO QR ACTIVITY
        Intent launchQRActivity = new Intent(BottomNav.this,QrCodeActivity.class);
        startActivity(launchQRActivity);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_guides:
                            selectedFragment = new GuidesFragment();
                            break;
                        case R.id.nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

                    return  true;
                }
            };

    public void launchGuidesFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new GuidesFragment()).commit();
    }
    public void launchLoginFromAccount(final View view) {
        txtNombre = findViewById(R.id.txt_nombre_account);
        txtEmail = findViewById(R.id.txt_email_account);
        txtPlace = findViewById(R.id.txt_lugar_account);
        txtCell = findViewById(R.id.txt_telefono_account);
        txtBirthday = findViewById(R.id.txt_birthday_account);
        btnEditar = findViewById(R.id.btn_edit_account);
        btnListo = findViewById(R.id.btn_log_out);

        if (btnListo.getText().equals("Listo")){
            final String link = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/updateInfTurista.php" +
                    "?nombre=" + txtNombre.getText().toString() + "&correo=" + txtEmail.getText().toString() + "&telefono=" + txtCell.getText().toString() +
                    "&fecha=" + txtBirthday.getText().toString() + "&lugar=" + txtPlace.getText().toString() + "&ID=" + Global.getObject().getId();
            JsonArrayRequest pet = new JsonArrayRequest(Request.Method.GET,
                    link,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //Toast.makeText(view.getContext(),""+link,Toast.LENGTH_LONG).show();
                            try {
                                JSONObject obj = response.getJSONObject(0);
                                if (obj.getString("success").equals("true")) {
                                    Toast.makeText(view.getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                                    txtNombre.setEnabled(false);
                                    txtEmail.setEnabled(false);
                                    txtPlace.setEnabled(false);
                                    txtCell.setEnabled(false);
                                    txtBirthday.setEnabled(false);
                                    btnListo.setText("Cerrar Sesión");
                                    btnEditar.setEnabled(false);

                                } else {
                                    Toast.makeText(view.getContext(), "El correo ingresado ya existe.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(view.getContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            RequestQueue peticion = Volley.newRequestQueue(view.getContext());
            peticion.add(pet);
        }else{
            if (AccessToken.getCurrentAccessToken() == null) {
                Intent launchLoginFromAccount = new Intent(this,LoginActivity.class);
                launchLoginFromAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchLoginFromAccount);
                // already logged out
            }else {
                disconnectFromFacebook(view.getContext());
            }
        }

    }

    public void disconnectFromFacebook(final Context context) {

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();
                Intent launchLoginFromAccount = new Intent(context,LoginActivity.class);
                launchLoginFromAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchLoginFromAccount);

            }
        }).executeAsync();
    }


    public void launchInboxActivity(View view) {
        Intent intent = new Intent(BottomNav.this,InboxActivity.class);
        startActivity(intent);
    }


    public void editarInfTurista(View view) {
        txtNombre=findViewById(R.id.txt_nombre_account);
        txtEmail=findViewById(R.id.txt_email_account);
        txtPlace=findViewById(R.id.txt_lugar_account);
        txtCell = findViewById(R.id.txt_telefono_account);
        txtBirthday = findViewById(R.id.txt_birthday_account);
        btnEditar=findViewById(R.id.btn_edit_account);
        btnListo=findViewById(R.id.btn_log_out);

        txtNombre.setEnabled(true);
        txtEmail.setEnabled(true);
        txtPlace.setEnabled(true);
        txtCell.setEnabled(true);
        txtBirthday.setEnabled(true);
        btnListo.setText("Listo");
        btnEditar.setEnabled(false);

    }

    public void launchFavoritesAct(View view) {
        Intent intent = new Intent(BottomNav.this,FavoritesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.qr_code) {
            launchQr();
        }
        return true;
    }



}
