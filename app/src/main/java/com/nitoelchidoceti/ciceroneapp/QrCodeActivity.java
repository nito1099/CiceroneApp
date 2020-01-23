package com.nitoelchidoceti.ciceroneapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nitoelchidoceti.ciceroneapp.Global.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class QrCodeActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private TextView textView;
    private QREader qrEader;
    private ArrayList<qrCode> qrCodes = new ArrayList<>();
    ProgressDialog progressDialog ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        verifyCameraPermissions(this);
        textView = findViewById(R.id.txtEscanea2);
        Toolbar toolbar = findViewById(R.id.toolbarQR);
        progressDialog = new ProgressDialog(QrCodeActivity.this);
        surfaceView = findViewById(R.id.cameraPreview);
        setSupportActionBar(toolbar);
        getQrCodes();
        peticionDePermisos();

    }

    private void peticionDePermisos() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setUpCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void getQrCodesDeActivacion() {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/qrActivationCodes.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i <response.length(); i++){
                                JSONObject qr = response.getJSONObject(i);
                                qrCode qrCode = new qrCode(qr.getString("Nombre"),
                                        qr.getString("ID"),qr.getString("FK_Sitio"),
                                        true);
                                qrCodes.add(qrCode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QrCodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void canjearCupon(final qrCode qrCode) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/canjearCupon.php?id="+Global.getObject().getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                                JSONObject jsonObject = response.getJSONObject(0);
                                if (jsonObject.getString("success").equals("true")){//si cuenta con cupon
                                    launchReproduccionTour(qrCode.getID());
                                }else {
                                    comprobarTourPagado(qrCode,true);
                                    qrEader.start();
                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QrCodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void getQrCodes() {
            final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/qrCodes.php";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            getQrCodesDeActivacion();
                            try {
                                for (int i = 0; i <response.length(); i++){
                                    JSONObject qr = response.getJSONObject(i);
                                    qrCode qrCode = new qrCode(qr.getString("Nombre"),
                                            qr.getString("ID"),qr.getString("FK_Sitio"), false);
                                    qrCodes.add(qrCode);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(QrCodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonArrayRequest);
    }

    private void setUpCamera() {
        setUpQR();
        qrEader.start();
    }

    private void setUpQR() {
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        for (qrCode dato : qrCodes){
                            if (dato.getNombre().equals(data)){//qr de acceso al tour
                                    if (!dato.deActivacion){
                                        qrEader.stop();
                                        if (dato.getID().equals("1")){//si el qr es del sitio del cupon
                                            canjearCupon(dato);
                                        }else {
                                            comprobarTourPagado(dato,false);//comprueba si ya pago el tour que quiere activar
                                            qrEader.start();
                                        }
                                    }else {//QR de activacion
                                        qrEader.stop();
                                        progressDialog.setTitle("Actualizando Pago");
                                        progressDialog.setMessage("Por favor espere...");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        comprobarTourPagado(dato,false);//comprueba si ya pago el tour que quiere activar
                                        qrEader.start();
                                    }
                            }
                        }
                    }
                });

            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(surfaceView.getHeight())
                .width(surfaceView.getWidth())
                .build();
    }

    private void comprobarTourPagado(final qrCode code, final Boolean palTour)
    {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/comprobarTourPagado.php?sitio="+code.FK_Sitio+"&turista="+Global.getObject().getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            if (palTour){//para acceder al tour

                                if (jsonObject.getString("success").equals("false")){
                                    launchReproduccionTour(code.getID());
                                }else {
                                    Toast.makeText(QrCodeActivity.this, "Lo invitamos a primero pagar nuestro tour :)", Toast.LENGTH_SHORT).show();
                                }
                            }else {//para pagar
                                if (jsonObject.getString("success").equals("false")){
                                    Toast.makeText(QrCodeActivity.this, "Usted ya ha pagado por el Tour, gracias!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }else {
                                    actualizarQuePago(code);//si no ha pagado actualiza el pago
                                }
                            }

                        } catch (JSONException e) {
                            Toast.makeText(QrCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(QrCodeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void actualizarQuePago(qrCode dato) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/" +
                "Cicerone/PHP/tourPagado.php?sitio="+dato.FK_Sitio+"&turista="+ Global.getObject().getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            if (jsonObject.getString("success").equals("true")){
                                Toast.makeText(QrCodeActivity.this, "Se ha realizado el pago correctamente", Toast.LENGTH_SHORT).show();
                                reiniciarActivity();
                            }else {
                                Toast.makeText(QrCodeActivity.this, "No se pudo realizar el pago :(", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(QrCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(QrCodeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void reiniciarActivity() {
        progressDialog.dismiss();
        finish();
        startActivity(new Intent(this,QrCodeActivity.class));
    }

    private void launchReproduccionTour(String dato) {
        Intent intent = new Intent(QrCodeActivity.this,ReproduccionTourActivity.class);
        intent.putExtra("tour",dato);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader != null) {
                            qrEader.initAndStart(surfaceView);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        finish();
                        Toast.makeText(QrCodeActivity.this, "Debes Habilitar los permisos de camara", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    //TOOLBAR
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
            case R.id.item_help:
                launchUserHelpActivity();
                break;
        }
        return true;
    }

    private void launchUserHelpActivity() {
        Intent intent = new Intent(QrCodeActivity.this, UserHelpActivity.class);
        startActivity(intent);
    }
    private void launchReporProblem() {
        Intent IlaunchReportProblem = new Intent(QrCodeActivity.this,ReportProblemActivity.class);
        startActivity(IlaunchReportProblem);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {

                    finish();
                    Toast.makeText(QrCodeActivity.this,
                            "Es necesario activar el permiso de camara", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public static boolean verifyCameraPermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.CAMERA
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else{
            return true;
        }
    }

    class qrCode implements Serializable {
        private String Nombre;
        private String ID;
        private String FK_Sitio;
        private Boolean deActivacion;

        public qrCode() {
        }

        public qrCode(String nombre, String ID, String FK_Sitio, Boolean deActivacion) {
            Nombre = nombre;
            this.FK_Sitio = FK_Sitio;
            this.ID = ID;
            this.deActivacion = deActivacion;
        }


        public String getNombre() {
            return Nombre;
        }

        public void setNombre(String nombre) {
            Nombre = nombre;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getFK_Sitio() {
            return FK_Sitio;
        }

        public void setFK_Sitio(String FK_Sitio) {
            this.FK_Sitio = FK_Sitio;
        }
    }
}
