package com.nitoelchidoceti.ciceroneapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;


import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputLayout;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nitoelchidoceti.ciceroneapp.Fragments.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.widget.Toast.LENGTH_SHORT;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputEmail,textInputPassword;
    private LoginButton loginButtonFb;
    private CallbackManager callbackManager;
    private EditText txtEmail, txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputPassword.setCounterMaxLength(14);
        textInputPassword.setCounterEnabled(true);
        textInputPassword.setNextFocusDownId(R.id.btnLogin);

        loginButtonFb = findViewById(R.id.login_button_fb);
        txtName = findViewById(R.id.contraseña);
        txtEmail = findViewById(R.id.correo);

        callbackManager =  CallbackManager.Factory.create();

        checkLoginStatus();

        loginButtonFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken==null){                  //SI NO FUNCA*******
                txtName.setText("se deslogueo");
                txtEmail.setText("se deslogueo");
                Toast.makeText(LoginActivity.this,"User Loged Out", Toast.LENGTH_SHORT).show();
            }else {
                loadUserProfile(currentAccessToken);

            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) { //OBTENIENDO LOS STRINGS PREVIAMENTE DEFINIDOS
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");


                    txtName.setText(first_name +" "+ last_name);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle paraemters = new Bundle();
        paraemters.putString("fields", "first_name, last_name");//LO QUE SE VA A OBTENER DE FACEBOOK
        request.setParameters(paraemters);
        request.executeAsync();
    }

    private void checkLoginStatus(){
        if (AccessToken.getCurrentAccessToken()!= null){
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    private void launchBottomNavActivity(){
        Intent intent = new Intent(this,BottomNav.class);
        startActivity(intent);
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

    public void confirmInput(View view) {//METODO ONCLICK DEL BOTON INICIAR SESIÓN
        if (!validateEmail()|!validatePassword()){
            return;
        }

        String input = "Email: " + textInputEmail.getEditText().getText().toString();
        input += "\n";
        input += "Password: " + textInputPassword.getEditText().getText().toString();

        Toast.makeText(this,input, LENGTH_SHORT).show();
        launchBottomNavActivity();
    }

    public void launchRegistryActivity(View view) { // ONCLICK DE REGISTRO
        Intent registryAct = new Intent(this,RegistryActivity.class);
        startActivity(registryAct);
    }

    public void launch_get_email(View view) {
        Intent getEMailAct = new Intent(this, GetMailForgotPassword.class);
        startActivity(getEMailAct);
    }
}
