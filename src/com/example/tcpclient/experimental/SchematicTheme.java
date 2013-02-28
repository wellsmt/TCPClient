// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class SchematicTheme {

    public static final Paint COMPONENT_LABEL_PAINT = getComponentLabelPaint();
    public static final Paint ICONIZED_COMPONENT_PAINT = getIconizedComponentPaint();
    
    protected static Paint getIconizedComponentPaint(){
	Paint temp = new Paint();
	temp.setColor(Color.argb(255, 255, 0, 0));
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
}
