// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;

public class Connection {
    private Drawable source;
    private Drawable endpoint;
    private Paint paint = new Paint();
    
    Connection(Drawable source, Drawable endpoint){
	this.source = source;
	this.endpoint = endpoint;
	paint.setColor(Color.argb(255, 80, 121, 0));
	paint.setStrokeWidth(3);
	paint.setStrokeCap(Cap.ROUND);
    }
    
    public void draw(Canvas canvas){
	float startX = source.getOutGoingConnectionPoint().x;
	float startY = source.getOutGoingConnectionPoint().y;
	float endX = endpoint.getIncomingConnectionPoint().x;
	float endY = endpoint.getIncomingConnectionPoint().y;
	
	float midX = startX + (endX - startX)/2;
	
	canvas.drawLine(startX, startY, midX, startY, paint);
	canvas.drawLine(midX, startY, midX, endY, paint);
	canvas.drawLine(midX, endY, endX, endY, paint);
    }
}
