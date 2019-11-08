package com.nitoelchidoceti.ciceroneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nitoelchidoceti.ciceroneapp.Fragments.AccountFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.GuidesFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.HomeFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.MapFragment;
import com.nitoelchidoceti.ciceroneapp.Fragments.SearchFragment;
import com.nitoelchidoceti.ciceroneapp.Global.Global;

public class BottomNav extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        BottomNavigationView bottomNav = findViewById(R.id.menu_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toast.makeText(this,""+ Global.getObject().getId(),Toast.LENGTH_LONG).show();
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
        Toast.makeText(getApplicationContext(),"Se presionó QR",Toast.LENGTH_SHORT).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_guides:
                            selectedFragment = new GuidesFragment();
                            break;
                        case R.id.nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

                    return  true;
                }
            };

    public void launchLoginFromAccount(View view) {
        Intent launchLoginFromAccount = new Intent(this,LoginActivity.class);
        launchLoginFromAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchLoginFromAccount);
    }

    public void launchHomeFragment(View view) {

        BottomNavigationView bottomNav = findViewById(R.id.menu_bottom_navigation);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    public void launchInboxActivity(View view) {
    }

    public void launchEditActivity() {
        //Intent launchEditAccount = new Intent(this,EditAccountActivity.class);
        //startActivity(launchEditAccount);
    }
}
