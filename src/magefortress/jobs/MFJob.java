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
package magefortress.jobs;

import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.Queue;
import magefortress.channel.MFIChannelSender;
import magefortress.core.MFCreature;
import magefortress.jobs.subtasks.MFSubtask;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;

/**
 * Base class for all jobs. Configuration for the subclasses can be placed in the
 * following methods:
 * <li>
 * <ul>startJob()</ul>
 * <ul>pauseJob()</ul>
 * <ul>cancelJob()</ul>
 * </li>
 *
 * <p><strong>startJob()</strong> is supposed to be used to create the necessary
 * subtasks needed to perform the job and push them in the queue. It will be
 * called when the owner starts working on the job.
 *</p><p>
 * <strong>pauseJob()</strong> can be used to do some clean up when the original
 * job owner threw the towel and the job is waiting for a new owner. Will be
 * called when <code>setOwner()</code> is called with a <code>null</code>
 * parameter.
 *</p><p>
 * <strong>cancelJob()</strong> is called when the job was canceled because
 * of an error in one of the subtasks. Clean-up code can be placed here.
 * </p>
 */
public abstract class MFJob
{
  /**
   * Constructor
   * @param _sender The sender of the job
   */
  public MFJob(final MFIChannelSender _sender)
  {
    this.sender = _sender;
    this.subtaskQueue = new LinkedList<MFSubtask>();
  }

  /**
   * Gets the sender of the job.
   * @return The sender of the job
   */
  final public MFIChannelSender getSender()
  {
    return this.sender;
  }

  /**
   * Adds an owner when the job is given to a creature. Can be set to <code>null
   * </code> in case the original owner stops doing it which leads to the
   * <code>pauseJob()</code> method being called.
   * @param _newOwner The creature who will be doing the job
   */
  final public void setOwner(final MFCreature _newOwner)
  {
    if (this.owner == null && _newOwner != null ) {
      this.owner = _newOwner;
      this.initJob();
    } else if (this.owner != null && _newOwner == null) {
      this.pauseJob();
      this.owner = _newOwner;
    } else {
      this.owner = _newOwner;
    }

  }

  /**
   * Gets the current owner. May be <code>null</code> if the job is not taken yet.
   * @return The current owner or <code>null</code>
   */
  final public MFCreature getOwner()
  {
    return this.owner;
  }

  /**
   * Updates the current task. Internally updates the currently active subtask.
   * If all subtasks are done the job is finished.
   * In case of an error <code>cancelJob()</code> will be called, so that the 
   * job gets an opportunity to clean up.
   * @return <code>true</code> if the job is done
   */
  final public boolean update()
  {
    if (this.owner == null) {
      String msg = "Job: update() must not be called when the job has no owner.";
      logger.severe(msg);
      throw new RuntimeException(msg);
    }

    final MFSubtask currentTask = this.subtaskQueue.peek();

    // stores if the current subtask is done
    boolean subtaskDone = true;

    try {
      subtaskDone = currentTask.update();
    } catch (MFSubtaskCanceledException e) {
      logger.info(e.getMessage());
      this.cancelJob();
      return true;
    }

    // stores if the whole job is done
    boolean jobDone = false;

    // subtask finished, switch to the next one
    if (subtaskDone) {
      this.subtaskQueue.remove();
      // any more subtasks? if not, the whole job is done
      if (this.subtaskQueue.isEmpty())
        jobDone = true;
    }
    return jobDone;
  }

  /**
   * Gets the currently active subtask. If the job is not active it returns
   * <code>null</code>.
   * @return The subtask or <code>null</code>
   */
  final public MFSubtask getActiveSubtask()
  {
    if (!this.isActive()) {
      return null;
    }

    return this.subtaskQueue.peek();
  }

  /**
   * Gets wether the job is currently active, meaning the job has an owner and
   * the list of subtasks is not empty.
   * @return <code>true</code> if the job has an owner and subtasks left
   */
  final public boolean isActive()
  {
    boolean result = true;

    if (this.owner == null) {
      result = false;
    } else if (this.subtaskQueue.peek() == null) {
      result = false;
    }

    return result;
  }

  abstract void initJob();
  public abstract void pauseJob();
  public abstract void cancelJob();

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Log */
  static final Logger logger = Logger.getLogger(MFJob.class.getName());
  /** List of subtasks */
  final Queue<MFSubtask> subtaskQueue;
  /** Where we got the job */
  private final MFIChannelSender sender;
  /** The creature currently doing the job */
  private MFCreature owner;
  
}
