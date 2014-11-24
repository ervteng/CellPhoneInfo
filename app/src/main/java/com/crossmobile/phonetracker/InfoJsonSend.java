package com.crossmobile.phonetracker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;


import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.JsonReader;
import android.util.Log;
import android.location.Location;
import com.b2msolutions.reyna.*;
import com.b2msolutions.reyna.services.StoreService;


public class InfoJsonSend{

	double Latitude;
	double Longitude;
	String latitudeStr;
	String longitudeStr;
	String jsonOutput;
	TelephonyManager  cellInfo;
	String IMEINumber;
	String IMSINumber;
	int signalStrengthDB;
	Long tsLong;
	Long timeStamp;
	String ts;
	String ipaddress, ipaddress_mobile;
	Context my_context;
	MyPhoneStateListener MyListener;

    Socket socket = null, socket2 = null;
    DataOutputStream op = null, op2 = null;
    InputStream ip = null, ip2 = null;

    String dest_addr = "10.0.23.67";
    int port = 2000;

    JSONObject neg_obj = new JSONObject();
    JSONObject neg_obj2 = new JSONObject();
    JsonReader reply_obj, reply_obj2;


    String token;
    TimerTask doAsynchronousTask;
    int bandwidth = 0;

	public InfoJsonSend(Context context, String ipaddressin, String ipaddress_mobilein){
		//gps = new GPSTracker(context);
		ipaddress = ipaddressin;
		ipaddress_mobile = ipaddress_mobilein;
		//gps.setInfoJson(InfoJsonSend.this);
		cellInfo = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		IMEINumber = cellInfo.getDeviceId();
		
		IMSINumber = cellInfo.getSubscriberId();
		my_context = context;
		signalStrengthDB = 0;
		 
		 MyListener = new MyPhoneStateListener();
		 cellInfo.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		 
		 
		
		}
	
	private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each time there is an update */
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         int signalStrengthInt = signalStrength.getGsmSignalStrength();
         if(signalStrengthInt == 99){
        	 signalStrengthDB = -1;
         }
         else
        	 signalStrengthDB = -113 + signalStrengthInt*2;

      }

    }/* End of private Class */

    public int sendToReyna(Location location, String ipaddr){
		
        // for example value of first element

        Latitude = location.getLatitude();
        Longitude = location.getLongitude();

        latitudeStr = String.valueOf(Latitude);
        longitudeStr = String.valueOf(Longitude);

        GsmCellLocation gsmLoc = (GsmCellLocation)cellInfo.getCellLocation();

        tsLong = System.currentTimeMillis();
        timeStamp = tsLong/1000;

//        ConnectivityManager cm = (ConnectivityManager)m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();

        ts = tsLong.toString();

        JSONObject jsonInfo = new JSONObject();
        JSONObject userInfo = new JSONObject();
        try{

            userInfo.put("latitude", latitudeStr);
            userInfo.put("longitude", longitudeStr);
            userInfo.put("imei", IMEINumber);
            userInfo.put("imsi", IMSINumber);
            userInfo.put("RSSI", String.valueOf(signalStrengthDB));
            userInfo.put("lac", String.valueOf(gsmLoc.getLac()));
            userInfo.put("timestamp", tsLong);
            //storeData.put("timestamp_seconds", timeStamp);
            jsonInfo.put("user_location",userInfo);


            jsonOutput = jsonInfo.toString();
        }
        catch (JSONException e){
            return -1;
        }

        Log.i("cellPhoneInfo", jsonOutput);
        Header[] headers = new Header[] {
                new Header("Accept", "application/json"),
                new Header("Content-Type", "application/json"),
        };

        URI theURI;
        try{
            theURI = new URI(ipaddr);
        }
        catch (URISyntaxException e)
        {
            return -1;
        }
        Message message = new Message(
                theURI,
                jsonInfo.toString(),
                headers);

        StoreService.start(my_context,message);
        StoreService.setLogLevel(Log.DEBUG);
		return 1;
	}

    //Checks if server is
	public void postToServer(Location location)
	{
		String sendToThisIP;
		Log.i("cellPhoneInfo","Trying to Connect");
		ConnectivityManager connMgr = (ConnectivityManager) 
				my_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable()){
			sendToThisIP = ipaddress;
		}
		else {
			sendToThisIP = ipaddress_mobile;
		}

        sendToReyna(location, sendToThisIP);
	}

    public int postMobileUsage(Location location,
                 long bandwidth, String type,
                                String ipAddr){

        Latitude = location.getLatitude();
        Longitude = location.getLongitude();

        latitudeStr = String.valueOf(Latitude);
        longitudeStr = String.valueOf(Longitude);

        //GsmCellLocation gsmLoc = (GsmCellLocation)cellInfo.getCellLocation();

        tsLong = System.currentTimeMillis();
        timeStamp = tsLong/1000;

        ts = tsLong.toString();

        String bw;
        bw = String.valueOf(bandwidth);

        JSONObject jsonInfo = new JSONObject();
        JSONObject userInfo = new JSONObject();
        try{

            userInfo.put("latitude", latitudeStr);
            userInfo.put("longitude", longitudeStr);
            userInfo.put("imei", IMEINumber);
            userInfo.put("imsi", IMSINumber);
            userInfo.put("RSSI", String.valueOf(signalStrengthDB));
            userInfo.put("lac", "hello");
                    //String.valueOf(gsmLoc.getLac()));
            userInfo.put("timestamp", tsLong);
            userInfo.put("bandwidth", bw);
            userInfo.put("typeOfComm",type);

            jsonInfo.put("mobile_usage_stat",userInfo);


            jsonOutput = jsonInfo.toString();
        }
        catch (JSONException e){
            return -1;
        }

        Log.i("cellPhoneInfo", jsonOutput);
        Header[] headers = new Header[] {
                new Header("Accept", "application/json"),
                new Header("Content-Type", "application/json"),
        };

        URI theURI;
        try{
            theURI = new URI(ipAddr);
        }
        catch (URISyntaxException e)
        {
            return -1;
        }

        HttpPost post = new HttpPost(theURI);

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonOutput);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(entity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }



        return 1;
    }

    public int negotiate(String type){

        int result = 0;

        try {
            socket = new Socket(dest_addr, port);
            op = new DataOutputStream(socket.getOutputStream());
            ip = socket.getInputStream();

            neg_obj.put("message","reservation_request");
            neg_obj.put("type",type);

            op.writeBytes(neg_obj.toString());

            reply_obj = new JsonReader(new InputStreamReader(ip));
            reply_obj.setLenient(true);
            reply_obj.beginObject();
            System.out.println(reply_obj.nextName());


            bandwidth = reply_obj.nextInt();
            System.out.println("bandwidth:" + bandwidth);
            reply_obj.endObject();


            socket.close();
            op.close();
            ip.close();
            neg_obj.remove("message");
            neg_obj.remove("type");
            reply_obj.close();

            socket = new Socket(dest_addr, port);
            op = new DataOutputStream(socket.getOutputStream());
            ip = socket.getInputStream();

            neg_obj.put("message","reserve_confirmation");
            neg_obj.put("value",bandwidth);

            op.writeBytes(neg_obj.toString());

            reply_obj = new JsonReader(new InputStreamReader(ip));
            reply_obj.setLenient(true);
            reply_obj.beginObject();
            System.out.println(reply_obj.nextName());
            token = reply_obj.nextString();

            callAsynchronousTask();

            result = bandwidth;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int release(){
        doAsynchronousTask.cancel();
        return 1;
    }

    private class SendIsAlive extends AsyncTask<Integer,Integer,Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {

            try {
                socket2 = new Socket(dest_addr, port);
                op2 = new DataOutputStream(socket2.getOutputStream());
                ip2 = socket2.getInputStream();

                neg_obj2.put("message","is_alive");
                neg_obj2.put("value",token);

                op2.writeBytes(neg_obj2.toString());

                reply_obj2 = new JsonReader(new InputStreamReader(ip));
                reply_obj2.setLenient(true);
                reply_obj2.beginObject();
                while(reply_obj2.hasNext()){
                    System.out.println(reply_obj2.nextName());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            SendIsAlive performBackground = new SendIsAlive();
                            performBackground.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50 ms
    }

}
