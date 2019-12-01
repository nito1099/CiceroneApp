package com.nitoelchidoceti.ciceroneapp;

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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeBusqueda;
import com.nitoelchidoceti.ciceroneapp.Fragments.AccountFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.GuidesFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.HomeFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.MapFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.SearchFragment;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoLugar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BottomNav extends AppCompatActivity {

    private EditText txtNombre,txtEmail,txtPlace,txtCell,txtBirthday;
    TextView btnListo;
    private AdapterDeBusqueda adapter;
    public ArrayList<PojoLugar> lugaresDeAqui;
    FloatingActionButton btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        BottomNavigationView bottomNav = findViewById(R.id.menu_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        lugaresDeAqui=new ArrayList<>();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtBirthday = findViewById(R.id.txt_birthday_account);
        llenarpojo();
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
            Intent launchLoginFromAccount = new Intent(this,LoginActivity.class);
            launchLoginFromAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchLoginFromAccount);
        }

    }

    public void launchHomeFragment(View view) {

        BottomNavigationView bottomNav = findViewById(R.id.menu_bottom_navigation);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    public void launchInboxActivity(View view) {
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
    public void launchInfoLugar(View view) {
        Intent intent = new Intent(BottomNav.this,InfoLugarActivity.class);
        startActivity(intent);
    }

}
