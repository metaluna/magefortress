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

import magefortress.creatures.behavior.MFIHoldable;
import magefortress.map.MFTile;

/**
 * Puts the currently held item into the workshop at the present location. If
 * the target workshop isn't at the current location an exception will be thrown.
 */
public class MFPutDraggedItemSubtask extends MFHoldingSubtask
{

  public MFPutDraggedItemSubtask(final MFIHoldable _holdable)
  {
    super(_holdable);
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    // puts it down
    final boolean success = this.getHoldable().putDown();

    if (!success) {
      String msg = "Could not put down item.";
      logger.severe(msg);
      throw new MFSubtaskCanceledException(msg);
    }

    return true;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
}
