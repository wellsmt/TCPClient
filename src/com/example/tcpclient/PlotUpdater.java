// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import android.app.Activity;
import android.util.Log;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.lp.io.Message;
import com.lp.io.MessageConsumer;
import com.lp.io.SimpleDeviceMessage;

/**
 * The plot updater is a consumer of devices messages from the
 *  IO data interpretor. Messages received by this class are added
 *  to the corresponding plot series and the UI is refreshed.
 *  This class also implements runnable to ensure that the 
 *  UI updates occur in the activities UI thread.
 * @author marc
 */
public class PlotUpdater implements MessageConsumer, Runnable{
	/** Sensor series. */
	private SensorDataSeries[] series;
	/** The activity that the plot is in. */
	private Activity activity;
	/** The XYPloy used to plot data. */
	XYPlot dataPlot;
	
	/**
	 * Constructor. 
	 * @param series
	 * @param activity
	 * @param dataPlot
	 */
	PlotUpdater(SensorDataSeries[] series, Activity activity, XYPlot dataPlot){
		this.series = series;
		this.activity = activity;
		this.dataPlot = dataPlot;

	}
	
	@Override
	public void onMessage(Message msg) {
		SimpleDeviceMessage deviceMsg = (SimpleDeviceMessage)msg;
		int channel = deviceMsg.getChannel();
		if(channel < series.length){
			series[channel].addLast(deviceMsg.getTimestamp(), deviceMsg.getValue());
		}
		else {
			Log.e("DATA SERIES", String.format("Invalid channel received. Channel %d is greater than number of expected channels, %d.", channel, series.length));
		}
		// Run the UI update
		activity.runOnUiThread(this);
	}
	
	@Override
	public void run(){
		// The run method is used to provide thread safe updates to the UI.
    	double range = Math.max(series[0].getRange(), series[1].getRange());
    	range = Math.max(series[2].getRange(), range);
    	double rangeAdder = 0.05*range;
    	if(rangeAdder < 0.0001){
    		rangeAdder = 0.1;
    	}
    	double max = Math.max(series[0].getMaximum().doubleValue(), series[1].getMaximum().doubleValue());
    	max = Math.max(series[2].getMaximum().doubleValue(), max);
    	
    	double min = Math.min(series[0].getMinimum().doubleValue(), series[1].getMinimum().doubleValue());
    	min = Math.min(series[2].getMinimum().doubleValue(), min);
    	
    	dataPlot.setRangeBoundaries(min - rangeAdder, max + rangeAdder, BoundaryMode.FIXED);
    	dataPlot.redraw();
	}
}
