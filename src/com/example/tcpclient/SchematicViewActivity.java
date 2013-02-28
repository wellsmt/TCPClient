// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.tcpclient.R;
import com.example.tcpclient.experimental.SchematicView;

public class SchematicViewActivity extends AppMenuActivity  {
    private SchematicView schematic;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.schematic_view_layout);
	schematic = new SchematicView(this);
	schematic.setBackgroundColor(Color.rgb(235, 255, 215));
	
	FrameLayout layout = (FrameLayout) findViewById(R.id.viewport);
	layout.addView(schematic, 2000, 2000);
    }
}
