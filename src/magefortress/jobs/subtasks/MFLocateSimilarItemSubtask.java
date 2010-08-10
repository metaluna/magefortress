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

import magefortress.items.MFItem;
import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;

/**
 * Looks for a type of item, finds a best path and saves both as the owner's
 * current heading.
 */
public class MFLocateSimilarItemSubtask extends MFSubtask
{
  
  public MFLocateSimilarItemSubtask(final MFCreature _owner, final MFItem _itemType)
  {
    super(_owner);
    this.itemType = _itemType;
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    MFLocation location = null;//this.getOwner().lookForSimilarItems(itemType);

    if (location == null) {
      throw new MFSubtaskCanceledException("Couldn't locate similar items: " + itemType.getName());
    } else {
      this.getOwner().setCurrentHeading(location);
    }
    return true;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  final private MFItem itemType;

}
