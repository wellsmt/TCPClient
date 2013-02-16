// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.io.IOException;

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

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.devices_layout);
	 context = getApplicationContext();
	ipAddressInput = (EditText) findViewById(R.id.ip_address2);
	portInput = (EditText) findViewById(R.id.port2);
	connect = (Button) findViewById(R.id.connect_button2);
	listAdapter = new ConnectedDeviceListAdapter(context);
	
	deviceList = (ListView)findViewById(R.id.device_list);
	deviceList.setAdapter(listAdapter);
	
	discovery = new BackgroundDiscovery();
	discovery.execute("");
    }

    public void connectClickHandler(View view) {
	listAdapter.add(new DeviceConnectionInformation(ipAddressInput.getText().toString(), 
		Integer.valueOf(portInput.getText().toString()), "????"));
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
     * @param view
     */
    public void refreshClickHandler(View view){
	try{
	    UdpBroadcast broadcaster = ConnectionManager.INSTANCE.getBroadcaster(context);
	    broadcaster.send("Who is out there?\r\n", ConnectionManager.DEVICE_LISTENING_PORT);
	}
	catch(IOException err){
	    Log.e("DEVICE_CONNECTION", "Error connection to UDP broadcast socket.", err);
	    toast("Could not refresh device list.");
	}
    }
    
    protected void toast(final CharSequence message){
	Toast heresToASuccessfulConnection = Toast.makeText(context,
		message, Toast.LENGTH_SHORT);
	heresToASuccessfulConnection.show();
    }

    /**
     * Background discovery task.
     */
    public class BackgroundDiscovery 
    	extends AsyncTask<String, String, String> implements MessageConsumer {
	private static final String TAG = "BACKGROUND_DISCOVERY";
	
	private UdpBroadcast broadcaster;
	
	public BackgroundDiscovery(){
	    try{
		broadcaster = ConnectionManager.INSTANCE.getBroadcaster(context);
		broadcaster.registerObserver(this);
	    }catch(IOException err){
		Log.e(TAG, "Error connection to UDP broadcast socket.", err);
	    }
	}
	
	@Override
	protected String doInBackground(String... params) {
	    try{
		if(broadcaster != null){
		    broadcaster.send("Who is out there?\r\n", ConnectionManager.DEVICE_LISTENING_PORT);
		    broadcaster.run();
		}
	    }
	    catch(IOException err){
		Log.e(TAG, "Error sending UDP broadcast.", err);
	    }
	    return null;
	}

	@Override
	public void onMessage(Message message) {
	    DeviceBroadcastMessage msg = (DeviceBroadcastMessage)message;
	    publishProgress(msg.getHost(),
		    Integer.toString(msg.getTcpPort()),
		    msg.getMacAddress());
	    
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
	    super.onProgressUpdate(values);
	    listAdapter.add(new DeviceConnectionInformation(values[0], 
		    Integer.parseInt(values[1]),
		    values[2]));
	}
	
	
    }
}
