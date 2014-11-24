package com.crossmobile.phonetracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;

import static android.view.View.*;


public class Audio_screen extends Activity{

    Button pause, pause2, pause3, pause4;
    ImageButton play,play2,play3,play4;

    //MediaPlayer mediaPlayer1, mediaPlayer2, mediaPlayer3, mediaPlayer4;
    MediaPlayer_Lib mediaPlayer1 = new MediaPlayer_Lib(this);
    MediaPlayer_Lib mediaPlayer2 = new MediaPlayer_Lib(this);
    MediaPlayer_Lib mediaPlayer3 = new MediaPlayer_Lib(this);
    MediaPlayer_Lib mediaPlayer4 = new MediaPlayer_Lib(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_screen);
        pause = (Button)findViewById(R.id.Play1);
        pause2 = (Button)findViewById(R.id.Play2);
        pause3 = (Button)findViewById(R.id.Play3);
        pause4 = (Button)findViewById(R.id.Play4);

        play = (ImageButton) findViewById(R.id.audio_play);
        play2 = (ImageButton) findViewById(R.id.audio_play2);
        play3 = (ImageButton) findViewById(R.id.audio_play3);
        play4 = (ImageButton) findViewById(R.id.audio_play4);

        mediaPlayer1.setDataSource(this, Uri.parse("http://vprbbc.streamguys.net:80/vprbbc24.mp3"));
        mediaPlayer2.setDataSource(this, Uri.parse("http://vprbbc.streamguys.net:80/vprbbc24.mp3"));
        mediaPlayer3.setDataSource(this, Uri.parse("http://vprbbc.streamguys.net:80/vprbbc24.mp3"));
        mediaPlayer4.setDataSource(this, Uri.parse("http://vprbbc.streamguys.net:80/vprbbc24.mp3"));

        try {
            mediaPlayer1.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer1.isPlaying())
                    mediaPlayer1.pause();
            }
        });

        pause2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer2.isPlaying())
                    mediaPlayer2.pause();
            }
        });

        pause3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer3.isPlaying())
                    mediaPlayer3.pause();
            }
        });

        pause4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer1.isPlaying())
                    mediaPlayer1.pause();
            }
        });


        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer1.isPlaying()){
                    mediaPlayer1.pause();
                }
                else{
                    mediaPlayer1.start();
                }

            }
        });

        play2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer2.isPlaying()){
                    mediaPlayer2.pause();
                }
                else{
                    try{
                        mediaPlayer2.prepare();
                        mediaPlayer2.start();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }


                }

            }
        });

        play3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer3.isPlaying()){
                    mediaPlayer3.pause();
                }
                else{
                    try {
                        mediaPlayer3.prepare();
                        mediaPlayer3.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        play4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer4.isPlaying()){
                    mediaPlayer4.pause();
                }
                else{
                    try {
                        mediaPlayer4.prepare();
                        mediaPlayer4.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.audio_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
