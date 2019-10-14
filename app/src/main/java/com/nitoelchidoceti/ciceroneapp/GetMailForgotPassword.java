package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GetMailForgotPassword extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_mail_forgot_password);
    }

    public void launchLogin(View view) {
        Intent launchLog = new Intent(this,LoginActivity.class);
        launchLog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchLog);
    }

    public void launchRecoverPassword(View view) {
        Intent launchRecPass = new Intent(this, RecoverPasswordActivity.class);
        startActivity(launchRecPass);
    }
}
