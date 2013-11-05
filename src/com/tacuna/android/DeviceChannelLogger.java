package com.tacuna.android;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import com.lp.io.MessageProducer;
import com.tacuna.common.devices.DeviceInterface;

public class DeviceChannelLogger {

    private static final String FILE_DIR = "/WIFIDaq/";
    private final File sdCard = Environment.getExternalStorageDirectory();
    private final File dir = new File(sdCard.getAbsolutePath() + FILE_DIR);
    private final String extension = ".csv";

    DeviceInterface device;
    LogFileWriter fileWriter;

    /**
     * Constructor.
     * 
     * @param device
     */
    public DeviceChannelLogger(DeviceInterface device) {
	super();
	this.device = device;
	dir.mkdirs();
    }

    /**
     * Starts the logging.
     */
    public void startLogging() {

	// Create and register the log file writer
	fileWriter = new LogFileWriter(extension, dir);

	String datetime = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
		.format(new Date(System.currentTimeMillis()));
	String filename = device.getDeviceType() + "_" + datetime;
	try {
	    fileWriter.startNewFile(filename);
	    MessageProducer producer = device.getEx();
	    if (null != producer) {
		producer.registerObserver(fileWriter);
	    }
	} catch (IOException err) {
	    Log.e(LogFileWriter.TAG,
		    "Could not open log file. No data log will be created.",
		    err);
	}
    }

    /**
     * Stops the logging.
     * 
     * TODO: This needs to do some file clean up.
     */
    public void stopLogging() {
	MessageProducer producer = device.getEx();
	if (null != producer && null != fileWriter) {
	    producer.removeObserver(fileWriter);
	}
    }
}
