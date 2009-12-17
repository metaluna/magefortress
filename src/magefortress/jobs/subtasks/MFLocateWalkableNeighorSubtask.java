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

import magefortress.core.MFCreature;
import magefortress.core.MFLocation;

/**
 * Locates the closest unoccupied, walkable neighbor to the specified location
 */
public class MFLocateWalkableNeighorSubtask extends MFSubtask
{

  public MFLocateWalkableNeighorSubtask(MFCreature _owner, MFLocation _location)
  {
    super(_owner);
    this.location = _location;
  }
  
  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    final MFLocation goal = this.getOwner().findNearestNeighboringTile(this.location);
    if (goal == null) {
      throw new MFSubtaskCanceledException(this.getOwner().getName() +
              ": Could not find path to " + this.location);
    }
    this.getOwner().setCurrentHeading(goal);
    return true;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFLocation location;

}
