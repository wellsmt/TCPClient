// Copyright Marc Bernardini 2013
package com.lp.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The DataInterpreter produces messages using data received from 
 *  a source. Typically the source is a Socket but can be anything
 *  that produces bytes of data.
 * @author marc
 */
public class DataInterpreter implements MessageProducer {
   private List<MessageConsumer> observers = new ArrayList<MessageConsumer>();

   public void addRawData(byte[] buffer){
      System.out.println(buffer);
   }

   @Override
   public void registerObserver(MessageConsumer consumer){
      if(consumer == null){
         throw new NullPointerException("Cannot register null MessageConsumer.");
      }
      observers.add(consumer);
   }

   @Override
   public void removeObserver(MessageConsumer consumer) {
      observers.remove(consumer);
   }

   @Override
   public void notifyObservers(Message message){
      for (MessageConsumer consumer:observers){
         consumer.onMessage(message);
      }
   }

}
