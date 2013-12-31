// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.example.tcpclient.R;
import com.tacuna.android.plot.AnalogChannelToXYSeriesAdapter;
import com.tacuna.common.components.ConnectionManager;
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

	Button addChannelBtn = (Button) findViewById(R.id.addChannelBtn);
	addChannelBtn.setOnClickListener(new AddChannelClickListener());

	Button removeChannelBtn = (Button) findViewById(R.id.removeChannelBtn);
	removeChannelBtn
		.setOnClickListener(new ChannelUtilities.ChannelRemoveSelectedClickListener());
    }

    @Override
    /**
     * onResume lifecycle method used to configure the plot for any data
     *   that comes off the connected devices.
     */
    public void onResume() {
	super.onResume();
	DeviceInterface device = ConnectionManager.INSTANCE.getLastDevice();
	if (device == null) {
	    return;
	}
	int ii = 0;

	TableLayout activeChannels = (TableLayout) findViewById(R.id.activeChannels);
	activeChannels.removeAllViews();

	ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	for (ChannelInterface channel : activeChannelsList) {
	    Log.i("DataPlotActivity", "Adding " + channel.getName());
	    addDataSeries(plot, new AnalogChannelToXYSeriesAdapter(
		    (AnalogInputChannel) channel), ii);
	    addChannel(activeChannels, channel);
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

    protected void addChannel(TableLayout table, ChannelInterface channel) {
	table.addView(ChannelUtilities.getChannelRow(getApplicationContext(),
		channel));
    }

    protected void redrawTable() {
	TableLayout activeChannels = (TableLayout) findViewById(R.id.activeChannels);
	activeChannels.removeAllViews();
	ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	for (ChannelInterface channel : activeChannelsList) {
	    addChannel(activeChannels, channel);
	}
    }

    class TimeFormat extends Format {

	private final SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
	/**
	 * 
	 */
	private static final long serialVersionUID = 3268501563200906015L;

	@Override
	public StringBuffer format(Object object, StringBuffer buffer,
		FieldPosition field) {
	    double timeValue = (Double) object;
	    Date time = new Date((long) timeValue);

	    return formatter.format(time, buffer, field);
	}

	@Override
	public Object parseObject(String string, ParsePosition position) {
	    // TODO Auto-generated method stub
	    return null;
	}

    }

    @SuppressWarnings("deprecation")
    protected void onCreatePlot() {
	// get handles to our View defined in layout.xml:
	plot = (XYPlot) findViewById(R.id.chart);
	plot.setPadding(0, 0, 0, 0);
	plot.setTitle("");
	plot.setTitleWidget(null);

	// only display whole numbers in domain labels
	plot.getGraphWidget().setDomainValueFormat(new TimeFormat());
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
	plot.setDomainStepValue(11);
	// thin out domain/range tick labels so they dont overlap each other:
	plot.setTicksPerDomainLabel(2);
	plot.setTicksPerRangeLabel(2);
	plot.disableAllMarkup();
	// freeze the range boundaries:
	plot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);

	plot.setRangeLabel("V");
	plot.setDomainLabel("Time");

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
	redrawTable();

    }

    @Override
    protected void onActivityResult(int requestCode, int resulCode, Intent data) {

    }

    protected class AddChannelClickListener implements View.OnClickListener {

	ArrayList<Integer> selectedChannels = new ArrayList<Integer>();
	ArrayList<ChannelInterface> allChannels = new ArrayList<ChannelInterface>();

	public AddChannelClickListener() {

	}

	@Override
	public void onClick(View view) {
	    startActivityForResult(new Intent(DataPlotActivity.this,
		    ChannelSelectActivity.class),
		    ChannelSelectActivity.PICK_CHANNELS);
	}
    }
}
