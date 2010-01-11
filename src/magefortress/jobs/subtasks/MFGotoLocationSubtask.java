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
import magefortress.core.MFCreature;
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
import magefortress.core.MFLocation;
import magefortress.map.MFIPathFinderListener;
import magefortress.map.MFMap;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;
import magefortress.map.MFTile;

/**
 * Grabs the current heading from the owner, calculates a path and moves
 * forward with the creatures speed until it reaches the goal.
 */
public class MFGotoLocationSubtask extends MFSubtask implements MFIPathFinderListener
{

  /**
   * Find a path to the location stored as the creature's current heading by a
   * previous subtask.
   * @param _map the map to move on
   * @param _owner The creature to move
   */
  public MFGotoLocationSubtask(MFMap _map, MFCreature _owner)
  {
    super(_owner);
    this.map = _map;
  }

  /**
   * Find a path to a location. Stores the location argument as the creature's
   * current heading.
   * @param _map the map to move on
   * @param _owner The creature to move
   * @param _location The target location
   */
  public MFGotoLocationSubtask(MFMap _map, MFCreature _owner, MFLocation _location)
  {
    this(_map, _owner);
    _owner.setCurrentHeading(_location);
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    if (!this.searchingForPath) {
      // path search returned no path
      if (this.noPathFound) {
        throw new MFSubtaskCanceledException(this.getOwner().getName() +
             " couldn't find a path to " + this.getOwner().getCurrentHeading());
      // calculate new path if the subtask is updated for the first time
      // or if the current path is no longer valid
      } else if (this.path == null || !this.path.isPathValid()) {
        searchPath();
      // check if it's time to move and the search for a subpath has finished
      } else if (this.updateCount >= this.getOwner().getSpeed() && this.path.hasNext()) {
        // get next tile to move to
        MFEDirection nextMove = this.path.next();

        boolean movedSuccessful = this.getOwner().move(nextMove);
        // reset counter
        this.updateCount = 0;

        // couldn't move to target tile
        if (!movedSuccessful) {
          // recalculate path
          this.searchPath();
          return false;
        }

        // check if we made the last move of the path
        if (!this.path.hasNext()) {
          // target reached?
          if (this.getOwner().getLocation().equals(this.getOwner().getCurrentHeading())) {
            return true;
          } else {
            // error during pathfinding
            throw new MFSubtaskCanceledException(this.getOwner().getName() +
                  " couldn't reach target (" + this.getOwner().getCurrentHeading() + ")");
          }
        }
      }
    }
    ++this.updateCount;
    return false;
  }

  /**
   * Retrieves the path and resets the update counter so that the next update
   * starts moving the creature. If the parameter is <code>null</code> no path
   * was found and the update() method has to be notified of this.
   * @param _path <code>null</code> if no path was found
   */
  @Override
  public void pathSearchFinished(final MFPath _path)
  {
    this.path = _path;
    this.searchingForPath = false;
    if (_path != null) {
      this.updateCount = this.getOwner().getSpeed();
    } else {
      this.noPathFound = true;
    }
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Stores the map */
  private final MFMap map;
  
  /** Stores the time past since the last move */
  private int updateCount;
  /** The path the creature will take */
  private MFPath path;
  /** Flag for unsuccessful path finding. Cannot be reset */
  private boolean noPathFound;
  /** Flag for the state */
  private boolean searchingForPath;

  /**
   * Enqueues a path
   */
  private void searchPath()
  {
    final MFTile startTile = this.map.getTile(this.getOwner().getLocation());
    final MFTile goalTile  = this.map.getTile(this.getOwner().getCurrentHeading());
    final int clearance = this.getOwner().getClearance();
    final EnumSet<MFEMovementType> capabilities = this.getOwner().getCapabilities();
    
    MFPathFinder.getInstance().enqueuePathSearch(this.map, startTile, goalTile,
       clearance, capabilities, this);

    this.noPathFound = false;
    this.searchingForPath = true;
  }
}
