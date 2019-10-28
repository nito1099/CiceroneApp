package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterRegistro;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoRegistro;
import java.util.Calendar;



public class RegistryActivity extends AppCompatActivity {
    private static final String[] CITIES = new String[]{
      "Guadalajara", "Tlaquepaque", "Zapopan", "Tonalá", "Tlajomulco",
        "El Salto", "CDMX", "Monterrey", "Gomez Palacio", " Guanajuato"
    };
    EditText etxtDate;
    Button registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        registrar = findViewById(R.id.btnRegister);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//ONCLICK DE BOTON REGISTRAR
                AdapterRegistro inf = new AdapterRegistro();
                PojoRegistro pojo = new PojoRegistro();
                pojo=inf.llenarDatosAlPojo();

                if(v.getId()== R.id.btnRegister);{

                    String url = "http://ec2-35-166-69-188.us-west-2.compute.amazonaws.com/Cicerone/PHP/registroTurista.php?nombre="+pojo.getNombre()+
                            "&correo="+pojo.getCorreo()+"&contraseña="+pojo.getContraseña()+"&telefono="+pojo.getTelefono()+
                            "&fecha="+pojo.getNacimiento()+"&ciudad="+pojo.getCiudad();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(RegistryActivity.this,"Datos agregados correctamente"+response.length(),Toast.LENGTH_SHORT).show();
                                    launchBottom();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegistryActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue queue = Volley.newRequestQueue(RegistryActivity.this);
                    queue.add(stringRequest);
                }
            }
        });

        AutoCompleteTextView aTxtViewCiudad = findViewById(R.id.aTxtViewCiudad);
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, CITIES);
        aTxtViewCiudad.setAdapter(adapterCiudad);

        etxtDate = findViewById(R.id.txt_nacimiento);
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
                        String date = day+"/"+ month+"/"+ year;

                        etxtDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();;
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

}
