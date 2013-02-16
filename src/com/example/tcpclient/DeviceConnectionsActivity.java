// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lp.io.DeviceBroadcastMessage;
import com.lp.io.Message;
import com.lp.io.MessageConsumer;
import com.lp.io.UdpBroadcast;

/**
 * This activity is used to manage device connections.
 * 
 * @author marc
 * 
 */
public class DeviceConnectionsActivity extends Activity {

    private EditText ipAddressInput;
    private EditText portInput;
    private Button connect;
    private BackgroundConnectionTask connectionTask;
    private Context context;
    private ListView deviceList;

    private ConnectedDeviceListAdapter listAdapter;

    private BackgroundDiscovery discovery;
    private BackgroundSend send;
    
    private final String HELLO = "Who is out there?\r\n";

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.devices_layout);
	context = getApplicationContext();
	ipAddressInput = (EditText) findViewById(R.id.ip_address2);
	portInput = (EditText) findViewById(R.id.port2);
	connect = (Button) findViewById(R.id.connect_button2);
	listAdapter = new ConnectedDeviceListAdapter(context);

	deviceList = (ListView) findViewById(R.id.device_list);
	deviceList.setAdapter(listAdapter);

	discovery = new BackgroundDiscovery(this);
	send = new BackgroundSend();
	send.execute(HELLO);
    }

    public void connectClickHandler(View view) {
	listAdapter.add(new DeviceConnectionInformation(ipAddressInput
		.getText().toString(), Integer.valueOf(portInput.getText()
		.toString()), "????"));
	// Attempt connection.
	connectionTask = new BackgroundConnectionTask(context);
	connectionTask.setHost(ipAddressInput.getText().toString());
	connectionTask.setPort(Integer.valueOf(portInput.getText().toString()));
	connectionTask.execute("");
    }

    public void closeAllClickHandler(View view) {
	ConnectionManager.INSTANCE.closeAll();

	listAdapter.clear();
	toast("All connections have been closed.");

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

    protected void toast(final CharSequence message) {
	Toast heresToASuccessfulConnection = Toast.makeText(context, message,
		Toast.LENGTH_SHORT);
	heresToASuccessfulConnection.show();
    }

    public class BackgroundSend extends AsyncTask<String, String, String> {
	private static final String TAG = "BACKGROUND_SEND_TASK";

	@Override
	protected String doInBackground(String... params) {
	    try {
		UdpBroadcast broadcaster = ConnectionManager.INSTANCE
			.getBroadcaster(context);
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

	private List<DeviceConnectionInformation> queue;
	private UdpBroadcast broadcaster;
	private Activity view;

	public BackgroundDiscovery(Activity activity) {
	    try {
		view = activity;
		queue =  Collections.synchronizedList(new ArrayList<DeviceConnectionInformation>());
		broadcaster = ConnectionManager.INSTANCE
			.getBroadcaster(context);
		broadcaster.registerObserver(this);
	    } catch (IOException err) {
		Log.e(TAG, "Error connection to UDP broadcast socket.", err);
	    }
	}

	@Override
	public void onMessage(Message message) {
	    DeviceBroadcastMessage msg = (DeviceBroadcastMessage) message;
	    queue.add(new DeviceConnectionInformation(msg.getHost(),msg.getTcpPort(),msg.getMacAddress()));
	    view.runOnUiThread(this);
	}

	@Override
	public void run() {
	   listAdapter.addAll(queue);
	   queue.clear();
	}
    }
}
