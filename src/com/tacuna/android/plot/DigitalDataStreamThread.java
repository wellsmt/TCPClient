package com.tacuna.android.plot;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tacuna.common.components.ConnectionManager;
import com.tacuna.common.devices.ChannelInterface;
import com.tacuna.common.devices.DigitalInputChannel;

public class DigitalDataStreamThread extends DataStreamThread {
    float paddingRight = 5;
    float paddingLeft = 25;
    private Picture gridLayout;

    public DigitalDataStreamThread(SurfaceHolder surfaceHolder, Context context) {
	super(surfaceHolder, context);

	paddingLeft = textPaint.getTextSize() + padding;
    }

    @Override
    public void run() {
	while (running) {
	    ArrayList<ChannelInterface> activeChannelsList = ConnectionManager.INSTANCE.activeChannelsList;
	    synchronized (surfaceHolder) {
		drawGridToPicture();
		int numCh = activeChannelsList.size();
		// if (total != numCh && numCh > 0) {
		// total = numCh;
		// drawGridToPicture(activeChannelsList);
		// }
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

    protected void doDraw(Canvas canvas,
	    ArrayList<ChannelInterface> activeChannelsList) {
	canvas.restore();

	canvas.drawColor(Color.BLACK);
	gridLayout.draw(canvas);
	try {

	    int channelIndex = 0;
	    for (ChannelInterface ci : activeChannelsList) {
		if (ci instanceof DigitalInputChannel) {
		    doDrawChannel(canvas, (DigitalInputChannel) ci,
			    channelIndex++);
		}
	    }
	} catch (Exception err) {
	    Log.e("AnalogDataStreamThread", "Error drawing channel data", err);
	}
    }

    int numberOfPointsToShow = 50;

    private void doDrawChannel(Canvas canvas, DigitalInputChannel channel,
	    int channelIndex) {
	int numberOfSamples = channel.getNumberOfSamples();
	float gridWidth = canvasWidth - paddingRight - paddingLeft;
	float boundingBoxHeight = canvasHeight - 10;
	float cellWidth = gridWidth / numberOfPointsToShow;
	float cellHeight = (boundingBoxHeight - 2 * padding) / 8;
	for (int ii = 0; ii < numberOfPointsToShow; ii++) {
	    float value = channel.getIndex(numberOfSamples - ii - 1).value;
	    float right = paddingRight + gridWidth - ii * cellWidth;
	    float top = 10 + channelIndex * cellHeight;
	    if (value > 0.5) {
		canvas.drawRect(right - cellWidth + 1, top + 1, right - 1, top
			+ cellHeight - 1, PlotStyle.DIGITAL_CELL_PAINT);
	    }
	}
    }

    protected void drawGridToPicture() {
	gridLayout = new Picture();
	Canvas canvas = gridLayout.beginRecording(canvasWidth, canvasHeight);
	// Draw Horizontal
	float gridWidth = canvasWidth - paddingRight - paddingLeft;
	float boundingBoxHeight = canvasHeight - 10;
	int numGridLines = 8;
	float hlineSpacing = (boundingBoxHeight - 2 * padding) / numGridLines;
	float x1 = paddingLeft;
	float x2 = x1 + gridWidth;
	for (int ii = 0; ii <= numGridLines; ii++) {
	    float y = padding + ii * hlineSpacing;
	    canvas.drawLine(x1, y, x1 - 3, y, gridPaint);
	    canvas.drawLine(x2, y, x2 + 3, y, gridPaint);
	    if (ii == 0 || ii == numGridLines) {
		canvas.drawLine(x1, y, x2, y, gridPaint);
	    } else {
		canvas.drawLine(x1, y, x2, y, gridPaintInner);
	    }
	}

	for (int ii = 0; ii < numGridLines; ii++) {
	    // Draw the channel name on the graph
	    canvas.save();
	    canvas.rotate(-90);
	    String text = String.format("DI%d", ii);
	    float textWidth = textPaint.measureText(text);
	    float y = padding + ii * hlineSpacing;
	    canvas.drawText(text, -(y + hlineSpacing / 2 + textWidth / 2),
		    padding / 2 + textPaint.getTextSize(), textPaint);
	    canvas.restore();
	}

	int numberOfVerticalLines = numberOfPointsToShow;
	float vlineSpacing = gridWidth / numberOfVerticalLines;
	float y1 = padding;
	float y2 = y1 + boundingBoxHeight - 2 * padding;

	for (int ii = 0; ii <= numberOfVerticalLines; ii++) {
	    float x = paddingLeft + ii * vlineSpacing;
	    canvas.drawLine(x, y1, x, y1 - 3, gridPaint);
	    canvas.drawLine(x, y2, x, y2 + 3, gridPaint);
	    if (ii == 0 || ii == numberOfVerticalLines) {
		canvas.drawLine(x, y1, x, y2, gridPaint);
	    } else {
		canvas.drawLine(x, y1, x, y2, gridPaintInner);
	    }
	}
	gridLayout.endRecording();
    }
}
