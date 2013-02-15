// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
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
import com.lp.io.SocketConnector;
import com.lp.io.UdpBroadcast;
/**
 * This activity is used to manage device connections.
 * @author marc
 *
 */
public class DeviceConnectionsActivity extends Activity {

    public static final int DEVICE_LISTENING_PORT = 30303;
    
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
	// Attempt connection.
	connectionTask = new BackgroundConnectionTask();
	connectionTask.setHost(ipAddressInput.getText().toString());
	connectionTask.setPort(Integer.valueOf(portInput.getText().toString()));
	connectionTask.execute("");
    }
    
    public void closeAllClickHandler(View view) {
	ConnectionManager.INSTANCE.closeAll();
	
	listAdapter.clear();
	CharSequence text = "All connections have been closed.";
	Toast heresToASuccessfulConnection = Toast.makeText(context,
		text, Toast.LENGTH_SHORT);
	heresToASuccessfulConnection.show();
    }
    
    public void refreshClickHandler(View view){
	if(discovery == null){
	    discovery.cancel(true);
	}
	discovery = new BackgroundDiscovery();
	discovery.execute("");
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
		InetAddress broadcastAddress = getBroadcastAddress();
		broadcaster = new UdpBroadcast(31313,broadcastAddress);
		broadcaster.registerObserver(this);
	    }catch(IOException err){
		Log.e(TAG, "Error connection to UDP broadcast socket.", err);
	    }
	}
	
	@Override
	protected String doInBackground(String... params) {
	    try{
		broadcaster.send("Who is out there?\r\n", DEVICE_LISTENING_PORT);
		broadcaster.run();
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
	
	InetAddress getBroadcastAddress() throws IOException {
	    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}
	
    }
    
    /**
     * Background connection task. Attempts to make the connection and will notify
     *  the user (via a toast) if the connection succeeded or not.
     * @author marc
     *
     */
    public class BackgroundConnectionTask extends
	    AsyncTask<String, String, String> implements PropertyChangeListener {
	private String host;
	private int port;

	public void setHost(String host) {
	    this.host = host;
	}

	public void setPort(int port) {
	    this.port = port;
	}

	private SocketConnector connection;

	@Override
	protected String doInBackground(String... message) {
	    try {
		// Attempt connection.
		connection = ConnectionManager.INSTANCE.createConnection(host,
			port);
		connection.addChangeListener(this);
	    } catch (Exception err) {
		Log.e("CONNECTION FAILURE",
			"Error connecting to device. Reason: "
				+ err.getMessage());
		publishProgress();
	    }
	    return null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
	    super.onProgressUpdate(values);
	    if (connection != null) {
		listAdapter.add(new DeviceConnectionInformation(connection.getHost(), connection.getPort(), "????"));
		
		Toast heresToASuccessfulConnection = Toast.makeText(context,
			toStatusMessage(connection.getConnectionState()), Toast.LENGTH_SHORT);
		heresToASuccessfulConnection.show();
		
		
	    } else {
		CharSequence text = "Failed to connect.";
		Toast atLeastYouTried = Toast.makeText(context, text,
			Toast.LENGTH_SHORT);
		atLeastYouTried.show();
	    }
	}
	
	protected CharSequence toStatusMessage(SocketConnector.State connectionState){
	    switch(connectionState){
	    	case Connected:
	    	    return "Successfully connected!";
	    	case Failed:
	    	    return "Failed to connect.";
	    	case Closed:
	    	    return "Connection closed.";
	    	default:
	    	    return "";
	    }
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	    publishProgress();
	}

    }
}
