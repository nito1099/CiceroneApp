package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nitoelchidoceti.ciceroneapp.Adapters.AdapterMensajes;
import com.nitoelchidoceti.ciceroneapp.POJOS.PojoMensaje;

public class ChatActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText etxtMensaje;
    private RecyclerView recyclerViewMensajes;
    private ImageButton btnEnviarMensaje;
    private AdapterMensajes adapterMensajes;
    private TextView txtNombreChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instancias();
    }

    private void instancias() {
        etxtMensaje=findViewById(R.id.etxtMensaje);
        recyclerViewMensajes=findViewById(R.id.recycleChat);
        btnEnviarMensaje=findViewById(R.id.btnEnviarMsgChat);
        txtNombreChat=findViewById(R.id.txtNombreChat);
        adapterMensajes=new AdapterMensajes(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMensajes.setLayoutManager(linearLayoutManager);
        recyclerViewMensajes.setAdapter(adapterMensajes);
        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterMensajes.addMensaje(new PojoMensaje(etxtMensaje.getText().toString(),
                        "00:00",txtNombreChat.getText().toString(),"1"));
            }
        });
    }
}
