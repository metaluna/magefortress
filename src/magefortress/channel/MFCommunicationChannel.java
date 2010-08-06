/*
 *  Copyright (c) 2009 Simon Hardijanto
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package magefortress.channel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>The class represents a messaging system and event bus to which actors and
 * event generating objects can subscribe. Each kind of job has its own channel.
 * At the moment only notifications that listeners can apply for available jobs
 * at the sender are broadcast.</p>
 * <p>When an actor receives a message, he's supposed to save a task "apply for job"
 * in his task queue. As soon as he comes around to do it, he has to check if
 * the offer is still up. If it is, he can take it. But until he's actually doing
 * something the job emitter may cancel his contract and give it to someone in
 * the vicinity of the job.</p>
 * <p>When a new creature subscribes to the channel, all senders will be notified
 * and may send messages about currently enqueued jobs to the new listeners.</p>
 *
 * @see MFChannelMessage
 * @see MFIChannelSender
 * @see MFIChannelSubscriber
 *
 */
public class MFCommunicationChannel
{
  /**
   * Constructor
   */
  public MFCommunicationChannel(String _name)
  {
    if (_name == null) {
      String message = "New Channel: Channel name mustn't be null.";
      logger.log(Level.SEVERE, message);
      throw new NullPointerException(message);
    } else if (_name.trim().equals("")) {
      String message = "New Channel: Channel name mustn't be empty.";
      logger.log(Level.SEVERE, message);
      throw new IllegalArgumentException(message);
    }
    
    this.name = _name;
    this.subscribers = new LinkedList<MFIChannelSubscriber>();
    this.messageQueue = new LinkedList<MFChannelMessage>();
    this.senders = new LinkedList<MFIChannelSender>();
    this.newSubscribers = new LinkedList<MFIChannelSubscriber>();
  }

  /**
   * The name of the channel
   * @return The name of the channel
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Lets an interested object subscribe to this channel.
   * @param subscriber The subscriber
   */
  public void subscribe(MFIChannelSubscriber _subscriber)
  {
    if (_subscriber == null) {
      String message = "Channel '" + this.name + "': Subscribing subscriber mustn't be null.";
      logger.log(Level.SEVERE, message);
      throw new IllegalArgumentException(message);
    }
    logger.fine(this + ": new subscriber " + _subscriber);
    this.subscribers.add(_subscriber);
    this.newSubscribers.add(_subscriber);
  }

  /**
   * Let's an object unsubscribe itself if it's no longer interested in receiving
   * messages from this channel.
   * @param subscriber The subscriber who wants to leave the channel
   */
  public void unsubscribe(MFIChannelSubscriber _subscriber)
  {
    if (_subscriber == null) {
      String message = "Channel '" + this.name + "': Unsubscribing subscriber mustn't be null.";
      logger.log(Level.SEVERE, message);
      throw new IllegalArgumentException(message);
    }

    logger.fine(this + ": subscriber " + _subscriber + " is unsubscribing");
    this.subscribers.remove(_subscriber);
    if (this.newSubscribers.contains(_subscriber)) {
      this.newSubscribers.remove(_subscriber);
    }
  }

  /**
   * Subscribe a new sender. The sender will receive messages about any new
   * listeners to this channel. <strong>Attention!</strong> Sending to the 
   * channel is possible without having subscribed as a sender.
   * @param _sender The new sender
   */
  public void subscribeSender(MFIChannelSender _sender)
  {
    if (_sender == null) {
      String message = "Channel '" + this.name + "': Subscribing sender mustn't be null.";
      logger.log(Level.SEVERE, message);
      throw new IllegalArgumentException(message);
    }

    logger.fine(this + ": new sender " + _sender);
    this.senders.add(_sender);
  }

  /**
   * Subscribed senders may unsubscribe from the channel. This is necessary if
   * the sender is destroyed.
   * @param _sender The sender who wants to unsubscribe
   */
  public void unsubscribeSender(MFIChannelSender _sender)
  {
    if (_sender == null) {
      String message = "Channel '" + this.name + "': Unsubscribing sender mustn't be null.";
      logger.log(Level.SEVERE, message);
      throw new IllegalArgumentException(message);
    }

    logger.fine(this + ": sender " + _sender + " is unsubscribing");
    this.senders.remove(_sender);
    for(Iterator<MFChannelMessage> it = this.messageQueue.iterator();
          it.hasNext(); ) {
      MFChannelMessage message = it.next();
      if (message.getSender() == _sender) {
        it.remove();
      }
    }
  }

  /**
   * Notifies all subscribers of new messages and senders of new subscribers.
   */
  public void update()
  {
    this.notifySenders();
    this.notifySubscribers();
  }

  /**
   * Saves a message in the delivery queue. It will be delivered when the main
   * game loop can spare enough time to let the channel notify its subscribers
   * about new messages.
   * @param message The message to save for later delivery
   */
  public void enqueueMessage(MFChannelMessage _message)
  {
    if (_message == null) {
      String message = "Channel '" + this.name + "': Enqueued message mustn't be null.";
      logger.log(Level.SEVERE, message);
      throw new IllegalArgumentException(message);
    }
    logger.fine( this + ": enqueing message (" +
                                      _message.getSender() +
                                      "@" + _message.getSender().getLocation());
    this.messageQueue.add(_message);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The name of the channel */
  private String name;
  /** The list of subscribers */
  private LinkedList<MFIChannelSubscriber> subscribers;
  /** The list of senders */
  private LinkedList<MFIChannelSender> senders;
  /** The queue of messages waiting to be delivered to the subscribers */
  private LinkedList<MFChannelMessage> messageQueue;
  /** The list of new subscribers who will be sent to all registered senders */
  private LinkedList<MFIChannelSubscriber> newSubscribers;
  /** The logger */
  private static final Logger logger = Logger.getLogger(MFCommunicationChannel.class.getName());

  /**
   * Calls the <code>update()</code> method on all subscribers telling them
   * of enqueued messages.
   */
  private void notifySubscribers()
  {
    // iterate over all messages sending each to all subscribers.
    for (MFChannelMessage message : this.messageQueue) {
      for (MFIChannelSubscriber subscriber : this.subscribers) {
        subscriber.update(message);
      }
    }
    // clear queue
    this.messageQueue.clear();
  }

  /**
   * Calls the <code>newSubscriber()</code> method on all sender telling them
   * of new subscribers.
   */
  private void notifySenders()
  {
    // iterate over all new subscribers sending each to all senders.
    for (MFIChannelSubscriber subscriber : this.newSubscribers) {
      for (MFIChannelSender sender : this.senders) {
        sender.newSubscriber(subscriber);
      }
    }
    // clear queue
    this.newSubscribers.clear();
  }
}
