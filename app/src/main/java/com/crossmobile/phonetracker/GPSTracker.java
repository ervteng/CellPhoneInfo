package com.crossmobile.phonetracker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.content.BroadcastReceiver;
 
public class GPSTracker extends Service implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

 
    // flag for GPS status
    boolean canGetLocation = false;

    
    LocationClient mLocationClient;

    // Do we want debug toast messages?
    boolean isDebugMsg;

    // Do we want dynamic location updates?
    boolean isDynamic;

    InfoJsonSend jsonOutput;
	String ipAddress, ipAddressMobile;
	IBinder mBinder = new LocalBinder();
	LocationRequest mLocationRequest;
	boolean mUpdatesRequested;

    private static final long MIN_DISTANCE = 10; // 0 meters
    
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
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
    	ipAddress=intent.getExtras().getString("IP");
    	ipAddressMobile = intent.getExtras().getString("IP_mobile");
        int updateInterval = intent.getExtras().getInt("updateInterval");
    	jsonOutput = new InfoJsonSend(this, ipAddress, ipAddressMobile);
        mLocationClient = new LocationClient(this, this, this);	
        isDebugMsg = intent.getExtras().getBoolean("DebugOn");
        isDynamic = intent.getExtras().getBoolean("DynamicOn");
        //jsonOutput.postToServer(location);	
       
        //mLocationClient.requestLocationUpdates()
        
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
        mLocationClient.connect();
        return Service.START_NOT_STICKY;
    }

     
    
 
    @Override
    public void onLocationChanged(Location location) {
    	Log.i("cellPhoneInfo", "Location Changed");
		jsonOutput.postToServer(mLocationClient.getLastLocation());
        if(isDebugMsg){
		    Toast.makeText(this, "New Location " + mLocationClient.getLastLocation().toString() + " posted to server", Toast.LENGTH_SHORT).show();
        }
    }
    

    @Override
    public void onDestroy(){
    	Log.i("GPSTracker","stopped");
    	//removeLocationUpdates(this);
    	if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates((LocationListener)this);
        }
    	mLocationClient.disconnect();
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

		Log.d("GPSTracker",mLocationClient.getLastLocation().toString());
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		jsonOutput.postToServer(mLocationClient.getLastLocation());
        Log.d("GPSTracker", Float.toString(mLocationRequest.getSmallestDisplacement()));
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Logging stopped", Toast.LENGTH_SHORT).show();
		
	}


 
}






