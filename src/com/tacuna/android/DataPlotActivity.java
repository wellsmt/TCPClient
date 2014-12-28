// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import static com.tacuna.android.ApplicationUtilities.toast;

import java.io.File;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.example.tcpclient.R;
import com.tacuna.android.data.DatabaseWriteThread;
import com.tacuna.android.data.MeasurementsDataSource;
import com.tacuna.android.intents.IntentFactory;
import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.components.MovingAverage;
import com.tacuna.common.devices.AD7195W;
import com.tacuna.common.devices.ChannelInterface;

/**
 * An activity for all of the charting.
 * 
 * @author marc
 * 
 */
public class DataPlotActivity extends AppMenuActivity implements Runnable {

    private XYPlot plot;

    private DatabaseWriteThread dbThread;

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
    public void onDestroy() {
	super.onDestroy();
    }

    @Override
    /**
     * On create used to create this activity.
     */
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.plot_layout);
	// onCreatePlot();

	Button addChannelBtn = (Button) findViewById(R.id.addChannelBtn);
	addChannelBtn.setOnClickListener(new AddChannelClickListener());

	Button removeChannelBtn = (Button) findViewById(R.id.removeChannelBtn);
	removeChannelBtn
		.setOnClickListener(new ChannelUtilities.ChannelRemoveSelectedClickListener());

	Button startChannel = (Button) findViewById(R.id.startBtn);
	startChannel.setOnClickListener(new StartStream());

	Button stopChannel = (Button) findViewById(R.id.stopBtn);
	stopChannel.setOnClickListener(new StopStream());

	Button emailDataButton = (Button) findViewById(R.id.emailDataBtn);
	emailDataButton.setOnClickListener(new SendEmailOnClickListener());

	Button uploadDataButton = (Button) findViewById(R.id.uploadToGoogleDriveBtn);
	uploadDataButton
		.setOnClickListener(new UploadToGoogleDriveOnClickListener());
    }

    @Override
    /**
     * onResume lifecycle method used to configure the plot for any data
     *   that comes off the connected devices.
     */
    public void onResume() {
	super.onResume();

	int ii = 0;

	// TableLayout activeChannels = (TableLayout)
	// findViewById(R.id.activeChannels);
	// activeChannels.removeAllViews();

	ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	for (ChannelInterface channel : activeChannelsList) {
	    Log.i("DataPlotActivity", "Adding " + channel.getName());
	    // addDataSeries(plot, new AnalogChannelToXYSeriesAdapter(
	    // (AnalogInputChannel) channel), ii);
	    // addChannel(activeChannels, channel, ii);
	    ii++;
	}

	executor.scheduleWithFixedDelay(updater, 1, 250, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onPause() {
	super.onPause();

	// for (XYSeries ser : displayedDataSeries) {
	// plot.removeSeries(ser);
	// }

	executor.remove(updater);
    }

    protected void addChannel(TableLayout table, ChannelInterface channel,
	    int index) {
	table.addView(ChannelUtilities.getChannelRow(getApplicationContext(),
		channel, linePointColors[index % 5][0],
		linePointColors[index % 5][1]));
    }

    private long lastTime = 0;
    private long lastNumSamples = 0;
    private final MovingAverage freqAvg = new MovingAverage(10);

    @SuppressLint("SimpleDateFormat")
    /**
     * Time formatter used for the Android plot Time axis. 
     * @author Marc
     *
     */
    class TimeFormat extends Format {

	/**
	 * Formatter for converting the time in ms to the time in
	 * minutes/seconds
	 */
	private final SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
	/**
	 * Required for serialization warning.
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
	try {
	    // Get the min and max value to display using the active channels
	    ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	    float max = Float.MIN_VALUE;
	    float min = Float.MAX_VALUE;

	    for (ChannelInterface channel : activeChannelsList) {
		float channelMax = channel.getMaximum();
		max = (channelMax > max) ? channelMax : max;
		float channelMin = channel.getMinimum();
		min = (channelMin < min) ? channelMin : min;
	    }

	    if (activeChannelsList.size() > 0) {
		AD7195W device = (AD7195W) activeChannelsList.get(0)
			.getDevice();
		TextView samples = (TextView) findViewById(R.id.numberOfSamples);
		samples.setText(String.format("Number of samples: %d\t",
			device.getTotalMessageCount()));

		TextView sampleFreq = (TextView) findViewById(R.id.sampleFreq);
		long now = new Date().getTime();
		if (lastTime > 0) {
		    float deltaTime = (now - lastTime) / 1000.0f;
		    float freq = ((device.getTotalMessageCount() - lastNumSamples) / deltaTime);
		    freqAvg.add(freq);
		    sampleFreq
			    .setText(String.format(
				    "Sample Frequency (Hz): %f\t",
				    freqAvg.getAverage()));
		}
		lastTime = now;
		lastNumSamples = device.getTotalMessageCount();
	    }

	} catch (Exception err) {
	    Log.e("DATA_PLOT", "Error during redraw", err);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resulCode, Intent data) {

    }

    /**
     * OnClickListener for the AddChannel button.
     * 
     * @author Marc
     * 
     */
    protected class AddChannelClickListener implements View.OnClickListener {

	ArrayList<Integer> selectedChannels = new ArrayList<Integer>();
	ArrayList<ChannelInterface> allChannels = new ArrayList<ChannelInterface>();

	public AddChannelClickListener() {

	}

	@Override
	public void onClick(View view) {
	    startActivityForResult(new Intent(DataPlotActivity.this,
		    ChannelSelectActivity.class),
		    ChannelSelectActivity.PICK_ANALOG_CHANNELS);
	}
    }

    /**
     * OnClickListener for the AddChannel button.
     * 
     * @author Marc
     * 
     */
    protected class ConfigureChannelClickListener implements
	    View.OnClickListener {

	ArrayList<Integer> selectedChannels = new ArrayList<Integer>();
	ArrayList<ChannelInterface> allChannels = new ArrayList<ChannelInterface>();

	public ConfigureChannelClickListener() {

	}

	@Override
	public void onClick(View view) {
	    startActivityForResult(new Intent(DataPlotActivity.this,
		    ChannelConfigureActivity.class),
		    ChannelConfigureActivity.CONFIG_ANALOG_IN_CHANNEL);
	}
    }

    /**
     * OnClickListener that starts the streaming of data.
     * 
     * @author Marc
     * 
     */
    protected class StartStream implements View.OnClickListener {

	public StartStream() {

	}

	@Override
	public void onClick(View v) {
	    Button emailDataButton = (Button) findViewById(R.id.emailDataBtn);
	    emailDataButton.setEnabled(false);

	    Button uploadDataButton = (Button) findViewById(R.id.uploadToGoogleDriveBtn);
	    uploadDataButton.setEnabled(false);

	    ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	    if (activeChannelsList.size() > 0) {
		AD7195W dev = (AD7195W) activeChannelsList.get(0).getDevice();
		dev.startStreaming();
	    }
	    dbThread = new DatabaseWriteThread(getApplicationContext(), true);

	}

    }

    protected class StopStream implements View.OnClickListener {

	public StopStream() {

	}

	@Override
	public void onClick(View v) {
	    ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	    if (activeChannelsList.size() > 0) {
		AD7195W dev = (AD7195W) activeChannelsList.get(0).getDevice();
		dev.stopStreaming();
	    }
	    if (dbThread != null) {
		dbThread.complete();
	    }

	    Button emailDataButton = (Button) findViewById(R.id.emailDataBtn);
	    emailDataButton.setEnabled(true);

	    Button uploadDataButton = (Button) findViewById(R.id.uploadToGoogleDriveBtn);
	    uploadDataButton.setEnabled(true);
	}

    }

    protected File convertDatabaseToCsv() {
	MeasurementsDataSource ds = new MeasurementsDataSource(this);
	ds.open();
	File csvFile = ds.writeToCsv();
	ds.close();
	return csvFile;
    }

    protected class SendEmailOnClickListener implements View.OnClickListener {
	@Override
	public void onClick(View v) {
	    sendData();
	}
    }

    private final GoogleDriveHelper gdriveHelper = new GoogleDriveHelper(this);

    protected class UploadToGoogleDriveOnClickListener implements
	    View.OnClickListener {
	@Override
	public void onClick(View v) {
	    gdriveHelper.uploadFile(convertDatabaseToCsv());
	}
    }

    protected void sendData() {
	Intent emailIntent = IntentFactory
		.getSendEmailIntent(convertDatabaseToCsv());
	try {
	    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	} catch (android.content.ActivityNotFoundException ex) {
	    toast(this, "No email client installed.");
	}
    }
}
