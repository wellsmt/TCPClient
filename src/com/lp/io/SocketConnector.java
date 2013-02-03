/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lp.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

/**
 * The Socket connector connects and reads data off of
 *  a Socket. Data read off of the socket is passed to
 *  the DataInterpreter which breaks the data stream into
 *  discrete messages.
 * @author marc
 */
public class SocketConnector extends Thread {

   public enum State {

      Connecting,
      Connected,
      Failed,
      Closed
   }
   private static Logger log = Logger.getLogger(SocketConnector.class.getName());
   private String host;

   public String getHost() {
      return host;
   }

   public int getPort() {
      return port;
   }
   /**
    * Field port
    */
   private int port;
   private Socket cSocket;
   private State state;
   private int bytesSent = 0;

   public int getBytesReceived() {
      return bytesReceived;
   }

   public int getBytesSent() {
      return bytesSent;
   }
   private int bytesReceived = 0;
   private Date firstConnected;
   private Date lastConnected;

   /**
    * Returns the number of milliseconds that this connection
    * has been connected.
    * @return
    */
   public long getConnectionTime(){
      if(firstConnected == null){
         return 0;
      }
      if(lastConnected == null){
         return (new Date()).getTime() - firstConnected.getTime(); 
      }
      return lastConnected.getTime() - firstConnected.getTime();
   }

   /**
    * Protected set state updates the current state of the
    *  SocketConnector object and notifies observers when
    *  the connection state changes.
    * @param state
    */
   protected void setState(State state) {
      State oldState = this.state;
      this.state = state;
      if(this.state != oldState){
         if(state == State.Connected){
            firstConnected = new Date();
         }
         if(oldState == State.Connected){
            lastConnected = new Date();
         }
         notifyListeners("state", oldState, state);
      }
   }

   private DataInterpreter dataInterpreter;
   private int bufferSize = 1024;

   /**
    * Returns the internal buffer size used to pull
    *  data off of the input stream. This should be
    *  sized to be just large enough to fit the incoming
    *  messages.
    * @return bufferSize
    */
   public int getBufferSize() {
      return bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   public SocketConnector(String host, int port, DataInterpreter dataInterpreter) {
      this.host = host;
      this.port = port;
      this.dataInterpreter = dataInterpreter;
      state = SocketConnector.State.Connecting;
      try{
         cSocket = new Socket(host, port);
         start();
      }
      catch(IOException err){
         state = SocketConnector.State.Failed;
      }
   }

   public SocketConnector(Socket socket, DataInterpreter di){
      if(socket.isConnected()){
         host = socket.getInetAddress().getHostName();
         port = socket.getPort();
         cSocket = socket;
         start();
      }

   }

   @Override
   public void run() {
      try {
         cSocket.setTcpNoDelay(true);

         setState(SocketConnector.State.Connected);
         byte[] buffer = new byte[bufferSize];
         while (cSocket.isConnected()) {
            
            //This blocks
            int read = cSocket.getInputStream().read(buffer, 0, bufferSize);
            bytesReceived += read;
            dataInterpreter.addRawData(Arrays.copyOf(buffer, read));
         }
      } catch (Exception exp) {
         log.warning(exp.toString());
         setState(State.Failed);
      }
   }

   public Boolean isConnected() {
      return (state == State.Connected);
   }

   public SocketConnector.State getConnectionState() {
      return this.state;
   }

   public void send(byte[] data) throws IOException {
      if (state == State.Connected) {
         cSocket.getOutputStream().write(data);
         bytesSent += data.length;
      }
   }

   private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
   public void addChangeListener(PropertyChangeListener listener){
      listeners.add(listener);
   }
   public void removeChangeListener(PropertyChangeListener listener){
      listeners.remove(listener);
   }
   protected void notifyListeners(String property, Object oldValue, Object newValue){
      for(PropertyChangeListener listener: listeners){
         listener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
      }
   }
}
