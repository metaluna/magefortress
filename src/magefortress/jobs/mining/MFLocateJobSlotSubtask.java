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

import java.util.LinkedList;
import java.util.List;
import magefortress.core.MFLocation;
import magefortress.core.MFRoom;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.creatures.behavior.movable.MFIMovable;
import magefortress.jobs.MFJobSlot;
import magefortress.jobs.subtasks.MFMovingSubtask;
import magefortress.jobs.subtasks.MFNoPathFoundException;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;
import magefortress.map.MFIPathFinderListener;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;

/**
 *
 */
public class MFLocateJobSlotSubtask extends MFMovingSubtask implements MFIPathFinderListener
{
  public MFLocateJobSlotSubtask(MFIMovable _movable, MFRoom _room,
                                                      MFPathFinder _pathFinder)
  {
    super(_movable);
    this.room = _room;
    this.pathFinder = _pathFinder;
    this.paths = new LinkedList<MFPath>();
  }

  //---vvv---     SUBTASK METHODS     ---vvv---
  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    if (this.startedSearchesCount == 0) {
      if (isAlreadyThere()) {
        this.getMovable().setCurrentHeading(this.getMovable().getLocation());
        return true;
      } else {
        this.startSearching();
      }
    } else if (this.finishedSearchesCount == this.startedSearchesCount) {
      if (this.paths.isEmpty()) {
        MFLocation goal1 = this.room.getJobSlots().get(0).getLocation();
        String msg = this.getClass().getSimpleName() + ": Cannot find path " +
                                                            "to room@" + goal1;
        logger.info(msg);
        throw new MFNoPathFoundException(msg, this.getMovable().getLocation(),
                                  goal1);
      } else {
        MFPath path = this.selectShortestPath();
        MFLocation goal = path.getGoal().getLocation();
        this.getMovable().setCurrentHeading(goal);
      }
      return true;
    }
    return false;
  }

  //---vvv---     PATH FINDER LISTENER INTERFACE METHODS    ---vvv---
  public void pathSearchFinished(MFPath _path)
  {
    ++this.finishedSearchesCount;
    if (_path != null) {
      this.paths.add(_path);
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFPathFinder pathFinder;
  private final MFRoom room;

  private final List<MFPath> paths;
  private int startedSearchesCount;
  private int finishedSearchesCount;

  /**
   * Returns <code>true</code> if the movable is already standing on a target slot.
   * @return <code>true</code> if already on a slot of the target room
   */
  private boolean isAlreadyThere()
  {
    MFLocation currentLocation = this.getMovable().getLocation();
    for (MFJobSlot slot : this.room.getJobSlots()) {
      if (slot.getLocation().equals(currentLocation)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Starts searches for all job slots of the target room.
   */
  private void startSearching()
  {
    final MFLocation start        = this.getMovable().getLocation();
    final int clearance           = this.getMovable().getClearance();
    final MFCapability capability = this.getMovable().getCapability();

    this.resetData();

    for (MFJobSlot slot : this.room.getJobSlots()) {
      MFLocation goal = slot.getLocation();
      this.pathFinder.enqueuePathSearch(start, goal, clearance, capability, this);
      ++this.startedSearchesCount;
    }
  }

  /**
   * Resets all searching data.
   */
  private void resetData()
  {
    this.startedSearchesCount = this.finishedSearchesCount = 0;
    this.paths.clear();
  }

  private MFPath selectShortestPath()
  {
    assert paths.size() > 0 : "Cannot find shortest path without any found paths.";

    MFPath shortestPath = this.paths.get(0);

    for (MFPath path : this.paths) {
      if (path.getCost() < shortestPath.getCost()) {
        shortestPath = path;
      }
    }

    return shortestPath;
  }

}
