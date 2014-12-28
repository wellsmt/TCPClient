// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import static com.tacuna.android.ApplicationUtilities.getBroadcastAddress;
import static com.tacuna.android.ApplicationUtilities.toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.tcpclient.R;
import com.lp.io.DeviceBroadcastMessage;
import com.lp.io.MessageConsumer;
import com.lp.io.UdpBroadcast;
import com.lp.io.messages.Message;
import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.DeviceFactory;
import com.tacuna.common.devices.DeviceInterface;

/**
 * This activity is used to manage device connections.
 * 
 * @author marc
 * 
 */
public class DeviceConnectionsActivity extends AppMenuActivity implements
	PropertyChangeListener {

    private final static String TAG = "DEVICE_CONNECTIONS_ACTIVITY";

    private Context context;
    private ListView deviceList;

    private ConnectedDeviceListAdapter listAdapter;

    private BackgroundDiscovery discovery;
    private BackgroundSend send;

    private final String HELLO = "Discovery: Who is out there?\r\n";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.devices_layout);
	context = getApplicationContext();

	try {
	    ConnectionManager.INSTANCE
		    .setBroadcastAddress(getBroadcastAddress(context));
	} catch (IOException e) {
	    Log.e(TAG, "Unable to get network broadcast address.", e);
	}

	listAdapter = new ConnectedDeviceListAdapter(context);

	deviceList = (ListView) findViewById(R.id.device_list);
	deviceList.setAdapter(listAdapter);

	discovery = new BackgroundDiscovery(this);
    }

    @Override
    public void onResume() {
	super.onResume();
	ConnectionManager.INSTANCE.addChangeListener(this);

	send = new BackgroundSend();
	send.execute(HELLO);
    }

    @Override
    public void onPause() {
	super.onPause();
	ConnectionManager.INSTANCE.removeChangeListener(this);
    }

    /**
     * Called by the close all button.
     * 
     * @param view
     */
    public void closeAllClickHandler(View view) {
	ConnectionManager.INSTANCE.closeAll();
	listAdapter.clear();
	toast(context, "All connections have been closed.");
    }

    /**
     * The handler for clicks on the refresh button.
     * 
     * @param view
     */
    public void refreshClickHandler(View view) {
	send.cancel(true);
	send = new BackgroundSend();
	send.execute(HELLO);
    }

    /**
     * Async Send task for sending out the UDP broadcasts. Note that on certian
     * andriod devices networks calls must be made in a background thread.
     * 
     * @author marc
     * 
     */
    public class BackgroundSend extends AsyncTask<String, String, String> {
	private static final String TAG = "BACKGROUND_SEND_TASK";

	@Override
	protected String doInBackground(String... params) {
	    try {
		UdpBroadcast broadcaster = ConnectionManager.INSTANCE
			.getBroadcaster();
		Log.i(TAG, "Sending " + params[0]
			+ " to the UDP broadcast socket.");
		broadcaster.send(params[0]);
		return null;
	    } catch (IOException err) {
		Log.e(TAG, "Error connection to UDP broadcast socket.", err);
	    }
	    return null;
	}
    }

    /**
     * Background discovery task.
     */
    public class BackgroundDiscovery implements MessageConsumer, Runnable {
	private static final String TAG = "BACKGROUND_DISCOVERY";

	private List<DeviceInterface> queue;
	private UdpBroadcast broadcaster;
	private Activity view;

	public BackgroundDiscovery(Activity activity) {
	    try {
		view = activity;
		queue = Collections
			.synchronizedList(new ArrayList<DeviceInterface>());
		broadcaster = ConnectionManager.INSTANCE.getBroadcaster();
		broadcaster.registerObserver(this);
	    } catch (IOException err) {
		Log.e(TAG, "Error connection to UDP broadcast socket.", err);
	    }
	}

	@Override
	public void onMessage(Message message) {
	    DeviceInterface device = DeviceFactory
		    .getDevice((DeviceBroadcastMessage) message);
	    queue.add(device);
	    view.runOnUiThread(this);
	}

	@Override
	public void run() {
	    listAdapter.addAll(queue);
	    queue.clear();
	}
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
	runOnUiThread(new Runnable() {

	    @Override
	    public void run() {
		listAdapter.notifyDataSetChanged();
	    }
	});
    }
}
