package com.nitoelchidoceti.ciceroneapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeComentarios;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeReservaciones;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoComentario;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoReservacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ReservacionActivity extends AppCompatActivity {

    EditText etxtDate, etxtHora;
    PojoGuia pojoGuia;
    ArrayList<PojoReservacion> reservaciones,reservacionesFiltradas;
    AdapterDeReservaciones adapterDeReservaciones;
    RecyclerView recycleReservaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion);

        inicializaciones();
        configuracionPickers();
        consultarReservaciones();
    }

    private void consultarReservaciones() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/consultaReservaciones.php?guia=" + pojoGuia.getId();
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
                            e.printStackTrace();
                            Toast.makeText(ReservacionActivity.this, "response: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            PojoReservacion reservacion = new PojoReservacion();
            JSONObject objeto;
            objeto = response.getJSONObject(i);
            reservacion.setFecha(objeto.getString("Fecha"));
            reservacion.setHora(objeto.getString("Hora"));
            reservaciones.add(reservacion);
        }
        adapterDeReservaciones = new AdapterDeReservaciones(reservaciones);
        recycleReservaciones.setAdapter(adapterDeReservaciones);
    }

    private void configuracionPickers() {
        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hora = calendar.get(Calendar.HOUR_OF_DAY);
        final int minuto = calendar.get(Calendar.MINUTE);
        etxtDate.setOnClickListener(new View.OnClickListener() {//ONCLICK DE FECHA DE NACIMIENTO
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ReservacionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = year + "-" + month + "-" + day;

                        etxtDate.setText(date);
                    }
                }, year, month, day);
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

                for (PojoReservacion reservacion: reservaciones){

                    if (reservacion.getFecha().contentEquals(s)){
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
                        String minutoFormateado = (minute < 10) ? ("0" + minute) : String.valueOf(minute);
                        etxtHora.setText(horaFormateada + ":" + minutoFormateado + " ");
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
        recycleReservaciones = findViewById(R.id.recycle_reservaciones_rec);
        recycleReservaciones.setLayoutManager(new LinearLayoutManager(ReservacionActivity.this,
                LinearLayoutManager.VERTICAL, false));
        reservaciones=new ArrayList<>();
    }

    public void reservarTour(View view) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/reservarTour.php?fecha=" +
                etxtDate.getText() + "&hora=" +
                etxtHora.getText() + ":00" +
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
                            if (jsonObject.getString("success").equals(false)) {
                                Toast.makeText(ReservacionActivity.this, "Ya hay una reservacion a esa hora.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReservacionActivity.this, "Se ha reservado correctamente.", Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(ReservacionActivity.this);
        queue.add(jsonArrayRequest);
    }
}
