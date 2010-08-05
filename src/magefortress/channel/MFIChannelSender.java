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

import magefortress.core.*;
import magefortress.jobs.MFAssignableJob;

/**
 * Each object that has jobs to offer has to implement this interface. Actors
 * who wish to apply for the job will listen to the channel and ask the sender
 * if the job is still available when they have time to do it. Finally they'll
 * grab the job.
 *
 * @see MFCommunicationChannel
 * @see MFChannelMessage
 * @see MFIChannelSubscriber

 */
public interface MFIChannelSender
{
  /**
   * Before a job applicant can grab the job he has to check if it's still
   * available by calling this method.
   * @return Is a job still available?
   */
  public boolean isJobAvailable();

  /**
   * When a job applicant has checked if there's still a job available he is
   * allowed to grab it. Job takers have to assign themselves to the job.
   * @return Returns <code>null</code> if no job is available. Otherwise it
   * returns a job.
   */
  public MFAssignableJob getJob();

  /**
   * The location of the sender.
   * @return The location of the sender.
   */
  public MFLocation getLocation();

  /**
   * Will be called when a new subscriber registered to the channel of this
   * sender. The sender is supposed to enqueue messages to the new subscriber
   * as soon as possible.
   * @param subscriber The new subscriber
   */
  public void newSubscriber(MFIChannelSubscriber subscriber);
}
