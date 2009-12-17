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

import java.util.Queue;
import magefortress.core.MFCreature;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;

/**
 * Grabs the current heading from the owner, calculates a path and moves
 * forward with the creatures speed until it reaches the goal.
 */
public class MFGotoLocationSubtask extends MFSubtask
{

  /**
   * Find a path to the location stored as the creature's current heading by a
   * previous subtask.
   * @param _owner The creature to move
   */
  public MFGotoLocationSubtask(MFCreature _owner)
  {
    super(_owner);
  }

  /**
   * Find a path to a location. Stores the location argument as the creature's
   * current heading.
   * @param _owner The creature to move
   * @param _location The target location
   */
  public MFGotoLocationSubtask(MFCreature _owner, MFLocation _location)
  {
    this(_owner);
    _owner.setCurrentHeading(_location);
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    // calculate new path when the subtask is updated for the first time
    if (this.path == null) {
      initPath();
    }

    // move if it's time to
    if (this.updateCount >= this.getOwner().getSpeed()) {
      // get next tile to move to
      MFEDirection nextMove = this.path.poll();

      boolean movedSuccessful = this.getOwner().move(nextMove);
      // reset counter
      this.updateCount = 0;

      // couldn't move to target tile
      if (!movedSuccessful) {
        // recalculate path
        initPath();
        return false;
      }

      // check if we made the last move of the path
      if (this.path.peek() == null) {
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
    ++this.updateCount;
    return false;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Stores the time past since the last move */
  private int updateCount;
  /** The path the creature will take */
  private Queue<MFEDirection> path;

  /**
   * Calculates a path and resets the update counter so that the next update
   * starts moving the creature.
   * @throws MFSubtaskCanceledException when no path can be found
   */
  private void initPath() throws MFSubtaskCanceledException
  {
    this.path = this.getOwner().calculatePath();
    if (this.path == null) {
      throw new MFSubtaskCanceledException(this.getOwner().getName() +
              " couldn't find a path to " + this.getOwner().getCurrentHeading());
    }
    this.updateCount = this.getOwner().getSpeed();
  }
}
