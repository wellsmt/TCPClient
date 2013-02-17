// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * A collection of utilities for the Android app.
 * @author marc
 *
 */
public class ApplicationUtilities {
    /**
     * A utility method for making a toast. All toast should use this method that way
     *  if we need to modify them all, we can do it in one place.
     * @param context Application context
     * @param message Message to display
     */
    public static void toast(Context context, CharSequence message){
	Toast heresToASuccessfulConnection = Toast.makeText(context, message,
		Toast.LENGTH_SHORT);
	heresToASuccessfulConnection.setGravity(Gravity.TOP, 0, 20);
	heresToASuccessfulConnection.show();
    }
}
