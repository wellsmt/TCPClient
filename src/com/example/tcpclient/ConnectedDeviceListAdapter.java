// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This is the list adapter used for updating the Devices
 *  List view.
 * @author marc
 *
 */
public class ConnectedDeviceListAdapter extends BaseAdapter {
    private static final String TAG = "CONNECTED_DEVICE_LIST_ADAPTER";
    /** The application context */
    private Context context;
    /** The list of devices that are known to the application */
    private HashSet<DeviceConnectionInformation> devicesItems;
    /** The layout inflater. */
    private LayoutInflater layoutInflater;
    
    private BackgroundConnectionTask task;
    
    /**
     * Constructor. Requires the application context. 
     * @param context
     */
    ConnectedDeviceListAdapter(Context context){
	this.devicesItems = new HashSet<DeviceConnectionInformation>();
        
	this.context = context;
	//get the layout inflater
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    
    public void add(final DeviceConnectionInformation info){
	devicesItems.add(info);
	this.notifyDataSetChanged();
    }
    
    public void addAll(Collection<? extends DeviceConnectionInformation> collection){
	devicesItems.addAll(collection);
	this.notifyDataSetChanged();
    }
    
    public void clear(){
	devicesItems.clear();
	this.notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return  devicesItems.size();
    }

    @Override
    public Object getItem(int position) {
	Iterator<DeviceConnectionInformation> iter = devicesItems.iterator();
	DeviceConnectionInformation item = iter.next();
	for(int ii = 1; ii <= position; ii++){
	    item = iter.next();
	}
	return item;
    }

    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return 0;//(long)devicesItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check to see if the reused view is null or not, if is not null then reuse it
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.device_info_row, null);
        }

        //get the string item from the position "position" from array list to put it on the TextView
        final DeviceConnectionInformation  info = (DeviceConnectionInformation) getItem(position);
        if (info != null) {

            TextView itemName = (TextView) convertView.findViewById(R.id.device_name_text_view);
            if (itemName != null) {
                //set the item name on the TextView
                itemName.setText(info.getHost());
            }
            
            TextView macAddressView = (TextView) convertView.findViewById(R.id.device_mac_text_view);
            if (macAddressView != null) {
                //set the item name on the TextView
        	macAddressView.setText(info.getMacAddress());
            }
            final ToggleButton deviceConnectionToggle = (ToggleButton)convertView.findViewById(R.id.device_connection_toggle);
            deviceConnectionToggle.setOnClickListener(new View.OnClickListener() {
                private ToggleButton toggle = deviceConnectionToggle;
                private DeviceConnectionInformation connectionInfo = info;
    	    @Override
    	    public void onClick(View v) {
    		// Toggle Orientation Fix
    		if(toggle.isChecked()){
    		    Log.i(TAG,String.format("Starting background task to connect to %s:%d", connectionInfo.getHost(), connectionInfo.getPort()));
    		    task = new BackgroundConnectionTask(context);
    		    task.setHost(connectionInfo.getHost());
    		    task.setPort(connectionInfo.getPort());
    		    task.execute("");
    		}
    		else{
    		    task.cancel(true);
    		    ConnectionManager.INSTANCE.closeAll();
    		}
    	    }
    	});
        }

        //this method must return the view corresponding to the data at the specified position.
        return convertView;
    }

}
