// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lp.io.DeviceMessageInterpreter;
import com.lp.io.MessageProducer;
import com.lp.io.SocketConnector;
import com.lp.io.UdpBroadcast;
import com.tacuna.common.devices.scpi.ScpiMessageExchange;

/**
 * Singleton connection manager class. Manages connections and their data
 * interpreters. A singleton instance is used so that connections persist
 * between views. This Singleton uses the singleton enum pattern.
 * 
 * @author marc
 * 
 */
public enum ConnectionManager implements PropertyChangeListener {
    /** The singleton instance of the ConnectionManager */
    INSTANCE;
    private static final String TAG = "CONNECTION MANAGER";

    /** This is the devices UDP listening port. */
    public static final int DEVICE_LISTENING_PORT = 30303;
    /**
     * UDP response port. Note that for now this must be the same as the
     * listening port due to device limitations.
     */
    public static final int RESPONSE_PORT = 30303;

    private UdpBroadcast broadcaster;
    private SocketConnector connection;
    private final DeviceMessageInterpreter dataInterpreter = new DeviceMessageInterpreter();
    private final ScpiMessageExchange exchange = new ScpiMessageExchange(null,
	    dataInterpreter);
    // private DataInterpreter dataInterpreter = new
    // ProtoBuffersDataFrameInterpretor();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
	    1);

    /**
     * Socket connection factory method. Currently, the APP can only have one
     * open connection but that may change
     * 
     * @param host
     * @param port
     * @return
     */
    public SocketConnector createConnection(final String host, int port) {
	// Attempt connection.
	try {
	    if (connection != null && connection.isConnected()) {
		connection.removeChangeListener(this);
		connection.close();
	    }

	    Log.i(TAG,
		    String.format("Creating connection to %s:%d", host, port));
	    connection = new SocketConnector(host, port, dataInterpreter);
	    connection.addChangeListener(this);

	    exchange.setConnection(connection);
	    // executor.scheduleWithFixedDelay(new ScheduledCommand(new Command(
	    // "MEASure:EXT:ADC?", 1), exchange), 1, 1, TimeUnit.SECONDS);
	    // executor.scheduleWithFixedDelay(new ScheduledCommand(new Command(
	    // "MEASure:EXT:ADC?", 2), exchange), 1, 1, TimeUnit.SECONDS);
	    // executor.scheduleWithFixedDelay(new ScheduledCommand(new Command(
	    // "MEASure:EXT:ADC?", 3), exchange), 1, 1, TimeUnit.SECONDS);
	    return connection;
	} catch (final Exception err) {
	    Log.e(TAG, "Unable to create connection.", err);

	}
	return null;
    }

    /**
     * Returns true if the application is connected to the device specified by
     * the device connection info.
     * 
     * @param info
     * @return
     */
    public boolean isAppConnected(DeviceConnectionInformation info) {
	if (connection == null) {
	    return false;
	}
	if (connection.isConnected()
		&& connection.getHost().equals(info.getHost())) {
	    return true;
	}
	return false;
    }

    public MessageProducer getConnectionMessageProducer() {
	return exchange;
    }

    /**
     * Returns the UdpBroadcaster. This method will create the broadcaster the
     * first time it is called.
     * 
     * @param context
     *            Application context needed to access the WIFI service.
     * @return UdpBroadcast
     * @throws IOException
     */
    public UdpBroadcast getBroadcaster(Context context) throws IOException {
	if (broadcaster == null) {
	    broadcaster = new UdpBroadcast(RESPONSE_PORT,
		    getBroadcastAddress(context));
	    new UdpListenThread(broadcaster);
	}
	return broadcaster;
    }

    /**
     * Returns the broadcast address used on the current WIFI network.
     * 
     * @param context
     *            Application context needed to access the WIFI service.
     * @return InetAdress for broadcasts
     * @throws IOException
     */
    protected InetAddress getBroadcastAddress(Context context)
	    throws IOException {
	WifiManager wifi = (WifiManager) context
		.getSystemService(Context.WIFI_SERVICE);
	// Throw if no WifiManager available
	if (wifi == null) {
	    throw new IOException(
		    "NULL WifiManager. You must be connected to a wifi network.");
	}

	DhcpInfo dhcp = wifi.getDhcpInfo();
	// If the dhcp info is null, just return the default broadcast address.
	if (dhcp == null) {
	    return InetAddress.getByName("255.255.255.255");
	}
	int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	byte[] quads = new byte[4];
	for (int k = 0; k < 4; k++)
	    quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	return InetAddress.getByAddress(quads);
    }

    /**
     * Closes the connections. Right now there is only one connection but that
     * may change.
     */
    public void closeAll() {
	if (connection != null) {
	    connection.close();
	}
    }

    /**
     * In order to make the UDP broad cast listen in the background we have to
     * run it in its own thread. Originally I had set this up to just be an
     * AsynchTask but that doesn't work for blocking calls (like receive) on
     * Android 3.+ due to the API running all asynch tasks on a single thread.
     */
    protected class UdpListenThread extends Thread {
	/**
	 * Constructor. Takes in the UdpBroadcast instance and runs it in its
	 * own thread.
	 * 
	 * @param broadcaster
	 */
	UdpListenThread(UdpBroadcast broadcaster) {
	    super(broadcaster);
	    start();
	}

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
	notifyListeners("connection", null, connection);
    }

    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    /**
     * Register a change listener for receiving state change updates.
     * 
     * @param listener
     */
    public void addChangeListener(PropertyChangeListener listener) {
	listeners.add(listener);
    }

    /**
     * Removes a previously registered listener.
     * 
     * @param listener
     */
    public void removeChangeListener(PropertyChangeListener listener) {
	listeners.remove(listener);
    }

    protected void notifyListeners(String property, Object oldValue,
	    Object newValue) {
	for (PropertyChangeListener listener : listeners) {
	    listener.propertyChange(new PropertyChangeEvent(this, property,
		    oldValue, newValue));
	}
    }
}
