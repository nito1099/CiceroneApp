package com.nitoelchidoceti.ciceroneapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoRegistro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.widget.Toast.LENGTH_SHORT;


public class RegistryActivity extends AppCompatActivity {
    private static final String[] CITIES = new String[]{
      "Guadalajara", "Tlaquepaque", "Zapopan", "Tonalá", "Tlajomulco",
        "El Salto", "CDMX", "Monterrey", "Gomez Palacio", " Guanajuato"
    };
    EditText etxtDate;
    Button registrar;
    EditText nombre, correo, contraseña, telefono, ciudad;
    TextView consulta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        registrar = findViewById(R.id.btnRegister);
        nombre = findViewById(R.id.etxt_nombre_registry_activity);
        correo = findViewById(R.id.etxt_correo_registry_activity);
        contraseña = findViewById(R.id.etxt_input_password_registry_activity);
        telefono = findViewById(R.id.etxt_telefono_registry_activity);
        ciudad = findViewById(R.id.atxt_view_ciudad_registry_activity);
        ciudad.setNextFocusDownId(telefono.getId());
        consulta = findViewById(R.id.txt_ingresa_registry_activity);
        AutoCompleteTextView aTxtViewCiudad = findViewById(R.id.atxt_view_ciudad_registry_activity);
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, CITIES);
        aTxtViewCiudad.setAdapter(adapterCiudad);

        etxtDate = findViewById(R.id.etxt_nacimiento_registry_activity);

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etxtDate.setText("2019/03/23");
        nombre.setText("Alan Lomeli");
        contraseña.setText("clave");
        telefono.setText("3336266817");
        ciudad.setText("Zapopan");
        correo.setText("alanlomeli@gmail.com");

        etxtDate.setOnClickListener(new View.OnClickListener() {//ONCLICK DE FECHA DE NACIMIENTO
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegistryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month=month+1;
                        String date = year + "/" + month + "/" + day;

                        etxtDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private boolean comprobarCamposVacios() {
        if (etxtDate.getText().toString().isEmpty()){
            etxtDate.setError("Este campo es obligatorio");
            return true;
        }
        if (nombre.getText().toString().isEmpty()){
            nombre.setError("Este campo es obligatorio");
            return true;
        }
        if (correo.getText().toString().isEmpty()){
            correo.setError("Este campo es obligatorio");
            return true;
        }
        if (contraseña.getText().toString().isEmpty()){
            contraseña.setError("Este campo es obligatorio");
            return true;
        }
        if (telefono.getText().toString().isEmpty()){
            telefono.setError("Este campo es obligatorio");
            return true;
        }
        if (ciudad.getText().toString().isEmpty()){
            ciudad.setError("Este campo es obligatorio");
            return true;
        }
        return false;
    }

    public void launchLogin(View view) {
        Intent log = new Intent(this, LoginActivity.class);
        startActivity(log);
    }

    public void launchBottom(){
        Intent BottomNavActivity = new Intent(this,BottomNav.class);
        startActivity(BottomNavActivity);
    }


    public void insertarNuevoTurista(View view) {

        if (comprobarCamposVacios()){//si no hay campos vacios
            return;
        }
        final PojoRegistro pojo = new PojoRegistro();
        pojo.setNombre(nombre.getText().toString());
        pojo.setCorreo(correo.getText().toString());
        pojo.setContraseña(contraseña.getText().toString());
        pojo.setTelefono((telefono.getText().toString()));
        pojo.setNacimiento(etxtDate.getText().toString());
        pojo.setCiudad(ciudad.getText().toString());


        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/registroTurista.php?nombre=" + pojo.getNombre() +
                "&correo=" + pojo.getCorreo() + "&contraseña=" + pojo.getContraseña() + "&telefono=" + pojo.getTelefono() +
                "&fecha=" + pojo.getNacimiento() + "&lugar="+ pojo.getCiudad();
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONObject success = response.getJSONObject(0);
                    if (success.getString("success").equals("false")){
                        Toast.makeText(RegistryActivity.this,"Ya se ha registrado " +
                                "ese correo.",Toast.LENGTH_SHORT).show();
                    }else{
                        String ID;
                        ID=success.getString("id");
                        Global.getObject().setId(ID);
                        Global.getObject().setNombre(pojo.getNombre());
                        agregarCupon(ID);

                    }
                }catch (Exception e){
                    Toast.makeText(RegistryActivity.this,"Error al obtene" +
                            "r los datos del Json",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistryActivity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(RegistryActivity.this);
        queue.add(jsonRequest);
    }


    private void actualizarToken(final String id) {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.d("NOTICIAS","Token: "+ instanceIdResult.getToken());
                consultaActualizarToken(id,instanceIdResult.getToken());
            }
        });
    }

    /**
     * actualiza el token de firebase de notificaciones a la db de amazon
     * @param ID
     * @param token
     */
    private void consultaActualizarToken(String ID,String token) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/actualizarToken.php?id="+ID+"&token="+token;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Intent intent = new Intent(RegistryActivity.this, BottomNav.class);
                        finish();
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistryActivity.this, " Error: " + error.getMessage(), LENGTH_SHORT).show();
                    }
                });
        RequestQueue ejecuta = Volley.newRequestQueue(RegistryActivity.this);
        ejecuta.add(jsonArrayRequest);
    }
    private void agregarCupon(final String id) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/agregarCupon.php?id="+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            if (jsonObject.getString("success").equals("true")) {
                                actualizarToken(id);

                            } else {
                                Toast.makeText(RegistryActivity.this,
                                        "No se pudo agregar el cupon correctamente",
                                        LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistryActivity.this, " Error: " + error.getMessage(), LENGTH_SHORT).show();
                    }
                });
        RequestQueue ejecuta = Volley.newRequestQueue(RegistryActivity.this);
        ejecuta.add(jsonArrayRequest);
    }
}
