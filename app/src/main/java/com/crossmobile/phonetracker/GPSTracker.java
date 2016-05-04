package com.crossmobile.phonetracker;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GPSTracker extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "GPSTracker";

    GoogleApiClient mClient;

    // Do we want debug toast messages?
    boolean isDebugMsg;

    // Do we want dynamic location updates?
    boolean isDynamic;

    // Do we want to allow sending on GPRS?
    boolean sendOnMobile;

    InfoJsonSend jsonOutput;
	String ipAddress, ipAddressMobile;
	IBinder mBinder = new LocalBinder();
	LocationRequest mLocationRequest;
	boolean mUpdatesRequested;

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ipAddress = sharedPref.getString(SettingsActivity.KEY_WIFI_IP_ADDRESS, "");
        ipAddressMobile = sharedPref.getString(SettingsActivity.KEY_GPRS_IP_ADDRESS, "");
        int updateInterval = Integer.valueOf(sharedPref.getString(SettingsActivity.KEY_UPDATE_INTERVAL, "5"));
        double pressureASL = Double.valueOf(sharedPref.getString(SettingsActivity.KEY_PRESSURE_ASL, "1013.25"));
    	jsonOutput = new InfoJsonSend(this, ipAddress, ipAddressMobile, sendOnMobile, pressureASL);
        //mLocationClient = new LocationClient(this, this, this);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        isDebugMsg = sharedPref.getBoolean(SettingsActivity.KEY_DEBUG, false);
        isDynamic = sharedPref.getBoolean(SettingsActivity.KEY_DYNAMIC_LOCATIONS, true);
        sendOnMobile = true;
        
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

        //Check for barometer


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






