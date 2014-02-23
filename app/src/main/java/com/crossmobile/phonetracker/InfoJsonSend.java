package com.crossmobile.phonetracker;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;


import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
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
	MyPhoneStateListener    MyListener;
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



	
	
}
