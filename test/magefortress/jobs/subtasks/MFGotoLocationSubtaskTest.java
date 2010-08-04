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
package magefortress.jobs.subtasks;

import java.util.EnumSet;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.core.MFUnexpectedStateException;
import magefortress.creatures.MFCreature;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGotoLocationSubtaskTest
{
  private MFGotoLocationSubtask gotoTask;
  private MFCreature mockOwner;
  private MFPathFinder mockPathFinder;

  @Before
  public void setUp()
  {
    this.mockOwner = mock(MFCreature.class);
    this.mockPathFinder = mock(MFPathFinder.class);
    this.gotoTask = new MFGotoLocationSubtask(this.mockOwner, this.mockPathFinder);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldStartPathSearchFirstThing() throws MFSubtaskCanceledException
  {
    gotoTask.update();
    verify(mockPathFinder).enqueuePathSearch(any(MFLocation.class),
            any(MFLocation.class), anyInt(), any(EnumSet.class), eq(gotoTask));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldStartPathSearchIfPathIsInvalid() throws MFSubtaskCanceledException
  {
    // path that's first valid and then invalid
    MFPath path = mock(MFPath.class);
    when(path.isPathValid()).thenReturn(true).thenReturn(false);

    gotoTask.update();
    gotoTask.pathSearchFinished(path);
    gotoTask.update();
    verify(mockPathFinder).enqueuePathSearch(any(MFLocation.class),
            any(MFLocation.class), anyInt(), any(EnumSet.class), eq(gotoTask));
    gotoTask.update();
    verify(mockPathFinder, times(2)).enqueuePathSearch(any(MFLocation.class),
            any(MFLocation.class), anyInt(), any(EnumSet.class), eq(gotoTask));

  }

  @Test(expected=MFNoPathFoundException.class)
  public void shouldCancelIfNoPathWasFound() throws MFSubtaskCanceledException
  {
    gotoTask.update();
    gotoTask.pathSearchFinished(null);
    gotoTask.update();
  }

  @Test
  public void shouldBeginToMoveOwnerWhenPathWasFound() throws MFSubtaskCanceledException
  {
    // never-ending path northwards
    MFPath path = mock(MFPath.class);
    when(path.isPathValid()).thenReturn(true);
    when(path.hasNext()).thenReturn(true);
    when(path.next()).thenReturn(MFEDirection.N);

    gotoTask.update();
    gotoTask.pathSearchFinished(path);
    gotoTask.update();
    verify(mockOwner).move(any(MFEDirection.class));
  }

  @Test
  public void shouldMoveOwnerAccordingToHisSpeed() throws MFSubtaskCanceledException
  {
    // never-ending path northwards
    MFPath path = mock(MFPath.class);
    when(path.isPathValid()).thenReturn(true);
    when(path.hasNext()).thenReturn(true);
    when(path.next()).thenReturn(MFEDirection.N);

    when(mockOwner.getSpeed()).thenReturn(3);

    gotoTask.update();
    gotoTask.pathSearchFinished(path);
    gotoTask.update();
    verify(mockOwner).move(any(MFEDirection.class));
    gotoTask.update();
    verify(mockOwner).move(any(MFEDirection.class));
    gotoTask.update();
    verify(mockOwner).move(any(MFEDirection.class));
    gotoTask.update();
    verify(mockOwner, times(2)).move(any(MFEDirection.class));
  }

  @Test
  public void shouldBeDoneWhenGoalWasReached() throws MFSubtaskCanceledException
  {
    MFLocation goal = new MFLocation(1, 2, 3);
    // 2-tile path northwards
    MFPath path = mock(MFPath.class);
    when(path.isPathValid()).thenReturn(true);
    when(path.hasNext()).thenReturn(true);
    when(path.next()).thenReturn(MFEDirection.N);

    when(mockOwner.getSpeed()).thenReturn(3);
    when(mockOwner.getCurrentHeading()).thenReturn(goal);

    boolean done = true;
    done = gotoTask.update();
    assertFalse(done);
    gotoTask.pathSearchFinished(path);
    // 1st move
    done = gotoTask.update();
    assertFalse(done);
    done = gotoTask.update();
    assertFalse(done);
    done = gotoTask.update();
    assertFalse(done);

    // 2nd move
    when(mockOwner.getLocation()).thenReturn(goal);
    when(path.hasNext()).thenReturn(true).thenReturn(false);
    done = gotoTask.update();
    assertTrue(done);
  }

  @Test(expected=MFUnexpectedStateException.class)
  public void shouldThrowExceptionWhenPathEndsNotAtGoal() throws MFSubtaskCanceledException
  {
    MFLocation goal = new MFLocation(1, 2, 3);
    // 2-tile path northwards
    MFPath path = mock(MFPath.class);
    when(path.isPathValid()).thenReturn(true);
    when(path.hasNext()).thenReturn(true);
    when(path.next()).thenReturn(MFEDirection.N);

    when(mockOwner.getSpeed()).thenReturn(3);
    when(mockOwner.getCurrentHeading()).thenReturn(goal);

    gotoTask.update();
    gotoTask.pathSearchFinished(path);
    // 1st move
    gotoTask.update();
    gotoTask.update();
    gotoTask.update();

    // 2nd move
    when(mockOwner.getLocation()).thenReturn(new MFLocation(1,1,3));
    when(path.hasNext()).thenReturn(true).thenReturn(false);
    gotoTask.update();
  }

  @Test(expected=MFUnexpectedStateException.class)
  public void shouldThrowExceptionAfterGoalWasReached() throws MFSubtaskCanceledException
  {
    MFLocation goal = new MFLocation(1, 2, 3);
    // 2-tile path northwards
    MFPath path = mock(MFPath.class);
    when(path.isPathValid()).thenReturn(true);
    when(path.hasNext()).thenReturn(true);
    when(path.next()).thenReturn(MFEDirection.N);

    when(mockOwner.getSpeed()).thenReturn(3);
    when(mockOwner.getCurrentHeading()).thenReturn(goal);

    gotoTask.update();
    gotoTask.pathSearchFinished(path);
    // 1st move
    gotoTask.update();
    gotoTask.update();
    gotoTask.update();

    // 2nd move
    when(mockOwner.getLocation()).thenReturn(goal);
    when(path.hasNext()).thenReturn(true).thenReturn(false);
    boolean done = gotoTask.update();
    assertTrue(done);

    gotoTask.update();
  }

}