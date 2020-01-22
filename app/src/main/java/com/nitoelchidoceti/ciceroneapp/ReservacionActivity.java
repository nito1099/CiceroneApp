package com.nitoelchidoceti.ciceroneapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeComentarios;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeReservaciones;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoComentario;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoReservacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservacionActivity extends AppCompatActivity {

    EditText etxtDate, etxtHora;
    PojoGuia pojoGuia;
    ArrayList<String> reservaciones,reservacionesFiltradas;
    AdapterDeReservaciones adapterDeReservaciones;
    RecyclerView recycleReservaciones;
    private final static String Url = "https://fcm.googleapis.com/fcm/send";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion);

        inicializaciones();
        configuracionPickers();
        consultarReservaciones();
    }

    private void consultarReservaciones() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new Date());
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/consultaReservaciones.php?guia=" + pojoGuia.getId()+
                "&fecha="+dateString;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            agregarReservaciones(response);

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReservacionActivity.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(ReservacionActivity.this);
        queue.add(jsonArrayRequest);
    }

    private void agregarReservaciones(JSONArray response) throws JSONException {
        if (reservaciones.size()!=0){
            reservaciones.clear();
        }
        for (int i = 0; i < response.length(); i++) {
            String reservacion;
            JSONObject objeto;
            objeto = response.getJSONObject(i);
            reservacion=objeto.getString("Fecha");
            reservaciones.add(reservacion);
        }
        adapterDeReservaciones = new AdapterDeReservaciones(reservaciones);
        recycleReservaciones.setAdapter(adapterDeReservaciones);
    }

    private void configuracionPickers() {
        final Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hora = calendar.get(Calendar.HOUR_OF_DAY);
        final int minuto = calendar.get(Calendar.MINUTE);
        etxtDate.setOnClickListener(new View.OnClickListener() {//ONCLICK DE FECHA DE NACIMIENTO
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ReservacionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String mes  = (month < 10) ? ("0" + month) : String.valueOf(month);
                        String dia  = (day< 10) ? ("0" + day) : String.valueOf(day);
                        String date = year + "-" + mes + "-" + dia;
                        etxtDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        etxtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (reservacionesFiltradas.size()!=0){
                    reservacionesFiltradas.clear();
                }

                for (String reservacion: reservaciones){

                    if (reservacion.contains(s)){
                        reservacionesFiltradas.add(reservacion);
                    }
                }
                adapterDeReservaciones = new AdapterDeReservaciones(reservacionesFiltradas);
                recycleReservaciones.setAdapter(adapterDeReservaciones);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etxtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        ReservacionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String horaFormateada = (hourOfDay < 10) ? ("0" + hourOfDay) : String.valueOf(hourOfDay);
                        //String minutoFormateado = (minute < 10) ? ("0" + minute) : String.valueOf(minute);
                        etxtHora.setText(horaFormateada + ":" + "00");
                    }
                }, hora, minuto, true);
                timePickerDialog.show();
            }
        });
    }

    private void inicializaciones() {
        Intent intent = getIntent();
        pojoGuia = (PojoGuia) intent.getSerializableExtra("Guia");
        etxtDate = findViewById(R.id.etxt_fecha_reservacion);
        etxtHora = findViewById(R.id.etxt_hora_reservacion);
        reservacionesFiltradas=new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbarReservacion);
        setSupportActionBar(toolbar);
        recycleReservaciones = findViewById(R.id.recycle_reservaciones_rec);
        recycleReservaciones.setLayoutManager(new LinearLayoutManager(ReservacionActivity.this,
                LinearLayoutManager.VERTICAL, false));
        reservaciones=new ArrayList<>();
    }

    public void reservarTour(View view) {
        if (etxtHora.getText().toString().matches("")||etxtDate.getText().toString().matches("")){
            Toast.makeText(this, "Por favor no deje campos vacios", Toast.LENGTH_SHORT).show();
        }else {
            final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/reservarTour.php?fecha=" +
                    etxtDate.getText() + " "+
                    etxtHora.getText()+
                    "&turista=" + Global.getObject().getId() +
                    "&guia=" + pojoGuia.getId();
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
                                if (jsonObject.getString("success").equals("false")) {
                                    Toast.makeText(ReservacionActivity.this, "Ya hay una reservacion a esa hora.", Toast.LENGTH_SHORT).show();
                                } else {
                                    mandarNotificacion();
                                    Toast.makeText(ReservacionActivity.this, "Se ha reservado correctamente.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ReservacionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue queue = Volley.newRequestQueue(ReservacionActivity.this);
            queue.add(jsonArrayRequest);
        }

    }

    private void mandarNotificacion() throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject mainObj = new JSONObject();
        mainObj.put("to" , pojoGuia.getToken());
        JSONObject notificationObj = new JSONObject();
        notificationObj.put( "title", "Tienes una nueva reservación!");
        notificationObj.put("body", "El " + etxtDate.getText() + " a las: " + etxtHora.getText()+ " hrs" );
        mainObj.put("notification", notificationObj);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url,
                mainObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReservacionActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> header = new HashMap<>();
                header.put("content-type", "application/json");
                header.put("authorization","key=AIzaSyBhgDb3RGS8SPavXMQDQ95z59vOTQ0wjrg");
                return  header;
            }
        };
        requestQueue.add(request);
    }

    //toolbar

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
        Intent launchQRActivity = new Intent(ReservacionActivity.this,QrCodeActivity.class);
        startActivity(launchQRActivity);
    }
}
