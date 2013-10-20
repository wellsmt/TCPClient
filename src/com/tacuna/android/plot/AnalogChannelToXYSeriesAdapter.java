package com.tacuna.android.plot;

import com.androidplot.series.XYSeries;
import com.tacuna.common.devices.AnalogInputChannel;

/**
 * An adapter class to enable the Analog Channel data to be used by the XYPlot.
 * 
 * @author Marc
 * 
 */
public class AnalogChannelToXYSeriesAdapter implements XYSeries {

    private final AnalogInputChannel channel;

    /**
     * @param channel
     */
    public AnalogChannelToXYSeriesAdapter(AnalogInputChannel channel) {
	super();
	this.channel = channel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.androidplot.series.Series#getTitle()
     */
    @Override
    public String getTitle() {
	return channel.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.androidplot.series.Series#size()
     */
    @Override
    public int size() {
	return channel.getNumberOfSamples();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.androidplot.series.XYSeries#getX(int)
     */
    @Override
    public Number getX(int ii) {
	Number value = channel.getIndex(ii).time;
	return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.androidplot.series.XYSeries#getY(int)
     */
    @Override
    public Number getY(int ii) {
	Number value = channel.getIndex(ii).value;
	return value;
    }

}
