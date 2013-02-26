package com.example.tcpclient;

import android.app.Activity;
import android.widget.ToggleButton;

import com.google.calculator.calc.CalculatorInputTextView;
import com.google.calculator.calc.ComputationResult;

public class ScaleData {
    // TODO: Make generic 
    private CalculatorInputTextView channel0Scale;
    private ToggleButton channel0Toggle;
    
    private CalculatorInputTextView channel1Scale;
    private ToggleButton channel1Toggle;
    
    private CalculatorInputTextView channel2Scale;
    private ToggleButton channel2Toggle;
    
    private String wildcard="$data";    
    
    public ScaleData(String channeldata, Activity activity)
    {		
	wildcard = channeldata;
	channel0Scale = (CalculatorInputTextView)activity.findViewById(R.id.channel0_scale);
	channel0Toggle = (ToggleButton)activity.findViewById(R.id.channel0_toggle);
	
	channel1Scale = (CalculatorInputTextView)activity.findViewById(R.id.channel1_scale);
	channel1Toggle = (ToggleButton)activity.findViewById(R.id.channel1_toggle);
	
	channel2Scale = (CalculatorInputTextView)activity.findViewById(R.id.channel2_scale);
	channel2Toggle = (ToggleButton)activity.findViewById(R.id.channel2_toggle);
    }
    
    public float scaleChannelData(int channel, float data)
    {
	float val = data;
	switch(channel){
		case 0: {		    
		    if(channel0Toggle.isChecked()){			
			ComputationResult result = channel0Scale.performComputation(wildcard,data);			
			val = Float.valueOf( result.toString() );			
		    }			
		}break;
		case 1: {
		    if(channel1Toggle.isChecked()){			
			ComputationResult result = channel1Scale.performComputation(wildcard,data);			
			val = Float.valueOf( result.toString() );	
		    }
		}break;
		case 2: {
		    if(channel2Toggle.isChecked()){			
			ComputationResult result = channel2Scale.performComputation(wildcard,data);			
			val = Float.valueOf( result.toString() );	
		    }
		}break;
		default: break;
	}
	
	return val;
    }
}
