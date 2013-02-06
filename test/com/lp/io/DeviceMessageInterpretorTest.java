// Copyright 2013 Marc Bernardini.
package com.lp.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeviceMessageInterpretorTest implements MessageConsumer {

    private Message messageReceived = null;
    @Override
    public void onMessage(Message message){
       messageReceived = message;
    }
    
    DeviceMessageInterpretor instance;
    @Before
    public void setUp(){
	instance = new DeviceMessageInterpretor();
	instance.registerObserver(this);
    }
    
    @After
    public void tearDown(){
	instance.removeObserver(this);
    }
    
    @Test
    public void testWellFormed() {
	byte[] buf = "1,12345,0.123552\r\n".getBytes();
	instance.addRawData(buf);
	
	assertNotNull(messageReceived);
	assertTrue(messageReceived instanceof SimpleDeviceMessage);
	assertEquals(1, ((SimpleDeviceMessage)messageReceived).getChannel());
	assertEquals(12345, ((SimpleDeviceMessage)messageReceived).getDeviceTimestamp());
	assertEquals(0.123552, ((SimpleDeviceMessage)messageReceived).getValue(), 0.0001);
    }
    
    @Test
    public void testNotWellFormed() {
	byte[] buf = "1,fs12345,0ads.123552\r\n".getBytes();
	instance.addRawData(buf);
	
	assertNull(messageReceived);
    }

}
