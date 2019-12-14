package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ReproduccionTourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproduccion_tour);
        Toast.makeText(this, "Dato:"+ getIntent().getSerializableExtra("tour"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        launchHome();
    }

    private void launchHome() {
        Intent intent = new Intent(ReproduccionTourActivity.this,BottomNav.class);
        startActivity(intent);
    }
}
