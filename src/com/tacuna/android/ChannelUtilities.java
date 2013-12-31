package com.tacuna.android;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.ChannelInterface;

public class ChannelUtilities {

    public static Collection<ChannelInterface> selected = new HashSet<ChannelInterface>();

    public static class ChannelRemoveSelectedClickListener implements
	    View.OnClickListener {

	@Override
	public void onClick(View v) {
	    for (ChannelInterface channel : selected) {
		ConnectionManager.INSTANCE.removeChannel(channel);
	    }
	    selected.clear();
	}

    }

    public static class ChannelRowClickListener implements View.OnClickListener {

	private final ChannelInterface channel;

	/**
	 * @param channel
	 */
	public ChannelRowClickListener(ChannelInterface channel) {
	    super();
	    this.channel = channel;
	}

	@Override
	public void onClick(View view) {

	    if (selected.contains(channel)) {
		selected.remove(channel);
	    } else {
		view.setBackgroundColor(Color.CYAN);
		selected.add(channel);
	    }
	}

    }

    public static TableRow getChannelRow(Context context,
	    ChannelInterface channel) {
	TableRow tr = new TableRow(context);
	tr.setClickable(true);
	tr.setOnClickListener(new ChannelRowClickListener(channel));
	if (selected.contains(channel)) {
	    tr.setBackgroundColor(Color.CYAN);
	}

	// Channel label:
	TextView label = new TextView(context);
	label.setText(channel.getName());
	label.setPadding(5, 0, 5, 5);
	tr.addView(label);

	TextView measuredValue = new TextView(context);
	measuredValue.setText(String.format("%f %s", channel.getCurrentValue(),
		channel.getUnit()));
	measuredValue.setTextSize(20);
	measuredValue.setTextColor(Color.BLACK);
	measuredValue.setPadding(10, 5, 5, 5);
	tr.addView(measuredValue);
	return tr;

    }
}
