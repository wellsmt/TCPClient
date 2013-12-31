// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import static com.tacuna.android.ApplicationUtilities.toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lp.io.SocketConnector;
import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.DeviceInterface;

/**
 * Background connection task. Attempts to make the connection and will notify
 * the user (via a toast) if the connection succeeded or not.
 * 
 * @author marc
 * 
 */
public class BackgroundConnectionTask extends AsyncTask<String, String, String>
	implements PropertyChangeListener {

    private final Context context;
    private final DeviceInterface device;

    /**
     * Constructor. Takes the application Context.
     * 
     * @param context
     */
    BackgroundConnectionTask(Context context, DeviceInterface device) {
	this.context = context;
	this.device = device;
    }

    private SocketConnector connection;

    @Override
    protected String doInBackground(String... message) {
	try {
	    // Attempt connection.
	    connection = ConnectionManager.INSTANCE.createConnection(device);
	    connection.addChangeListener(this);
	} catch (Exception err) {
	    Log.e("CONNECTION FAILURE", "Error connecting to device. Reason: "
		    + err.getMessage());
	    publishProgress();
	}
	return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
	super.onProgressUpdate(values);
	if (connection != null) {
	    toast(context, toStatusMessage(connection.getConnectionState()));

	} else {
	    toast(context, "Failed to connect.");
	}
    }

    protected CharSequence toStatusMessage(SocketConnector.State connectionState) {
	switch (connectionState) {
	case Connected:
	    return "Successfully connected!";
	case Failed:
	    return "Failed to connect.";
	case Closed:
	    return "Connection closed.";
	case ClosedByPeer:
	    return "Connection was lost or closed by peer.";
	default:
	    return "";
	}
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
	publishProgress();
    }
}
