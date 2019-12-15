package com.nitoelchidoceti.ciceroneapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterDeComentarios;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoComentario;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoGuiaActivity extends AppCompatActivity {


    PojoGuia pojoGuia;
    ArrayList<PojoComentario> comentarios;
    TextView nombreDelGuia, descripcionDeGuiaCompleto, idiomasGuia, infAcademicaGuia,
            duracionTourGuia, horarioGuia, costosGuia, calificacionGuia;
    AdapterDeComentarios adapterDeComentarioGuia;
    RecyclerView recycleComentarioGuia;
    TextInputLayout escribirComentarioGuia;
    ImageButton oneStar, twoStar, threeStar, fourStar, fiveStar;
    EditText comentario;
    String calificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_guia);
        inicializacion();
        consultaComentarios();//VERSION BETA*****
        llenarInformacion();
        calcularCalificacion();
    }

    /**
     * inicializa todas la variables de la actividad
     * y los configura
     */
    private void inicializacion() {
        Toolbar toolbar = findViewById(R.id.toolbar_inf_guia);
        escribirComentarioGuia = findViewById(R.id.inTxtEscComentarioGuia);
        escribirComentarioGuia.setCounterMaxLength(255);
        escribirComentarioGuia.setNextFocusDownId(R.id.btnPublicarComentarioGuia);
        oneStar = findViewById(R.id.UnaEstrellaGuia);
        twoStar = findViewById(R.id.DosEstrellasGuia);
        threeStar = findViewById(R.id.TresEstrellasGuia);
        fourStar = findViewById(R.id.CuatroEstrellasGuia);
        fiveStar = findViewById(R.id.CincoEstrellasGuia);
        comentario = findViewById(R.id.txtComentarioNuevoGuia);
        calificacionGuia = findViewById(R.id.txtCalificacionNumeroGuia);
        comentarios = new ArrayList<>();
        recycleComentarioGuia = findViewById(R.id.recycleComentariosGuia);
        recycleComentarioGuia.setLayoutManager(new LinearLayoutManager(InfoGuiaActivity.this,
                LinearLayoutManager.VERTICAL, false));
        nombreDelGuia = findViewById(R.id.txtNombreGuia);
        descripcionDeGuiaCompleto = findViewById(R.id.txtDescripcionGuia);
        infAcademicaGuia = findViewById(R.id.txtInfAcademicaGuia);
        duracionTourGuia = findViewById(R.id.txtDuracionTourGuia);
        horarioGuia = findViewById(R.id.txtHorarioGuia);
        costosGuia = findViewById(R.id.txtCostosTourGuia);
        idiomasGuia=findViewById(R.id.txtIdiomasGuia);
        Intent intent = getIntent();
        pojoGuia = (PojoGuia) intent.getSerializableExtra("Guia");
        setSupportActionBar(toolbar);
    }

    /**
     * calcula el promedio del Guia
     */
    private void calcularCalificacion() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/promedioCalificacion.php?lugar=" + pojoGuia.getId()+
                "&esSitio=false";
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
                            calificacionGuia.setText(jsonObject.getString("Calificacion"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(InfoGuiaActivity.this);
        queue.add(jsonArrayRequest);
    }

    /**
     * obtiene los comentarios de la DB
     */
    private void consultaComentarios() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/comentariosLugar.php?id_place=" + pojoGuia.getId()+
                "&esSitio=false";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            mostrarComentarios(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InfoGuiaActivity.this,""+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(InfoGuiaActivity.this);
        queue.add(jsonArrayRequest);
    }


    /**
     * Obtiene los datos y los agrega al arreglo de comentarios
     * para despues mandarles el array
     *
     * @param response
     * @throws JSONException
     */
    private void mostrarComentarios(JSONArray response) throws JSONException {
        comentarios.clear();
        for (int i = 0; i < response.length(); i++) {
            PojoComentario comentario = new PojoComentario();
            JSONObject objeto;
            objeto = response.getJSONObject(i);
            comentario.setComentario(objeto.getString("Descripcion"));
            comentario.setFecha(objeto.getString("Fecha"));
            comentario.setUserName(objeto.getString("Nombre"));
            comentario.setFK_lugar(objeto.getString("FK_Sitios"));
            comentarios.add(comentario);
        }
        adapterDeComentarioGuia = new AdapterDeComentarios(comentarios);
        recycleComentarioGuia.setAdapter(adapterDeComentarioGuia);
    }


    /**
     * Llena la inf de los lugares
     */
    private void llenarInformacion() {
        nombreDelGuia.setText(pojoGuia.getNombre());
        //descripcionDeGuiaCompleto.setText(pojoGuia.getDescripcion());
        duracionTourGuia.setText(pojoGuia.getDuracion());
        String aux = new String();

        for (int i=0;i<pojoGuia.getTitulos().size();i++){
            aux+="* "+pojoGuia.getTitulos().get(i)+"\n";
        }
        infAcademicaGuia.setText(aux);

        String aux2 = new String();

        for (int i=0;i<pojoGuia.getIdiomas().size();i++){
            aux2+="* "+pojoGuia.getIdiomas().get(i)+"\n";
        }
        idiomasGuia.setText(aux2);

        horarioGuia.setText(pojoGuia.getHorario());
        duracionTourGuia.setText("Aproximadamente "+pojoGuia.getDuracion()+ " Hrs.");
        Double[] array;
        array = pojoGuia.getCostos();
        costosGuia.setText("Niños: " + array[0] + " MXN" + "\n" +
                "Estudiantes o 3ra Edad: " + array[1] + " MXN" + "\n" +
                "Adultos: " + array[2] + " MXN");
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

    private void launchQr() {//CODIGO QR ACTIVITY
        Intent launchQRActivity = new Intent(InfoGuiaActivity.this, QrCodeActivity.class);
        startActivity(launchQRActivity);
    }


    /**
     * Cuando se presiona el boton de favoritos
     * Agrega el sitio o si no lo tiene lo elimina
     *
     * @param view vista de xml
     */


    public void selectedStar(View view) {
        stars(1);

    }

    public void selected2Stars(View view) {
        stars(2);

    }

    public void selectedThreeStars(View view) {
        stars(3);

    }

    public void selectedFourStars(View view) {
        stars(4);

    }

    public void selected5FiveStars(View view) {

        stars(5);
    }

    /**
     * Dependiendo del numero de estrella de calificacion presionado cambia la vista
     * de la calificacion del lugar y registra que ya se ha asignado una calificacion
     *
     * @param numero numero de estrella clickeada
     */
    private void stars(int numero) {
        switch (numero) {
            case 1:
                oneStar.setBackgroundResource(R.drawable.ic_one_star);
                twoStar.setBackgroundResource(R.drawable.ic__empty_star);
                threeStar.setBackgroundResource(R.drawable.ic__empty_star);
                fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                calificacion = "1";
                break;
            case 2:
                oneStar.setBackgroundResource(R.drawable.ic_one_star);
                twoStar.setBackgroundResource(R.drawable.ic_one_star);
                threeStar.setBackgroundResource(R.drawable.ic__empty_star);
                fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                calificacion = "2";

                break;
            case 3:
                oneStar.setBackgroundResource(R.drawable.ic_one_star);
                twoStar.setBackgroundResource(R.drawable.ic_one_star);
                threeStar.setBackgroundResource(R.drawable.ic_one_star);
                fourStar.setBackgroundResource(R.drawable.ic__empty_star);
                fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                calificacion = "3";
                break;
            case 4:
                oneStar.setBackgroundResource(R.drawable.ic_one_star);
                twoStar.setBackgroundResource(R.drawable.ic_one_star);
                threeStar.setBackgroundResource(R.drawable.ic_one_star);
                fourStar.setBackgroundResource(R.drawable.ic_one_star);
                fiveStar.setBackgroundResource(R.drawable.ic__empty_star);

                calificacion = "4";
                break;
            case 5:
                oneStar.setBackgroundResource(R.drawable.ic_one_star);
                twoStar.setBackgroundResource(R.drawable.ic_one_star);
                threeStar.setBackgroundResource(R.drawable.ic_one_star);
                fourStar.setBackgroundResource(R.drawable.ic_one_star);
                fiveStar.setBackgroundResource(R.drawable.ic_one_star);

                calificacion = "5";
                break;
        }
    }

    //********************************** AUN NO JALA LA DE ABAJO***********************************************
    /**
     * Se agrega el review a la DB
     *
     * @param view
     */
    public void agregarReviewGuia(View view) {
        if (calificacion != null || comentario.getText() == null) {
            String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/" +
                    "agregarReviewSitio.php?nombre=" + Global.getObject().getId() + "&sitio=" + pojoGuia.getId() +
                    "&calificacion=" + calificacion + "&comentario=" + comentario.getText()+"&esSitio=false";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = response.getJSONObject(0);
                                if (jsonObject.getString("success").equals("true")) {
                                    Toast.makeText(InfoGuiaActivity.this, "Se ha " +
                                            "publicado su comentario correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(InfoGuiaActivity.this, "Ya ha agregado " +
                                            "un comentario previamente.", Toast.LENGTH_SHORT).show();
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
            RequestQueue requestQueue = Volley.newRequestQueue(InfoGuiaActivity.this);
            requestQueue.add(jsonArrayRequest);
        } else {
            Toast.makeText(InfoGuiaActivity.this, "Porfavor no deje campos vacios.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
