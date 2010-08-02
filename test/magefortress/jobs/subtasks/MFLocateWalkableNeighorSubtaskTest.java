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
package magefortress.jobs.subtasks;

import magefortress.creatures.MFCreature;
import magefortress.core.MFLocation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFLocateWalkableNeighorSubtaskTest
{
  private MFLocateWalkableNeighorSubtask task;
  private MFCreature mockCreature;
  private MFLocation mockLocation;

  @Before
  public void setUp()
  {
    this.mockCreature = mock(MFCreature.class);
    this.mockLocation = mock(MFLocation.class);
    this.task = new MFLocateWalkableNeighorSubtask(mockCreature, mockLocation);
  }

  @Test
  public void shouldFindPath() throws MFSubtaskCanceledException
  {
   MFLocation expGoal = mock(MFLocation.class);
   when(mockCreature.findNearestNeighboringTile(mockLocation)).thenReturn(expGoal);
   this.task.update();
   verify(this.mockCreature).setCurrentHeading(expGoal);
  }

  @Test(expected=MFSubtaskCanceledException.class)
  public void shouldNotFindPath() throws MFSubtaskCanceledException
  {
    when(mockCreature.findNearestNeighboringTile(mockLocation)).thenReturn(null);
    this.task.update();
  }

}