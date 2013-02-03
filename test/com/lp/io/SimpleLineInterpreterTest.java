/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marc
 */
public class SimpleLineInterpreterTest implements MessageConsumer {

    public SimpleLineInterpreterTest() {
    }

    private Message lastMessageReceived = null;
    private int numberOfMessagesReceived = 0;
    @Override
    public void onMessage(Message message){
       lastMessageReceived = message;
       numberOfMessagesReceived++;
    }

    @Before
    public void clear(){
       lastMessageReceived = null;
       numberOfMessagesReceived = 0;
    }
    
   /**
    * Test of setEncoding method, of class SimpleLineInterpreter.
    */
   @Test
   public void testSetGetEncoding() {
      System.out.println("setEncoding");
      String encoding = "";
      SimpleLineInterpreter instance = new SimpleLineInterpreter();
      instance.setEncoding("US-ASCII");
      assertEquals("US-ASCII", instance.getEncoding());
   }

   /**
    * Test of setLineTerminator method, of class SimpleLineInterpreter.
    */
   @Test
   public void testSetGetLineTerminator() {
      System.out.println("setLineTerminator");
      String lineTerminator = "\r\n";
      SimpleLineInterpreter instance = new SimpleLineInterpreter();
      instance.setLineTerminator(lineTerminator);
      assertEquals(lineTerminator, instance.getLineTerminator());
   }


   /**
    * Test of addRawData method, of class SimpleLineInterpreter.
    */
   @Test
   public void testAddRawData() throws Exception {
      System.out.println("addRawData");
      String testLine = "This is a test line\n";
      byte[] buf = testLine.getBytes("UTF-8");
      SimpleLineInterpreter instance = new SimpleLineInterpreter();
      instance.registerObserver(this);
      instance.addRawData(buf);

      assertEquals(testLine, lastMessageReceived.getData());
   }

   /**
    * Test of addRawData method, of class SimpleLineInterpreter.
    */
   @Test
   public void testAddRawDataMultipleLines() throws Exception {
      System.out.println("testAddRawDataMultipleLines");
      String testLine = "This is a test line\n";
      String testLine2 = "This is a second test line\n";
      String data = testLine + testLine2;
      byte[] buf = data.getBytes("UTF-8");
      SimpleLineInterpreter instance = new SimpleLineInterpreter();
      instance.registerObserver(this);
      instance.addRawData(buf);

      assertEquals(testLine2, lastMessageReceived.getData());
      assertEquals(2, numberOfMessagesReceived);
   }

      /**
    * Test of addRawData method, of class SimpleLineInterpreter.
    */
   @Test
   public void testAddRawDataLineSpansBuffers() throws Exception {
      System.out.println("testAddRawDataLineSpansBuffers");
      String startOfData = "This is a test line with out terminator. ";
      String endOfData = "This is a second line with a \n";
      String data = startOfData + endOfData;

      SimpleLineInterpreter instance = new SimpleLineInterpreter();
      instance.registerObserver(this);
      instance.addRawData(startOfData.getBytes("UTF-8"));
      instance.addRawData(endOfData.getBytes("UTF-8"));

      assertEquals(data, lastMessageReceived.getData());
      assertEquals(1, numberOfMessagesReceived);
   }
}