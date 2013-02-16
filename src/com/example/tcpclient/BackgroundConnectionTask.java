// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.lp.io.SocketConnector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Background connection task. Attempts to make the connection and will notify
 *  the user (via a toast) if the connection succeeded or not.
 * @author marc
 *
 */
public class BackgroundConnectionTask extends
	AsyncTask<String, String, String> implements PropertyChangeListener {

    	private Context context;
    	private String host;
    	private int port;
    	/**
    	 * Constructor. Takes the application Context.
    	 * @param context
    	 */
    	BackgroundConnectionTask(Context context){
    	    this.context = context;
    	}

    	/**
    	 * Sets the host name or IP address for this connection.
    	 * @param host
    	 */
	public void setHost(String host) {
	    this.host = host;
	}

	/**
	 * Sets the port for this connection.
	 * @param port
	 */
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