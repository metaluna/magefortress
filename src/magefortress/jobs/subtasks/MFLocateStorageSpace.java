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
package magefortress.jobs.subtasks;

import magefortress.core.MFIStorageLocator;
import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;
import magefortress.items.MFBlueprint;

/**
 * This task simply asks the storage locator for a location where the item
 * last picked up by the owner can be dropped.
 */
public class MFLocateStorageSpace extends MFSubtask
{
  public MFLocateStorageSpace(MFCreature _owner, MFIStorageLocator _locator)
  {
    super(_owner);
    if (_locator == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                  "without a storage locator.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_owner.getItem() == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                "without the owner " + _owner.getName() +
                                " currently carrying an item.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.locator = _locator;
    this.itemToStore = _owner.getItem().getBlueprint();
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    MFLocation goal = this.locator.findStorage(itemToStore);

    if (goal == MFLocation.NOWHERE) {
      String msg = "No storage space found for item " + this.itemToStore.getName() +
                   ". " + this.getOwner().getName() + " is desparate.";
      logger.severe(msg);
      throw new MFSubtaskCanceledException(msg);
    }

    this.getOwner().setCurrentHeading(goal);
    return true;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFBlueprint itemToStore;
  private final MFIStorageLocator locator;
}
