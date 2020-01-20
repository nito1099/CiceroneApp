package com.nitoelchidoceti.ciceroneapp.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.LoginActivity;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoCuenta;
import com.nitoelchidoceti.ciceroneapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {



    private EditText txtNombre,txtEmail,txtPlace,txtCell,txtBirthday;
    Context contexto;
    private FloatingActionButton btnEditarInfCuenta;
    private CircleImageView imageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_account,container,false);

        txtNombre=myView.findViewById(R.id.txt_nombre_account);
        txtEmail=myView.findViewById(R.id.txt_email_account);
        txtPlace=myView.findViewById(R.id.txt_lugar_account);
        txtCell=myView.findViewById(R.id.txt_telefono_account);
        txtBirthday=myView.findViewById(R.id.txt_birthday_account);
        btnEditarInfCuenta = myView.findViewById(R.id.btn_edit_account);
        imageView= myView.findViewById(R.id.img_circle_view);
        contexto=myView.getContext();
        if (Global.getObject().getImagen() != null){
            Glide.with(contexto).load(Global.getObject().getImagen()).into(imageView);
        }

        infDeTurista(myView,contexto);

        Calendar calendario = Calendar.getInstance();

        final int year = calendario.get(Calendar.YEAR);
        final int month = calendario.get(Calendar.MONTH);
        final int day = calendario.get(Calendar.DAY_OF_MONTH);

        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        String date = year + "/"+month+"/"+ dayOfMonth ;
                        txtBirthday.setText(date);
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });

        return myView;
    }

    public void infDeTurista(View view, final Context contexto2){

        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/infCuentaTurista.php?id="+ Global.getObject().getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject turista = response.getJSONObject(0);
                            PojoCuenta pojo = new PojoCuenta();
                            pojo.setNombre(turista.getString("Nombre"));
                            pojo.setTelefono(turista.getString("Telefono"));
                            pojo.setCorreo(turista.getString("Correo"));
                            pojo.setCiudad(turista.getString("Ciudad"));
                            pojo.setNacimiento(turista.getString("Nacimiento"));
                            mostrarDatosTurista(pojo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(contexto2,""+error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue ejecuta = Volley.newRequestQueue(view.getContext());
        ejecuta.add(jsonArrayRequest);
    }


    private void mostrarDatosTurista(PojoCuenta inf) {
        txtNombre.setText(inf.getNombre());
        txtCell.setText(inf.getTelefono());
        txtEmail.setText(inf.getCorreo());
        txtPlace.setText(inf.getCiudad());
        txtBirthday.setText(inf.getNacimiento());
    }
}
