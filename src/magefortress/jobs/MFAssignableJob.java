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

import magefortress.channel.MFIChannelSender;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.creatures.MFCreature;

/**
 * Base class for all jobs that can be assigned and re-assigned. That includes
 * jobs emitted from workshops and construction sites.
 * <p>AssignableJobs won't be active and can't be updated as long as nobody owns/executes
 * them.
 */
public abstract class MFAssignableJob extends MFBaseJob
{
  /**
   * Constructor
   * @param _sender The sender of the job
   */
  public MFAssignableJob(final MFIChannelSender _sender)
  {
    super(null);
    this.sender = _sender;
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
      this.pauseJob();
      this.owner = _newOwner;
      this.initJob();
    }

  }

  @Override
  final public boolean update()
  {
    if (this.owner == null) {
      String msg = "Job: update() must not be called when the job has no owner.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }

    return super.update();
  }

  /**
   * Gets wether the job is currently active, meaning the job has an owner and
   * the list of subtasks is not empty.
   * @return <code>true</code> if the job has an owner and subtasks left
   */
  @Override
  final public boolean isActive()
  {
    boolean result = true;

    if (this.owner == null || !super.isActive()) {
      result = false;
    }

    return result;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Where we got the job */
  private final MFIChannelSender sender;
  
}
