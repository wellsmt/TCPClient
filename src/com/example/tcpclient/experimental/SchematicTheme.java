// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

public class SchematicTheme {

    public static final Paint COMPONENT_LABEL_PAINT = getComponentLabelPaint();
    public static final Paint ICONIZED_COMPONENT_PAINT = getIconizedComponentPaint();
    public static final Paint BACKGROUND_MAJOR_LINES_PAINT = getBackgroundLinePaint();
    public static final Paint BACKGROUND_MINOR_LINES_PAINT = getBackgroundMinorLinePaint();
    
    protected static Paint getIconizedComponentPaint(){
	Paint temp = new Paint();
	temp.setColor(Color.argb(255, 55, 55, 55));
	temp.setStyle(Style.STROKE);
	temp.setStrokeWidth(1);
	temp.setAntiAlias(true);
	return temp;
    };
    
    protected static Paint getComponentLabelPaint() {
	Paint temp = new Paint();

	temp.setColor(Color.argb(255, 50, 50, 50));
	temp.setStrokeWidth(2);
	temp.setTextSize(20);
	temp.setAntiAlias(true);
	temp.setTypeface(Typeface.SANS_SERIF);
	return temp;
    }
    
    protected static Paint getBackgroundLinePaint(){
	Paint temp = new Paint();
	temp.setColor(Color.argb(200, 100, 100, 100));
	temp.setStrokeWidth(1.2f);
	return temp;
    }
    
    protected static Paint getBackgroundMinorLinePaint(){
	Paint temp = new Paint();
	temp.setColor(Color.argb(100, 100, 100, 100));
	temp.setStrokeWidth(0.5f);
	temp.setAntiAlias(true);
	return temp;
    }
}
