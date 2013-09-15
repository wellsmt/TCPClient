// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import com.example.tcpclient.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ComponentOptionsActivity extends AppMenuActivity  {

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.component_options_layout);
    }
    
    public void closeClickEvent(View view){
        Intent i = new Intent(this, AndroidTabLayoutActivity.class);  
        startActivity(i);
    }
}
