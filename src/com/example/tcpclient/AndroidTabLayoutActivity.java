// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.ToggleButton;

public class AndroidTabLayoutActivity extends TabActivity {
    /** Called when the activity is first created. */
    private ToggleButton orientFix;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
 
        orientFix = (ToggleButton)findViewById(R.id.toggleOrientFix);
        
        TabHost tabHost = getTabHost();
 
        // Tab Label --------
        TabSpec mainspec = tabHost.newTabSpec("Main");
        // setting Title and Icon for the Tab
        mainspec.setIndicator("Logs");//TODO: Add an icon
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainspec.setContent(mainIntent);
 
        // Tab Label --------
        TabSpec deviceConnections = tabHost.newTabSpec("Device");
        deviceConnections.setIndicator("Devices");//TODO: Add an icon
        Intent deviceConnectionsIntent = new Intent(this, DeviceConnectionsActivity.class);
        deviceConnections.setContent(deviceConnectionsIntent);
 
        // Tab Label --------
        TabSpec plottab = tabHost.newTabSpec("Charts");
        plottab.setIndicator("Charts");//TODO: Add an icon
        Intent plotIntent = new Intent(this, DataPlotActivity.class);
        plottab.setContent(plotIntent);       
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(deviceConnections); // Adding deviceConnections tab
        tabHost.addTab(mainspec); // Adding main tab
        tabHost.addTab(plottab); // Adding plot tab        
        
        orientFix.setOnClickListener(new View.OnClickListener() {
            
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// Toggle Orientation Fix
		if(orientFix.isChecked()){
		    //orientation fixed
		    mLockScreenRotation();
		}
		else{
		    //orientation automatic changed
		    mUnLockScreenRotation();
		}
	    }
	});
    }
    /*
     * Locks the screen orientation for all Activities to the current position the phone is
     * being held in.  This is not the "typical" mode of operation, but it is useful when in
     * zero-g 8-).
     */
    private void mLockScreenRotation() {
	// Stop the screen orientation changing during an event
        switch (this.getResources().getConfiguration().orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
            case Configuration.ORIENTATION_LANDSCAPE:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
        }
    }
    /*
     * UnLocks the screen orientation so that it automatically rotates when the phone 
     * is rotated.  This is the "typical" mode of Activities.
     */
    private void mUnLockScreenRotation() {
	// allow screen rotations again
	this.setRequestedOrientation(
	ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
