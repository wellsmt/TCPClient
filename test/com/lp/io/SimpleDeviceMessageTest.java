package com.lp.io;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleDeviceMessageTest {

	@Test
	public void testChannelSet() {
		SimpleDeviceMessage msg = new SimpleDeviceMessage("1,123456,0.298348957");
		assertEquals(1, msg.getChannel());
	}

	@Test
	public void testValueSet() {
		SimpleDeviceMessage msg = new SimpleDeviceMessage("1,123456,0.298348957");
		assertEquals(0.298348757, msg.getValue(),0.00001);
	}
	
	@Test
	public void testIncorrectFormat(){
	    SimpleDeviceMessage msg = new SimpleDeviceMessage("");
	    assertEquals(0,msg.getChannel());
	    assertEquals(0.0, msg.getValue(), 0.0001);
	}
	
	@Test
	public void testIncorrectFormatDos(){
	    SimpleDeviceMessage msg = new SimpleDeviceMessage("ksursfg");
	    assertEquals(-1,msg.getChannel());
	    assertEquals(0.0, msg.getValue(), 0.0001);
	}
	
	@Test(expected=NumberFormatException.class)
	public void testIncorrectFormatTres(){
	    SimpleDeviceMessage msg = new SimpleDeviceMessage("1.0hj,b0.34sf56;29,f");
	    assertEquals(0,msg.getChannel());
	    assertEquals(0.0, msg.getValue(), 0.0001);
	}
}
