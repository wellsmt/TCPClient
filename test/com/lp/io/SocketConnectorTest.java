/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import com.lp.io.SocketConnector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.Socket;
import java.net.ServerSocket;
/**
 *
 * @author marc
 */
public class SocketConnectorTest extends DataInterpreter implements PropertyChangeListener {

   private boolean propertyChangeWasCalled = false;
   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      propertyChangeWasCalled = true;
   }

    public SocketConnectorTest() {
    }

    //TODO: Test that data sent to the socket is
    // passed the the DataInterpreter
    private boolean wasCalled = false;
    @Override
    public void addRawData(byte[] buf){
       wasCalled = true;
    }

    @Before
    public void setUp() {
      propertyChangeWasCalled = false;
      wasCalled = false;
    }

    @After
    public void tearDown() {
    }

   /**
    * Test of run method, of class SocketConnector.
    */
   @Test
   public void testRun() throws Exception {
      System.out.println("run");
      SocketConnector instance = new SocketConnector("localhost", 8080,this);
      instance.addChangeListener(this);
      
      Thread.sleep(1000);
      assertTrue(instance.isConnected());
      assertEquals(SocketConnector.State.Connected,
                   instance.getConnectionState());
      assertTrue(propertyChangeWasCalled);
   }

      @Test
   public void testRun_NoServer() throws Exception {
      SocketConnector instance = new SocketConnector("localhost", 9999,this);
      instance.addChangeListener(this);

      Thread.sleep(1000);
      assertFalse(instance.isConnected());
      assertEquals(SocketConnector.State.Failed,
                   instance.getConnectionState());
      assertTrue(propertyChangeWasCalled);
   }


@Test
public void testGetConnectionTime_neverConnected(){
   SocketConnector instance = new SocketConnector("localhost", 9999,this);
   assertEquals(0,instance.getConnectionTime());
}
}