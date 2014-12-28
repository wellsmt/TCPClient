// Copyright 2013 Marc Bernardini.
package com.tacuna.android;

import java.util.concurrent.ArrayBlockingQueue;

import android.os.AsyncTask;
import android.util.Log;

import com.lp.io.MessageConsumer;
import com.lp.io.messages.Message;
import com.lp.io.messages.SimpleDeviceMessage;
import com.tacuna.common.devices.DeviceInterface;
import com.tacuna.common.devices.scpi.Command;

public class BackgroundScpiCommand extends
	AsyncTask<Command, Integer, SimpleDeviceMessage> implements
	MessageConsumer {

    private static final String TAG = "BACKGROUND SCPI COMMAND";
    protected Command outgoing;
    protected ArrayBlockingQueue<SimpleDeviceMessage> result;
    private final DeviceInterface device;

    // protected ScpiMessageExchange exchange;

    public BackgroundScpiCommand(DeviceInterface dev) {
	result = new ArrayBlockingQueue<SimpleDeviceMessage>(1);
	device = dev;
	// exchange = dev.getEx();
	// exchange.registerObserver(this);
    }

    @Override
    protected SimpleDeviceMessage doInBackground(Command... command) {
	try {
	    setOutgoing(command[0]);
	    device.send(outgoing);
	    return result.take();
	} catch (InterruptedException e) {
	    Log.e(TAG, "Interrupted waiting for response.", e);
	    return null;
	}
    }

    public synchronized void setOutgoing(final Command command) {
	this.outgoing = command;
    }

    @Override
    public void onMessage(Message message) {
	// SimpleDeviceMessage msg = (SimpleDeviceMessage) message;
	// if (msg.getChannel() == this.outgoing.getChannel()) {
	// exchange.removeObserver(this);
	// result.add(msg);
	// }
    }

}
