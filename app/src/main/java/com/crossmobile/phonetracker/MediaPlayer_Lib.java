package com.crossmobile.phonetracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by aloknerurkar on 8/14/14.
 */


public class MediaPlayer_Lib extends MediaPlayer {

    Context context;
    int bandwidth = 0;
    String viz_url = "http://10.0.18.117:3000/mobile_usage_stats";
    boolean negotiation = false;

    public MediaPlayer_Lib(){}

    public MediaPlayer_Lib(Context c){
        context = c;
    }

    InfoJsonSend json_send;
    LocationManager locationManager;



    public void setDataSource(Context context, Uri uri){
        Map<String,String> headers = new HashMap<String, String>();
        try {
            super.setDataSource(context,uri,headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        json_send.release();

        if(location != null){
            json_send = new InfoJsonSend(context,"ip","ip_mob");
            json_send.postMobileUsage(location,
                    0,"audio",viz_url);
        }

        super.pause();
    }

    @Override
    public void start() {

        while(!negotiation){
            bandwidth = json_send.negotiate("audio");
            if(bandwidth != -1){
                negotiation = true;
            }
        }
        negotiation = false;

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null){
            json_send = new InfoJsonSend(context,"ip","ip_mob");
            json_send.postMobileUsage(location,
                    0,"audio",viz_url);
        }
        super.start();
    }

    public boolean isPlaying(){
        return super.isPlaying();
    }

    public void prepare() throws IOException {
        super.prepare();
    }

    public void stop(){
        json_send.release();
        super.stop();
    }

}
