// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConnectedDeviceListAdapter extends BaseAdapter {
    private List<DeviceConnectionInformation> devicesItems;
    
    private LayoutInflater mLayoutInflater;
    
    ConnectedDeviceListAdapter(Context context){
	devicesItems = new ArrayList<DeviceConnectionInformation>();
        //get the layout inflater
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void add(final DeviceConnectionInformation info){
	devicesItems.add(info);
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
	// TODO Auto-generated method stub
	return devicesItems.get(position);
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
            convertView = mLayoutInflater.inflate(R.layout.device_info_row, null);
        }

        //get the string item from the position "position" from array list to put it on the TextView
        DeviceConnectionInformation  info = devicesItems.get(position);
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
        }

        //this method must return the view corresponding to the data at the specified position.
        return convertView;
    }

}
