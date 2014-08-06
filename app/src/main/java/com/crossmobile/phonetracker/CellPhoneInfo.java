package com.crossmobile.phonetracker;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.app.ActivityManager;
import java.util.List;
import java.util.Iterator;

import com.b2msolutions.reyna.services.ForwardService;
import com.b2msolutions.reyna.services.RepositoryService;
import com.b2msolutions.reyna.services.StoreService;
import com.google.android.gms.common.GooglePlayServicesUtil;


@SuppressLint("NewApi")
public class CellPhoneInfo extends Activity {

	ToggleButton getCellPhoneInfo;
	EditText ipAddressField;
	EditText ipAddressFieldGPRS;
    EditText updateFrequencyField;
    boolean isDebugMsg = false;
    boolean isDynamic = true;
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
		//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_cell_phone_info);



		//jsonInfo = new InfoJsonSend(CellPhoneInfo.this, "http://10.0.10.101:1234");
		// Getting Reference to CellPhoneInfo button
		getCellPhoneInfo = (ToggleButton) findViewById(R.id.toggleButton1);
		ipAddressField = (EditText)findViewById(R.id.editText2);
		ipAddressFieldGPRS = (EditText)findViewById(R.id.editTextGPRS);
        updateFrequencyField = (EditText)findViewById(R.id.editTextUpdateInterval);

        // Check if service is running, and set the button appropriately.
        Log.d("cellPhoneInfo", Boolean.toString(isServiceRunning("com.crossmobile.phonetracker.GPSTracker")));
        getCellPhoneInfo.setChecked(isServiceRunning("com.crossmobile.phonetracker.GPSTracker"));

		getCellPhoneInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	
		        if (isChecked) {
		        	Intent i = new Intent(CellPhoneInfo.this, GPSTracker.class);
		        	i.putExtra("IP", ipAddressField.getText().toString());
		        	i.putExtra("IP_mobile", ipAddressFieldGPRS.getText().toString());
                    i.putExtra("DebugOn", isDebugMsg);
                    i.putExtra("updateInterval",Integer.valueOf(updateFrequencyField.getText().toString()));
                    i.putExtra("DynamicOn", isDynamic);

                    // This lets you send to two different addresses depending on what your network connectivity is.
                    // For JIFX, we just send to one address, always.

                    i.putExtra("SendOnMobile", true);
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

    @Override
    protected void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);
        outstate.putString("IPaddr", ipAddressField.getText().toString());
        outstate.putString("IPaddrMobile", ipAddressFieldGPRS.getText().toString());
        outstate.putBoolean("isDynamic", isDynamic);
        outstate.putBoolean("isDebugMsg", isDebugMsg);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        MenuItem isDynamicBox = (MenuItem)findViewById(R.id.dynamic);
        MenuItem isDebugBox = (MenuItem)findViewById(R.id.debug);

        super.onRestoreInstanceState(savedInstanceState);
        ipAddressField.setText(savedInstanceState.getString("IPaddr"));
        ipAddressFieldGPRS.setText(savedInstanceState.getString("IPaddrMobile"));
        isDynamicBox.setChecked(savedInstanceState.getBoolean("isDynamic"));
        isDebugBox.setChecked(savedInstanceState.getBoolean("isDebugMsg"));
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
