// Copyright 2013 Marc Bernardini.
package com.example.tcpclient.experimental;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;

import com.example.tcpclient.experimental.SchematicView.DragManager;

class DeviceView extends SimpleRectangularDrawable {
	Paint strokePaint = new Paint();
	Paint fillPaint = new Paint();
	Paint highLightPaint = new Paint();
	
	boolean highlight = false;
	
	List<ChannelView> channels = new ArrayList<ChannelView>();
	
	DeviceView(DragManager dragManager) {
	    super(25,25,100,300);
	    strokePaint.setColor(Color.argb(255, 50, 50, 50));
	    strokePaint.setStrokeWidth(2);
	    strokePaint.setTextSize(20);
	    strokePaint.setAntiAlias(true);
	    strokePaint.setTypeface(Typeface.SANS_SERIF);
	    fillPaint.setColor(Color.argb(255,200,200,200));
	    fillPaint.setStrokeWidth(0);
	    highLightPaint.setColor(Color.YELLOW);
	    
	    channels.add(new ChannelView("A1"));
	    channels.add(new ChannelView("A2"));
	    channels.add(new ChannelView("A3"));
	    channels.add(new ChannelView("A4"));
	
	    for(ChannelView cv:channels){
		dragManager.register(cv);
	    }
	}
	
	/* (non-Javadoc)
	 * @see com.example.tcpclient.experimental.Drawable#setHighlight(boolean)
	 */
	@Override
	public void setHighlight(boolean highlight){
	    this.highlight = highlight;
	}

	/* (non-Javadoc)
	 * @see com.example.tcpclient.experimental.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
	    if(highlight){
		highLightPaint.setShadowLayer(10.0f, 0.0f, 0.0f, 0xFFFFFF00);
		highLightPaint.setAntiAlias(true);
		canvas.drawRect(x, y, x + width, y + height, highLightPaint);
	    }
	    
	    canvas.drawRect(x, y, x + width, y + height, strokePaint);
	    canvas.drawRect(x + 1, y + 1, x + width - 1, y + height - 1,
		    fillPaint);
	    
	    drawCenteredLabel("DAQ 1234", canvas, SchematicTheme.COMPONENT_LABEL_PAINT);
	    
	    float verticalCenter = x+width;
	    float verticalStart = 20 + y;
	    for(ChannelView cv:channels){
		if(!cv.isBeingDragged()){
		  cv.centerOn(verticalCenter, verticalStart);
		  cv.draw(canvas);
		}
		else {
		  canvas.drawLine(verticalCenter, verticalStart, cv.x, cv.y - cv.height/2, strokePaint);
		}
		    
		verticalStart += 30 + cv.height;
	    }
	}


	@Override
	public int getDragWeight() {
	    return 10;
	}

	@Override
	public boolean dropTarget(Drawable obj) {
	    return true;
	}

	@Override
	public PointF getOutGoingConnectionPoint() {
	    // TODO This is that same as the incoming connection point.
	    //  This might need to get changed.
	    return new PointF(x, y+height/2);
	}

	@Override
	public PointF getIncomingConnectionPoint() {
	    return new PointF(x, y+height/2);
	}
}
