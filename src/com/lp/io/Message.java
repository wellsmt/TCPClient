
package com.lp.io;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Somewhat generic message class. This class is used to
 *  encapsulate the message data read off of a socket.
 * @author marc
 */
public class Message {
   /**Time the message was created/received.*/
   private Date timestamp;
   /**The message data. Currently a string, but if
      encoding changes so too can the data type. */
   private String data;

   public Message(String data){
      this.timestamp = new Date();
      this.data = data;
   }

   /**
    * Converts the message to UTF8 encoded byte array.
    * @return
    * @throws UnsupportedEncodingException
    */
   public byte[] toBytes() throws UnsupportedEncodingException{
      return data.getBytes("UTF-8");
   }

   /**
    * Returns the message data. 
    * @return
    */
   public String getData(){
      return data;
   }
   /**
    * Returns the time this message was received
    * @return
    */
   public long getTimestamp(){
	   return timestamp.getTime();
   }
}
