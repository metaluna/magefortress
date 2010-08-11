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
package magefortress.jobs.mining;

import java.util.Arrays;
import magefortress.core.MFLocation;
import magefortress.core.MFRoom;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.jobs.MFJobSlot;
import magefortress.jobs.subtasks.MFNoPathFoundException;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFLocateJobSlotSubtaskTest
{
  private MFLocateJobSlotSubtask task;
  private MFCreature owner;
  private MFLocation ownerLoc;
  private MFRoom room;
  private MFPathFinder pathFinder;

  private MFJobSlot freeJobSlot1;
  private MFLocation freeSlotLoc1;
  private MFJobSlot freeJobSlot2;
  private MFLocation freeSlotLoc2;
  private MFJobSlot freeJobSlot3;
  private MFLocation freeSlotLoc3;
  private MFJobSlot occupiedJobSlot;
  private MFLocation occupiedSlotLoc;

  @Before
  public void setUp()
  {
    this.room = mock(MFRoom.class);

    this.owner = mock(MFCreature.class);
    this.ownerLoc = new MFLocation(42, 42, 42);
    final int clearance = 23;
    final MFCapability capability = MFCapability.WALK_FLY;
    when(this.owner.getLocation()).thenReturn(this.ownerLoc);
    when(this.owner.getCapability()).thenReturn(capability);
    when(this.owner.getClearance()).thenReturn(clearance);

    this.pathFinder = mock(MFPathFinder.class);

    this.task = new MFLocateJobSlotSubtask(this.owner, this.room, this.pathFinder);

    this.freeJobSlot1 = mock(MFJobSlot.class);
    this.freeSlotLoc1 = new MFLocation(1, 2, 3);
    when(this.freeJobSlot1.getLocation()).thenReturn(freeSlotLoc1);
    when(this.freeJobSlot1.isAvailable()).thenReturn(true);

    this.freeJobSlot2 = mock(MFJobSlot.class);
    this.freeSlotLoc2 = new MFLocation(3, 2, 3);
    when(this.freeJobSlot2.getLocation()).thenReturn(freeSlotLoc2);
    when(this.freeJobSlot2.isAvailable()).thenReturn(true);

    this.freeJobSlot3 = mock(MFJobSlot.class);
    this.freeSlotLoc3 = new MFLocation(5, 2, 3);
    when(this.freeJobSlot3.getLocation()).thenReturn(freeSlotLoc3);
    when(this.freeJobSlot3.isAvailable()).thenReturn(true);

    this.occupiedJobSlot = mock(MFJobSlot.class);
    this.occupiedSlotLoc = new MFLocation(7, 2, 3);
    when(this.occupiedJobSlot.getLocation()).thenReturn(occupiedSlotLoc);
    when(this.occupiedJobSlot.isAvailable()).thenReturn(false);

  }

  @Test
  public void shouldNotStartSearchesIfAlreadyThere() throws MFSubtaskCanceledException
  {
    // given a creature standing on a job slot
    when(this.owner.getLocation()).thenReturn(freeSlotLoc1);
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(freeJobSlot1));

    // when the creature needs to go to a job slot
    boolean done = this.task.update();

    // then the task should finish immediately
    assertTrue(done);

    // and the creature's heading should be set to its current location
    verify(this.owner).setCurrentHeading(freeSlotLoc1);
  }

  @Test
  public void shouldStartSearchesForAllJobSlots() throws MFSubtaskCanceledException
  {
    // given a creature standing outside of the target room
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(
                                    freeJobSlot1, freeJobSlot2, freeJobSlot3));

    // when the creature needs to go to a job slot
    boolean done = this.task.update();

    // then the task should not be finished
    assertFalse(done);

    // and 3 searches should have been enqueued
    verify(this.pathFinder).enqueuePathSearch(eq(ownerLoc), eq(freeSlotLoc1),
                              anyInt(), any(MFCapability.class), eq(this.task));
    verify(this.pathFinder).enqueuePathSearch(eq(ownerLoc), eq(freeSlotLoc2),
                              anyInt(), any(MFCapability.class), eq(this.task));
    verify(this.pathFinder).enqueuePathSearch(eq(ownerLoc), eq(freeSlotLoc3),
                              anyInt(), any(MFCapability.class), eq(this.task));

  }

  @Test
  public void shouldSelectNearestJobSlot() throws MFSubtaskCanceledException
  {
    // given 3 searches for job slots were started
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(
                                    freeJobSlot1, freeJobSlot2, freeJobSlot3));
    this.task.update();
    verify(this.pathFinder, times(3)).enqueuePathSearch(eq(ownerLoc),
                                        any(MFLocation.class), anyInt(),
                                        any(MFCapability.class), eq(this.task));

    // when 3 searches return with different lengths
    MFPath longestPath = mock(MFPath.class);
    MFLocation furthestLoc = freeSlotLoc1;
    MFTile furthestTile = mock(MFTile.class);
    when(furthestTile.getLocation()).thenReturn(furthestLoc);
    when(longestPath.getGoal()).thenReturn(furthestTile);
    when(longestPath.getCost()).thenReturn(10);

    MFPath longerPath = mock(MFPath.class);
    MFLocation farLoc = freeSlotLoc2;
    MFTile farTile = mock(MFTile.class);
    when(farTile.getLocation()).thenReturn(farLoc);
    when(longerPath.getGoal()).thenReturn(farTile);
    when(longerPath.getCost()).thenReturn(7);

    MFPath shortestPath = mock(MFPath.class);
    MFLocation closestLoc = freeSlotLoc3;
    MFTile closestTile = mock(MFTile.class);
    when(closestTile.getLocation()).thenReturn(closestLoc);
    when(shortestPath.getGoal()).thenReturn(closestTile);
    when(shortestPath.getCost()).thenReturn(5);
    
    this.task.pathSearchFinished(longestPath);
    this.task.pathSearchFinished(longerPath);
    this.task.pathSearchFinished(shortestPath);
    this.task.update();

    // then the nearest one should be set as the owners heading
    verify(this.owner).setCurrentHeading(closestLoc);
  }

  @Test(expected=MFNoPathFoundException.class)
  public void shouldCancelIfNoPathIsFound() throws MFSubtaskCanceledException
  {
    // given 3 searches for job slots were started
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(
                                    freeJobSlot1, freeJobSlot2, freeJobSlot3));
    this.task.update();
    verify(this.pathFinder, times(3)).enqueuePathSearch(eq(ownerLoc),
                                        any(MFLocation.class), anyInt(),
                                        any(MFCapability.class), eq(this.task));

    // when 3 searches for job slots return nothing
    this.task.pathSearchFinished(null);
    this.task.pathSearchFinished(null);
    this.task.pathSearchFinished(null);

    // then a no path found exception should be thrown
    this.task.update();

    // and no heading should have been set
    verify(this.owner, never()).setCurrentHeading(any(MFLocation.class));
  }

  @Test
  public void shouldStartSearchesForUnoccupiedJobSlots() throws MFSubtaskCanceledException
  {
    // given 2 free and 1 occupied job slots
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(
                                  freeJobSlot1, freeJobSlot2, occupiedJobSlot));

    // when the creature needs to go to a job slot
    this.task.update();

    // then 2 searches should have been enqueued
    verify(this.pathFinder).enqueuePathSearch(eq(ownerLoc), eq(freeSlotLoc1),
                              anyInt(), any(MFCapability.class), eq(this.task));
    verify(this.pathFinder).enqueuePathSearch(eq(ownerLoc), eq(freeSlotLoc2),
                              anyInt(), any(MFCapability.class), eq(this.task));
    verify(this.pathFinder, never()).enqueuePathSearch(eq(ownerLoc), 
                                        eq(occupiedSlotLoc), anyInt(),
                                        any(MFCapability.class), eq(this.task));

  }

  @Test
  public void shouldOccupyJobSlotWhenClosestOneWasFound() throws MFSubtaskCanceledException
  {
    // given 1 search for job slots was started
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(freeJobSlot1));
    this.task.update();
    verify(this.pathFinder).enqueuePathSearch(eq(ownerLoc), eq(freeSlotLoc1),
                              anyInt(), any(MFCapability.class), eq(this.task));

    // when a valid path returns
    MFPath path = mock(MFPath.class);
    MFTile goalTile = mock(MFTile.class);
    when(goalTile.getLocation()).thenReturn(freeSlotLoc1);
    when(path.getGoal()).thenReturn(goalTile);
    when(path.getCost()).thenReturn(5);

    this.task.pathSearchFinished(path);
    this.task.update();
    
    // then the job slot at that location should be occupied
    verify(freeJobSlot1).occupy(owner);
  }

}