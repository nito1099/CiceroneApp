package com.nitoelchidoceti.ciceroneapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoRegistro;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;


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

    public void launchLogin(View view) {
        Intent log = new Intent(this, LoginActivity.class);
        startActivity(log);
    }

    public void launchBottom(){
        Intent BottomNavActivity = new Intent(this,BottomNav.class);
        startActivity(BottomNavActivity);
    }


    public void insertarNuevoTurista(View view) {

        PojoRegistro pojo = new PojoRegistro();
        pojo.setNombre(nombre.getText().toString());
        pojo.setCorreo(correo.getText().toString());
        pojo.setContraseña(contraseña.getText().toString());
        pojo.setTelefono((telefono.getText().toString()));
        pojo.setNacimiento(etxtDate.getText().toString());
        pojo.setCiudad(ciudad.getText().toString());


        final String url = "http://192.168.1.72/Cicerone/PHP/registroTurista.php?nombre=" + pojo.getNombre() +
                "&correo=" + pojo.getCorreo() + "&contraseña=" + pojo.getContraseña() + "&telefono=" + pojo.getTelefono() +
                "&fecha=" + pojo.getNacimiento() + "&lugar=2";
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
                        Intent intent = new Intent(RegistryActivity.this,BottomNav.class);
                        ID=success.getString("id");
                        Global.getObject().setId(ID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
}
