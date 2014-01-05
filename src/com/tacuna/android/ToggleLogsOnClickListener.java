package com.tacuna.android;

import java.io.IOException;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.tacuna.android.LogFileWriter.StorageNotAvailable;
import com.tacuna.common.devices.DeviceInterface;

/**
 * Implements an OnClickListener for toggling logging on and off. If logging can
 * not be turned on, a toast is displayed
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
	try {
	    if (isClicked) {
		logger.startLogging();
	    } else {
		logger.stopLogging();
	    }
	} catch (IOException err) {
	    Log.e(LogFileWriter.TAG,
		    "Could not open log file. No data log will be created.",
		    err);
	    ApplicationUtilities.toast(toggle.getContext(),
		    "Unable to log data. " + err.getMessage());
	    toggleButton.setChecked(false);

	} catch (StorageNotAvailable noStorage) {
	    Log.e(LogFileWriter.TAG,
		    "Could not open log file. No data log will be created.",
		    noStorage);
	    ApplicationUtilities.toast(toggle.getContext(),
		    noStorage.getMessage());
	    toggleButton.setChecked(false);
	} catch (Exception err) {
	    Log.e(LogFileWriter.TAG,
		    "Could not open log file. No data log will be created.",
		    err);
	    ApplicationUtilities.toast(toggle.getContext(),
		    "Unable to log data to file.");
	    toggleButton.setChecked(false);
	}
    }

}
