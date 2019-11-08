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


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import com.nitoelchidoceti.ciceroneapp.Global.Global;

import org.json.JSONArray;
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
    private EditText txtEmail, txtName, txtPass;
    public String ID;
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
        txtName = findViewById(R.id.etxt_contraseña_login);
        txtEmail = findViewById(R.id.etxt_correo_login);
        txtPass = findViewById(R.id.etxt_contraseña_login);

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


                    Toast.makeText(LoginActivity.this,"Bienvenido: "+first_name, LENGTH_SHORT);

                    //launchBottomNavActivity();


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

    private void launchBottomNavActivity(final View vista){

        final String url = "http://192.168.1.72/Cicerone/PHP/login.php?correo="+txtEmail.getText().toString()+
                "&contraseña="+txtPass.getText().toString();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject loginTurista = response.getJSONObject(0);
                            if (loginTurista.getString("success").equals("false")){
                                Toast.makeText(vista.getContext(),"Verifique sus credenciales.",LENGTH_SHORT).show();
                            }else{
                                Intent intent = new Intent(vista.getContext(),BottomNav.class);
                                ID=loginTurista.getString("id");
                                Global.getObject().setId(ID);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(vista.getContext(),"Si estas concectado a la red? Error: "+error.getMessage(),LENGTH_SHORT).show();
                    }
                });
        RequestQueue ejecuta = Volley.newRequestQueue(vista.getContext());
        ejecuta.add(jsonArrayRequest);
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

        //Toast.makeText(this,input, LENGTH_SHORT).show();
        launchBottomNavActivity(view);
    }

    public void launchRegistryActivity(View view) { // ONCLICK DE REGISTRO
        Intent registryAct = new Intent(this,RegistryActivity.class);
        registryAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registryAct);
    }

    public void launch_get_email(View view) {
        Intent getEMailAct = new Intent(this, GetMailForgotPassword.class);
        getEMailAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(getEMailAct);
    }
}
