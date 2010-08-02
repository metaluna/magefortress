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

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;
import magefortress.creatures.MFCreature;
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
abstract class MFBaseJob implements MFIJob
{
  public MFBaseJob(MFCreature _owner)
  {
    this.owner = _owner;
    this.subtaskQueue = new LinkedList<MFSubtask>();
  }

  /**
   * Updates the current task. Internally updates the currently active subtask.
   * If all subtasks are done the job is finished.
   * <p>In case of an error <code>cancelJob()</code> will be called, so that the
   * job gets an opportunity to clean up.
   * <p>Only to be called when the job has been assigned to somebody!
   * @return <code>true</code> if the job is done
   */
  @Override
  public boolean update()
  {
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
   * Gets wether the job is currently active, meaning the list of subtasks is
   * not empty.
   * @return <code>true</code> if the job has any subtasks left
   */
  @Override
  public boolean isActive()
  {
    return this.subtaskQueue.peek() != null;
  }

  /**
   * Gets the currently active subtask. If the job is not active, it returns
   * <code>null</code>.
   * @return The subtask or <code>null</code>
   */
  @Override
  public MFSubtask getActiveSubtask()
  {
    if (!this.isActive()) {
      return null;
    }

    return this.subtaskQueue.peek();
  }

  /**
   * Gets the current owner. May be <code>null</code> if the job is not taken yet.
   * @return The current owner or <code>null</code>
   */
  @Override
  public MFCreature getOwner()
  {
    return this.owner;
  }

  //---vvv---      PROTECTED METHODS      ---vvv---
  /** Log */
  protected static final Logger logger = Logger.getLogger(MFBaseJob.class.getName());
  /** The creature currently doing the job */
  protected MFCreature owner;

  protected void addSubtask(MFSubtask _subtask)
  {
    this.subtaskQueue.add(_subtask);
  }

  protected void clearSubtasks()
  {
    this.subtaskQueue.clear();
  }

  protected abstract void initJob();
  public    abstract void pauseJob();
  public    abstract void cancelJob();

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** List of subtasks */
  private final Queue<MFSubtask> subtaskQueue;

}
