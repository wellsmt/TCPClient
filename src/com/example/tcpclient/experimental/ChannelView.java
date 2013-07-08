// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

class ChannelView extends SimpleRectangularDrawable {
	float originalX,originalY;
	
	String name;
	boolean isBeingDragged = false;
	
	Paint fillPaint = new Paint();
	Paint textPaint = new Paint();
	ChannelView(final String name){
	    super(0,0,50,50);
	    fillPaint.setColor(Color.argb(255, 255, 255, 99));
	    
	    textPaint.setColor(Color.argb(255, 50, 50, 50));
	    textPaint.setAntiAlias(true);
	    this.name = name;
	}
	

	@Override
	public void setHighlight(boolean highlight) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public void draw(Canvas canvas) {
	    //canvas.drawRect(x, y, x + width, y + height, fillPaint);
	    canvas.drawRoundRect(new RectF(x, y, x + width, y + height), 5, 5, fillPaint);
	    textPaint.setStrokeWidth(2);
	    textPaint.setTextSize(14);
	    canvas.drawText(name, x+10, y+height-3, textPaint);
	}


	@Override
	public int getDragWeight() {
	    return 100;
	}

	@Override
	public void setDragState(boolean isBeingDragged) {
	    this.isBeingDragged = isBeingDragged;
	    if(isBeingDragged){
	       this.originalX = this.x;
	       this.originalY = this.y;
	    }
	    else {
	       this.x = this.originalX;
	       this.y = this.originalY;
	    }
	}

	@Override
	public boolean isBeingDragged() {
	    return isBeingDragged;
	}

	@Override
	public boolean dropTarget(Drawable obj) {
	    return false;
	}

	@Override
	public PointF getOutGoingConnectionPoint() {
	    return new PointF(x+width, y+height/2);
	}

	@Override
	public PointF getIncomingConnectionPoint() {
	    //There is no incoming point
	    return null;
	}
}