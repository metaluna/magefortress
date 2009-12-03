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

package magefortress.core;

import java.util.LinkedList;

/**
 * The class represents a messaging system and event bus to which actors and
 * event generating objects can subscribe. Each kind of job has its own channel.
 * At the moment only notifications that listeners can apply for available jobs
 * at the sender are broadcast.
 * When an actor receives a message, he's supposed to save a task "apply for job"
 * in his task queue. As soon as he comes around to do it, he has to check if
 * the offer is still up. If it is, he can take it. But until he's actually doing
 * something the job emitter may cancel his contract and give it to someone in
 * the vicinity of the job.
 * 
 */
public class MFCommunicationChannel
{
  private String name;
  private LinkedList<MFIChannelSubscriber> subscribers;
  private LinkedList<MFChannelMessage> messageQueue;

  /**
   * Constructor
   */
  public MFCommunicationChannel(String _name)
  {
    this.name = _name;
    subscribers = new LinkedList<MFIChannelSubscriber>();
    messageQueue = new LinkedList<MFChannelMessage>();
  }

  /**
   * Lets an interested object subscribe to this channel.
   * @param subscriber The subscriber
   */
  public void subscribe(MFIChannelSubscriber subscriber)
  {
    this.subscribers.add(subscriber);
  }

  /**
   * Let's an object unsubscribe itself if it's no longer interested in receiving
   * messages from this channel.
   * @param subscriber The subscriber who wants to leave the channel
   */
  public void unsubscribe(MFIChannelSubscriber subscriber)
  {
    this.subscribers.remove(subscriber);
  }

  /**
   * Calls the <code>update()</code> method on all subscribers telling them
   * of the next enqueued message.
   */
  public void notifySubscribers()
  {
    if (this.messageQueue.size() > 0)
    {
      MFChannelMessage message = this.messageQueue.remove();
      for (MFIChannelSubscriber subscriber : this.subscribers)
      {
        subscriber.update(message);
      }
    }
  }

  /**
   * Saves a message in the delivery queue. It will be delivered when the main
   * game loop can spare enough time to let the channel notify its subscribers
   * about new messages.
   * @param message The message to save for later delivery
   */
  public void enqueueMessage(MFChannelMessage message)
  {
    this.messageQueue.add(message);
  }

    //---vvv---      PRIVATE METHODS      ---vvv---
}
