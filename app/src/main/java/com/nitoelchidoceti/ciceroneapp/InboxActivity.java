package com.nitoelchidoceti.ciceroneapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterInbox;
import com.nitoelchidoceti.ciceroneapp.Global.Global;
import com.nitoelchidoceti.ciceroneapp.POJOS.MensajeRecibir;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoGuia;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMensajes;
    private AdapterInbox adapterInbox;

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private MensajeRecibir mensaje;

    private ArrayList<String> idsGuias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        instancias();
        databaseConfiguration();
        recycleConfiguration();
    }

    private void instancias() {
        Toolbar toolbar = findViewById(R.id.toolbarInbox);
        setSupportActionBar(toolbar);
        recyclerViewMensajes = findViewById(R.id.recycle_inbox);
    }

    private void databaseConfiguration() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {//recorro todas las conversaciones
                    String []idGuia = ds.getKey().split("_");
                    idsGuias.add(idGuia[3]);
                    if (ds.getKey().indexOf("Turista_" + Global.getObject().getId()) != -1) {//filtro las conversaciones del turista
                        Query lastQuery = databaseReference.child(ds.getKey()).orderByChild(ds.getKey()).limitToLast(1);//obtengo el ultimo mensaje enviado de cada conversacion
                        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    mensaje = child.getValue(MensajeRecibir.class);
                                    adapterInbox.addMensaje(mensaje);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(InboxActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRestart() {//cuando presione el boton de regreso hacia esta act
        super.onRestart();
        adapterInbox.clearList();
        databaseConfiguration();
        recycleConfiguration();
    }

    private void recycleConfiguration() {
        adapterInbox = new AdapterInbox(this, new AdapterInbox.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                launchChat(position);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMensajes.setLayoutManager(linearLayoutManager);
        recyclerViewMensajes.setAdapter(adapterInbox);
    }

    private void launchChat(int pos) {
        Intent intent = new Intent(this, ChatActivity.class);
        for (PojoGuia guia : Global.getObject().getGuias()){
            if (idsGuias.get(pos).equals(String.valueOf(guia.getId()))) {
                intent.putExtra("Guia",guia);
                startActivity(intent);
            }else {
                Toast.makeText(this, "No se encontro el pojo", Toast.LENGTH_SHORT).show();
            }
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
        Intent launchQRActivity = new Intent(InboxActivity.this, QrCodeActivity.class);

        startActivity(launchQRActivity);
    }
}
