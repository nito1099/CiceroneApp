package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class UserHelpActivity extends AppCompatActivity {

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
