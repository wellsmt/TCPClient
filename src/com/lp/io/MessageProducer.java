/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

/**
 *
 * @author marc
 */
public interface MessageProducer {
   public void registerObserver(MessageConsumer o);
   public void removeObserver(MessageConsumer o);
   public void notifyObservers(Message message);
}
