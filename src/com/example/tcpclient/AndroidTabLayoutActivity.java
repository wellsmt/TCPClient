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

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        
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
        
        // Tab Label --------
        TabSpec schematics = tabHost.newTabSpec("Schematics");
        schematics.setIndicator("Schematics");//TODO: Add an icon
        Intent schematicConnectionsIntent = new Intent(this, SchematicViewActivity.class);
        schematics.setContent(schematicConnectionsIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(schematics);
        tabHost.addTab(deviceConnections); // Adding deviceConnections tab
        tabHost.addTab(mainspec); // Adding main tab
        tabHost.addTab(plottab); // Adding plot tab        
        
    }
}
