// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lp.io.DataInterpreter;
import com.lp.io.DeviceMessageInterpretor;
import com.lp.io.MessageProducer;
import com.lp.io.SocketConnector;
import com.lp.io.UdpBroadcast;
/**
 * Singleton connection manager class. Managers connections and their
 *  data interpreters. A singleton instance is used so that connections
 *  persist between views. This Singleton uses the singleton enum pattern.
 * @author marc
 *
 */
public enum ConnectionManager {
    /** The singleton instance of the ConnectionManager */
    INSTANCE;
    private static final String TAG = "CONNECTION MANAGER";

    /** This is the devices UDP listening port. */
    public static final int DEVICE_LISTENING_PORT = 30303;
    /**
     *  UDP response port. Note that for now this must be 
     * the same as the listening port due to device limitations.
     */
    public static final int RESPONSE_PORT = 30303;
    
    private UdpBroadcast broadcaster;
    private UdpListenThread listenThread;
    
    private Map<String,SocketConnector> connections = new HashMap<String,SocketConnector>();
    private Map<String,DataInterpreter> dataStream = new HashMap<String,DataInterpreter>();
    private SocketConnector connection;
    private DeviceMessageInterpretor dataInterpreter = new DeviceMessageInterpretor();
    
    /**
     * Socket connection factory method. Currently, the APP can only have one open
     *  connection but that may change
     * @param host
     * @param port
     * @return
     */
    public SocketConnector createConnection(final String host, int port){
	// Attempt connection.
	try{
	    if(connection !=null && connection.isConnected()){
		connection.close();
	    }
	    
	    Log.i(TAG, String.format("Creating connection to %s:%d", host,port));
	    connection = new SocketConnector(host, port, dataInterpreter);
	    return connection;
	}
	catch(final Exception err){
	    Log.e(TAG, "Unable to create connection.", err);
	    
	}
	return null;
    }
    
    /**
     * Returns true if the application is connected to the device 
     *  specified by the device connection info.
     * @param info
     * @return
     */
    public boolean isAppConnected(DeviceConnectionInformation info){
	if(connection == null){
	    return false;
	}
	if(connection.isConnected() && connection.getHost().equals(info.getHost())){
	    return true;
	}
	return false;
    }
    
    public MessageProducer getConnectionMessageProducer(){
	return dataInterpreter;
    }
    
    /**
     * Returns the UdpBroadcaster. This method will create the broadcaster
     *  the first time it is called.
     * @param context Application context needed to access the WIFI service.
     * @return UdpBroadcast
     * @throws IOException
     */
    public UdpBroadcast getBroadcaster(Context context) throws IOException{
	if(broadcaster == null){
	    broadcaster = new UdpBroadcast(RESPONSE_PORT,getBroadcastAddress(context));
	    listenThread = new UdpListenThread(broadcaster);
	}
	return broadcaster;
    }
    
    /**
     * Returns the broadcast address used on the current WIFI network.
     * @param context Application context needed to access the WIFI service.
     * @return InetAdress for broadcasts
     * @throws IOException
     */
    protected InetAddress getBroadcastAddress(Context context) throws IOException {
	
	    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
    }
    
    /**
     * Closes the connections. Right now there is only one connection
     * but that may change.
     */
    public void closeAll(){
	if(connection != null){
	    connection.close();
	}
    }
    
    /**
     * In order to make the UDP broad cast listen in the
     *  background we have to run it in its own thread. Originally
     *  I had set this up to just be an AsynchTask but that
     *  doesn't work for blocking calls (like receive) on
     *  Android 3.+ due to the API running all asynch tasks on
     *  a single thread.
     */
    protected class UdpListenThread extends Thread{
	/**
	 * Constructor. Takes in the UdpBroadcast instance
	 *  and runs it in its own thread.
	 * @param broadcaster
	 */
	UdpListenThread(UdpBroadcast broadcaster){
	    super(broadcaster);
	    start();
	}
	
	
    }
}
