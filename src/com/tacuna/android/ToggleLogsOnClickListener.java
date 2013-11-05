package com.tacuna.android;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.tacuna.common.devices.DeviceInterface;

/**
 * Implements an OnClickListener for toggling logging on and off.
 * 
 * @author Marc
 * 
 */
public class ToggleLogsOnClickListener implements OnClickListener {

    private final DeviceChannelLogger logger;

    public ToggleLogsOnClickListener(DeviceInterface device) {
	this.logger = new DeviceChannelLogger(device);
    }

    @Override
    public void onClick(View toggle) {
	ToggleButton toggleButton = (ToggleButton) toggle;
	boolean isClicked = toggleButton.isChecked();
	if (isClicked) {
	    logger.startLogging();
	} else {
	    logger.stopLogging();
	}
    }

}
