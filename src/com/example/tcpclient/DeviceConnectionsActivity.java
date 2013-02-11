// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lp.io.SocketConnector;
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

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.devices_layout);
	 context = getApplicationContext();
	ipAddressInput = (EditText) findViewById(R.id.ip_address2);
	portInput = (EditText) findViewById(R.id.port2);
	connect = (Button) findViewById(R.id.connect_button2);
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
	
	CharSequence text = "All connections have been closed.";
	Toast heresToASuccessfulConnection = Toast.makeText(context,
		text, Toast.LENGTH_SHORT);
	heresToASuccessfulConnection.show();
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
		onProgressUpdate();
	    }
	    return null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
	    super.onProgressUpdate(values);

	    if (connection != null && connection.isConnected()) {
		CharSequence text = "Successfully connected!";
		Toast heresToASuccessfulConnection = Toast.makeText(context,
			text, Toast.LENGTH_SHORT);
		heresToASuccessfulConnection.show();
	    } else {
		CharSequence text = "Failed to connect.";
		Toast atLeastYouTried = Toast.makeText(context, text,
			Toast.LENGTH_SHORT);
		atLeastYouTried.show();
	    }
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	    publishProgress();
	}

    }
}
