/*
 *  Copyright (c) 2010 Simon Hardijanto
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
package magefortress.jobs;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;
import magefortress.channel.MFChannelMessage;
import magefortress.creatures.MFCreature;

/**
 * Handles job receiving, processing, ordering, and updating.
 */
public class MFJobQueue
{

  public MFJobQueue(MFCreature _owner)
  {
    if (_owner == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a " +
                                                  "job queue without an owner.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.owner = _owner;

    this.jobs = new EnumMap<MFEPriority, Queue<MFIJob>>(MFEPriority.class);
    for (MFEPriority prio : MFEPriority.values()) {
      this.jobs.put(prio, new LinkedList<MFIJob>());
    }

    this.jobAds = new LinkedList<MFChannelMessage>();
  }

  /**
   * Adds a job to the queue.
   * @param _job The new job
   */
  public void add(MFIJob _job)
  {
    if (_job == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot add null job " +
                                      "to job queue of " + this.owner.getName();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.jobs.get(_job.getPriority()).add(_job);
  }

  /**
   * Adds a channel message containing a job offer to the job offer queue. These
   * will be used when there no jobs with normal priority.
   * @param _jobAd The job offer message
   */
  public void addMessage(MFChannelMessage _jobAd)
  {
    if (_jobAd == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot add null " +
                      "channel message to job queue of " + this.owner.getName();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.jobAds.add(_jobAd);
  }

  /**
   * Updates the currently active job. If there is none, it fetches the next
   * available job from the queue.
   */
  public void update()
  {
    final MFIJob job = this.getCurrentJob();

    if (job != null) {
      this.processCurrentJob(job);
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFCreature.class.getName());
  private final MFCreature owner;
  private final EnumMap<MFEPriority, Queue<MFIJob>> jobs;
  private final Queue<MFChannelMessage> jobAds;
  
  private MFIJob currentJob;

  /**
   * Gets the currently active job, otherwise it fetches the first element of
   * the first non-empty queue, beginning with HIGHEST priority.
   * @return the next job or <code>null</code> if empty
   */
  private MFIJob getCurrentJob()
  {
    MFIJob result = this.currentJob;
    
    if (result == null) {
      result = getNextJob();
    }
    
    return result;
  }

  /**
   * Retrieves the next job from the queue with the highest priority.
   * @return The next job or <code>null</code> if all queues are empty
   */
  private MFIJob getNextJob()
  {
    assert this.currentJob == null;

    MFIJob result = null;
    
    for (MFEPriority prio : MFEPriority.values()) {
      // everything above lower priority is empty, first check one of the job ads
      if (prio == MFEPriority.LOWER) {
        result = this.getNextAdvertisedJob();
      }
      // get the first element of this priority
      if (result == null) {
        result = this.jobs.get(prio).poll();
      }
      // found a job?
      if (result != null) {
        break;
      }
    }
    return result;
  }

  /**
   * Checks if the next job advertisement is valid and retrieves it.
   * @return the next advertised job or <code>null</code> if none is available
   */
  private MFIJob getNextAdvertisedJob()
  {
    MFAssignableJob result = null;

    for (MFChannelMessage jobAd : this.jobAds) {
      if (jobAd.getSender().isJobAvailable()) {
        result = jobAd.getSender().getJob();
        result.setOwner(this.owner);
        break;
      }
    }

    return result;
  }

  /**
   * Updates the current job and marks it as finished if it is done.
   * <p>Cannot set the next job now because there might be none. But maybe later
   * a job gets pushed into the queue(s), because of which we have to check each
   * tick.
   * @param _job The job to update
   */
  private void processCurrentJob(MFIJob _job)
  {
    assert _job != null;

    boolean done = _job.update();

    if (done) {
      this.currentJob = null;
    }

  }

}
