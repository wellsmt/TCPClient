package com.lp.io;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

public class DeviceMessageInterpretor  extends DataInterpreter {

	   private static Logger log = Logger.getLogger(SimpleLineInterpreter.class.getName());
	   private String encoding = "UTF-8";

	   public String getEncoding() {
	      return encoding;
	   }

	   public String getLineTerminator() {
	      return lineTerminator;
	   }
	   private StringBuilder buffer = new StringBuilder();
	   private String lineTerminator = "\r\n";

	   public DeviceMessageInterpretor(){

	   }
	   
	   @Override
	   public void addRawData(byte[] buf){
	      try{
	         buffer.append(new String(buf,encoding));
	         parseData();
	      }
	      catch(UnsupportedEncodingException err){
	         log.severe(err.toString());
	      }
	   }

	   private void parseData(){
	      int startOfLineTerminator = buffer.indexOf(lineTerminator);
	      final int NOT_FOUND = -1;
	      while(NOT_FOUND != startOfLineTerminator){
	         //Since indexOf is the start of the line terminator sequence,
	         //  the actual end of line is the indexOf(lineTerminator)+lineTerminator.length()
	         int eol = startOfLineTerminator+lineTerminator.length();
	         notifyObservers(new SimpleDeviceMessage(buffer.substring(0, eol)));
	         buffer.delete(0, eol);
	         startOfLineTerminator = buffer.indexOf(lineTerminator);
	      }
	   }

}
