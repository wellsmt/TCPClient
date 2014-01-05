package com.tacuna.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.lp.io.Message;
import com.lp.io.MessageConsumer;
import com.lp.io.SimpleDeviceMessage;
import com.tacuna.common.devices.ChannelInterface;

/**
 * The log file writer class is a message consumer that writes all of the
 * messages received to a log file.
 */
public class LogFileWriter implements MessageConsumer {

    public static class StorageNotAvailable extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7014355808227973141L;

	/**
	 * 
	 */
	public StorageNotAvailable() {
	    super();
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public StorageNotAvailable(String detailMessage, Throwable throwable) {
	    super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public StorageNotAvailable(String detailMessage) {
	    super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public StorageNotAvailable(Throwable throwable) {
	    super(throwable);
	}
    }

    public static final String TAG = "LOG FILE WRITER";

    String extension;
    File data;
    File dir;
    BufferedWriter buf;

    /**
     * Constructor. Takes the directory and extension used when creating the log
     * file. The actual file is opened by calling the <code>startNewFile</code>.
     * 
     * @param dir
     * @param extension
     */
    LogFileWriter(String extension, File directory) {
	this.extension = extension;
	this.dir = directory;
    }

    /**
     * Creates a new file with the specified file name to log out data.
     * 
     * @param filename
     * @throws IOException
     */
    public void startNewFile(String filename) throws IOException,
	    StorageNotAvailable {
	if (buf != null) {
	    buf.close();
	}

	if (isExternalStorageWritable()) {

	    String filePath = filename + extension;
	    Log.d(TAG,
		    String.format("Attempting to open log file: %s", filePath));
	    data = new File(dir, filePath);
	    if (!data.exists()) {
		data.createNewFile();
	    }
	    buf = new BufferedWriter(new FileWriter(data, true));
	} else {
	    Log.e(TAG, "External storage is not writable.");
	    throw new StorageNotAvailable("External storage is not writable.");
	}
    }

    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
	String state = Environment.getExternalStorageState();
	if (Environment.MEDIA_MOUNTED.equals(state)) {
	    return true;
	}
	return false;
    }

    @Override
    public void onMessage(Message message) {
	// If the buffer is null, do nothing.
	if (buf == null) {
	    return;
	}
	try {
	    SimpleDeviceMessage msg = (SimpleDeviceMessage) message;
	    ChannelInterface channel = msg.getChannel();
	    String line = String.format("%d,%s,%f", msg.getTimestamp(),
		    channel.getName(), msg.getValue());
	    buf.append(line);
	    buf.newLine();
	    buf.flush();
	} catch (IOException err) {
	    Log.e(TAG, "Unable to append data to file.", err);
	}
    }

    public void close() {
	try {
	    buf.close();
	} catch (IOException e) {
	    Log.e(TAG, "Error closing buffer.", e);
	}
    }
}