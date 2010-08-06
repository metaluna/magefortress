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

import magefortress.channel.MFChannelMessage;
import magefortress.channel.MFIChannelSender;
import magefortress.creatures.MFCreature;
import magefortress.creatures.MFRace;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFJobQueueTest
{

  private MFJobQueue jobQueue;
  private MFCreature owner;

  @Before
  public void setUp()
  {

    MFRace mockRace = mock(MFRace.class);
    this.owner = new MFCreature("Job Queue Owner", mockRace);

    this.jobQueue = new MFJobQueue(this.owner);
    this.owner.setJobQueue(this.jobQueue);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutOwner()
  {
    new MFJobQueue(null);
  }

  @Test
  public void shouldAddAndActivateSingleJob()
  {
    MFIJob job = mock(MFIJob.class);
    when(job.getPriority()).thenReturn(MFEPriority.NORMAL);
    this.jobQueue.add(job);
    this.jobQueue.update();

    verify(job).update();
  }

  @Test
  public void shouldPlaceMoreImportantJobFirstWhenNotUpdated()
  {
    MFIJob job = mock(MFIJob.class);
    when(job.getPriority()).thenReturn(MFEPriority.NORMAL);
    this.jobQueue.add(job);
    MFIJob moreImportant = mock(MFIJob.class);
    when(moreImportant.getPriority()).thenReturn(MFEPriority.HIGHER);
    this.jobQueue.add(moreImportant);

    this.jobQueue.update();

    verify(moreImportant).update();
  }

  @Test
  public void shouldActivateTheFollowingJob()
  {
    MFIJob job1 = mock(MFIJob.class);
    when(job1.getPriority()).thenReturn(MFEPriority.NORMAL);
    when(job1.update()).thenReturn(true);
    this.jobQueue.add(job1);
    MFIJob job2 = mock(MFIJob.class);
    when(job2.getPriority()).thenReturn(MFEPriority.NORMAL);
    when(job2.update()).thenReturn(true);
    this.jobQueue.add(job2);
    MFIJob job3 = mock(MFIJob.class);
    when(job3.getPriority()).thenReturn(MFEPriority.NORMAL);
    when(job3.update()).thenReturn(true);
    this.jobQueue.add(job3);

    this.jobQueue.update();
    verify(job1).update();
    verify(job2, never()).update();
    verify(job3, never()).update();

    this.jobQueue.update();
    verify(job1).update();
    verify(job2).update();
    verify(job3, never()).update();

    this.jobQueue.update();
    verify(job1).update();
    verify(job2).update();
    verify(job3).update();

    this.jobQueue.update();
    verify(job3).update();

  }

  @Test
  public void shouldPrioritizeJobAdsBeforeLowerPriority()
  {
    // create low prio job
    MFIJob lowPriority = mock(MFIJob.class);
    when(lowPriority.getPriority()).thenReturn(MFEPriority.LOWER);
    this.jobQueue.add(lowPriority);

    // create job ad channel message, sender and job
    MFIChannelSender jobSender = mock(MFIChannelSender.class);
    MFAssignableJobMock jobMock = new MFAssignableJobMock(jobSender);
    MFAssignableJob advertisedJob = spy(jobMock);

    when(jobSender.getJob()).thenReturn(advertisedJob);
    when(jobSender.isJobAvailable()).thenReturn(true);

    MFChannelMessage jobAd = new MFChannelMessage(jobSender);
    this.jobQueue.addMessage(jobAd);

    // start test
    this.jobQueue.update();
    verify(lowPriority, never()).update();
    verify(advertisedJob).update();
  }

}