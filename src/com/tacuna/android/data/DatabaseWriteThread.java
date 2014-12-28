package com.tacuna.android.data;

import android.content.Context;
import android.util.Log;

import com.lp.io.messages.MeasurementMessage;
import com.lp.io.messages.MessageFactory;
import com.tacuna.common.components.ApplicationBuffer;

/**
 * Performs the background data persistence to the database. This thread will
 * read off the DATABASE_WRITE_BUFFER and insert the measurements using the
 * Measurement data source.
 * 
 * @author Marc
 * 
 */
public class DatabaseWriteThread extends Thread {

    private static final int TRANSACTION_SIZE = 1000;
    private static String TAG = "DATABASE WRITE THREAD";
    private final MeasurementsDataSource ds;
    private boolean running;
    ApplicationBuffer buffer;
    int writes = 0;

    /**
     * Constructs the db write threads and calls start.
     * 
     * @param context
     *            Android context
     * @param truncate
     *            If true, the database will be truncated on creation
     */
    public DatabaseWriteThread(Context context, boolean truncate) {
	ds = new MeasurementsDataSource(context);
	ds.open();
	if (truncate) {
	    ds.truncate();
	}
	running = true;
	buffer = ApplicationBuffer.DATABASE_WRITE_BUFFER;
	start();
    }

    @Override
    public void run() {
	try {
	    while (running || buffer.size() > 0) {
		MeasurementMessage msg = (MeasurementMessage) buffer.take();

		// Begin the transaction.
		if (writes % TRANSACTION_SIZE == 0) {
		    ds.beginTransaction();
		    Log.i(TAG,
			    String.format(
				    "Beginning Transaction. Database writes commited: %d",
				    writes));
		}

		ds.insert(msg);
		MessageFactory.INSTANCE.release(msg);
		writes++;

		// End transaction if necessary
		if (writes % TRANSACTION_SIZE == 0) {
		    Log.i(TAG, String.format(
			    "Ending Transaction. Database writes commited: %d",
			    writes));
		    ds.endTransaction();
		}
	    }
	} catch (Exception err) {
	    Log.e(TAG, "A database error occured.", err);
	} finally {
	    ds.endTransaction();
	    ds.close();
	    Log.i(TAG, String.format(
		    "Run complete. Database writes commited: %d", writes));
	}
    }

    /**
     * Tells the thread that the thread should complete the remaining work then
     * stop.
     */
    public void complete() {
	running = false;
	Log.i(TAG,
		String.format(
			"Database write completing. Measurements on buffer: %d. Writes completed: %d",
			buffer.size(), writes));
    }

    /**
     * Returns true if running is false and the buffer has been emptied.
     * 
     * @return
     */
    public boolean isComplete() {
	return !running && buffer.size() == 0;
    }
}
