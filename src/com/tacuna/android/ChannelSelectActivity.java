package com.tacuna.android;

import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.tcpclient.R;
import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.ChannelInterface;

/**
 * The ChannelSelect activity is a FragmentActivity that pops up an alert dialog
 * that allows a user to select multiple channels. Selecting either the 'Ok' or
 * the 'Cancel' buttons on the alert dialog causes the activity to finish with
 * the appropriate response code set.
 * 
 * @author Marc
 * 
 */
public class ChannelSelectActivity extends FragmentActivity {

    /**
     * Int used for stating this activity using an Intent.
     */
    public static final int PICK_ANALOG_CHANNELS = 0;

    ArrayList<Integer> selectedChannels = new ArrayList<Integer>();
    ArrayList<ChannelInterface> allChannels = new ArrayList<ChannelInterface>();

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	setContentView(R.layout.channel_select_layout);
    }

    @Override
    protected void onResume() {
	super.onResume();
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Avaliable Channels");

	Collection<ChannelInterface> channels = ConnectionManager.INSTANCE
		.getNonActiveChannels();
	if (channels.size() == 0) {
	    builder.setMessage("No channels available. Try connecting to a Wifi DAQ first.");
	} else {
	    CharSequence[] channelItems = new CharSequence[channels.size()];
	    int index = 0;
	    for (ChannelInterface channel : channels) {
		channelItems[index++] = channel.getDevice().getDeviceName()
			+ " " + channel.getName();
		allChannels.add(channel);
	    }

	    builder.setMultiChoiceItems(channelItems, null,
		    new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which,
				boolean isChecked) {
			    if (isChecked) {
				selectedChannels.add(which);
			    } else if (selectedChannels.contains(which)) {
				// Using Integer.valueOf here to avoid removing
				// indexes and instead removing values
				selectedChannels.remove(Integer.valueOf(which));
			    }
			}
		    });
	}

	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		for (Integer index : selectedChannels) {
		    ChannelInterface channel = allChannels.get(index);
		    ConnectionManager.INSTANCE.addChannel(channel);
		}
		selectedChannels.clear();
		setResult(RESULT_OK);
		finish();
	    }
	});

	builder.setNegativeButton("Cancel",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			selectedChannels.clear();
			setResult(RESULT_CANCELED);
			finish();
		    }
		});

	AlertDialog dialog = builder.create();
	dialog.show();
    }

}
