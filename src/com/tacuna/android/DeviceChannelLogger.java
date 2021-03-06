package com.tacuna.android;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.lp.io.MessageProducer;
import com.tacuna.android.LogFileWriter.StorageNotAvailable;
import com.tacuna.common.devices.DeviceInterface;

/**
 * TODO: This file is no longer needed. Migrate functionality to generated data
 * files from the database
 * 
 * @author Marc
 * 
 */
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
    public void startLogging() throws StorageNotAvailable, IOException {

	// Create and register the log file writer
	fileWriter = new LogFileWriter(extension, dir);

	String datetime = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
		.format(new Date(System.currentTimeMillis()));
	String filename = device.getDeviceType() + "_" + datetime;

	fileWriter.startNewFile(filename);
	MessageProducer producer = device;
	if (null != producer) {
	    producer.registerObserver(fileWriter);
	}

    }

    /**
     * Stops the logging.
     * 
     * TODO: This needs to do some file clean up.
     */
    public void stopLogging() {
	MessageProducer producer = device;
	if (null != producer && null != fileWriter) {
	    producer.removeObserver(fileWriter);
	}
    }
}
