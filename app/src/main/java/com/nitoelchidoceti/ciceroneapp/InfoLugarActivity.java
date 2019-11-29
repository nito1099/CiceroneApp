package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class InfoLugarActivity extends AppCompatActivity {
    ImageButton addFav;
    Boolean selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lugar);
        selected=false;
        Toolbar toolbar = findViewById(R.id.toolbar_inf_lugar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.qr_code){
            launchQr();
        }
        return true;
    }

    private void launchQr() {//CODIGO QR ACTIVITY
        Intent launchQRActivity = new Intent(InfoLugarActivity.this,QrCodeActivity.class);
        startActivity(launchQRActivity);
    }

    public void addFavPlace(View view) {
        addFav = findViewById(R.id.imgbtnFav);
        if (selected==false){
            addFav.setBackgroundResource(R.drawable.ic_favoritos_selected);

            selected=true;
        }else{
            addFav.setBackgroundResource(R.drawable.ic_favoritos);
            selected=false;
        }
    }
}
