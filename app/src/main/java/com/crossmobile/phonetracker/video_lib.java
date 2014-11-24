package com.crossmobile.phonetracker;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.widget.VideoView;
import android.content.Context;

import com.google.android.gms.location.LocationClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

/**
 * Created by aloknerurkar on 7/30/14.
 */

public class video_lib  extends VideoView{

    boolean negotiation = false;

    InfoJsonSend json_send = new InfoJsonSend(this.getContext(),"ip","ip_mobile");
    LocationManager locationManager;
    int bandwidth = 0;

    String viz_url = "http://10.0.18.117:3000/mobile_usage_stats";

    public video_lib(Context context) {
        super(context);
    }
    public video_lib(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public video_lib(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVideoURI(Uri uri){
        super.setVideoURI(uri);
    }

    @Override
    public void pause() {

        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        json_send.release();
        if(location != null){
            json_send.postMobileUsage(location,
                    bandwidth,"video",viz_url);
        }
        super.pause();
    }
    @Override
    public void start() {

        while(!negotiation){
            bandwidth = json_send.negotiate("video");
            if(bandwidth != -1){
                negotiation = true;
            }
        }
        negotiation = false;
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            json_send.postMobileUsage(location,
                    bandwidth,"video",viz_url);

        }
        super.start();
    }

    public boolean isPlaying(){
        return super.isPlaying();
    }

    @Override
    public void stopPlayback(){
        super.stopPlayback();
    }








}