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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.tcpclient.R;
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

	    final ToggleButton deviceLogToggle = (ToggleButton) convertView
		    .findViewById(R.id.device_log_data_toggle);
	    deviceLogToggle.setOnClickListener(new ToggleLogsOnClickListener(
		    device));
	    // deviceLogToggle.setEnabled(isConnected);
	}

	// this method must return the view corresponding to the data at the
	// specified position.
	return convertView;
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
}
