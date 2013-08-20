package com.example.tcpclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

import com.lp.io.Message;
import com.lp.io.MessageConsumer;

/**
 * The log file writer class is a message consumer that writes all of the
 * messages received to a log file.
 */
public class LogFileWriter implements MessageConsumer {
    public static final String TAG = "LOG FILE WRITER";
    String directory;
    String extension;
    File data;
    BufferedWriter buf;

    /**
     * Constructor. Takes the directory and extension used when creating the log
     * file. The actual file is opened by calling the <code>startNewFile</code>.
     * 
     * @param dir
     * @param extension
     */
    LogFileWriter(String dir, String extension) {
	this.directory = dir;
	this.extension = extension;
    }

    /**
     * Creates a new file with the specified file name to log out data.
     * 
     * @param filename
     * @throws IOException
     */
    public void startNewFile(String filename) throws IOException {
	if (buf != null) {
	    buf.close();
	}
	String filePath = directory + "/" + filename + extension;
	Log.d(TAG, String.format("Attempting to open log file: %s", filePath));
	data = new File(filePath);
	if (!data.exists()) {
	    data.createNewFile();
	}
	buf = new BufferedWriter(new FileWriter(data, true));
    }    
    
    @Override
    public void onMessage(Message message) {
	// If the buffer is null, do nothing.
	if (buf == null) {
	    return;
	}
	try {
	    buf.append(message.getTimestamp()+","+message.getData());
	    buf.newLine();
	    buf.flush();
	} catch (IOException err) {
	    Log.e(TAG, "Unable to append data to file.", err);
	}
    }

}