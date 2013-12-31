// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.tcpclient.R;
import com.lp.io.SimpleDeviceMessage;
import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.DeviceCommandSchedule;
import com.tacuna.common.devices.DeviceInterface;
import com.tacuna.common.devices.scpi.Command;

/**
 * This is the list adapter used for updating the Devices List view.
 * 
 * @author marc
 * 
 */
public class ConnectedDeviceListAdapter extends BaseAdapter {
    private static final String TAG = "CONNECTED_DEVICE_LIST_ADAPTER";
    /** The application context */
    private final Context context;
    /** The list of devices that are known to the application */
    private final HashSet<DeviceInterface> devicesItems;
    /** The layout inflater. */
    private final LayoutInflater layoutInflater;

    private BackgroundConnectionTask task;

    /**
     * Constructor. Requires the application context.
     * 
     * @param context
     */
    ConnectedDeviceListAdapter(Context context) {
	this.devicesItems = new HashSet<DeviceInterface>();

	this.context = context;
	// get the layout inflater
	this.layoutInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(final DeviceInterface info) {
	devicesItems.add(info);
	this.notifyDataSetChanged();
    }

    public void addAll(Collection<? extends DeviceInterface> collection) {
	devicesItems.addAll(collection);
	this.notifyDataSetChanged();
    }

    public void clear() {
	devicesItems.clear();
	this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return devicesItems.size();
    }

    @Override
    public Object getItem(int position) {
	Iterator<DeviceInterface> iter = devicesItems.iterator();
	DeviceInterface item = iter.next();
	for (int ii = 1; ii <= position; ii++) {
	    item = iter.next();
	}
	return item;
    }

    @Override
    public long getItemId(int position) {
	return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	// check to see if the reused view is null or not, if is not null then
	// reuse it
	if (convertView == null) {
	    convertView = layoutInflater
		    .inflate(R.layout.device_info_row, null);
	}

	// get the string item from the position "position" from array list to
	// put it on the TextView
	final DeviceInterface device = (DeviceInterface) getItem(position);
	if (device != null) {
	    TextView itemDeviceName = (TextView) convertView
		    .findViewById(R.id.device_name_text_view);
	    if (itemDeviceName != null) {
		itemDeviceName.setText(device.getDeviceName());
	    }
	    TextView deviceTypeName = (TextView) convertView
		    .findViewById(R.id.device_type_name_text_view);
	    if (deviceTypeName != null) {
		deviceTypeName.setText(device.getDeviceType());
	    }
	    TextView itemHostName = (TextView) convertView
		    .findViewById(R.id.device_host_name_text_view);
	    if (itemHostName != null) {
		// set the item name on the TextView
		itemHostName.setText(device.getNetworkAddress().getHostName());
	    }

	    TextView macAddressView = (TextView) convertView
		    .findViewById(R.id.device_mac_text_view);
	    if (macAddressView != null) {
		// set the item name on the TextView
		macAddressView.setText(device.getMacAddress());
	    }
	    final ToggleButton deviceConnectionToggle = (ToggleButton) convertView
		    .findViewById(R.id.device_connection_toggle);

	    boolean isConnected = (device.getConnection() == null) ? false
		    : device.getConnection().isConnected();
	    deviceConnectionToggle.setChecked(isConnected);

	    deviceConnectionToggle.setOnClickListener(new ToggleConnection(
		    device, deviceConnectionToggle));

	    final ToggleButton deviceLogToggle = (ToggleButton) convertView
		    .findViewById(R.id.device_log_data_toggle);
	    deviceLogToggle.setOnClickListener(new ToggleLogsOnClickListener(
		    device));
	    deviceLogToggle.setEnabled(isConnected);
	    // if (isConnected) {
	    // addInputChannels(convertView, device);
	    // }
	}

	// this method must return the view corresponding to the data at the
	// specified position.
	return convertView;
    }

    protected void addInputChannels(View convertView, DeviceInterface device) {
	int NUMBER_OF_AI_CHANNELS = device.getNumberOfAnalogInChannels();
	TableLayout table = (TableLayout) convertView
		.findViewById(R.id.channelTable);
	table.removeAllViews();
	for (int channel = 0; channel < NUMBER_OF_AI_CHANNELS; channel++) {
	    TableRow tr = new TableRow(context);
	    // tr.setId(channel + 100);
	    // tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT));

	    // Channel label:
	    TextView label = new TextView(context);
	    // label.setId(channel + 200);
	    label.setText("AI" + channel);
	    label.setPadding(5, 0, 5, 5);
	    // label.setLayoutParams(new
	    // LayoutParams(LayoutParams.WRAP_CONTENT));
	    tr.addView(label);

	    TextView measuredValue = new TextView(context);
	    // measuredValue.setId(channel + 300);
	    measuredValue.setText("+0.0000");
	    measuredValue.setTextSize(20);
	    measuredValue.setTextColor(Color.BLACK);
	    measuredValue.setPadding(10, 5, 5, 5);
	    tr.addView(measuredValue);

	    Button measureBtn = new Button(context);
	    // measureBtn.setId(channel + 400);

	    measureBtn.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
	    measureBtn.setText("Measure");
	    measureBtn.setOnClickListener(new MeasureValue(measuredValue,
		    new Command("MEASure:EXT:ADC?", channel)));
	    tr.addView(measureBtn);

	    ToggleButton toggleAm = new ToggleButton(context);
	    // toggleAm.setId(channel + 440);
	    // toggleAm.setGravity(Gravity.CENTER_HORIZONTAL
	    // | Gravity.FILL_VERTICAL);
	    toggleAm.setText("Off");
	    toggleAm.setTextOn("On");
	    toggleAm.setTextOff("Off");
	    toggleAm.setOnClickListener(new AutoMeasureValue(measureBtn,
		    toggleAm, new Command("MEASure:EXT:ADC?", channel), device));
	    tr.addView(toggleAm);

	    table.addView(tr);
	}

	int NUMBER_OF_DI_CHANNELS = device.getNumberOfDigitalInChannels();
	for (int channel = 0; channel <= NUMBER_OF_DI_CHANNELS; channel++) {
	    TableRow tr = new TableRow(context);
	    tr.setId(channel + 500);
	    // tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT));

	    // Channel label:
	    TextView label = new TextView(context);
	    label.setId(channel + 600);
	    label.setText("DI" + channel);
	    label.setPadding(5, 0, 5, 5);
	    // label.setLayoutParams(new
	    // LayoutParams(LayoutParams.WRAP_CONTENT));
	    tr.addView(label);

	    TextView measuredValue = new TextView(context);
	    measuredValue.setId(channel + 700);
	    measuredValue.setText("0");
	    measuredValue.setTextSize(20);
	    measuredValue.setTextColor(Color.BLACK);
	    measuredValue.setPadding(10, 5, 5, 5);
	    // measuredValue.setLayoutParams(new
	    // LayoutParams(LayoutParams.WRAP_CONTENT));
	    tr.addView(measuredValue);

	    Button measureBtn = new Button(context);
	    measureBtn.setId(channel + 800);

	    // measureBtn.setLayoutParams(new
	    // LayoutParams(LayoutParams.WRAP_CONTENT));
	    measureBtn.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
	    measureBtn.setText("Measure");
	    measureBtn.setOnClickListener(new MeasureValue(measuredValue,
		    new Command("INPut:PORt:STATe?", channel)));
	    tr.addView(measureBtn);

	    table.addView(tr);
	}
    }

    protected class AutoMeasureValue implements View.OnClickListener {
	/**
	 * @param measureButton
	 * @param button
	 * @param command
	 */
	public AutoMeasureValue(Button measureButton, ToggleButton button,
		Command command, DeviceInterface device) {
	    super();
	    this.measureButton = measureButton;
	    this.button = button;
	    this.command = command;
	    this.device = device;
	}

	private final Button measureButton;
	private final ToggleButton button;
	private final Command command;
	private final DeviceInterface device;

	@Override
	public void onClick(View v) {
	    boolean selected = button.isChecked();
	    // DeviceInterface device = ConnectionManagerAndriod.INSTANCE
	    // .getDevice();
	    DeviceCommandSchedule schedule = ConnectionManager.INSTANCE
		    .getScheduleByDeviceName(device.getDeviceName());
	    if (selected) {
		measureButton.setEnabled(false);
		schedule.schedule(command, 1000);
	    } else {
		measureButton.setEnabled(true);
		schedule.remove(command);
	    }
	}
    }

    /**
     * OnClick Listener used to handle button clicks that make a SCPI command
     * and set the result to a TextView.
     * 
     * @author marc
     * 
     */
    protected class MeasureValue implements View.OnClickListener {

	/**
	 * Measure SCPI command extends the background SCPI command class and is
	 * used to update the view with the results from executing the SCPI
	 * command.
	 * 
	 * @author marc
	 * 
	 */
	protected class MeasureScpiCommand extends BackgroundScpiCommand {
	    private final TextView text;

	    public MeasureScpiCommand(TextView text, View enableOnComplete) {
		super();
		this.text = text;
	    }

	    @Override
	    /**
	     * Overriden to set the text of the TextView with the response from
	     *   the SCPI command.
	     */
	    protected void onPostExecute(SimpleDeviceMessage result) {
		super.onPostExecute(result);
		text.setText(result.getData());
	    }
	}

	public MeasureValue(TextView text, Command command) {
	    super();
	    this.text = text;
	    this.command = command;
	}

	private final TextView text;
	private final Command command;
	MeasureScpiCommand async;

	@Override
	/**
	 * The onClick method has been overridden to send
	 *  a SCPI command to the device.
	 * @param view
	 */
	public void onClick(View v) {
	    if (isNotRunning()) {
		async = new MeasureScpiCommand(text, v);
		async.execute(command);
	    }
	}

	protected Boolean isNotRunning() {
	    return async == null
		    || async.getStatus() != AsyncTask.Status.RUNNING;
	}
    }

    public class ToggleConnection implements View.OnClickListener,
	    PropertyChangeListener {
	private final ToggleButton toggle;
	private final DeviceInterface device;

	ToggleConnection(DeviceInterface info,
		ToggleButton deviceConnectionToggle) {
	    this.device = info;
	    this.toggle = deviceConnectionToggle;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	    // TODO Auto-generated method stub

	}

	@Override
	/**
	 * The onClick method has been overridden to toggle
	 * the connection on and off.
	 * @param view
	 */
	public void onClick(View v) {
	    if (toggle.isChecked()) {
		Log.i(TAG, String.format(
			"Starting background task to connect to %s:%d", device
				.getNetworkAddress().getHostName(), device
				.getNetworkAddress().getPort()));
		task = new BackgroundConnectionTask(context, device);
		task.execute("");
	    } else {
		device.getConnection().close();
		task.cancel(true);
	    }
	}

    }
}
