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

import java.util.LinkedList;
import java.util.List;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.creatures.behavior.movable.MFIMovable;
import magefortress.map.MFIPathFinderListener;
import magefortress.map.MFMap;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;
import magefortress.map.MFTile;

/**
 * Locates the closest unoccupied, walkable neighbor to the specified location
 */
public class MFLocateNearestNeighorSubtask extends MFMovingSubtask implements MFIPathFinderListener
{

  /**
   * Constructor
   * @param _movable The game object to move
   * @param _location The ultimate goal whose neighbors will be calculated
   * @param _map The map
   * @param _pathFinder The path finding queue
   */
  public MFLocateNearestNeighorSubtask(MFIMovable _movable,
                     MFLocation _location, MFMap _map, MFPathFinder _pathFinder)
  {
    super(_movable);
    this.location = _location;
    this.map = _map;
    this.pathFinder = _pathFinder;
    this.paths = new LinkedList<MFPath>();
  }
  
  @Override
  public boolean update() throws MFNoPathFoundException
  {
    boolean result = false;

    if (this.startedSearchesCount == 0) {
      // already there
      if (this.getMovable().getLocation().isNeighborOf(this.location)) {
        this.getMovable().setCurrentHeading(this.getMovable().getLocation());
        result = true;
      } else {
        this.searchNearestNeighboringTile();
      }
    } else if (this.finishedSearchesCount == this.startedSearchesCount) {
      // no path found
      if (this.paths.isEmpty()) {
        throw new MFNoPathFoundException("Could not find path to " + this.location,
                                  this.getMovable().getLocation(), this.location);
      } else {
        MFPath path = this.selectShortestPath();
        MFLocation goal = path.getGoal().getLocation();
        this.getMovable().setCurrentHeading(goal);
      }
      // done in any case
      result = true;
    }
    return result;
  }

  @Override
  public void pathSearchFinished(MFPath _path)
  {
    ++this.finishedSearchesCount;
    if (_path != null) {
      this.paths.add(_path);
    }

  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFLocation location;
  private final MFPathFinder pathFinder;
  private final List<MFPath> paths;
  private final MFMap map;
  private int startedSearchesCount;
  private int finishedSearchesCount;

  private void searchNearestNeighboringTile()
  {
    this.startedSearchesCount = this.finishedSearchesCount = 0;
    this.paths.clear();

    for (MFEDirection dir : MFEDirection.values()) {

      MFLocation neighboringLocation = this.location.locationOf(dir);
      if (this.map.isInsideMap(neighboringLocation)) {
        MFTile neighboringTile = this.map.getTile(neighboringLocation);

        if (neighboringTile != null && 
            neighboringTile.isWalkable(this.getMovable().getCapability()) ) {
            // && neighboringTile.getClearance(this.getMovable().getCapability()) >= this.getMovable().getClearance()) {
            this.searchPath(neighboringLocation);
            ++this.startedSearchesCount;
        }
      }

    }
    
  }

  /**
   * Enqueues a path
   * @param _goal The goal of the path
   */
  private void searchPath(MFLocation _goal)
  {
    assert _goal != null : "Cannot search a path with no goal.";
    
    final int clearance = this.getMovable().getClearance();
    final MFCapability capability = this.getMovable().getCapability();

    this.pathFinder.enqueuePathSearch(this.getMovable().getLocation(), _goal,
                                                 clearance, capability, this);
  }

  /**
   * Selects the path with the least cost.
   * @return The shortest path
   */
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
