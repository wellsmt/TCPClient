// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.tcpclient.ComponentOptionsActivity;

public class SchematicView extends View {
    private static final String TAG = "SCHEMATIC_VIEW";
    private static final int INVALID_POINTER_ID = -1;
    
    private float MIN_X_COORDINATE = -580f;
    private float MAX_X_COORDINATE = 1020f;
    private int displayWidth = 0;
    private int displayHeight = 0;
    
    
    private List<Drawable> drawables = new ArrayList<Drawable>();
    private Set<Connection> connections = new HashSet<Connection>();
    private DragManager dragManager = new DragManager();
    
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    
    private float xTranslation = 0.f;
    private float yTranslation = 0.f;
    
    
    
    GestureDetector gestureDetector;
    LayoutInflater inflater;
    
    SchematicSaver saver;
    
    public SchematicView(Context context) {
	super(context);
	initialize(context);
    }
    public SchematicView(Context context, AttributeSet attr){
	super(context, attr);
	initialize(context);
    }
    public SchematicView(Context context, AttributeSet attr, int defStyle){
	super(context, attr, defStyle);
	initialize(context);
    }
    
    protected void initialize(Context context){
	saver = new SchematicSaver(context);
	
	inflater = (LayoutInflater)context.getSystemService
		      (Context.LAYOUT_INFLATER_SERVICE);
	
	setLongClickable(true);
	mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	
	
	DeviceView device1 = new DeviceView(dragManager);
	device1.centerOn(50, 150);
	drawables.add(device1);
	dragManager.register(device1);
	DeviceView device2 = new DeviceView(dragManager);
	device2.centerOn(150, 150);
	drawables.add(device2);
	dragManager.register(device2);
	
	TimeSeriesChartView chart = new TimeSeriesChartView(getResources());
	chart.centerOn(300, 300);
	drawables.add(chart);
	dragManager.register(chart);
	
	DataLoggerView log = new DataLoggerView(getResources());
	log.centerOn(450, 300);
	drawables.add(log);
	dragManager.register(log);
	
	TabularDataView table = new TabularDataView(getResources());
	log.centerOn(300, 450);
	drawables.add(table);
	dragManager.register(table);
	
	
	gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
	    public void onLongPress(MotionEvent e) {
	        Log.e(TAG, "onLongPress");
	        Intent i = new Intent(getContext(), ComponentOptionsActivity.class);  
	        getContext().startActivity(i);
	    }
	});
    }
    
    public void save(){
	JSONArray components = new JSONArray();
	for(Drawable drw:drawables){
	    JSONObject obj = new JSONObject();
	    try {
		obj.put("componentName", "test");
		PointF coords = drw.getCenterLocation();
		obj.put("xCenter", coords.x);
		obj.put("yCenter", coords.y);
		components.put(obj);
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
	}
	JSONObject schematic= new JSONObject();
	try {
	    schematic.put("components", components);

	    saver.save(schematic);
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException err) {
	    Log.e(TAG, "Error writing out schematic.", err);
	}
    }
    
    @Override
    public void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	canvas.save();
	canvas.scale(mScaleFactor, mScaleFactor);
	canvas.translate(xTranslation, yTranslation);
	Log.i(TAG,"onDraw");
	
	//TODO: This can probably be made much more efficient.
	Canvas bg = canvas;
	for(int ii = -50; ii != 51; ii++){
	    int minorOffset = 20*ii;
	    if( ii%10 == 0){
		int majorOffset = 20*ii;
		bg.drawLine(-1000, majorOffset, 1000, majorOffset, SchematicTheme.BACKGROUND_MAJOR_LINES_PAINT);
		bg.drawLine(majorOffset, -1000, majorOffset,1000, SchematicTheme.BACKGROUND_MAJOR_LINES_PAINT);
	    }else{
		bg.drawLine(-1000, minorOffset, 1000, minorOffset, SchematicTheme.BACKGROUND_MINOR_LINES_PAINT);
	    	bg.drawLine(minorOffset, -1000, minorOffset,1000, SchematicTheme.BACKGROUND_MINOR_LINES_PAINT);
	    }
	}
	
	for(Drawable obj:drawables){
	    obj.draw(canvas);
	}
	
	for(Connection con:connections){
	    con.draw(canvas);
	}
	
	canvas.restore();
    }
    
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	displayWidth = widthMeasureSpec;
	displayHeight = heightMeasureSpec;
    }

    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;

    
    class DragManager{
    private Drawable objectBeingDragged = null;
    private List<Drawable> draggables = new ArrayList<Drawable>();
    public void register(Drawable drawable){
	draggables.add(drawable);
    }
    
    public void unregister(Drawable object){
	draggables.remove(object);
    }
    
    public boolean isDragInProgress(){
	return (objectBeingDragged != null);
    }
    
    protected void beginObjectDrag(float x,float y){
	for(Drawable obj:draggables){
	    if(obj.isWithinBoundingBox((x - xTranslation)/mScaleFactor, (y - yTranslation)/mScaleFactor)){
		if(objectBeingDragged == null || obj.getDragWeight() > objectBeingDragged.getDragWeight()){
		    objectBeingDragged = obj;
		}
	    }
	}
	if(objectBeingDragged != null){
	    objectBeingDragged.setDragState(true);
	}
    }
    
    protected void translateTouchedObject(float dx, float dy){
	if(objectBeingDragged != null){
	    objectBeingDragged.translate(dx/mScaleFactor, dy/mScaleFactor);
	    invalidate();
	}
    }
    
    protected void stopObjectDrag(float x, float y){
	if(objectBeingDragged != null){
	    objectBeingDragged.setHighlight(false);
	    objectBeingDragged.setDragState(false);
	    float translatedX = (x - xTranslation)/mScaleFactor;
	    float translatedY = (y - yTranslation)/mScaleFactor;
	    for(Drawable dropTarget:draggables){
		if(objectBeingDragged != dropTarget && dropTarget.isWithinBoundingBox(translatedX,translatedY)){
		    if(dropTarget.dropTarget(objectBeingDragged)){
			connections.add(new Connection(objectBeingDragged, dropTarget));
		    }
		}
	    }
	    int xMultiplier = (int)translatedX/20;
	    int yMultiplier = (int)translatedY/20;
	    objectBeingDragged.centerOn(20f*xMultiplier, 20f*yMultiplier);
	    invalidate();
	}
	objectBeingDragged = null;
     }
    }
    
    
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	// Let the ScaleGestureDetector inspect all events.
	mScaleDetector.onTouchEvent(ev);
	if(mScaleDetector.isInProgress()){
	    return true;
	}
	gestureDetector.onTouchEvent(ev);
	final int action = ev.getAction();
	switch (action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN: {
	    final float x = ev.getX();
	    final float y = ev.getY();
	    
	    dragManager.beginObjectDrag(x,y);
	    
	    mLastTouchX = x;
	    mLastTouchY = y;
	    mActivePointerId = ev.getPointerId(0);
	    Log.i(TAG, String.format("ACTION_DOWN: [%f,%f]", x, y));
	    break;
	}

	case MotionEvent.ACTION_MOVE: {
	    final int pointerIndex = ev.findPointerIndex(mActivePointerId);
	    final float x = ev.getX(pointerIndex);
	    final float y = ev.getY(pointerIndex);
	    Log.i(TAG, String.format("ACTION_MOVE: [%f,%f]", x, y));
	    // Only move if the canvasScaleGestureDetector isn't processing a gesture.
	    /*
	     * if (!mScaleDetector.isInProgress()) { final float dx = x -
	     * mLastTouchX; final float dy = y - mLastTouchY;
	     * 
	     * mPosX += dx; mPosY += dy;
	     * 
	     * invalidate(); }
	     */
            final float dy = y - mLastTouchY;
	    final float dx = x - mLastTouchX;
	    if(dragManager.isDragInProgress()){
		dragManager.translateTouchedObject(dx,dy);
	    }
	    else{
		xTranslation += dx;
		Log.i(TAG, String.format("Translation [%f,%f]",xTranslation,yTranslation));
		if(xTranslation < MIN_X_COORDINATE){
		    xTranslation = MIN_X_COORDINATE;
		}
		if(xTranslation > MAX_X_COORDINATE) {
		    xTranslation = MAX_X_COORDINATE;
		}
		
		yTranslation += dy;
		invalidate();
	    }
	    mLastTouchX = x;
	    mLastTouchY = y;

	    break;
	}

	case MotionEvent.ACTION_UP: {
	    mActivePointerId = INVALID_POINTER_ID;
	    dragManager.stopObjectDrag(mLastTouchX, mLastTouchY);
	    break;
	}

	case MotionEvent.ACTION_CANCEL: {
	    mActivePointerId = INVALID_POINTER_ID;
	    break;
	}

	case MotionEvent.ACTION_POINTER_UP: {
	    final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	    final int pointerId = ev.getPointerId(pointerIndex);
	    if (pointerId == mActivePointerId) {
		// This was our active pointer going up. Choose a new
		// active pointer and adjust accordingly.
		final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
		mLastTouchX = ev.getX(newPointerIndex);
		mLastTouchY = ev.getY(newPointerIndex);
		mActivePointerId = ev.getPointerId(newPointerIndex);
	    }
	    break;
	}
	
	}
	return true;
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();

	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 50.0f));

	        invalidate();
	        return true;
	    }
	}
}
