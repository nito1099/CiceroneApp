package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nitoelchidoceti.ciceroneapp.JavaMail.JavaMailAPI;

public class GetMailForgotPassword extends AppCompatActivity {


    private Button sendMail;
    private TextInputLayout textInputLayoutEmail;
    private TextInputEditText etxtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_mail_forgot_password);
        sendMail = findViewById(R.id.btnRegister);
        etxtEmail = findViewById(R.id.etxtCorreo_RecPass);
        textInputLayoutEmail = findViewById(R.id.txt_Correo);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        String emailInput = textInputLayoutEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()){
            textInputLayoutEmail.setError("Este campo no puede esta vacío");
        }else {
            JavaMailAPI javaMailAPI = new JavaMailAPI(GetMailForgotPassword.this ,
                    etxtEmail.getText().toString(),
                    "Código de verificación Cicerone",
                    "Tu código de verificación para recuperar tu contraseña es: FGJ36DS7");
            javaMailAPI.execute();
        }
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
