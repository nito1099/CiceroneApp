package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Global.Global;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportProblemActivity extends AppCompatActivity {

    Spinner spnProblemas;
    EditText problema;
    int Position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        Toolbar toolbar = findViewById(R.id.toolbarSendReport);
        setSupportActionBar(toolbar);
        problema = findViewById(R.id.etxtProblema);
        spnProblemas = findViewById(R.id.spnProblemas);

        final ArrayAdapter<CharSequence> spinnerProblemasAdapter = ArrayAdapter.createFromResource(
                ReportProblemActivity.this,R.array.problemas,android.R.layout.simple_spinner_item);
        spinnerProblemasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProblemas.setSelection(0);
        spnProblemas.setAdapter(spinnerProblemasAdapter);

        spnProblemas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_actionbar_send_report,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.item_send_report){
            sendReportAndLaunchHomeFragment();
        }
        return true;
    }


    public void launcHomeFragmenr(View view) {
        sendReportAndLaunchHomeFragment();
    }

    public void sendReportAndLaunchHomeFragment(){
        if(Position!=0||problema.getText().equals("")||problema.getText().equals("Cuéntanos:")){
            String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone" +
                    "/PHP/enviarReporte.php?tipo="+Position+"&descripcion="+problema.getText()+"&id="+Global.getObject().getId();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("success").equals("true")){
                                    Toast.makeText(ReportProblemActivity.this,"Reporte enviado correctamente, gracias! :).",Toast.LENGTH_SHORT).show();
                                    Intent launchBottomFromReport = new Intent(ReportProblemActivity.this,BottomNav.class);
                                    launchBottomFromReport.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(launchBottomFromReport);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ReportProblemActivity.this,"Volley error: "+ error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
            RequestQueue ejecutaRquest = Volley.newRequestQueue(ReportProblemActivity.this);
            ejecutaRquest.add(jsonObjectRequest);
        }else{
            Toast.makeText(ReportProblemActivity.this,"Porfavor no dejes campos vacíos.",Toast.LENGTH_SHORT).show();
        }

    }
}
