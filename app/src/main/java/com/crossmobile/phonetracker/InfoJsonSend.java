package com.crossmobile.phonetracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;


import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.location.Location;
import android.net.wifi.WifiManager;
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
    boolean sendOnMobile;
	MyPhoneStateListener    MyListener;
    WifiManager wifiManager;

    public InfoJsonSend(Context context, String ipaddressin, String ipaddress_mobilein, boolean sendOnMobile){
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

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        sendOnMobile = sendOnMobile;


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
	
	public int sendToReyna(JSONObject jsonInfo, String ipaddr){
		
        // for example value of first element


        jsonOutput = jsonInfo.toString();
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

    private class PostToServerTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... locationandip) {
            Log.i("AsyncTask","Posting...");

            JSONObject loc = (JSONObject)locationandip[0];
            String ip = (String)locationandip[1];
            // params comes from the execute() call: params[0] is the url.
            try {
                return sendToServer(loc, ip);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "JSON Exception";
            }
        }
    }
    public String sendToServer(JSONObject jsonInfo, String ipaddr) throws JSONException, IOException{

        DefaultHttpClient client = new DefaultHttpClient();
        try{
            HttpPost post = new HttpPost(ipaddr);
            post.setEntity(new StringEntity(jsonInfo.toString()));
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            client.execute(post);
        }
        catch(UnsupportedEncodingException e){
            e.printStackTrace();

        }
        catch(ClientProtocolException e){
            e.printStackTrace();

        }
        catch(IOException e){
            e.printStackTrace();

        }

        return jsonInfo.toString();


    }
    //Checks if server is accessible by mobile or wifi, and puts in a request.
	public void postToServer(Location location)
	{
        // Get latitude and longitude from location
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();

        latitudeStr = String.valueOf(Latitude);
        longitudeStr = String.valueOf(Longitude);

        GsmCellLocation gsmLoc = (GsmCellLocation)cellInfo.getCellLocation();

        tsLong = System.currentTimeMillis();
        timeStamp = tsLong/1000;

        ts = tsLong.toString();

        // Get WIFI connectivity information
        WifiInfo winfo = wifiManager.getConnectionInfo();
        String macAddress = winfo.getMacAddress();
        int phoneIPAddress = winfo.getIpAddress();


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
            userInfo.put("mac", macAddress);
            userInfo.put("ipaddr", String.valueOf(phoneIPAddress));
            //storeData.put("timestamp_seconds", timeStamp);
            jsonInfo.put("user_location",userInfo);

        }
        catch (JSONException e){

        }

		String sendToThisIP;
		Log.i("cellPhoneInfo","Trying to Connect");
		ConnectivityManager connMgr = (ConnectivityManager)
				my_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() && !sendOnMobile){
			new PostToServerTask().execute(jsonInfo, ipaddress_mobile);
		}
		else {
            sendToReyna(jsonInfo, ipaddress);
		}

	}



	
	
}
