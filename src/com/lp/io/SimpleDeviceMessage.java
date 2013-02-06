package com.lp.io;

/**
 * Data message type sent by the sensor device. The
 *  single line message is passed to the constructor and
 *  parsed into the appropriate fields.
 * @author marc
 *
 */
public class SimpleDeviceMessage extends Message {
	private final int EXPECTED_NUMBER_OF_PARAMS = 3;
	private final int CHANNEL_INDEX=0;
	private final int TIME_STAMP_INDEX=1;
	private final int VALUE_INDEX=2;
	int channel;
	long timestamp;
	double value;
	
	public SimpleDeviceMessage(String data) {
		super(data);
		String[] split = data.split(",");
		if(split.length == EXPECTED_NUMBER_OF_PARAMS){
			channel = Integer.parseInt(split[CHANNEL_INDEX]);
			timestamp = Long.parseLong(split[TIME_STAMP_INDEX]);
			value = Double.parseDouble(split[VALUE_INDEX]);
		}
	}

	public int getChannel() {
		return channel;
	}

	public double getValue() {
		return value;
	}

}
