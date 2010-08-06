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

import magefortress.channel.MFIChannelSender;
import magefortress.creatures.MFCreature;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFAssignableJobTest
{
  private MFAssignableJobMock job;
  private MFIChannelSender mockSender;

  @Before
  public void setUp()
  {
    mockSender = mock(MFIChannelSender.class);
    job = new MFAssignableJobMock(mockSender);
  }

  @Test
  public void shouldGetSender()
  {
    assertEquals(mockSender, job.getSender());
  }

  @Test
  public void shouldCallInitWhenThereWasNoOwner()
  {
    MFAssignableJob spy = spy(job);
    MFCreature owner = mock(MFCreature.class);
    
    assertNull(spy.getOwner());
    spy.setOwner(owner);

    assertEquals(owner, spy.getOwner());
    verify(spy).initJob();
  }

  @Test
  public void shouldCallPauseWhenSetToNoOwner()
  {
    MFAssignableJob spy = spy(job);

    MFCreature owner = mock(MFCreature.class);

    spy.setOwner(owner);
    spy.setOwner(null);
    assertEquals(null, spy.getOwner());
    verify(spy).pauseJob();
  }

  @Test
  public void shouldCallInitAndPauseWhenTransferingOwners()
  {
    MFAssignableJob spy = spy(job);

    MFCreature owner1 = mock(MFCreature.class);
    MFCreature owner2 = mock(MFCreature.class);

    spy.setOwner(owner1);
    spy.setOwner(owner2);

    assertEquals(owner2, spy.getOwner());
    verify(spy, times(2)).initJob();
    verify(spy).pauseJob();
  }

  @Test
  public void shouldNotBeActiveWithoutOwner()
  {
    assertFalse(job.isActive());
  }

  @Test
  public void shouldBeActiveWithOwner()
  {
    MFCreature owner = mock(MFCreature.class);

    job.setOwner(owner);

    assertTrue(job.isActive());
  }

  @Test
  public void shouldBeInactiveWhenPaused()
  {
    MFCreature owner = mock(MFCreature.class);

    job.setOwner(owner);
    job.setOwner(null);

    assertFalse(job.isActive());
  }

  @Test
  public void shouldNotifySenderWhenDone()
  {
    // given an assigned job
    job.setOwner(mock(MFCreature.class));

    // when the job is done
    boolean done = this.job.update();
    assertTrue(done);

    // then notify the sender
    verify(this.mockSender).jobDone(job);
  }
}