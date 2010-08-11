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
package magefortress.items;

import java.util.logging.Logger;
import magefortress.items.placeable.MFIPlaceable;
import magefortress.items.placeable.MFUnplaceable;
import magefortress.jobs.MFIProducible;

/**
 * Base class for all items
 */
public class MFItem implements MFIPlaceable, MFIProducible
{
  public MFItem(MFBlueprint _blueprint)
  {
    if (_blueprint == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                        "without a blueprint.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.blueprint = _blueprint;

    // set default behaviors
    this.placingBehavior = UNPLACEABLE;
  }
  
  public String getName()
  {
    return this.blueprint.getName();
  }

  //---vvv---     PLACEABLE INTERFACE METHODS     ---vvv---
  public void setPlacingBehavior(MFIPlaceable _placingBehavior)
  {
    if (_placingBehavior == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot set placing " +
                                                            "behavior to null.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    
    this.placingBehavior = _placingBehavior;

    String msg = this.getClass().getSimpleName() + ": Successfully set " +
          "placing behavior to " + _placingBehavior.getClass().getSimpleName();
    logger.finer(msg);
  }

  @Override
  public boolean isPlaceable()
  {
    return this.placingBehavior.isPlaceable();
  }

  @Override
  public boolean setPlaced(boolean _placed)
  {
    return this.placingBehavior.setPlaced(_placed);
  }

  @Override
  public boolean isPlaced()
  {
    return this.placingBehavior.isPlaced();
  }

  @Override
  public int getLivingValue()
  {
    return this.placingBehavior.getLivingValue();
  }

  //---vvv---     PRODUCIBLE INTERFACE METHODS     ---vvv---
  @Override
  public MFBlueprint getBlueprint()
  {
    return this.blueprint;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFItem.class.getName());
  private static final MFIPlaceable UNPLACEABLE = new MFUnplaceable();

  private final MFBlueprint blueprint;
  
  // behaviors
  private MFIPlaceable placingBehavior;
}
