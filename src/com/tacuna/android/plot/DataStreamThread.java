package com.tacuna.android.plot;

import android.content.Context;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class DataStreamThread extends Thread {

    protected int canvasWidth = 200;
    protected int canvasHeight = 400;
    protected boolean running = false;

    protected final Paint paint = PlotStyle.PAINT;
    protected final Paint gridPaint = PlotStyle.GRID_PAINT;
    protected final Paint gridPaintInner = PlotStyle.GRID_PAINT_INNER;
    protected final Paint textPaint = PlotStyle.TEXT_PAINT;
    protected float padding = 10f;

    protected SurfaceHolder surfaceHolder;
    protected final Context context;

    public DataStreamThread(SurfaceHolder surfaceHolder, Context context) {
	this.surfaceHolder = surfaceHolder;
	this.context = context;
    }

    public void setRunning(boolean running) {
	this.running = running;
    }

    public void setSurfaceSize(int width, int height) {
	synchronized (surfaceHolder) {
	    canvasWidth = width;
	    canvasHeight = height;

	    doStart();
	}
    }

    public void doStart() {

    }

}