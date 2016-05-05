package com.crossmobile.phonetracker;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import au.com.bytecode.opencsv.CSVWriter;

public class GPSTracker extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "GPSTracker";
    public static final String CSV_FIELDS = "time,mcc,mnc,network_type,signal_strength,lat,lon,alt";

    GoogleApiClient mClient;

    // Do we want debug toast messages?
    boolean isDebugMsg;

    // Do we want dynamic location updates?
    boolean isDynamic;

    // DO we want to write to CSV?
    boolean isOffline;

    // Do we want to allow sending on GPRS?
    boolean sendOnMobile;

    InfoJsonSend jsonOutput;
	String ipAddress, ipAddressMobile;
	IBinder mBinder = new LocalBinder();
	LocationRequest mLocationRequest;
	boolean mUpdatesRequested;

    CSVWriter writer = null;

    private static final long MIN_DISTANCE = 2; // 0 meters
    
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    //public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    //private static final long UPDATE_INTERVAL =
    //        MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // The minimum time between updates in milliseconds



//    public GPSTracker(Context context, String ipaddress) {
//        this.mContext = context;
//        jsonOutput = new InfoJsonSend(context, ipaddress);
//        getLocation();
//    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Get shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ipAddress = sharedPref.getString(SettingsActivity.KEY_WIFI_IP_ADDRESS, "");
        ipAddressMobile = sharedPref.getString(SettingsActivity.KEY_GPRS_IP_ADDRESS, "");
        int updateInterval = Integer.valueOf(sharedPref.getString(SettingsActivity.KEY_UPDATE_INTERVAL, "5"));
        double pressureASL = Double.valueOf(sharedPref.getString(SettingsActivity.KEY_PRESSURE_ASL, String.valueOf(SensorManager.PRESSURE_STANDARD_ATMOSPHERE)));
        isOffline = sharedPref.getBoolean(SettingsActivity.KEY_OFFLINE, true);
        isDebugMsg = sharedPref.getBoolean(SettingsActivity.KEY_DEBUG, false);
        isDynamic = sharedPref.getBoolean(SettingsActivity.KEY_DYNAMIC_LOCATIONS, true);
        sendOnMobile = true;
        // Initialize CSV writer

        if(isOffline){
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fileDate = sdf.format(cal.getTime());
            String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            String fileName = fileDate + ".csv";

            String folderName = "CROSSTracker_CSV";
            String directoryPath = baseDir + File.separator + folderName + File.separator;
            File directory = new File(directoryPath);
            // Make directory if neccessary
            directory.mkdirs();
            File f = new File(directory, fileName );


            // File exist
            try {
                if (f.exists() && !f.isDirectory()) {
                    FileWriter mFileWriter = new FileWriter(f.getAbsolutePath(), true);
                    writer = new CSVWriter(mFileWriter);
                } else {
                    writer = new CSVWriter(new FileWriter(f.getAbsolutePath()));
                }
                String[] initialLine = {"time", "mcc", "mnc", "network_type", "signal_strength","lat","lon","alt"};
                writer.writeNext(initialLine);
            }
            catch (Exception e) {
                Log.e("InfoJsonSend", "File write error.", e);
            }
            Log.i(TAG, "Opened file " + fileDate);

        }



    	jsonOutput = new InfoJsonSend(this, ipAddress, ipAddressMobile, sendOnMobile, pressureASL, writer);
        //mLocationClient = new LocationClient(this, this, this);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(updateInterval*1000);


        if(isDynamic){
            mLocationRequest.setSmallestDisplacement(MIN_DISTANCE);
            //mLocationRequest.setFastestInterval(updateInterval*1000/2);
        }
        else {
            //mLocationRequest.setSmallestDisplacement(0);
            mLocationRequest.setFastestInterval(updateInterval*1000);
        }
        mUpdatesRequested = true;

        mClient.connect();

        // Initialize CSV writer


        //mLocationClient.connect();
        return Service.START_NOT_STICKY;
    }

    // Sensor/barometer methods

    
 
    @Override
    public void onLocationChanged(Location location) {
    	Log.i("cellPhoneInfo", "Location Changed");
		String debugMessage = jsonOutput.postToServer(location);
        if(isDebugMsg){
		    Toast.makeText(this, debugMessage, Toast.LENGTH_SHORT).show();
        }
    }
    

    @Override
    public void onDestroy(){
    	Log.i("GPSTracker","stopped");
    	//removeLocationUpdates(this);
    	if (mClient.isConnected()) {
            mClient.disconnect();
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            //mClient.removeLocationUpdates((LocationListener)this);
        }

    	//mLocationClient.disconnect();
        if (writer != null) {
            try {
                writer.close();
            }
            catch (Exception e){
                Log.e(TAG,"failed to close file");
            }
        }
        writer = null;
    	Toast.makeText(this, "Logging stopped", Toast.LENGTH_SHORT).show();
    }
 
 
    @Override
    public IBinder onBind(Intent arg0) {
    	
    	
        return mBinder;
    }
    
    public class LocalBinder extends Binder {
    	  public GPSTracker getServerInstance() {
    	   return GPSTracker.this;
    	  }
    	 }

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		Log.d("GPSTracker", "Connection Failed");
		Toast.makeText(this, "Google Play Services connection failed", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {

		//Log.d("GPSTracker",mLocationClient.getLastLocation().toString());
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		//mLocationClient.requestLocationUpdates(mLocationRequest, this);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mClient, mLocationRequest, this);
		//jsonOutput.postToServer(mLocationClient.getLastLocation());
        //Log.d("GPSTracker", Float.toString(mLocationRequest.getSmallestDisplacement()));
	}

//	@Override
//	public void onDisconnected() {
//		Toast.makeText(this, "Logging stopped", Toast.LENGTH_SHORT).show();
//
//	}

    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
        Toast.makeText(this, "Logging stopped", Toast.LENGTH_SHORT).show();
    }


 
}






