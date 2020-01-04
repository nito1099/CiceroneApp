package com.nitoelchidoceti.ciceroneapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        verifyCameraPermissions(this);
        textView = findViewById(R.id.txtEscanea2);
        Toolbar toolbar = findViewById(R.id.toolbarQR);
        setSupportActionBar(toolbar);
        getQrCodes();
        //Request Permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setUpCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QrCodeActivity.this, "Debes Habilitar los permisos de camara", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
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
                            try {
                                for (int i = 0; i <response.length(); i++){
                                    JSONObject qr = response.getJSONObject(i);
                                    qrCode qrCode = new qrCode(qr.getString("Nombre"),qr.getString("ID"));
                                    qrCodes.add(qrCode);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonArrayRequest);
    }

    private void setUpCamera() {
        surfaceView = findViewById(R.id.cameraPreview);
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
                        if (data.equals("mirasifuncionaestamadre")) {

                        }
                        for (qrCode dato : qrCodes){
                            if (dato.getNombre().equals(data)){
                                qrEader.stop();
                                launchReproduccionTour(dato.getID());
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
                        Toast.makeText(QrCodeActivity.this, "Debes Habilitar los permisos de camara", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader != null) {
                            qrEader.releaseAndCleanup();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QrCodeActivity.this, "Debes Habilitar los permisos de camara", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

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

        public qrCode() {
        }

        public qrCode(String nombre, String ID) {
            Nombre = nombre;
            this.ID = ID;
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
    }
}
