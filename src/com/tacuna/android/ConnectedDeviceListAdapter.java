// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.tcpclient.R;
import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.DeviceInterface;

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
    private final Set<DeviceInterface> devicesItems;
    /** The layout inflater. */
    private final LayoutInflater layoutInflater;

    private BackgroundConnectionTask task;

    /**
     * Constructor. Requires the application context.
     * 
     * @param context
     */
    ConnectedDeviceListAdapter(Context context) {
	this.context = context;
	this.devicesItems = ConnectionManager.INSTANCE.knownDevices;
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

	    // deviceLogToggle.setEnabled(isConnected);

	    //
	    // Analog sample resolution:
	    //
	    final Spinner voltageSpinner = (Spinner) convertView
		    .findViewById(R.id.voltageRangeSpinner);
	    ArrayAdapter<CharSequence> voltageAdapter = ArrayAdapter
		    .createFromResource(this.context, R.array.voltageRanges,
			    android.R.layout.simple_spinner_item);
	    voltageAdapter
		    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    voltageSpinner.setAdapter(voltageAdapter);

	    //
	    // Sample Frequency:
	    //
	    final Spinner freqSpinner = (Spinner) convertView
		    .findViewById(R.id.frequencySpinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter
		    .createFromResource(this.context,
			    R.array.sampleFrequencies,
			    android.R.layout.simple_spinner_item);
	    // Specify the layout to use when the list of choices appears
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    freqSpinner.setAdapter(adapter);
	    int frequency = device.getSampleFrequency();
	    freqSpinner.setSelection(adapter.getPosition(Integer
		    .toString(frequency)));
	    freqSpinner
		    .setOnItemSelectedListener(new SetDeviceFrequencyListener(
			    device));

	}

	// this method must return the view corresponding to the data at the
	// specified position.
	return convertView;
    }

    /**
     * Toggles the device connection.
     * 
     * @author Marc
     * 
     */
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
		if (task != null) {
		    task.cancel(true);
		}
	    }
	}
    }

    /**
     * Listener to set the device sample frequency
     * 
     * @author Marc
     * 
     */
    public class SetDeviceFrequencyListener implements OnItemSelectedListener {

	private static final int DEFAULT_SAMPLE_FREQUENCY = 100;
	private final DeviceInterface device;

	public SetDeviceFrequencyListener(DeviceInterface device) {
	    this.device = device;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
		long id) {
	    int sampleFrequency = Integer.parseInt(parent
		    .getItemAtPosition(pos).toString());
	    device.setSampleFrequency(sampleFrequency);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	    // TODO: Make this a resource
	    device.setSampleFrequency(DEFAULT_SAMPLE_FREQUENCY);
	}
    }
}
