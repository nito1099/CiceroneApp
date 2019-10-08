package com.nitoelchidoceti.ciceroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputEmail,textInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputPassword.setCounterMaxLength(13);
        textInputPassword.setCounterEnabled(true);
        textInputPassword.setNextFocusDownId(R.id.btnLogin);

    }

    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()){
            textInputEmail.setError("El correo no puede estar vacío");
            return false;
        }else{
            textInputEmail.setError(null);
            textInputEmail.setErrorEnabled(false);//DESABILITA LOS POSIBLES ERRORES
            return true;
        }

    }

    private boolean validatePassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()){
            textInputPassword.setError("La contraseña no puede estar vacía");
            return false;
        }else if (passwordInput.length()>13){
            textInputPassword.setError("Contraseña demasiado Larga");
            return false;
        }else{
            textInputPassword.setError(null);
            textInputPassword.setErrorEnabled(false);//DESABILITA LOS POSIBLES ERRORES
            return true;
        }

    }

    public void confirmInput(View view) {
        if (!validateEmail()|!validatePassword()){
            return;
        }

        String input = "Email: " + textInputEmail.getEditText().getText().toString();
        input += "\n";
        input += "Password: " + textInputPassword.getEditText().getText().toString();

        Toast.makeText(this,input,Toast.LENGTH_SHORT).show();
    }
}
