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

import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.creatures.MFCreature;
import magefortress.jobs.subtasks.MFSubtask;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFNeedsJobTest
{
  private MFNeedsJob job;
  private MFSubtask subtask1;
  private MFSubtask subtask2;
  private MFSubtask subtask3;

  @Before
  public void setUp()
  {
    job = new MFNeedsJobMock(null);
  }

  @Test
  public void shouldPopSubtasksWhenDoneOnUpdate()
  {
    job.start();
    
    boolean done;
    assertEquals(subtask1, job.getActiveSubtask());

    done = job.update();
    assertFalse(done);
    assertEquals(subtask2, job.getActiveSubtask());

    done = job.update();
    assertFalse(done);
    assertEquals(subtask3, job.getActiveSubtask());

    done = job.update();
    assertTrue(done);
    assertNull(job.getActiveSubtask());
  }

  @Test
  public void shouldBeDoneWhenUpdatingNotStartedJob()
  {
    assertFalse(job.isActive());
    
    boolean done = job.update();
    assertTrue(done);
  }

  @Test
  public void shouldBeActiveWithSubtasksInTheQueue()
  {
    job.start();
    assertTrue(job.isActive());
  }

  @Test
  public void shouldNotBeActiveWhenPaused()
  {
    job.start();
    assertTrue(job.isActive());
    job.pause();
    assertFalse(job.isActive());
  }

  @Test
  public void shouldNotBeActiveWithoutSubtasks()
  {
    job.start();
    job.update();
    job.update();
    job.update();
    assertFalse(job.isActive());
  }

  @Test
  public void shouldGetActiveSubtask()
  {
    assertEquals(subtask1, job.getActiveSubtask());
  }

  public class MFNeedsJobMock extends MFNeedsJob
  {

    public MFNeedsJobMock(MFCreature _owner)
    {
      super(_owner);
    }

    public void initJob()
    {
      try {
        subtask1 = mock(MFSubtask.class);
        subtask2 = mock(MFSubtask.class);
        subtask3 = mock(MFSubtask.class);
        when(subtask1.update()).thenReturn(true);
        when(subtask2.update()).thenReturn(true);
        when(subtask3.update()).thenReturn(true);
        this.addSubtask(subtask1);
        this.addSubtask(subtask2);
        this.addSubtask(subtask3);
      } catch (MFSubtaskCanceledException ex) {
        Logger.getLogger(MFNeedsJobTest.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    public void pauseJob()
    {
      this.clearSubtasks();
    }

    public void cancelJob()
    {
    }
  }

}