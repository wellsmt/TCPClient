// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.tcpclient.R;

/**
 * The AndroidTabLayoutActivity is the main activity for the WiFi DAQ. It sets
 * up different child activities as tabs and allows the user to switch between
 * the tabs. Since this is also the main activity, some of the apps window
 * settings are also set here.
 * 
 * @author Marc
 * 
 */
public class AndroidTabLayoutActivity extends TabActivity {
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	// WindowManager.LayoutParams.FLAG_FULLSCREEN);

	setContentView(R.layout.tab_layout);

	TabHost tabHost = getTabHost();

	// Tab Label --------
	TabSpec deviceConnections = tabHost.newTabSpec("Device");

	deviceConnections.setIndicator("Network Devices", getResources()
		.getDrawable(R.drawable.wifi_icon));
	Intent deviceConnectionsIntent = new Intent(this,
		DeviceConnectionsActivity.class);
	deviceConnections.setContent(deviceConnectionsIntent);

	// Tab Label --------
	TabSpec plottab = tabHost.newTabSpec("Charts");
	plottab.setIndicator("Charts",
		getResources().getDrawable(R.drawable.charts_icon));
	Intent plotIntent = new Intent(this, DataPlotActivity.class);
	plottab.setContent(plotIntent);

	// Adding all TabSpec to TabHost
	tabHost.addTab(deviceConnections); // Adding deviceConnections tab
	tabHost.addTab(plottab); // Adding plot tab

    }
}
