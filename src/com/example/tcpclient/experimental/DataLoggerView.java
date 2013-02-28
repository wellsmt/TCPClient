// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.tcpclient.R;

public class DataLoggerView extends SimpleRectangularDrawable {
    private Bitmap bitmap;
    
    public DataLoggerView(Resources res){
	 bitmap = BitmapFactory.decodeResource(res, R.drawable.log_icon);
	 width = bitmap.getWidth();
	 height = bitmap.getHeight();
    }
    
    @Override
    public void setHighlight(boolean highlight) {
	// TODO Auto-generated method stub
    }

    @Override
    public void draw(Canvas canvas) {
	super.draw(canvas);
	
	canvas.drawRect(x, y, x+width, y+height,SchematicTheme.ICONIZED_COMPONENT_PAINT);
	canvas.drawBitmap(bitmap, x, y, SchematicTheme.ICONIZED_COMPONENT_PAINT);
	String label = "Data Logger";
	
	drawCenteredLabel(label, canvas, SchematicTheme.COMPONENT_LABEL_PAINT);
    }
}
