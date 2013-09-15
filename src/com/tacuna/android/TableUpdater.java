// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import android.app.Activity;
import android.widget.TextView;

import com.lp.io.Message;
import com.lp.io.MessageConsumer;
import com.lp.io.SimpleDeviceMessage;

public class TableUpdater implements Runnable, MessageConsumer {

    public static final int NUMBER_OF_CHANNELS=4;
    private Activity activity;
    private TextView timeView;
    private TextView channelViews[] = new TextView[NUMBER_OF_CHANNELS];
    private double values[] = new double[NUMBER_OF_CHANNELS];
    private long currentTime;
    
    public TableUpdater(Activity activity, TextView time, TextView c0, TextView c1, TextView c2, TextView c3){
	this.activity = activity;
	this.timeView = time;
	this.channelViews[0] = c0;
	this.channelViews[1] = c1;
	this.channelViews[2] = c2;
	this.channelViews[3] = c3;
    }
    
    @Override
    public void onMessage(Message message) {
	SimpleDeviceMessage msg = (SimpleDeviceMessage)message;
	currentTime = msg.getDeviceTimestamp();
	int channel = msg.getChannel();
	if(NUMBER_OF_CHANNELS > channel){
	    values[channel] = msg.getValue();
	}
	if(channel%NUMBER_OF_CHANNELS == 0){
	    activity.runOnUiThread(this);
	}
    }

    @Override
    public void run() {
	timeView.setText(Long.toString(currentTime));
	for(int ii = 0; ii < NUMBER_OF_CHANNELS; ii++){
	    channelViews[ii].setText(Double.toString(values[ii]));
	}
    }

}
