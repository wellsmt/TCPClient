package com.example.tcpclient;

import android.location.Address;

import com.androidplot.xy.SimpleXYSeries;
/**
 * Sensor Data series used to capture dynamic sensor
 *  data.
 * @author marc
 *
 */
public class SensorDataSeries extends SimpleXYSeries {

	/**
	 * Minimum series value. Default set to positive infinity
	 *  to ensure that when data is added the correct data min is taken.
	 *  (i.e. everything is less than infinity, so a new value will be set
	 *    as soon as data is added)
	 */
	private Number minimum = Double.POSITIVE_INFINITY;
	/**
	 * Maximum series value. Default set to positive infinity
	 *  to ensure that when data is added the correct max is taken.
	 *  (i.e. everything is less than infinity)
	 */
	private Number maximum = Double.NEGATIVE_INFINITY;
	
	private int maximumNumbeOfDataPoints = 20;
	
	/**
	 * Sensor Data constructor. Takes a string which is displayed as the
	 *  series title
	 * @param title
	 */
	public SensorDataSeries(String title, int maximumNumbeOfDataPoints) {
		super(title);
		this.maximumNumbeOfDataPoints = maximumNumbeOfDataPoints;
	}
	
	@Override
	public void addLast(Number time, Number value){
		// Set the min if the value is less than the current
		//  min
		if(minimum.doubleValue() > value.doubleValue()){
			minimum = value;
		}
		// set the max is the value is greater than the current
		// max
		if(maximum.doubleValue() < value.doubleValue()){
			maximum = value;
		}
		
		super.addLast(time, value);
		
		if(size() > maximumNumbeOfDataPoints){
			removeFirst();
		}
	}

	/**
	 * Returns the minimum value of this data series.
	 * @return
	 */
	public Number getMinimum() {
		return minimum;
	}

	/**
	 * Returns the maximum value of this data series.
	 * @return
	 */
	public Number getMaximum() {
		return maximum;
	}

	/**
	 * Returns the datas range
	 */
	public double getRange(){
		return maximum.doubleValue() - minimum.doubleValue();
	}
}