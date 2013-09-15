// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import com.example.tcpclient.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
/**
 * Base activity used to give all of the activities a common
 *  menu/action bar (Menu on 2.x devices, action bar on 3.+ devices).
 *
 */
public class AppMenuActivity extends Activity {
    
    private final static String TAG = "APP_MENU_ACTIVITY";
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.activity_main, menu);
	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.app_options:
                Log.i(TAG, "App options. Not yet implemented.");
                return true;
            case R.id.rotation_lock:
        	Log.i(TAG, "Lock Orientation.");
        	toggleOrientationLock();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
	// Get the rotation menu item and set the text
	//  accordingly...
	MenuItem lockItem = menu.findItem(R.id.rotation_lock);
	if(isOrientationLocked()){
	   lockItem.setTitle(R.string.rotation_unlock); 
	}
	else{
	   lockItem.setTitle(R.string.rotation_lock);
	}
	return true;
    }
    
    /**
     * Returns true if the orientation is locked. Technically speaking,
     *  it actually returns true if the orientation is not unspecified.
     * @return true if screen is locked.
     */
    private boolean isOrientationLocked(){
	return !(this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
    
    /**
     * Toggles the orientation lock of the screen.
     */
    private void toggleOrientationLock(){
	if(isOrientationLocked()){
	    mUnLockScreenRotation();
	}
	else{
	    mLockScreenRotation();
	}
    }
    
    /**
     * Locks the screen orientation for all Activities to the current position the phone is
     * being held in.  This is not the "typical" mode of operation, but it is useful when in
     * zero-g 8-).tag
     */
    private void mLockScreenRotation() {
	// Stop the screen orientation changing during an event
        switch (this.getResources().getConfiguration().orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
            case Configuration.ORIENTATION_LANDSCAPE:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
        }
    }
    /**
     * UnLocks the screen orientation so that it automatically rotates when the phone 
     * is rotated.  This is the "typical" mode of Activities.
     */
    private void mUnLockScreenRotation() {
	// allow screen rotations again
	this.setRequestedOrientation(
	ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
