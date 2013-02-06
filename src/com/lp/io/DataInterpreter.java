/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import java.util.ArrayList;


/**
 *
 * @author marc
 */
public class DataInterpreter implements MessageProducer {
   private ArrayList<MessageConsumer> observers = new ArrayList<MessageConsumer>();

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
