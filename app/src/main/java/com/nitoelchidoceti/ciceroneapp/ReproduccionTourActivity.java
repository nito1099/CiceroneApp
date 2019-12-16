package com.nitoelchidoceti.ciceroneapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.TimeUnit;

public class ReproduccionTourActivity extends AppCompatActivity {

    private TextView nombreAudio, duracionMaxima, duracionActual, tituloLugar;
    private ImageButton skipPrevious, playPause, skipNext, tourEnTexto;
    private ImageView imgAudio;
    private androidx.appcompat.widget.AppCompatSeekBar seekBar;
    private double startTime = 0;
    private double endTime = 0;
    private Handler myHandler = new Handler();
    int canciones[] = {R.raw.audio1_introduccion, R.raw.audio2_sala1, R.raw.audio3_sala2};
    int index = 0;//indica que numero de cancion se esta reproducciendo
    private MediaPlayer mediaPlayer;
    private  boolean estaEnPlay;
    String can[] = {"audio1_introduccion.mp3", "audio2_Sala1dddddddddddddddddddddddddddddddddd.mp3", "audio3_sala2.mp3"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproduccion_tour);
        configuracionActivity();

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirCancion();
            }
        });

        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estaEnPlay==true){
                    playPause.setBackgroundResource(R.drawable.ic_play_audio);
                    mediaPlayer.pause();
                    estaEnPlay = !estaEnPlay;
                }
                index++;
                if ( index>2)index=0;
                configuraNuevaCancion();
                reproducirCancion();
            }
        });

        skipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estaEnPlay == true) {
                    playPause.setBackgroundResource(R.drawable.ic_play_audio);
                    mediaPlayer.pause();
                    estaEnPlay = !estaEnPlay;
                }
                index--;
                if (index<0)index=2;
                configuraNuevaCancion();
                reproducirCancion();
            }
        });

        tourEnTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });
    }

    private void reproducirCancion() {
        if (estaEnPlay == false) {
            playPause.setBackgroundResource(R.drawable.ic_pause_audio);
            mediaPlayer.start();
            endTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            estaEnPlay = !estaEnPlay;
            myHandler.postDelayed(UpdateSongTime, 100);
        } else {
            mediaPlayer.pause();
            estaEnPlay = !estaEnPlay;
            playPause.setBackgroundResource(R.drawable.ic_play_audio);
        }
    }

    private void configuraNuevaCancion() {
        mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(getApplication(), canciones[index]);//se reasigna el nuevo audio
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        nombreAudio.setText(can[index]);
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
                seekBar.setProgress((int) startTime);
            } else {
                playPause.setBackgroundResource(R.drawable.ic_play_audio);
                estaEnPlay = !estaEnPlay;
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
        mediaPlayer = MediaPlayer.create(this,canciones[index]);//agrega el audio a reproducir
        nombreAudio.setText(can[index]);//asigna el nombre de la cancion
        Toast.makeText(this, "Dato:"+ getIntent().getSerializableExtra("tour"), Toast.LENGTH_LONG).show();
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        seekBar.setMax((int) endTime);
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
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarQR);
        setSupportActionBar(toolbar);
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
}
