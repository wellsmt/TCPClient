package com.tacuna.android.plot;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.AnalogInputChannel;
import com.tacuna.common.devices.ChannelInterface;

public class DataStreamThread extends Thread {
    private int canvasWidth = 200;
    private int canvasHeight = 400;
    private boolean running = false;
    private final boolean redrawGrid = true;
    private Picture gridLayout;
    private final Paint paint;
    private final Paint gridPaint;
    private final Paint gridPaintInner;
    private final Paint textPaint;
    float padding = 10f;
    float paddingLeft;
    private final float MAX = 10.0f;
    private final float MIN = -10.0f;
    int total = 1;

    private final SurfaceHolder surfaceHolder;
    private final Context context;

    public DataStreamThread(SurfaceHolder surfaceHolder, Context context) {
	this.surfaceHolder = surfaceHolder;
	this.context = context;
	this.paint = new Paint();
	this.paint.setColor(Color.RED);
	// this.paint.setStrokeJoin(Paint.Join.ROUND);
	this.paint.setStrokeWidth(1.0f);

	this.gridPaint = new Paint();
	gridPaint.setColor(Color.GRAY);
	gridPaint.setStrokeWidth(1.0f);
	this.gridPaintInner = new Paint();
	gridPaintInner.setColor(Color.DKGRAY);
	gridPaintInner.setStrokeWidth(1.0f);
	gridPaintInner
		.setPathEffect(new DashPathEffect(new float[] { 2, 2 }, 0));

	this.textPaint = new Paint();
	textPaint.setColor(Color.WHITE);
	textPaint.setAntiAlias(true);
	textPaint.setSubpixelText(true);
	textPaint.setStrokeWidth(1.0f);

	paddingLeft = textPaint.getTextSize() + padding;
	;
    }

    @Override
    public void run() {
	while (running) {
	    ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	    synchronized (surfaceHolder) {

		int numCh = activeChannelsList.size();
		if (total != numCh && numCh > 0) {
		    total = numCh;
		    drawGridToPicture(activeChannelsList);
		}
	    }

	    Canvas c = null;
	    try {
		c = surfaceHolder.lockCanvas(null);
		synchronized (surfaceHolder) {
		    if (c != null) {
			doDraw(c, activeChannelsList);
		    }
		}
	    } finally {
		if (c != null) {
		    surfaceHolder.unlockCanvasAndPost(c);
		}
	    }
	}
    }

    public void doStart() {
	synchronized (surfaceHolder) {
	    ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	    drawGridToPicture(activeChannelsList);
	}
    }

    private float getBoundingBoxHeight() {
	return canvasHeight / total;
    }

    private float transformY(float value, int index) {
	float range = MAX - MIN;

	float boundingBoxHeight = getBoundingBoxHeight();
	float gridHeight = boundingBoxHeight - 2 * padding;
	return -value * (gridHeight / range) + (boundingBoxHeight / 2)
		* (1 + index * 2);
    }

    private void drawGridToPicture(
	    ArrayList<ChannelInterface> activeChannelsList) {
	gridLayout = new Picture();
	Canvas pcanvas = gridLayout.beginRecording(canvasWidth, canvasHeight);
	int channelIndex = 0;
	for (ChannelInterface ci : activeChannelsList) {
	    drawGrid(pcanvas, channelIndex);

	    // Draw the channel name on the graph
	    pcanvas.save();
	    pcanvas.rotate(-90);
	    float textWidth = textPaint.measureText(ci.getName());

	    pcanvas.drawText(ci.getName(), -1
		    * ((channelIndex * 2 + 1) * getBoundingBoxHeight()) / 2
		    - textWidth / 2, padding / 2 + textPaint.getTextSize(),
		    textPaint);
	    pcanvas.restore();

	    channelIndex++;
	}
	gridLayout.endRecording();
    }

    private void drawGrid(Canvas canvas, int index) {
	// Draw Horizontal
	float gridWidth = canvasWidth - padding - paddingLeft;
	float boundingBoxHeight = getBoundingBoxHeight();
	int numGridLines = 10;
	float hlineSpacing = (boundingBoxHeight - 2 * padding) / 10f;
	float x1 = paddingLeft;
	float x2 = x1 + gridWidth;
	for (int ii = 0; ii <= numGridLines; ii++) {
	    float y = padding + ii * hlineSpacing + boundingBoxHeight * index;
	    canvas.drawLine(x1, y, x1 - 3, y, gridPaint);
	    canvas.drawLine(x2, y, x2 + 3, y, gridPaint);
	    if (ii == 0 || ii == numGridLines) {
		canvas.drawLine(x1, y, x2, y, gridPaint);
	    } else {
		canvas.drawLine(x1, y, x2, y, gridPaintInner);
	    }
	}

	float vlineSpacing = gridWidth / numGridLines;
	float y1 = padding + boundingBoxHeight * index;
	float y2 = y1 + boundingBoxHeight - 2 * padding;

	for (int ii = 0; ii <= numGridLines; ii++) {
	    float x = paddingLeft + ii * vlineSpacing;
	    canvas.drawLine(x, y1, x, y1 - 3, gridPaint);
	    canvas.drawLine(x, y2, x, y2 + 3, gridPaint);
	    if (ii == 0 || ii == numGridLines) {
		canvas.drawLine(x, y1, x, y2, gridPaint);
	    } else {
		canvas.drawLine(x, y1, x, y2, gridPaintInner);
	    }
	}
    }

    private void doDrawChannel(Canvas canvas, ChannelInterface channel,
	    int channelIndex) {
	AnalogInputChannel ch = (AnalogInputChannel) channel;
	int samples = ch.getNumberOfSamples();
	float viewWidth = canvasWidth - padding - paddingLeft;
	float dx = viewWidth / samples;
	float lastY = transformY(ch.getIndex(0).value, channelIndex);
	for (int ii = 1; ii < samples; ii++) {
	    float x1 = dx * (ii - 1) + paddingLeft + 1;
	    float y1 = lastY;
	    float x2 = dx * ii + paddingLeft + 1;
	    float y2 = transformY(ch.getIndex(ii).value, channelIndex);
	    canvas.drawLine(x1, y1, x2, y2, paint);
	    lastY = y2;
	}
	float textX = paddingLeft + padding;
	float textY = transformY(8.0f, channelIndex);
	canvas.drawText(
		String.format("%f %s", ch.getCurrentValue(), ch.getUnit()),
		textX, textY, textPaint);
    }

    private void doDraw(Canvas canvas,
	    ArrayList<ChannelInterface> activeChannelsList) {
	canvas.restore();

	canvas.drawColor(Color.BLACK);
	gridLayout.draw(canvas);
	try {

	    int channelIndex = 0;
	    for (ChannelInterface ci : activeChannelsList) {
		doDrawChannel(canvas, ci, channelIndex++);
	    }
	} catch (Exception err) {
	    Log.e("DataStreamThread", "Error drawing channel data", err);
	}

	// for (int ii = 0; ii < canvasWidth; ii++) {
	// float x1 = (ii - 1);
	// float y1 = transformY((float) Math.sin(x1 / 10f) * 11f);
	// float x2 = ii;
	// float y2 = transformY((float) Math.sin(x2 / 10f) * 11f);
	// canvas.drawLine(x1, y1, x2, y2, paint);
	// }
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
}
