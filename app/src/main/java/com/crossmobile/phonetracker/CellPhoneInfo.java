package com.crossmobile.phonetracker;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;


@SuppressLint("NewApi")
public class CellPhoneInfo extends Activity implements View.OnClickListener{

	ToggleButton getCellPhoneInfo;
	EditText ipAddressField;
	EditText ipAddressFieldGPRS;
    EditText updateFrequencyField;
    boolean isDebugMsg = false;
    boolean isDynamic = true;
    Button send_video;
    Button send_audio;
    Button send_image;
    Context c = this;

	GPSTracker tracker;

    @Override
    public void onResume(){
        int errorcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getBaseContext());
        if(errorcode != 0){
            int requestCode = 0;
            Dialog errorDiag = GooglePlayServicesUtil.getErrorDialog(errorcode, this, requestCode);
            errorDiag.show();
        }
        super.onResume();

    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_cell_phone_info);



		//jsonInfo = new InfoJsonSend(CellPhoneInfo.this, "http://10.0.10.101:1234");
		// Getting Reference to CellPhoneInfo button
		getCellPhoneInfo = (ToggleButton) findViewById(R.id.toggleButton1);
		ipAddressField = (EditText)findViewById(R.id.editText2);
		ipAddressFieldGPRS = (EditText)findViewById(R.id.editTextGPRS);
        updateFrequencyField = (EditText)findViewById(R.id.editTextUpdateInterval);
        send_video = (Button)findViewById(R.id.button);
        send_audio = (Button)findViewById(R.id.button2);
        send_image = (Button)findViewById(R.id.button3);

		getCellPhoneInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	
		        if (isChecked) {
		        	Intent i = new Intent(CellPhoneInfo.this, GPSTracker.class);
		        	i.putExtra("IP", ipAddressField.getText().toString());
		        	i.putExtra("IP_mobile", ipAddressFieldGPRS.getText().toString());
                    i.putExtra("DebugOn", isDebugMsg);
                    i.putExtra("updateInterval",Integer.valueOf(updateFrequencyField.getText().toString()));
                    i.putExtra("DynamicOn", isDynamic);
		        	startService(i);
		        	//bindService(i, mConnection, BIND_AUTO_CREATE);
					//gps = new GPSTracker(CellPhoneInfo.this, ipAddressField.getText().toString());
		        } else {
		        	Log.i("cellPhoneInfo","StoppingService");
		        	stopService(new Intent(CellPhoneInfo.this, GPSTracker.class));
		        }
		    }
		});

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.debug:

                if (item.isChecked()){
                    isDebugMsg = false;
                    item.setChecked(false);
                }
                else {
                    isDebugMsg = true;
                    item.setChecked(true);
                }
                return super.onOptionsItemSelected(item);
            case R.id.dynamic:
                if (item.isChecked()){
                    isDynamic = false;
                    item.setChecked(false);
                }
                else {
                    isDynamic = true;
                    item.setChecked(true);
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cell_phone_info, menu);
		return true;
	}

    String video_message = "I want to send a video message";
    String audio_message = "I want to send an audio message";
    String image_message = "I want to send an image";
    byte[] message;
    int msg_length = 0;

    public void onClick(View v){
        final int id = v.getId();

        int server_port = 8888;
        InetAddress cmu_server = null;
        try {
            cmu_server = InetAddress.getByName("10.0.2.2");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        switch(id){
            case R.id.button:
                UDP_payload payload = new UDP_payload(this);

                try {
                    payload.sendUDP_Packet("10.0.2.118",3000,"hello");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(CellPhoneInfo.this, Video_screen.class);
                intent.putExtra("IP", ipAddressField.getText().toString());
                intent.putExtra("IP_mobile", ipAddressFieldGPRS.getText().toString());
                startActivity(intent);
                message = video_message.getBytes();
                msg_length = video_message.length();
                DatagramPacket p1 = new DatagramPacket(message, msg_length, cmu_server,server_port);
                try {
                    s.send(p1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.button2:
                Intent intent2 = new Intent(CellPhoneInfo.this, Audio_screen.class);
                startActivity(intent2);
                message = audio_message.getBytes();
                msg_length = audio_message.length();
                DatagramPacket p2 = new DatagramPacket(message, msg_length, cmu_server, server_port);
                try {
                    s.send(p2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.button3:
                HttpClient client = new DefaultHttpClient();
                URI uri = URI.create("http://www.google.com");
                URL url = null;
                try {
                    url = new URL("http://www.google.com");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpHost host = null;
                HTTP_payload payload1 = new HTTP_payload(client,host,uri,url);
                HttpResponse resp = payload1.send_http_get();
                String rep = resp.getStatusLine().toString();
                System.out.println(rep);
                Uri uri2 = Uri.parse("http://www.google.com");
                Intent intent3 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(intent3);
                break;
            default:
                return;

        }
    }

}
