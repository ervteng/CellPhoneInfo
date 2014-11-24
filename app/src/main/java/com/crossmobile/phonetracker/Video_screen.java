package com.crossmobile.phonetracker;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.crossmobile.phonetracker.R;

import static android.view.View.*;


public class Video_screen extends Activity{

    video_lib v;
    video_lib v2;
    Button button1;
    Button button2;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        v = new video_lib(this, null, 0);
        v2 = new video_lib(this, null, 0);

        setContentView(R.layout.activity_video_screen);

        v = (video_lib) findViewById(R.id.VideoView);
        v.setVideoURI(Uri.parse("http://download.wavetlan.com/SVV/Media/HTTP/H264/Talkinghead_Media/H264_test1_Talkinghead_mp4_480x360.mp4"));


        v2 = (video_lib) findViewById(R.id.VideoView1);
        v2.setVideoURI(Uri.parse("http://download.wavetlan.com/SVV/Media/HTTP/H264/Talkinghead_Media/H264_test1_Talkinghead_mp4_480x360.mp4"));

        button1 = (Button) findViewById(R.id.Play1);
        button2 = (Button) findViewById(R.id.Play2);


        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(v.isPlaying()){
                    v.pause();
                }
                else
                    v.start();
            }
        });

        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(v2.isPlaying()){
                    v2.pause();
                }
                else
                    v2.start();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_screen, menu);
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
