// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import android.util.Log;

import com.lp.io.DeviceMessageInterpretor;
import com.lp.io.MessageProducer;
import com.lp.io.SocketConnector;
/**
 * Singleton connection manager class. Manages connections and their
 *  data interpreters. A singleton instance is used so that connections
 *  persist between views.
 * @author marc
 *
 */
public enum ConnectionManager {
    INSTANCE;
    private static final String TAG = "CONNECTION MANAGER";
    //private List<SocketConnector> connections = new ArrayList<SocketConnector>();
    private SocketConnector connection;
    private DeviceMessageInterpretor dataInterpreter = new DeviceMessageInterpretor();
    public SocketConnector createConnection(final String host, int port){
	// Attempt connection.
	try{
	    connection = new SocketConnector(host, port, dataInterpreter);
	    return connection;
	}
	catch(final Exception err){
	    Log.e(TAG, "Unable to create connection.", err);
	    
	}
	return null;
    }
    
    public MessageProducer getConnectionMessageProducer(){
	return dataInterpreter;
    }
    
    public void closeAll(){
	if(connection != null){
	    connection.close();
	}
    }
}
