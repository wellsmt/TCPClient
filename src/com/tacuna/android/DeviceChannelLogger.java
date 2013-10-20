package com.tacuna.android;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.tacuna.common.devices.DeviceInterface;

public class DeviceChannelLogger {

    private static final String FILE_DIR = "/WIFIDaq/";
    private final File sdCard = Environment.getExternalStorageDirectory();
    private final File dir = new File(sdCard.getAbsolutePath() + FILE_DIR);
    private final String extension = ".txt";

    DeviceInterface device;

    /**
     * @param device
     */
    public DeviceChannelLogger(DeviceInterface device) {
	super();
	this.device = device;
	dir.mkdirs();
    }

    public void startLogging() {

	// Create and register the log file writer
	LogFileWriter fileWriter = new LogFileWriter(dir.getAbsolutePath(),
		extension);

	String filename = Long.toString(System.currentTimeMillis());
	try {
	    fileWriter.startNewFile(filename);
	} catch (IOException err) {
	    Log.e(LogFileWriter.TAG,
		    "Could not open log file. No data log will be created.",
		    err);
	}
    }
}
