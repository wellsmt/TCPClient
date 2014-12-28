package com.tacuna.android.intents;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

public class IntentFactory extends Intent {

    @SuppressLint("NewApi")
    /**
     * Creates an Email intent and attaches a csv data file.
     * @param csvFile
     * @return
     */
    public static Intent getSendEmailIntent(File csvFile) {
	Intent intent = new Intent(Intent.ACTION_SEND);
	intent.setType("message/rfc822");
	intent.putExtra(Intent.EXTRA_SUBJECT, "WIFIDaq Data File");
	Uri uri = Uri.fromFile(csvFile);
	csvFile.setReadable(true, false);
	intent.putExtra(Intent.EXTRA_STREAM, uri);
	return intent;
    }

}
