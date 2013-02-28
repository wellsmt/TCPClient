// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.graphics.Canvas;
import android.graphics.PointF;

interface Drawable {

    public abstract void centerOn(float x, float y);
    
    public abstract void translate(float dx, float dy);
    
    public abstract void setHighlight(boolean highlight);

    public abstract void draw(Canvas canvas);

    public abstract boolean isWithinBoundingBox(float xCoordinate,
	    float yCoordinate);
    
    public abstract int getDragWeight();
    
    public abstract void setDragState(boolean isBeingDragged);
    
    public abstract boolean isBeingDragged();
    
    public abstract boolean dropTarget(Drawable obj);
    
    public abstract PointF getOutGoingConnectionPoint();

    public abstract PointF getIncomingConnectionPoint();
}