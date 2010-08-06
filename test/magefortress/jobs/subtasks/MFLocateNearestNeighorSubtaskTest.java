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

import java.util.EnumSet;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.movable.MFEMovementType;
import magefortress.map.MFIPathFinderListener;
import magefortress.map.MFMap;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFLocateNearestNeighorSubtaskTest
{
  private MFLocateNearestNeighorSubtask task;
  private MFCreature mockCreature;
  private MFLocation start;
  private MFLocation goal;
  private MFMap mockMap;
  private MFPathFinder mockPathFinder;
  private static final EnumSet<MFEMovementType> CAPABILITIES = EnumSet.of(MFEMovementType.WALK);
  private static final int CLEARANCE = 1;

  @Before
  public void setUp()
  {
    this.mockCreature = mock(MFCreature.class);
    when(mockCreature.getCapabilities()).thenReturn(CAPABILITIES);
    when(mockCreature.getClearance()).thenReturn(CLEARANCE);
    this.start = new MFLocation(1, 1, 0);
    when(mockCreature.getLocation()).thenReturn(start);

    this.goal = new MFLocation(4, 1, 0);
    this.mockMap = mock(MFMap.class);
    when(this.mockMap.isInsideMap(any(MFLocation.class))).thenReturn(true);
    this.mockPathFinder = mock(MFPathFinder.class);
    this.task = new MFLocateNearestNeighorSubtask(mockCreature, goal,
                                                       mockMap, mockPathFinder);
  }

  @Test
  public void shouldEnqueueSearchesForAllNeighbors() throws MFNoPathFoundException
  {
    MFTile tile = new MFTile(0, 0, 0, 0, true, false, false, false, false, true, true);
    when(mockMap.getTile(any(MFLocation.class))).thenReturn(tile);
    
    task.update();
    for (MFEDirection dir : MFEDirection.values()) {
      MFLocation neighbor = goal.locationOf(dir);
      verify(mockPathFinder).enqueuePathSearch(start, neighbor, 1, CAPABILITIES, task);
    }
  }

  @Test
  public void shouldEnqueueSearchesForOneNeighbor() throws MFNoPathFoundException
  {
    MFTile openTile = new MFTile(0, 4, 0, 0, true, false, false, false, false, true, true);
    for (MFEDirection dir : MFEDirection.values()) {
      MFLocation loc = this.goal.locationOf(dir);
      MFTile tile;
      if (loc.equals(openTile.getLocation())) {
        tile = openTile;
      } else {
        tile = new MFTile(0, loc.x, loc.y, loc.z);
      }
      when(mockMap.getTile(loc)).thenReturn(tile);
    }

    task.update();
    verify(mockPathFinder).enqueuePathSearch(start, openTile.getLocation(), 1, CAPABILITIES, task);
    verifyNoMoreInteractions(mockPathFinder);
  }

  @Test
  public void shouldNotEnqueueSearches() throws MFNoPathFoundException
  {
    MFTile tile = new MFTile(0, 0, 0, 0);
    when(mockMap.getTile(any(MFLocation.class))).thenReturn(tile);

    task.update();
    verifyNoMoreInteractions(mockPathFinder);
  }

  @Test(expected=MFNoPathFoundException.class)
  public void shouldFindNoPathOfOne() throws MFNoPathFoundException
  {
    MFTile openTile = new MFTile(0, 4, 0, 0, true, false, false, false, false, true, true);
    for (MFEDirection dir : MFEDirection.values()) {
      MFLocation loc = this.goal.locationOf(dir);
      MFTile tile;
      if (loc.equals(openTile.getLocation())) {
        tile = openTile;
      } else {
        tile = new MFTile(0, loc.x, loc.y, loc.z);
      }
      when(mockMap.getTile(loc)).thenReturn(tile);
    }

    boolean done = true;
    try {
      done = task.update();
    } catch (MFNoPathFoundException e) {
      fail();
    }
    assertFalse(done);

    task.pathSearchFinished(null);
    try {
      task.update();
    } catch (MFNoPathFoundException e) {
      throw e;
    }
  }

  @Test(expected=MFNoPathFoundException.class)
  public void shouldFindNoPathOfEight() throws MFNoPathFoundException
  {
    MFTile tile = new MFTile(0, 0, 0, 0, true, false, false, false, false, true, true);
    when(mockMap.getTile(any(MFLocation.class))).thenReturn(tile);

    boolean done = true;
    try {
      done = task.update();
    } catch (MFNoPathFoundException ex) {
      fail();
    }
    assertFalse(done);

    for (int i = 0; i < 7; ++i) {
      try {
        task.pathSearchFinished(null);
        done = task.update();
        assertFalse(done);
      } catch (MFNoPathFoundException ex) {
        fail();
      }
    }
    
    try {
      task.pathSearchFinished(null);
      task.update();
    } catch (MFNoPathFoundException e) {
      throw e;
    }
  }

  @Test
  public void shouldFindPath() throws MFSubtaskCanceledException
  {
    MFTile openTile = new MFTile(0, 4, 0, 0, true, false, false, false, false, true, true);
    for (MFEDirection dir : MFEDirection.values()) {
      MFLocation loc = this.goal.locationOf(dir);
      MFTile tile;
      if (loc.equals(openTile.getLocation())) {
        tile = openTile;
      } else {
        tile = new MFTile(0, loc.x, loc.y, loc.z);
      }
      when(mockMap.getTile(loc)).thenReturn(tile);
    }

    boolean done = task.update();
    assertFalse(done);

    MFLocation goalLocation = new MFLocation(2, 3, 4);
    MFTile goalTile = mock(MFTile.class);
    when(goalTile.getLocation()).thenReturn(goalLocation);
    MFPath mockPath = mock(MFPath.class);
    when(mockPath.getGoal()).thenReturn(goalTile);

    task.pathSearchFinished(mockPath);

    done = task.update();
    assertTrue(done);
  }

  @Test
  public void shouldSelectShortestPath() throws MFNoPathFoundException
  {
    MFTile tile = new MFTile(0, 0, 0, 0, true, false, false, false, false, true, true);
    when(mockMap.getTile(any(MFLocation.class))).thenReturn(tile);

    boolean done = true;

    for (int i = 0; i < 8; i++) {
      done = task.update();
      assertFalse(done);
      
      MFLocation goalLocation = new MFLocation(i+1, i+1, 0);
      MFTile goalTile = mock(MFTile.class);
      when(goalTile.getLocation()).thenReturn(goalLocation);
      MFPath mockPath = mock(MFPath.class);
      when(mockPath.getGoal()).thenReturn(goalTile);
      when(mockPath.getLength()).thenReturn(i+1);

      task.pathSearchFinished(mockPath);
    }
    done = task.update();
    assertTrue(done);
    verify(this.mockCreature).setCurrentHeading(new MFLocation(1, 1, 0));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotSearchPathIfAlreadyNeighbor() throws MFNoPathFoundException
  {
    MFLocation currentLocation = goal.locationOf(MFEDirection.N);
    when(mockCreature.getLocation()).thenReturn(currentLocation);
    MFTile tile = new MFTile(0, 0, 0, 0, true, false, false, false, false, true, true);
    when(mockMap.getTile(any(MFLocation.class))).thenReturn(tile);

    boolean done = task.update();
    verify(mockPathFinder, never()).enqueuePathSearch(any(MFLocation.class), 
                          any(MFLocation.class), anyInt(),
                          any(EnumSet.class), any(MFIPathFinderListener.class));
    verify(mockCreature).setCurrentHeading(currentLocation);
    assertTrue(done);
  }

}