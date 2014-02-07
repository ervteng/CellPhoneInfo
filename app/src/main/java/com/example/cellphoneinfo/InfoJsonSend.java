package com.example.cellphoneinfo;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


//import java.io.UnsupportedEncodingException;







import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

//import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONException;
import org.json.JSONObject;








import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
//import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.location.Location;

public class InfoJsonSend{
	
	GPSTracker gps;
	double Latitude;
	double Longitude;
	String latitudeStr;
	String longitudeStr;
	String jsonOutput;
	TelephonyManager  cellInfo;
	String IMEINumber;
	String IMSINumber;
	StringEntity se;
	int signalStrengthDB;
	HttpResponse response;
	String filename;
	File file;
	String root;
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
		 filename = "cellPhoneInfo.txt";
		 
		 file = new File(Environment.getExternalStorageDirectory(), filename);
		 
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

    };/* End of private Class */
	
	public String postDataToServer(Location location, String ipaddr) throws JSONException, ClientProtocolException, IOException{
		
		// for example value of first element
		
		Latitude = location.getLatitude();
		Longitude = location.getLongitude();
		
		latitudeStr = String.valueOf(Latitude);
		longitudeStr = String.valueOf(Longitude);
		
		GsmCellLocation gsmLoc = (GsmCellLocation)cellInfo.getCellLocation();

		tsLong = System.currentTimeMillis();
		timeStamp = tsLong/1000;
		
		ts = tsLong.toString();
		
		
		
		JSONObject storeData =new JSONObject();
	    JSONObject infoJson =new JSONObject();
	    storeData.put("latitude", latitudeStr);
	    storeData.put("longitude", longitudeStr);
	    storeData.put("imei", IMEINumber);
	    storeData.put("imsi", IMSINumber);
	    storeData.put("RSSI", String.valueOf(signalStrengthDB));
	    storeData.put("lac", String.valueOf(gsmLoc.getLac()));
	    storeData.put("timestamp", tsLong);
	    //storeData.put("timestamp_seconds", timeStamp);
	    infoJson.put("user_location",storeData);


	     jsonOutput =infoJson.toString();
	     
	     Log.i("cellPhoneInfo", jsonOutput);
	     
	     
	     
	     
	     /*if(file.exists())
	     {
	          FileWriter fo = new FileWriter(file, true);
	          //BufferedWriter out = new BufferedWriter(fo);
	         
	          fo.append(jsonOutput+"\n");
	          fo.close();
	          System.out.println("file created: "+file);
	          
	     } 
	     
	     else
	     {
	    	file.createNewFile(); 
	     }*/
	     
	   
	     
	     DefaultHttpClient client = new DefaultHttpClient();
	     try{
	     HttpPost post = new HttpPost(ipaddr);
	     post.setEntity(new StringEntity(infoJson.toString()));
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
		
		 return jsonOutput;
		
		
	}
	
	private class PostToServerTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... locationandip) {
            Log.i("AsyncTask","Posting...");
            
            Location loc = (Location)locationandip[0];
            String ip = (String)locationandip[1];
            // params comes from the execute() call: params[0] is the url.
            try {
                return postDataToServer(loc, ip);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "JSON Exeption";
			}
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
           	Log.i("AsyncTask","result");
       }
    }
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
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	Log.i("cellPhoneInfo","Task Created, IP = " + sendToThisIP);
            new PostToServerTask().execute(location, sendToThisIP);
        } else {
            Log.i("cellPhoneInfo","No Network");
        }
	}

	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}



	/*@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}
*/
	
	
}
