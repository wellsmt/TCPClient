package com.tacuna.android.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lp.io.messages.MeasurementMessage;

public class MeasurementsDataSource {

    private static String TAG = "MEASUREMENTS DATA SOURCE";

    private SQLiteDatabase database;
    private final SqlLiteHelper dbHelper;
    private final Context context;

    public MeasurementsDataSource(Context context) {
	this.dbHelper = new SqlLiteHelper(context);
	this.context = context;
    }

    public void open() throws SQLException {
	database = dbHelper.getWritableDatabase();

    }

    public void close() {
	dbHelper.close();
    }

    public void beginTransaction() {
	database.beginTransaction();
    }

    public void endTransaction() {
	database.setTransactionSuccessful();
	database.endTransaction();
    }

    public void insert(MeasurementMessage message) {
	ContentValues values = new ContentValues();
	for (int ii = 0; ii != message.analogData.length; ii++) {
	    float value = message.analogData[ii];
	    if (Float.isNaN(value)) {
		continue;
	    }
	    values.put(String.format("ai%d", ii + 1), value);
	}
	long rowId = database.insert(SqlLiteHelper.TABLE, null, values);
	if (rowId == -1) {
	    Log.w(TAG, "Failed to insert row into database.");
	}
    }

    public void truncate() {
	Log.i(TAG, "Truncating database table");
	database.delete(SqlLiteHelper.TABLE, null, null);
    }

    public File writeToCsv() {
	File outputDir = context.getCacheDir();
	try {
	    File outputFile = File.createTempFile("measurements", ".csv",
		    outputDir);
	    BufferedWriter writer = new BufferedWriter(new FileWriter(
		    outputFile, true));
	    Cursor cursor = database.query(SqlLiteHelper.TABLE, null, null,
		    null, null, null, null);
	    writer.write("count,ai1,ai2,ai3,ai4,ai5,ai6,ai7,ai8\n");
	    cursor.moveToFirst();
	    int rows = 0;
	    while (!cursor.isAfterLast()) {
		writer.write(String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f\n",
			cursor.getLong(0), cursor.getFloat(1),
			cursor.getFloat(2), cursor.getFloat(3),
			cursor.getFloat(4), cursor.getFloat(5),
			cursor.getFloat(6), cursor.getFloat(7),
			cursor.getFloat(8)));
		cursor.moveToNext();
		rows++;
	    }
	    Log.i(TAG, String.format("Rows written to a file: %d", rows));
	    cursor.close();
	    writer.flush();
	    writer.close();

	    return outputFile;
	} catch (IOException e) {
	    Log.e(TAG, "Failure writing data file.", e);
	}
	return null;

    }

    public void createMeasurement(ContentValues[] values) {
	try {
	    database.beginTransaction();
	    for (ContentValues value : values) {
		database.insert(SqlLiteHelper.TABLE, null, value);
	    }
	    database.setTransactionSuccessful();
	} catch (SQLException e) {

	} finally {
	    database.endTransaction();
	}
    }
}
