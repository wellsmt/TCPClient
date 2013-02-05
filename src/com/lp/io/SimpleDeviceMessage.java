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
    private final int CHANNEL_INDEX = 0;
    private final int TIME_STAMP_INDEX = 1;
    private final int VALUE_INDEX = 2;

    public final int INVALID_CHANNEL = -1;
    int channel = INVALID_CHANNEL;
    long deviceTimestamp;
    double value;

    /**
     * String Constructor. An attempt is made to parse the string
     * into the proper structure.
     * @param data
     */
    public SimpleDeviceMessage(final String data) {
	super(data);
	String[] split = data.split(",");
	if (split.length == EXPECTED_NUMBER_OF_PARAMS) {
	    channel = Integer.parseInt(split[CHANNEL_INDEX]);
	    deviceTimestamp = Long.parseLong(split[TIME_STAMP_INDEX]);
	    value = Double.parseDouble(split[VALUE_INDEX]);
	}
    }

    /**
     * Returns the channel associated with this message.
     * @return channel
     */
    public int getChannel() {
	return channel;
    }

    /**
     * Returns the value that this channel reported at this time.
     * @return value
     */
    public double getValue() {
	return value;
    }
    /** 
     * Returns the time stamp sent from the device 
     * @Return deviceTimestamp ticks for the start of the device.
     */
    public long getDeviceTimestamp() {
        return deviceTimestamp;
    }

}
