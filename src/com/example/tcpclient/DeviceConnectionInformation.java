// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

/**
 * Simple class used for holding the devices connection information.
 * @author marc
 *
 */
public class DeviceConnectionInformation {
    
    private String deviceName;
    private String host;
    private int port;
    private String macAddress;
    
    /**
     * Constructor. 
     * @param host The devices host name or IP address
     * @param port The devices port (i.e 9760).
     * @param macAddress
     */
    public DeviceConnectionInformation(String host, int port, String macAddress) {
	super();
	this.host = host;
	this.port = port;
	this.macAddress = macAddress;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((host == null) ? 0 : host.hashCode());
	result = prime * result
		+ ((macAddress == null) ? 0 : macAddress.hashCode());
	result = prime * result + port;
	return result;
    }
    
    @Override
    /**
     * Eclipse generated equals method. Hots, port and mac
     *  must be equals.
     */
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DeviceConnectionInformation other = (DeviceConnectionInformation) obj;
	if (host == null) {
	    if (other.host != null)
		return false;
	} else if (!host.equals(other.host))
	    return false;
	if (macAddress == null) {
	    if (other.macAddress != null)
		return false;
	} else if (!macAddress.equals(other.macAddress))
	    return false;
	if (port != other.port)
	    return false;
	return true;
    }
    
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

}
