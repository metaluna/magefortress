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
  private MFRoom room;
  private MFPathFinder pathFinder;

  @Before
  public void setUp()
  {
    this.room = mock(MFRoom.class);
    this.owner = mock(MFCreature.class);
    this.pathFinder = mock(MFPathFinder.class);
    this.task = new MFLocateJobSlotSubtask(this.owner, this.room, this.pathFinder);
  }

  @Test
  public void shouldNotStartSearchesIfAlreadyThere() throws MFSubtaskCanceledException
  {
    // given a creature standing on a job slot
    MFLocation jobSlotLocation = new MFLocation(1, 2, 3);
    when(this.owner.getLocation()).thenReturn(jobSlotLocation);
    MFJobSlot mockSlot = mock(MFJobSlot.class);
    when(mockSlot.getLocation()).thenReturn(jobSlotLocation);
    when(this.room.getJobSlots()).thenReturn(Arrays.asList(mockSlot));

    // when the creature needs to go to a job slot
    boolean done = this.task.update();

    // then the task should finish immediately
    assertTrue(done);

    // and the creature's heading should be set to its current location
    verify(this.owner).setCurrentHeading(jobSlotLocation);
  }

  @Test
  public void shouldStartSearchesForAllJobSlots() throws MFSubtaskCanceledException
  {
    // given a creature standing outside of the target room
    MFLocation someLocation = new MFLocation(42, 42, 42);
    final int clearance = 23;
    final MFCapability capability = MFCapability.WALK_FLY;
    when(this.owner.getLocation()).thenReturn(someLocation);
    when(this.owner.getCapability()).thenReturn(capability);
    when(this.owner.getClearance()).thenReturn(clearance);

    MFLocation jobSlotLoc1 = new MFLocation(1, 2, 3);
    MFJobSlot mockSlot1 = mock(MFJobSlot.class);
    when(mockSlot1.getLocation()).thenReturn(jobSlotLoc1);
    MFLocation jobSlotLoc2 = new MFLocation(3, 2, 3);
    MFJobSlot mockSlot2 = mock(MFJobSlot.class);
    when(mockSlot2.getLocation()).thenReturn(jobSlotLoc2);
    MFLocation jobSlotLoc3 = new MFLocation(5, 2, 3);
    MFJobSlot mockSlot3 = mock(MFJobSlot.class);
    when(mockSlot3.getLocation()).thenReturn(jobSlotLoc3);
    when(this.room.getJobSlots()).thenReturn(
                                Arrays.asList(mockSlot1, mockSlot2, mockSlot3));

    // when the creature needs to go to a job slot
    boolean done = this.task.update();

    // then the task should not be finished
    assertFalse(done);

    // and 3 searches should have been enqueued
    verify(this.pathFinder).enqueuePathSearch(someLocation,
                                jobSlotLoc1, clearance, capability, this.task);
    verify(this.pathFinder).enqueuePathSearch(someLocation,
                                jobSlotLoc2, clearance, capability, this.task);
    verify(this.pathFinder).enqueuePathSearch(someLocation,
                                jobSlotLoc3, clearance, capability, this.task);

  }

  @Test
  public void shouldSelectNearestJobSlot() throws MFSubtaskCanceledException
  {
    // given 3 searches for job slots were started
    MFLocation someLocation = new MFLocation(3, 3, 3);
    final int clearance = 23;
    final MFCapability capability = MFCapability.WALK_FLY;
    when(this.owner.getLocation()).thenReturn(someLocation);
    when(this.owner.getCapability()).thenReturn(capability);
    when(this.owner.getClearance()).thenReturn(clearance);

    MFLocation jobSlotLocation = new MFLocation(1, 5, 6);
    MFJobSlot mockSlot = mock(MFJobSlot.class);
    when(mockSlot.getLocation()).thenReturn(jobSlotLocation);

    when(this.room.getJobSlots()).thenReturn(
                                Arrays.asList(mockSlot, mockSlot, mockSlot));
    this.task.update();
    verify(this.pathFinder, times(3)).enqueuePathSearch(someLocation,
                            jobSlotLocation, clearance, capability, this.task);

    // when 3 searches return with different lengths
    MFPath longestPath = mock(MFPath.class);
    MFLocation furthestLoc = new MFLocation(4, 5, 6);
    MFTile furthestTile = mock(MFTile.class);
    when(furthestTile.getLocation()).thenReturn(furthestLoc);
    when(longestPath.getGoal()).thenReturn(furthestTile);
    when(longestPath.getCost()).thenReturn(10);

    MFPath longerPath = mock(MFPath.class);
    MFLocation farLoc = new MFLocation(3, 5, 6);
    MFTile farTile = mock(MFTile.class);
    when(farTile.getLocation()).thenReturn(farLoc);
    when(longerPath.getGoal()).thenReturn(farTile);
    when(longerPath.getCost()).thenReturn(7);

    MFPath shortestPath = mock(MFPath.class);
    MFLocation closestLoc = new MFLocation(1, 5, 6);
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
    MFLocation someLocation = new MFLocation(42, 42, 42);
    final int clearance = 23;
    final MFCapability capability = MFCapability.WALK_FLY;
    when(this.owner.getLocation()).thenReturn(someLocation);
    when(this.owner.getCapability()).thenReturn(capability);
    when(this.owner.getClearance()).thenReturn(clearance);

    MFLocation jobSlotLocation = new MFLocation(1, 2, 3);
    MFJobSlot mockSlot = mock(MFJobSlot.class);
    when(mockSlot.getLocation()).thenReturn(jobSlotLocation);

    when(this.room.getJobSlots()).thenReturn(
                                Arrays.asList(mockSlot, mockSlot, mockSlot));
    this.task.update();
    verify(this.pathFinder, times(3)).enqueuePathSearch(someLocation,
                            jobSlotLocation, clearance, capability, this.task);

    // when 3 searches for job slots return nothing
    this.task.pathSearchFinished(null);
    this.task.pathSearchFinished(null);
    this.task.pathSearchFinished(null);

    // then a no path found exception should be thrown
    this.task.update();

    // and no heading should have been set
    verify(this.owner, never()).setCurrentHeading(any(MFLocation.class));
  }

}