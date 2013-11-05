// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.example.tcpclient.R;
import com.tacuna.android.plot.AnalogChannelToXYSeriesAdapter;
import com.tacuna.common.devices.AnalogInputChannel;
import com.tacuna.common.devices.ChannelInterface;
import com.tacuna.common.devices.DeviceInterface;

/**
 * An activity for all of the charting.
 * 
 * @author marc
 * 
 */
public class DataPlotActivity extends AppMenuActivity implements Runnable {
    private XYPlot plot;

    private final LinkedList<XYSeries> displayedDataSeries = new LinkedList<XYSeries>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
	    1);

    /**
     * Constant values for use with the data plot
     */
    private static final int[][] linePointColors = {
	    { Color.rgb(255, 51, 51), Color.rgb(255, 153, 153) },
	    { Color.rgb(51, 51, 255), Color.rgb(153, 153, 253) },
	    { Color.rgb(51, 255, 51), Color.rgb(153, 255, 153) },
	    { Color.rgb(255, 255, 51), Color.rgb(255, 255, 153) },
	    { Color.rgb(51, 255, 255), Color.rgb(255, 255, 153) },
	    // TODO: These need to be made into different colors.
	    { Color.rgb(255, 51, 51), Color.rgb(255, 153, 153) },
	    { Color.rgb(51, 51, 255), Color.rgb(153, 153, 253) },
	    { Color.rgb(51, 255, 51), Color.rgb(153, 255, 153) }, };

    /**
     * UI Updater that refreshes the grid.
     */
    private final UiUpdater updater = new UiUpdater(this, this);

    @Override
    /**
     * On create used to create this activity.
     */
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.plot_layout);
	onCreatePlot();

    }

    @Override
    /**
     * onResume lifecycle method used to configure the plot for any data
     *   that comes off the connected devices.
     */
    public void onResume() {
	super.onResume();
	DeviceInterface device = ConnectionManager.INSTANCE.getDevice();
	if (device == null) {
	    return;
	}
	int ii = 0;
	for (ChannelInterface channel : device.getChannels()) {
	    // TODO: Remove this if block once we have a better way to make
	    // channels
	    // active/ not active.
	    // if (ii == 1 || ii == 2 || ii == 3) {
	    Log.i("DataPlotActivity", "Adding " + channel.getName());
	    addDataSeries(plot, new AnalogChannelToXYSeriesAdapter(
		    (AnalogInputChannel) channel), ii);

	    // }
	    ii++;
	}

	executor.scheduleWithFixedDelay(updater, 1, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onPause() {
	super.onPause();

	for (XYSeries ser : displayedDataSeries) {
	    plot.removeSeries(ser);
	}

	executor.remove(updater);
    }

    @SuppressWarnings("deprecation")
    protected void onCreatePlot() {
	// get handles to our View defined in layout.xml:
	plot = (XYPlot) findViewById(R.id.chart);
	plot.setTitle("Data Plot");

	// only display whole numbers in domain labels
	plot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
	Paint bg = new Paint();
	bg.setColor(Color.BLACK);
	plot.getGraphWidget().setBackgroundPaint(bg);
	Paint plotBg = new Paint();
	plotBg.setColor(Color.rgb(0, 26, 0));
	plot.getGraphWidget().setGridBackgroundPaint(plotBg);

	plot.getBackgroundPaint().setColor(Color.BLACK);
	plot.getBorderPaint().setStrokeWidth(1);
	Paint gridPaint = new Paint();
	gridPaint.setColor(Color.rgb(0, 102, 0));
	plot.getGraphWidget().setGridLinePaint(gridPaint);
	// getInstance and position datasets:

	plot.setDomainStepMode(XYStepMode.SUBDIVIDE);
	plot.setDomainStepValue(10);
	// thin out domain/range tick labels so they dont overlap each other:
	plot.setTicksPerDomainLabel(2);
	plot.setTicksPerRangeLabel(2);
	plot.disableAllMarkup();
	// freeze the range boundaries:
	plot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);

	plot.setRangeLabel("V");
	plot.setDomainLabel("ms");

    }

    /**
     * Adds a DataSeries to the XY Plot.
     * 
     * @param plot
     * @param dataSeries
     * @param seriesNumber
     */
    protected void addDataSeries(XYPlot plot, XYSeries dataSeries,
	    int seriesNumber) {
	@SuppressWarnings("deprecation")
	LineAndPointFormatter formatter = new LineAndPointFormatter(
		linePointColors[seriesNumber % 5][0],
		linePointColors[seriesNumber % 5][1], null);
	formatter.getLinePaint().setStyle(Paint.Style.STROKE);
	formatter.getLinePaint().setStrokeWidth(2);
	plot.addSeries(dataSeries, formatter);
	displayedDataSeries.add(dataSeries);
    }

    /**
     * The UiUpdater class is used to update the Data Plot using a timer.
     * Although timers are all that great, the data comes in faster then the UI
     * can be updated so this is ultimately more efficient.
     * 
     * @author Marc
     * 
     */
    protected class UiUpdater implements Runnable {
	private final Activity activity;
	Runnable runnable;

	/**
	 * Constructor.
	 * 
	 * @param activity
	 * @param runnable
	 */
	public UiUpdater(Activity activity, Runnable runnable) {
	    this.activity = activity;
	    this.runnable = runnable;
	}

	@Override
	public void run() {
	    activity.runOnUiThread(runnable);
	}
    }

    @Override
    /**
     * Run method used to update the DataPlotActivity.
     */
    public void run() {
	plot.setRangeBoundaries(-5, 5, BoundaryMode.FIXED);
	Date now = new Date();
	plot.setDomainBoundaries(now.getTime() - 10000, now.getTime(),
		BoundaryMode.FIXED);
	plot.redraw();

    }
}
