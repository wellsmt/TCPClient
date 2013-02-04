// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import java.util.ArrayList;

import android.app.Activity;

import com.lp.io.Message;
import com.lp.io.MessageConsumer;

public class ListViewUpdater implements MessageConsumer, Runnable {
	private Activity activity;
	private MyCustomAdapter adapter;
	private ArrayList<String> messagesToDisplay;
	
	public ListViewUpdater(Activity activity, MyCustomAdapter adapter){
		this.activity = activity;
		this.adapter = adapter;
		messagesToDisplay = new ArrayList<String>(10);
	}
	
	@Override
	public void onMessage(Message message) {
		messagesToDisplay.add(message.getData());
		activity.runOnUiThread(this);
	}

	@Override
	public void run() {
		adapter.getmListItems().addAll(messagesToDisplay);
		messagesToDisplay.clear();
		adapter.notifyDataSetChanged();
	}

}
