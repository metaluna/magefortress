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

import magefortress.core.MFLocation;
import magefortress.core.MFUnexpectedStateException;
import magefortress.creatures.MFCreature;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDigOutTileSubtaskTest
{

  MFDigOutTileSubtask digTask;
  MFCreature mockOwner;
  MFMap mockMap;
  MFLocation goal;

  @Before
  public void setUp()
  {
    this.goal = new MFLocation(1,2,3);
    this.mockMap = mock(MFMap.class);
    this.mockOwner = mock(MFCreature.class);
    this.digTask = new MFDigOutTileSubtask(this.mockOwner, this.mockMap, this.goal);
  }

  @Test(expected=MFSubtaskCanceledException.class)
  public void shouldNotDigOutDugOutTile() throws MFSubtaskCanceledException
  {
    MFTile dugOutTile = mock(MFTile.class);
    when(dugOutTile.isDugOut()).thenReturn(true);
    when(mockMap.getTile(goal)).thenReturn(dugOutTile);

    this.digTask.update();
  }

  @Test(expected=MFSubtaskCanceledException.class)
  public void shouldNotDigIfNotBeneathGoal() throws MFSubtaskCanceledException
  {
    when(this.mockOwner.getLocation()).thenReturn(new MFLocation(3,2,3));
    MFTile mockTile = mock(MFTile.class);
    when(mockMap.getTile(goal)).thenReturn(mockTile);

    this.digTask.update();
  }
}