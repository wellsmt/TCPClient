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
}
