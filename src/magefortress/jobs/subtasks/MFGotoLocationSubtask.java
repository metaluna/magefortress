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

import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.core.MFUnexpectedStateException;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.creatures.behavior.movable.MFIMovable;
import magefortress.map.MFIPathFinderListener;
import magefortress.map.MFPath;
import magefortress.map.MFPathFinder;

/**
 * Grabs the current heading from the owner, calculates a path and moves
 * forward with the creatures speed until it reaches the goal.
 */
public class MFGotoLocationSubtask extends MFMovingSubtask implements MFIPathFinderListener
{

  /**
   * Find a path to the location stored as the creature's current heading by a
   * previous subtask.
   * @param _movable The game object to move
   * @param _pathFinder The path finder to query for paths
   */
  public MFGotoLocationSubtask(MFIMovable _movable, MFPathFinder _pathFinder)
  {
    this(_movable, null, _pathFinder);
  }

  /**
   * Find a path to a location. Stores the location argument and sets it as the 
   * creature's current heading when the task begins to search for a path.
   * @param _movable The game object to move
   * @param _location The target location. Will be set as current heading later.
   * @param _pathFinder The path finder to query for paths
   */
  public MFGotoLocationSubtask(MFIMovable _movable, MFLocation _location, MFPathFinder _pathFinder)
  {
    super(_movable);
    this.heading = _location;
    this.pathFinder = _pathFinder;
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    if (this.goalReached) {
      String msg = this.getClass().getSimpleName() + ": Trying to update " +
                                                     "after goal was reached";
      logger.severe(msg);
      throw new MFUnexpectedStateException(msg);
    }
    
    if (!this.searchingForPath) {
      
      // path search returned no path
      if (this.noPathFound) {
        String msg = "Could not find a path to " +
                                         this.getMovable().getCurrentHeading();
        logger.fine(msg);
        throw new MFNoPathFoundException(msg,
                                         this.getMovable().getLocation(),
                                         this.getMovable().getCurrentHeading());
      // calculate new path if the subtask is updated for the first time
      // or if the current path is no longer valid
      } else if (this.path == null || !this.path.isPathValid()) {

        // already there
        if (this.getMovable().getLocation().equals(this.heading) ||
            this.getMovable().getLocation().equals(this.getMovable().getCurrentHeading())) {
          this.goalReached = true;
          return true;
        } else {
          searchPath();
        }
        
      // check if it's time to move and the search for a subpath has finished
      } else if (this.path.hasNext()) {
        if (this.updateCount >= this.getMovable().getSpeed()) {

          move();
          boolean done = wasTargetReached();
          if (done) {
            this.goalReached = true;
            return true;
          }
        }
        ++this.updateCount;
      }
        
    }
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
      this.updateCount = this.getMovable().getSpeed();
    } else {
      this.noPathFound = true;
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The path finding algorithm */
  private final MFPathFinder pathFinder;
  /** Stores the heading for later use. May be null if the creature's current
   * heading is supposed to be used. */
  private final MFLocation heading;
  
  /** Stores the time passed since the last move */
  private int updateCount;
  /** The path the creature will take */
  private MFPath path;
  /** Flag for unsuccessful path finding. Cannot be reset */
  private boolean noPathFound;
  /** Flag for the state */
  private boolean searchingForPath;
  /** Reached goal */
  private boolean goalReached;

  /**
   * Enqueues a path
   */
  private void searchPath()
  {
    if (this.heading != null) {
      this.getMovable().setCurrentHeading(heading);
    }

    final int clearance = this.getMovable().getClearance();
    final MFCapability capability = this.getMovable().getCapability();
    
    this.pathFinder.enqueuePathSearch(this.getMovable().getLocation(),
            this.getMovable().getCurrentHeading(),clearance, capability, this);

    this.noPathFound = false;
    this.searchingForPath = true;
  }
  /**
   * Moves the owner of the subtask
   */
  private void move() throws MFSubtaskCanceledException
  {
    // get next tile to move to
    final MFEDirection nextMove = this.path.next();
    this.getMovable().move(nextMove);

    // reset counter
    this.updateCount = 0;
    // couldn't move to target tile
    //        if (!movedSuccessful) {
    //          // recalculate path
    //          this.searchPath();
    //          return false;
    //        }
  }

  private boolean wasTargetReached() throws MFSubtaskCanceledException
  {
    boolean result = false;

    // check if we made the last move of the path
    if (!this.path.hasNext()) {

      // target reached?
      MFLocation currentLoc = this.getMovable().getLocation();
      MFLocation goal = this.getMovable().getCurrentHeading();
      
      if (currentLoc.equals(goal)) {
        result = true;
      } else {
        // error during pathfinding
        String msg = this.getClass().getSimpleName() + ": Could not reach target (" +
                                  this.getMovable().getLocation() + "->" +
                                  this.getMovable().getCurrentHeading() + ")";
        logger.severe(msg);
        throw new MFUnexpectedStateException(msg);
      }

    }

    return result;
  }

}
