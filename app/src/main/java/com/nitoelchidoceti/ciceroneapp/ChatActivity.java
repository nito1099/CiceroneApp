package com.nitoelchidoceti.ciceroneapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterMensajes;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.MensajeEnviar;
import com.nitoelchidoceti.ciceroneapp.POJOS.MensajeRecibir;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoMensaje;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText etxtMensaje;
    private RecyclerView recyclerViewMensajes;
    private ImageButton btnEnviarMensaje, btnEnviarImagen;
    private AdapterMensajes adapterMensajes;
    private TextView txtNombreChat;
    private PojoGuia pojoGuia;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private final static String Url = "https://fcm.googleapis.com/fcm/send";



    private static final int PHOTO_SEND = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        verifyStoragePermissions(this);
        instancias();
    }

    private void instancias() {
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        pojoGuia = (PojoGuia) getIntent().getSerializableExtra("Guia");
        etxtMensaje=findViewById(R.id.etxtMensaje);
        recyclerViewMensajes=findViewById(R.id.recycleChat);
        btnEnviarMensaje=findViewById(R.id.btnEnviarMsgChat);
        btnEnviarImagen = findViewById(R.id.imgEnviarImagen);
        txtNombreChat=findViewById(R.id.txtNombreChat);
        txtNombreChat.setText(pojoGuia.getNombre());

        databaseConfiguration();
        recycleConfiguration();
        onClickEnviarMensaje();
        onClickEnviarImagen();
    }


    private void onClickEnviarImagen() {
        btnEnviarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//pedir obtener todos archivos
                intent.setType("image/jpeg");//configurar para que solo sean fotos
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);//lo agregas al intent para enviar
                //evaluar el resultado del intent (si se realizo correctamente)
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PHOTO_SEND);//1 = imagen
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //cuando la imagen se selecciona correctamente y cuando el resultado se reciba bien
        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK) {
            final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);

            progressDialog.setTitle("Subiendo Imagen...");
            progressDialog.setMessage("Por favor espere");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Uri uri = data.getData();//subir la img a la db
            storageReference = storage.getReference("imagenes_chat");
            final StorageReference imagenReferencia = storageReference.child(uri.getLastPathSegment());//key de nuestra foto
            UploadTask uploadTask = imagenReferencia.putFile(uri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imagenReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        PojoMensaje mensaje = new MensajeEnviar(
                                "Imagen",
                                Global.getObject().getNombre(),"2",
                                downloadUri.toString(),
                                "turista"+Global.getObject().getId(),
                                pojoGuia.getNombre(),pojoGuia.getFotografia(),ServerValue.TIMESTAMP);
                        try {
                            mandarNotificacion(mensaje.getMensaje());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        databaseReference.push().setValue(mensaje);
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(ChatActivity.this, "No se ha podido subir la imagen correctamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void onClickEnviarMensaje() {
        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etxtMensaje.getText().length()!=0){
                    databaseReference.push().setValue(new MensajeEnviar(etxtMensaje.getText().toString()
                            , Global.getObject().getNombre(), "1",
                            "turista"+Global.getObject().getId(),pojoGuia.getNombre(),pojoGuia.getFotografia(),ServerValue.TIMESTAMP));
                    try {
                        mandarNotificacion(etxtMensaje.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    etxtMensaje.setText("");
                }
            }
        });
    }

    private void mandarNotificacion(String mensaje) throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject mainObj = new JSONObject();
        mainObj.put("to", pojoGuia.getToken());
        Log.d("NOTICIAS","notificacion token:"+pojoGuia.getToken()+"\n");
        JSONObject notificationObj = new JSONObject();
        notificationObj.put("title", "Nuevo mensaje de " + Global.getObject().getNombre());
        notificationObj.put("body", mensaje);
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
                Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("content-type", "application/json");
                header.put("authorization", "key=AIzaSyBhgDb3RGS8SPavXMQDQ95z59vOTQ0wjrg");
                return header;
            }
        };
        requestQueue.add(request);
        etxtMensaje.setText("");
    }

    private void recycleConfiguration() {
        adapterMensajes=new AdapterMensajes(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMensajes.setLayoutManager(linearLayoutManager);
        recyclerViewMensajes.setAdapter(adapterMensajes);
        adapterMensajes.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {//cuando se inserta un nuevo objeto al recycle
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });
    }

    private void databaseConfiguration() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chatTurista_"+Global.getObject().getId()+"_Guia_"+pojoGuia.getId()+"_");//Sala de chat(nombre)
        storage = FirebaseStorage.getInstance();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MensajeRecibir mensaje = dataSnapshot.getValue(MensajeRecibir.class);
                adapterMensajes.addMensaje(mensaje);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setScrollBar() {//scroll la posicion del recycle view al ultimo item agregado
        recyclerViewMensajes.scrollToPosition(adapterMensajes.getItemCount() - 1);
    }

    public static boolean verifyStoragePermissions(ChatActivity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,

        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
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

    //toolbar
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
        Intent launchQRActivity = new Intent(ChatActivity.this, QrCodeActivity.class);
        startActivity(launchQRActivity);
    }
}
