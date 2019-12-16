package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeAyudaAlUsuario;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeBusquedaGuias;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeComentarios;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoComentario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserHelpActivity extends AppCompatActivity {

    private ArrayList<String> problemas,problemasCompletos = new ArrayList<>();
    private AdapterDeAyudaAlUsuario adapterDeAyudaAlUsuario;
    private RecyclerView recyclerView;
    private TextView txtProblema;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {//desactivo item de ayudo en el toolbar
        menu.getItem(0).setEnabled(false);
        menu.getItem(0).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_help);
        Toolbar toolbar = findViewById(R.id.toolbarUserHelp);
        setSupportActionBar(toolbar);
        problemas=new ArrayList<>();
        recyclerView = findViewById(R.id.recycle_help);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        consultarProblemas();
    }

    private void consultarProblemas() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/problemasRecurrentes.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            mostrarProblemas(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserHelpActivity.this,""+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(UserHelpActivity.this);
        queue.add(jsonArrayRequest);
    }

    private void mostrarProblemas(JSONArray response) throws JSONException {
        problemas.clear();
        for (int i = 0; i < response.length(); i++) {
            String problema;
            JSONObject objeto;
            objeto = response.getJSONObject(i);
            problema=objeto.getString("Titulo");
            problemas.add(problema);
            String problema2;
            problema2=objeto.getString("Titulo")+"\n"+objeto.getString("Descripcion");
            problemasCompletos.add(problema2);
            //collapseview
        }

        adapterDeAyudaAlUsuario = new AdapterDeAyudaAlUsuario(problemas,problemasCompletos, new AdapterDeAyudaAlUsuario.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //txtProblema.setText(problemasCompletos.get(position));
            }
        });
        recyclerView.setAdapter(adapterDeAyudaAlUsuario);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_actionbar_panic_btn,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.item_panic_button:
                launchReporProblem();
                break;
        }
        return true;
    }

    private void launchReporProblem() {
        Intent IlaunchReportProblem = new Intent(UserHelpActivity.this,ReportProblemActivity.class);
        startActivity(IlaunchReportProblem);
    }
}
