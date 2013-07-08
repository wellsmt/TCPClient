// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

public class SimpleRectangularDrawable implements Drawable {

    public final static float BOUNDING_BOX_TOLERANCE = 5f;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    private boolean isBeingDragged = false;

    public SimpleRectangularDrawable() {
	super();
    }
    
    public SimpleRectangularDrawable(float x, float y, float width, float height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }

    @Override
    public void centerOn(float x, float y) {
        this.x = x - width/2;
        this.y = y - width/2;
    }

    @Override
    public void translate(float dx, float dy) {
        x += dx;
        y += dy;
    }

    @Override
    public boolean isWithinBoundingBox(float xCoordinate, float yCoordinate) {
        return (xCoordinate < x + width +BOUNDING_BOX_TOLERANCE) && (xCoordinate > x - BOUNDING_BOX_TOLERANCE)
        	    && (yCoordinate < y + height + BOUNDING_BOX_TOLERANCE) && (yCoordinate > y -BOUNDING_BOX_TOLERANCE);
    }

    @Override
    public int getDragWeight() {
        return 10;
    }

    @Override
    public void setDragState(boolean isBeingDragged) {
        this.isBeingDragged = isBeingDragged;
    
    }

    @Override
    public boolean isBeingDragged() {
        return isBeingDragged;
    }

    @Override
    public boolean dropTarget(Drawable obj) {
        return true;
    }

    @Override
    public PointF getOutGoingConnectionPoint() {
        return new PointF(x+width, y + height/2);
    }

    @Override
    public PointF getIncomingConnectionPoint() {
        return new PointF(x, y + height/2);
    }

    @Override
    public void setHighlight(boolean highlight) {
	
    }

    @Override
    public void draw(Canvas canvas) {
	Log.i("SIMPLE_RACTANGULAR_DRAWABLE", "Drawing at ["+ Float.toString(x)+","+ Float.toString(y)+"]");
    }
    
    protected void drawCenteredLabel(String text, Canvas canvas, final Paint paint){
	float textWidth = paint.measureText(text);
	
	float textX =x;
	
	//Set up the starting x. If the text is wider, add a negative
	// offset of half the difference in widths to the start of the 
	// text. Otherwise, apply a positive offset of half the difference
	// in widths.
	if(textWidth > width){
	    textX -= (textWidth - width)/2;
	}
	else{
	    textX += (width - textWidth)/2;
	}
	canvas.drawText(text, textX, y-5, paint);
    }

    @Override
    public PointF getCenterLocation(){
	return new PointF(x + width/2, y + height/2);
    }

}