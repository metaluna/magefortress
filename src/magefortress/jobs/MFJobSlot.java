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
package magefortress.jobs;

import java.util.logging.Logger;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.creatures.MFCreature;

/**
 *
 */
public class MFJobSlot
{

  public MFJobSlot(MFLocation _location)
  {
    if (_location == null) {
      String msg = "Cannot create without a location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.location = _location;
  }

  public MFLocation getLocation()
  {
    return this.location;
  }

  public boolean isAvailable()
  {
    return creature == null;
  }

  public void occupy(MFCreature _creature)
  {
    if (_creature == null) {
      String msg = "Cannot let null occupy the slot@" + this.location;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.creature != null) {
      String msg = "Cannot let the creature " + _creature.getName() +
                   " occupy the slot@" + this.location + " currently used by " +
                   this.creature.getName();
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    this.creature = _creature;
  }

  public void free(MFCreature _creature)
  {
    if (_creature == null) {
      String msg = "Cannot let null leave the slot@" + this.location;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.creature == null) {
      String msg = "Cannot let creature " + _creature.getName() +
                   " leave not occupied slot@" + this.location;
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    if (_creature != this.creature) {
      String msg = "Cannot let other creature (" + _creature.getName() +
                   ") than the current occupier (" + this.creature.getName() +
                   ")leave the slot@" + this.location;
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    this.creature = null;
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFJobSlot.class.getName());

  private final MFLocation location;

  private MFCreature creature;
}
