package com.tacuna.android.plot;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

public class PlotStyle {

    public static final Paint PAINT = new Paint() {
	{
	    this.setColor(Color.RED);
	    // this.paint.setStrokeJoin(Paint.Join.ROUND);
	    this.setStrokeWidth(1.0f);
	}
    };

    public static final Paint DIGITAL_CELL_PAINT = new Paint() {
	{
	    this.setColor(Color.GREEN);
	    this.setStrokeJoin(Paint.Join.ROUND);
	    this.setStrokeWidth(1.0f);
	}
    };
    public static final Paint GRID_PAINT = new Paint() {
	{
	    this.setColor(Color.GRAY);
	    this.setStrokeWidth(1.0f);
	}
    };
    public static final Paint GRID_PAINT_INNER = new Paint() {
	{
	    this.setColor(Color.DKGRAY);
	    this.setStrokeWidth(1.0f);
	    this.setPathEffect(new DashPathEffect(new float[] { 2, 2 }, 0));
	}
    };
    public static final Paint TEXT_PAINT = new Paint() {
	{
	    this.setColor(Color.WHITE);
	    this.setAntiAlias(true);
	    this.setSubpixelText(true);
	    this.setStrokeWidth(1.0f);
	}
    };

    public PlotStyle() {
	// TODO Auto-generated constructor stub
    }

}
