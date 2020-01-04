package com.nitoelchidoceti.ciceroneapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReproduccionTourActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private TextView nombreAudio, duracionMaxima, duracionActual, tituloLugar;
    private ImageButton skipPrevious, playPause, skipNext, tourEnTexto;
    private ImageView imgAudio;
    private androidx.appcompat.widget.AppCompatSeekBar seekBar;
    private int startTime = 0;
    private int endTime = 0;
    private Handler myHandler = new Handler();

    int index = 0;//indica que numero de cancion se esta reproducciendo
    private MediaPlayer mediaPlayer;
    private ArrayList <audio>  audios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproduccion_tour);
        final String id = (String) getIntent().getSerializableExtra("tour");
        consultaTour(id);
        configuracionActivity();
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });

        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    playPause.setBackgroundResource(R.drawable.ic_play_audio);
                    mediaPlayer.pause();
                }
                index++;
                if ( index>audios.size()-1)index=0;
                configuraNuevaCancion();
                reproducirAudio();
            }
        });

        skipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playPause.setBackgroundResource(R.drawable.ic_play_audio);
                    mediaPlayer.pause();
                }
                index--;
                if (index<0)index=audios.size()-1;
                configuraNuevaCancion();
                reproducirAudio();
            }
        });

        tourEnTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });

        /*
        * if (mediaPlayer.isPlaying()){
                    SeekBar seekBar2 = (SeekBar) v;
                    int playPosition = (startTime/100)*seekBar2.getProgress();
                    mediaPlayer.seekTo(playPosition);
        * */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                startTime = progress;
                duracionActual.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime))));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                reproducirAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                reproducirAudio();
            }
        });
    }

    private void configuraNuevaCancion() {

        final ProgressDialog progressDialog = new ProgressDialog(ReproduccionTourActivity.this);
        AsyncTask<String, String, String> mp3Play = new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {

                progressDialog.setTitle("Cargando audio...");
                progressDialog.setMessage("Por favor espere");
                progressDialog.setCancelable(false);
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    mediaPlayer.setDataSource(strings[0]);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String s) {
                Glide.with(ReproduccionTourActivity.this).load(audios.get(index).getFotografia()).into(imgAudio);
                nombreAudio.setText(audios.get(index).getNombre());
                endTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();
                duracionMaxima.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
                );
                duracionActual.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
                );
                seekBar.setMax((int) endTime);
                seekBar.setProgress((int) startTime);
                progressDialog.dismiss();
            }
        };

        Boolean estabaReproduciendo = false;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mp3Play.cancel(true);
            estabaReproduciendo=true;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mp3Play.execute(audios.get(index).getUrl());//url del audio
        if (estabaReproduciendo==true){
            reproducirAudio();
        }else {
            seekBar.setProgress(0);
        }
    }

    private void reproducirAudio() {
        if (!mediaPlayer.isPlaying()) {
            playPause.setBackgroundResource(R.drawable.ic_pause_audio);
            mediaPlayer.start();
            endTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            myHandler.postDelayed(UpdateSongTime, 100);
            seekBar.setMax((int) endTime);
        } else {
            mediaPlayer.pause();
        }
    }


    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                startTime = mediaPlayer.getCurrentPosition();
                duracionActual.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );
                seekBar.setProgress(startTime);
            } else {
                playPause.setBackgroundResource(R.drawable.ic_play_audio);

            }
            myHandler.postDelayed(this,100);
        }
    };
    private void configuracionActivity() {
        nombreAudio = findViewById(R.id.txtTituloAudio);
        nombreAudio.setSelected(true);
        skipPrevious = findViewById(R.id.imgSkipPrevious);
        playPause = findViewById(R.id.imgPlayPause);
        imgAudio = findViewById(R.id.imgTourAudio);
        skipNext = findViewById(R.id.imgSkipNext);
        duracionActual = findViewById(R.id.txtDuracionActual);
        duracionMaxima = findViewById(R.id.txtDuracionMaxima);
        tituloLugar = findViewById(R.id.txtTituloLugarTour);
        tourEnTexto = findViewById(R.id.imgbtnTourATexto);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarQR);
        setSupportActionBar(toolbar);
    }

    private void consultaTour(String id) {
        final String url = "http://ec2-54-245-18-174.us-west-2.compute.amazonaws.com/Cicerone/PHP/tourGuiado.php?qrcode="+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            agregarAudios(response);
                        } catch (JSONException e) {
                            Toast.makeText(ReproduccionTourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ReproduccionTourActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void agregarAudios(JSONArray response) throws JSONException {
        for (int i = 0; i <response.length(); i++){
            JSONObject dato = response.getJSONObject(i);
            audio audio = new audio(dato.getString("Nombre"),
                    dato.getString("UrlAudio"),
                    dato.getString("Sitio"),
                    dato.getString("Fotografia"));
            audios.add(audio);
        }
        configuraNuevaCancion();
        tituloLugar.setText(audios.get(index).getSitio());
        nombreAudio.setText(audios.get(index).getNombre());//asigna el nombre de la cancion
        Glide.with(this).load(audios.get(index).getFotografia()).into(imgAudio);
    }

    @Override
    public void onBackPressed() {//para que no regrese a la vista de qrscan,regresara al home
        super.onBackPressed();
        mediaPlayer.stop();
        launchHome();
    }

    private void launchHome() {
        Intent intent = new Intent(ReproduccionTourActivity.this,BottomNav.class);
        startActivity(intent);
    }

    //TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_panic_btn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_panic_button:
                launchReporProblem();
                break;
            case R.id.item_help:
                launchUserHelpActivity();
                break;
        }
        return true;
    }

    private void launchUserHelpActivity() {
        Intent intent = new Intent(ReproduccionTourActivity.this, UserHelpActivity.class);
        startActivity(intent);
    }

    private void launchReporProblem() {
        Intent IlaunchReportProblem = new Intent(ReproduccionTourActivity.this, ReportProblemActivity.class);
        startActivity(IlaunchReportProblem);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {//cuando termine de reproducirse
        playPause.setBackgroundResource(R.drawable.ic_play_audio);
    }

    private class audio {
        private String nombre, url,sitio,fotografia;

        public audio() {
        }

        public audio(String nombre, String url, String sitio,String fotografia) {
            this.nombre = nombre;
            this.url = url;
            this.sitio = sitio;
            this.fotografia = fotografia;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSitio() {
            return sitio;
        }

        public void setSitio(String sitio) {
            this.sitio = sitio;
        }

        public String getFotografia() {
            return fotografia;
        }

        public void setFotografia(String fotografia) {
            this.fotografia = fotografia;
        }
    }
}
