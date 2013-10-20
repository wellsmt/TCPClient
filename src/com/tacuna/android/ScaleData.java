package com.tacuna.android;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.example.tcpclient.R;

public class ScaleData {
    // TODO: Make generic
    private final EditText channel0Scale;
    private final ToggleButton channel0Toggle;
    private final EditText channel1Scale;
    private final ToggleButton channel1Toggle;
    private final EditText channel2Scale;
    private final ToggleButton channel2Toggle;

    public ScaleData(Activity activity) {
	channel0Scale = (EditText) activity.findViewById(R.id.channel0_scale);
	channel0Toggle = (ToggleButton) activity
		.findViewById(R.id.channel0_toggle);
	channel1Scale = (EditText) activity.findViewById(R.id.channel1_scale);
	channel1Toggle = (ToggleButton) activity
		.findViewById(R.id.channel1_toggle);
	channel2Scale = (EditText) activity.findViewById(R.id.channel2_scale);
	channel2Toggle = (ToggleButton) activity
		.findViewById(R.id.channel2_toggle);
    }

    public float getScale(int channel) {
	float val = 1.00f;
	switch (channel) {
	case 0:
	    if (channel0Scale.getText().toString().length() != 0
		    && channel0Toggle.isChecked())
		val = Float.valueOf(channel0Scale.getText().toString());
	    break;
	case 1:
	    if (channel1Scale.getText().toString().length() != 0
		    && channel1Toggle.isChecked())
		val = Float.valueOf(channel1Scale.getText().toString());
	    break;
	case 2:
	    if (channel2Scale.getText().toString().length() != 0
		    && channel2Toggle.isChecked())
		val = Float.valueOf(channel2Scale.getText().toString());
	    break;
	default:
	    break;
	}

	return val;
    }
}
