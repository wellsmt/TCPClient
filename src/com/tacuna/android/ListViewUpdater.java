// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;

import com.lp.io.Message;
import com.lp.io.MessageConsumer;

/**
 * List View Updater class receives message from the data interpreter and adds
 * the raw message data to the display list.
 * 
 */
public class ListViewUpdater implements MessageConsumer, Runnable {
    private final Activity activity;
    private final MyCustomAdapter adapter;
    private final List<String> messagesToDisplay;

    /**
     * Constructor. Takes necessary dependencies.
     * 
     * @param activity
     * @param adapter
     */
    public ListViewUpdater(Activity activity, MyCustomAdapter adapter) {
	this.activity = activity;
	this.adapter = adapter;
	messagesToDisplay = Collections.synchronizedList(new ArrayList<String>(
		10));
    }

    @Override
    public void onMessage(Message message) {
	// Pass the messages to the UI thread for display.
	// The messagesToDisplay list is synchronized so
	// that this is thread safe.
	messagesToDisplay.add(message.getData().toString());
	activity.runOnUiThread(this);
    }

    @Override
    public void run() {
	adapter.getmListItems().addAll(messagesToDisplay);
	messagesToDisplay.clear();
	adapter.notifyDataSetChanged();
    }

}
