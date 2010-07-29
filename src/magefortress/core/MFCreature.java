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
package magefortress.core;

import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * Base class for all creatures
 */
public class MFCreature implements MFIMovable, MFIHoldable
{
  public MFCreature(String _name, MFRace _race)
  {

    // 1. set arguments
    this.setName(_name);

    if (_race == null) {
      String message = "Creature '" + this.name + "': " +
                        "Cannot create without a race.";
      logger.severe(message);
      throw new IllegalArgumentException(message);
    }
    this.race = _race;

    // 2. set default attributes
    this.location = MFLocation.NOWHERE;
    this.holdingBehavior = NULL_HOLDABLE;
    this.moveBehavior = NULL_MOVABLE;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String _name)
  {
    if (_name == null) {
      String message = "Creature '" + this.name + "': " +
                        "Cannot set name to null.";
      logger.severe(message);
      throw new IllegalArgumentException(message);
    }
    this.name = _name;
  }

  public MFRace getRace()
  {
    return this.race;
  }

  public MFLocation getLocation()
  {
    return this.location;
  }

  public void setItemYearnedFor(MFItem _item)
  {
    this.itemYearnedFor = _item;
  }

  public MFItem getItemYearnedFor()
  {
    return this.itemYearnedFor;
  }

  public void setMovingBehavior(MFIMovable _moveBehavior)
  {
    if (_moveBehavior == null) {
      String message = "Creature '" + this.name + "': " +
                        "Moving behavior must not be null.";
      logger.severe(message);
      throw new IllegalArgumentException(message);
    }
    // TODO make a copy of moving behavior - impossible?
    this.moveBehavior = _moveBehavior;
  }

  public MFIMovable getMovingBehavior()
  {
    return this.moveBehavior;
  }

  public void setHoldingBehavior(MFIHoldable _holdingBehavior)
  {
    if (_holdingBehavior == null) {
      String message = "Creature '" + this.name +"': " +
                        "Holding behavior must not be null.";
      logger.severe(message);
      throw new IllegalArgumentException(message);
    }
    // TODO make a copy of holding behavior - impossible?
    this.holdingBehavior = _holdingBehavior;
  }

  public MFIHoldable getHoldingBehavior()
  {
    return this.holdingBehavior;
  }

  public MFLocation lookForSimilarItems(MFItem _item)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public MFLocation findNearestNeighboringTile(MFLocation _location)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public void setSpeed(int _speed)
  {
    this.moveBehavior.setSpeed(_speed);
  }

  @Override
  public int getSpeed()
  {
    return this.moveBehavior.getSpeed();
  }

  @Override
  public boolean canMove()
  {
    return this.moveBehavior.canMove();
  }

  @Override
  public boolean move(MFEDirection _direction)
  {
    return this.moveBehavior.move(_direction);
  }

  @Override
  public void setCurrentHeading(MFLocation _location)
  {
    this.moveBehavior.setCurrentHeading(_location);
  }

  @Override
  public MFLocation getCurrentHeading()
  {
    return this.moveBehavior.getCurrentHeading();
  }

  @Override
  public EnumSet<MFEMovementType> getCapabilities()
  {
    return EnumSet.copyOf(this.moveBehavior.getCapabilities());
  }

  @Override
  public int getClearance()
  {
    return this.moveBehavior.getClearance();
  }

  @Override
  public boolean canHold()
  {
    return this.holdingBehavior.canHold();
  }

  @Override
  public boolean pickup()
  {
    //TODO move to MFIHoldable
    //final MFItem item = this.itemYearnedFor;
    //this.addItem(item);
    return this.holdingBehavior.pickup();
  }

  @Override
  public void addItem(MFItem _item)
  {
    this.holdingBehavior.addItem(_item);
  }

  @Override
  public boolean putItem(MFRoom _room)
  {
    return this.holdingBehavior.putItem(_room);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private String name;
  private final MFRace race;
  private MFLocation location;
  private MFItem itemYearnedFor;
  private MFIMovable moveBehavior;
  private MFIHoldable holdingBehavior;
  private static final Logger logger = Logger.getLogger(MFCreature.class.getName());
  private static final MFIMovable NULL_MOVABLE = new MFNullMovable();
  private static final MFIHoldable NULL_HOLDABLE = new MFNullHoldable();

}
