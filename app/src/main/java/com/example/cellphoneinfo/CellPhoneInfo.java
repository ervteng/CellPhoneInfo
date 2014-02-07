package com.example.cellphoneinfo;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;


@SuppressLint("NewApi")
public class CellPhoneInfo extends Activity {

	ToggleButton getCellPhoneInfo;
	EditText ipAddressField;
	EditText ipAddressFieldGPRS;
	InfoJsonSend jsonInfo;
    boolean isDebugMsg;
	GPSTracker tracker;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_cell_phone_info);
		
		//jsonInfo = new InfoJsonSend(CellPhoneInfo.this, "http://10.0.10.101:1234");
		// Getting Reference to CellPhoneInfo button
		getCellPhoneInfo = (ToggleButton) findViewById(R.id.toggleButton1);
		ipAddressField = (EditText)findViewById(R.id.editText2);
		ipAddressFieldGPRS = (EditText)findViewById(R.id.editTextGPRS);

		getCellPhoneInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	
		        if (isChecked) {
		        	Intent i = new Intent(CellPhoneInfo.this, GPSTracker.class);
		        	i.putExtra("IP", ipAddressField.getText().toString());
		        	i.putExtra("IP_mobile", ipAddressFieldGPRS.getText().toString());
                    i.putExtra("DebugOn", isDebugMsg);
		        	startService(i);
		        	//bindService(i, mConnection, BIND_AUTO_CREATE);
					//gps = new GPSTracker(CellPhoneInfo.this, ipAddressField.getText().toString());
		        } else {
		        	Log.i("cellPhoneInfo","StoppingService");
		        	stopService(new Intent(CellPhoneInfo.this, GPSTracker.class));
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

}
