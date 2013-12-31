// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.io.IOException;
import java.net.InetAddress;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.view.Gravity;
import android.widget.Toast;

/**
 * A collection of utilities for the Android app.
 * 
 * @author marc
 * 
 */
public class ApplicationUtilities {
    /**
     * A utility method for making a toast. All toast should use this method
     * that way if we need to modify them all, we can do it in one place.
     * 
     * @param context
     *            Application context
     * @param message
     *            Message to display
     */
    public static void toast(Context context, CharSequence message) {
	Toast heresToASuccessfulConnection = Toast.makeText(context, message,
		Toast.LENGTH_SHORT);
	heresToASuccessfulConnection.setGravity(Gravity.TOP, 0, 20);
	heresToASuccessfulConnection.show();
    }

    /**
     * Returns the broadcast address used on the current WIFI network.
     * 
     * @param context
     *            Application context needed to access the WIFI service.
     * @return InetAdress for broadcasts
     * @throws IOException
     */
    public static InetAddress getBroadcastAddress(Context context)
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
}
