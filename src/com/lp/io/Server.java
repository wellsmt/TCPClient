package com.lp.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author marc
 */
public class Server extends Thread {
   private static Logger log = Logger.getLogger(Server.class.getName());
   int port;
   DataInterpreter clientConnectionInterpreter;
   static final int SAMPLES_PER_SEC = 1;
   static final int TICKS_PER_SECOND = 1000;
   
   private int timeToSleepBtwSamples;
   
   public Server(int port, DataInterpreter dataInterpreter) {
      this.port = port;
      this.clientConnectionInterpreter = dataInterpreter;
      this.timeToSleepBtwSamples = (int)Math.floor((double)TICKS_PER_SECOND/(double)SAMPLES_PER_SEC);
      log.info(String.format("Listening on port %d",port));
      start();
   }

   @Override
   public void run(){
      try{
         ServerSocket sserver = new ServerSocket(port);

         while(sserver.isBound()){
            Socket clientSocket = sserver.accept();
            log.info("Accepting connection...");
            clientSocket.setTcpNoDelay(true);
            while(clientSocket.isConnected()){
               Thread.sleep(timeToSleepBtwSamples);
               Date time = new Date();
               String data = getDataLine(time.getTime());
               clientSocket.getOutputStream().write(data.getBytes());
            }
         }
         sserver.close();
      }
      catch(IOException err){
         log.warning(err.toString());
      }
      catch(InterruptedException ie){
         log.warning(ie.toString());
      }
   }
   protected String getDataLine(long timeMs){
	  String dataLine = String.format("%d,%10d,%9.8f\r\n",0,timeMs,123.34567);
      return dataLine;
   }

   public static void main(String[] args) {
      try{
         Server svr = new Server(8889, null);
         svr.join();
      }
      catch(InterruptedException ie){
         log.warning(ie.toString());
      }
   }
}
