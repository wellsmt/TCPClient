/**
 * 
 */
package com.tacuna.android.plot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author Marc
 * 
 */
public class AnalogDataStream extends SurfaceView implements
	SurfaceHolder.Callback {

    private DataStreamThread dst;

    /**
     * @param context
     */
    public AnalogDataStream(Context context) {
	super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public AnalogDataStream(Context context, AttributeSet attrs) {
	super(context, attrs);

	getHolder().addCallback(this);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AnalogDataStream(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);

	getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder sh, int format, int width,
	    int height) {
	Log.i("AnalogDataStream", String.format(
		"Surface changed. Width: %d\tHeight: %d", width, height));
	dst.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder sh) {
	Canvas canvas = sh.lockCanvas();
	canvas.drawColor(Color.BLACK);
	sh.unlockCanvasAndPost(canvas);

	dst = new DataStreamThread(sh, getContext());
	dst.setRunning(true);
	dst.setSurfaceSize(canvas.getWidth(), canvas.getHeight());
	dst.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
	boolean retry = true;
	dst.setRunning(false);
	while (retry) {
	    try {
		dst.join();
		retry = false;
	    } catch (InterruptedException e) {
	    }
	}
    }

}
