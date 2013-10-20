// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tcpclient.R;

public class ComponentOptionsActivity extends AppMenuActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.component_options_layout);
    }

    public void closeClickEvent(View view) {
	Intent i = new Intent(this, AndroidTabLayoutActivity.class);
	startActivity(i);
    }
}
