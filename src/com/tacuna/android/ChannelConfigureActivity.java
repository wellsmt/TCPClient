package com.tacuna.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.tcpclient.R;
import com.tacuna.common.devices.ChannelInterface;

public class ChannelConfigureActivity extends FragmentActivity {

    /**
     * Int used for stating this activity using an Intent.
     */
    public static final int CONFIG_ANALOG_IN_CHANNEL = 0;
    public static final int CONFIG_ANALOG_OUT_CHANNEL = 1;
    public static final int CONFIG_DIGITAL_IO_CHANNEL = 2;

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
	builder.setTitle("Configure Channel");

	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		setResult(RESULT_OK);
		finish();
	    }
	});

	builder.setNegativeButton("Cancel",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			setResult(RESULT_CANCELED);
			finish();
		    }
		});

	AlertDialog dialog = builder.create();
	dialog.show();
    }

}
