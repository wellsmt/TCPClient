package com.tacuna.android;

import static com.tacuna.android.ApplicationUtilities.toast;

import java.io.File;

import android.app.Activity;

public class GoogleDriveHelper {

    private final Activity activity;

    // private GoogleApiClient mGoogleApiClient;

    public GoogleDriveHelper(Activity activity) {
	this.activity = activity;
    }

    public void uploadFile(File file) {
	toast(activity, "Cloud uploads are not yet supported :(");
    }
}
