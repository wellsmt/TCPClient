package com.tacuna.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlLiteHelper extends SQLiteOpenHelper {

    public SqlLiteHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
	// TODO Auto-generated constructor stub
    }

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String TABLE = "measurements";
    private static final String DATABASE_NAME = "mesurements.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
	    + TABLE
	    + "("
	    + COLUMN_ID
	    + " integer primary key autoincrement, ai1 real,ai2 real,ai3 real,ai4 real,ai5 real,ai6 real,ai7 real,ai8 real);";

    @Override
    public void onCreate(SQLiteDatabase database) {
	database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	Log.w(SqlLiteHelper.class.getName(), "Upgrading database from version "
		+ oldVersion + " to " + newVersion
		+ ", which will destroy all old data");
	db.execSQL("DROP TABLE IF EXISTS measurements");
	onCreate(db);
    }

}
