/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marc
 */
public class DataInterpreterTest implements MessageConsumer {

    public DataInterpreterTest() {
    }

    private Message messageReceived = null;
    @Override
    public void onMessage(Message message){
       messageReceived = message;
    }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
   }

    @Before
    public void setUp() {
       messageReceived = null;
    }

    @After
    public void tearDown() {
    }

   /**
    * Test of addRawData method, of class DataInterpreter.
    */
   @Test
   public void testAddRawData() {
      System.out.println("addRawData");
      byte[] buffer = null;

      DataInterpreter instance = new DataInterpreter();
      instance.addRawData(buffer);
   }

   /**
    * Test of registerObserver method, of class DataInterpreter.
    */
   @Test
   public void testRegisterObserver() {
      System.out.println("registerObserver");
      MessageConsumer o = this;
      DataInterpreter instance = new DataInterpreter();
      instance.registerObserver(o);
      instance.notifyObservers(new Message("TestMessage"));
      
      assertEquals("TestMessage", messageReceived.getData());
   }

   @Test(expected=NullPointerException.class)
   public void testRegisterNullObserver() {
      System.out.println("registerObserver");
      MessageConsumer o = null;
      DataInterpreter instance = new DataInterpreter();
      instance.registerObserver(o);
   }

   /**
    * Test of removeObserver method, of class DataInterpreter.
    */
   @Test
   public void testRemoveObserverNotRegistered() {
      System.out.println("removeObserver");
      MessageConsumer o = this;
      DataInterpreter instance = new DataInterpreter();
      instance.removeObserver(o);
      instance.notifyObservers(new Message("TestMessage"));
      assertNull(messageReceived);
   }

      @Test
   public void testRemoveObserverPreviouslyRegistered() {
      System.out.println("removeObserver");
      MessageConsumer o = this;
      DataInterpreter instance = new DataInterpreter();
      instance.registerObserver(o);

      instance.removeObserver(o);
      instance.notifyObservers(new Message("TestMessage"));
      assertNull(messageReceived);
   }

}