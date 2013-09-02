// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.text.DecimalFormat;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

/**
 * An activity for all of the charting.
 * 
 * @author marc
 * 
 */
public class DataPlotActivity extends AppMenuActivity {
    private XYPlot plot;
    private final SensorDataSeries[] dataSeries = new SensorDataSeries[4];
    private PlotUpdater messageConsumer;
    private ScaleData scaleData;

    /**
     * Constant values for use with the data plot
     */
    private static final int[][] linePointColors = {
	    { Color.rgb(255, 51, 51), Color.rgb(255, 153, 153) },
	    { Color.rgb(51, 51, 255), Color.rgb(153, 153, 253) },
	    { Color.rgb(51, 255, 51), Color.rgb(153, 255, 153) },
	    { Color.rgb(255, 255, 51), Color.rgb(255, 255, 153) },
	    { Color.rgb(51, 255, 255), Color.rgb(255, 255, 153) } };

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.plot_layout);
	onCreatePlot();

	// Create and register the device message consumer.
	messageConsumer = new PlotUpdater(dataSeries, this, plot);
	scaleData = new ScaleData(this);
	ConnectionManager.INSTANCE.getConnectionMessageProducer()
		.registerObserver(messageConsumer);
    }

    public float getScaleData(int channel) {
	return scaleData.getScale(channel);
    }

    @SuppressWarnings("deprecation")
    protected void onCreatePlot() {
	// get handles to our View defined in layout.xml:
	plot = (XYPlot) findViewById(R.id.chart);
	plot.setTitle("Data Plot");
	dataSeries[0] = new SensorDataSeries("Channel 0", 30);
	dataSeries[1] = new SensorDataSeries("Channel 1", 30);
	dataSeries[2] = new SensorDataSeries("Channel 2", 30);
	dataSeries[3] = new SensorDataSeries("Channel 3", 30);

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

	// create a series using a formatter with some transparency applied:
	for (int ii = 0; ii != dataSeries.length; ii++) {
	    addDataSeries(plot, dataSeries[ii], ii);
	}
	// dynamicPlot.setGridPadding(5, 0, 5, 0);
	// hook up the plotUpdater to the data model:

	plot.setDomainStepMode(XYStepMode.SUBDIVIDE);
	plot.setDomainStepValue(10);
	// thin out domain/range tick labels so they dont overlap each other:
	plot.setTicksPerDomainLabel(2);
	plot.setTicksPerRangeLabel(2);
	plot.disableAllMarkup();
	// freeze the range boundaries:
	plot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);

    }

    protected void addDataSeries(XYPlot plot, SensorDataSeries dataSeries,
	    int seriesNumber) {
	@SuppressWarnings("deprecation")
	LineAndPointFormatter formatter = new LineAndPointFormatter(
		linePointColors[seriesNumber][0],
		linePointColors[seriesNumber][1], null);
	formatter.getLinePaint().setStyle(Paint.Style.STROKE);
	formatter.getLinePaint().setStrokeWidth(2);
	plot.addSeries(dataSeries, formatter);
    }
}
