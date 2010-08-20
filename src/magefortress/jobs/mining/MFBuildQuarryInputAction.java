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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.input.MFAreaInputAction;
import magefortress.jobs.MFConstructionSite;

/**
 *
 */
public class MFBuildQuarryInputAction extends MFAreaInputAction
{

  public MFBuildQuarryInputAction(MFGame _game, Collection<MFLocation> _memberLocations)
  {
    super(_game, _memberLocations);
    this.jobSlots = new HashSet<MFLocation>();
  }

  public int getJobSlotCount()
  {
    return 1;
  }

  boolean isValidJobSlotLocation(MFLocation _location)
  {
    return this.getArea().contains(_location);
  }

  public void putJobSlot(MFLocation _location)
  {
    if (_location == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot add null job slot.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.jobSlots.contains(_location)) {
      String msg = this.getClass().getSimpleName() + ": Cannot add two " +
                                                "job slots@" + _location + ".";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.jobSlots.size() == this.getJobSlotCount()) {
      String msg = this.getClass().getSimpleName() + ": Cannot add any " +
                            "more job slots@"+ _location +". All " +
                            this.getJobSlotCount() + " slots have been placed.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    if (!this.isValidJobSlotLocation(_location)) {
      String msg = this.getClass().getSimpleName() + ": Cannot add job slot. " +
                          "Location@" + _location + " is not inside the room.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    this.jobSlots.add(_location);
  }

  //---vvv---       INPUT ACTION ABSTRACT METHODS       ---vvv---
  @Override
  public void execute()
  {
    if (this.jobSlots.size() < this.getJobSlotCount()) {
      String msg = this.getClass().getSimpleName() + ": Cannot execute " +
                        "job until all job slots have been placed. Currently " +
                        this.jobSlots.size() + " of " + this.getJobSlotCount();
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    for (MFLocation location : this.jobSlots) {
      final MFConstructionSite site = this.game.getGameObjectFactory().createJobSlotSite(location);
      this.game.addConstructionSite(site);
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final Set<MFLocation> jobSlots;
}
