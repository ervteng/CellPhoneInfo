package com.crossmobile.phonetracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.b2msolutions.reyna.Header;
import com.b2msolutions.reyna.Message;
import com.b2msolutions.reyna.services.StoreService;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

// Classes for getting signal strength

public class InfoJsonSend{

	double Latitude;
	double Longitude;
    double Altitude;
	String latitudeStr;
	String longitudeStr;
	String jsonOutput;
    String altitudeStr;
	TelephonyManager  cellInfo;
	String IMEINumber;
	String IMSINumber;
	int signalStrengthDB;
	Long tsLong;
	Long timeStamp;
	String ts;
	String ipaddress, ipaddress_mobile;
	Context my_context;
    //Calibration value for barometer
    double pressureASL;
    boolean sendOnMobile;

    boolean hasBarometer = false;
	MyPhoneStateListener    MyListener;
    WifiManager wifiManager;

    public InfoJsonSend(Context context, String ipaddressin, String ipaddress_mobilein, boolean sendOnMobile, double pressureASL_in){
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


        MySensorListener mySensorListener = new MySensorListener();
        pressureASL = pressureASL_in;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_PRESSURE);


        if(sensors.size() > 0) {
            hasBarometer = true;

            Sensor sensor = sensors.get(0);
            sensorManager.registerListener(mySensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
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

    // Sensor listner for Barometer/Altitude
    private class MySensorListener implements SensorEventListener
    {
        int readingCount = 0;
        float pressureSum = 0;
        //We want to prevent a 0 reading at the very beginning
        boolean firstSensorReading = true;
        //Sensor/Barometer methods

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float pressure = event.values[0];
            pressureSum += pressure;
            readingCount ++;
            //Average the last 10 values
            if(readingCount == 10){
                float pressureAvg = pressureSum/10;
                Altitude = SensorManager.getAltitude((float)pressureASL, pressureAvg);
                readingCount = 0;
                pressureSum = 0;
            }
            else if(firstSensorReading){
                Altitude = SensorManager.getAltitude((float)pressureASL, pressure);
                firstSensorReading = false;
            }

        }
    }
	
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



    public myCellInfo getMyCellInfo()
    {
        myCellInfo mCInfo = new myCellInfo();
        try {
            //final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            for (final CellInfo info : cellInfo.getAllCellInfo()) {
                // Only get information for the currently registered network.
                if (info.isRegistered()) {
                    if (info instanceof CellInfoGsm) {
                        final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        //Log.e("InfoJsonSend", "getDBM:" + String.valueOf(gsm.getDbm()));
                        mCInfo.LAC = ((CellInfoGsm) info).getCellIdentity().getLac();
                        // Bug in Android? If no LAC, use getCellLocation (old API)
                        if(mCInfo.LAC == 0) {
                            GsmCellLocation location = (GsmCellLocation) cellInfo.getCellLocation();
                            mCInfo.LAC = location.getLac();
                        }
                        mCInfo.RSSI = gsm.getDbm();
                    } else if (info instanceof CellInfoCdma) {
                        final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                        mCInfo.RSSI = cdma.getDbm();
                    } else if (info instanceof CellInfoLte) {
                        final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        mCInfo.RSSI = lte.getDbm();
                    } else {
                        throw new Exception("Unknown type of cell signal!");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("InfoJsonSend", "Unable to obtain cell signal information", e);

        }
        return mCInfo;
    }

    //Class to return RSSI and LAC
    public class myCellInfo {
        int RSSI = -1;
        int LAC = -1;
    }

    //Checks if server is accessible by mobile or wifi, and puts in a request.
	public String postToServer(Location location)
	{
        Log.d("cellPHoneINfo",String.valueOf(pressureASL));
        // Get latitude and longitude from location
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
        if(!hasBarometer)
            Altitude = location.getAltitude();

        latitudeStr = String.valueOf(Latitude);
        longitudeStr = String.valueOf(Longitude);

        // Also, round the obnoxiously long altitude values.
        altitudeStr = String.format("%.2f", Altitude);

        //Toast.makeText(my_context, "Altitude is " + Double.toString(Altitude), Toast.LENGTH_SHORT).show();
        GsmCellLocation gsmLoc = (GsmCellLocation)cellInfo.getCellLocation();

        tsLong = System.currentTimeMillis();
        timeStamp = tsLong/1000;

        ts = tsLong.toString();
        myCellInfo currentCellInfo = getMyCellInfo();

        // Get WIFI connectivity information
        WifiInfo winfo = wifiManager.getConnectionInfo();
        String macAddress = winfo.getMacAddress();
        int phoneIPAddress = winfo.getIpAddress();

        // Format IP address
        String ipString = String.format(
                "%d.%d.%d.%d",
                (phoneIPAddress & 0xff),
                (phoneIPAddress >> 8 & 0xff),
                (phoneIPAddress >> 16 & 0xff),
                (phoneIPAddress >> 24 & 0xff));

        JSONObject jsonInfo = new JSONObject();
        JSONObject userInfo = new JSONObject();
        try{

            userInfo.put("latitude", latitudeStr);
            userInfo.put("longitude", longitudeStr);
            userInfo.put("altitude", altitudeStr);
            userInfo.put("imei", IMEINumber);
            userInfo.put("imsi", IMSINumber);
            if(currentCellInfo.RSSI != -1)
                userInfo.put("RSSI", String.valueOf(currentCellInfo.RSSI));
            //userInfo.put("RSSI", String.valueOf(i1) );
            userInfo.put("lac", String.valueOf(currentCellInfo.LAC));
            userInfo.put("timestamp", tsLong);
            userInfo.put("mac", macAddress);
            userInfo.put("ipaddr", ipString);
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
        return "Server Post Latitude: " + latitudeStr + " Longitude " + longitudeStr + " Altitude " + altitudeStr + " RSSI " + String.valueOf(currentCellInfo.RSSI);

	}



	
	
}
