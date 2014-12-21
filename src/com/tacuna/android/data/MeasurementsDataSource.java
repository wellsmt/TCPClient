package com.tacuna.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MeasurementsDataSource {

    private SQLiteDatabase database;
    private final SqlLiteHelper dbHelper;

    public MeasurementsDataSource(Context context) {
	dbHelper = new SqlLiteHelper(context);
    }

    public void open() throws SQLException {
	database = dbHelper.getWritableDatabase();
    }

    public void close() {
	dbHelper.close();
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
