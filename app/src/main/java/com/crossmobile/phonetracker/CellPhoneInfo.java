package com.crossmobile.phonetracker;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.b2msolutions.reyna.services.ForwardService;
import com.b2msolutions.reyna.services.StoreService;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Iterator;
import java.util.List;


@SuppressLint("NewApi")
public class CellPhoneInfo extends Activity {

	ToggleButton getCellPhoneInfo;

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
		//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_cell_phone_info);



		//jsonInfo = new InfoJsonSend(CellPhoneInfo.this, "http://10.0.10.101:1234");
		// Getting Reference to CellPhoneInfo button
		getCellPhoneInfo = (ToggleButton) findViewById(R.id.toggleButton1);

        // Check if service is running, and set the button appropriately.
        Log.d("cellPhoneInfo", Boolean.toString(isServiceRunning("com.crossmobile.phonetracker.GPSTracker")));
        getCellPhoneInfo.setChecked(isServiceRunning("com.crossmobile.phonetracker.GPSTracker"));

		getCellPhoneInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	
		        if (isChecked) {
		        	Intent i = new Intent(CellPhoneInfo.this, GPSTracker.class);

                    // This lets you send to two different addresses depending on what your network connectivity is.
                    // For JIFX, we just send to one address, always.

		        	startService(i);
                    //Log.d("cellPhoneInfo", Boolean.toString(isServiceRunning("com.crossmobile.phonetracker.GPSTracker")));
		        	//bindService(i, mConnection, BIND_AUTO_CREATE);
					//gps = new GPSTracker(CellPhoneInfo.this, ipAddressField.getText().toString());
		        } else {
		        	Log.i("cellPhoneInfo","StoppingService");
		        	stopService(new Intent(CellPhoneInfo.this, GPSTracker.class));
                    stopService(new Intent(CellPhoneInfo.this, StoreService.class));
                    stopService(new Intent(CellPhoneInfo.this, ForwardService.class));
                    CellPhoneInfo.this.deleteDatabase("reyna.db");
		        }
		    }
		});

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
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


    private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i.next();
            Log.d("SERVICE INFO", runningServiceInfo.service.getClassName());
            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;
            }
        }
        return serviceRunning;
    }

}
