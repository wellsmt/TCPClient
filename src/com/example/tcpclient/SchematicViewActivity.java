// Copyright 2013 Marc Bernardini.
package com.example.tcpclient;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.tcpclient.experimental.SchematicView;

public class SchematicViewActivity extends AppMenuActivity  {
    private SchematicView schematic;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.schematic_view_layout);
	schematic = (SchematicView) findViewById(R.id.viewport);;
	schematic.setBackgroundColor(Color.rgb(235, 255, 215));
	//FrameLayout layout = (FrameLayout) findViewById(R.id.viewport);
	//layout.addView(schematic, 2000, 2000);
    }
    
    public void showPopup(View view){
	SchematicViewAddDialog cdd=new SchematicViewAddDialog(this);
	cdd.show();  
    }
    
    @Override
    public void onPause(){
	super.onPause();
	schematic.save();
    }
}
