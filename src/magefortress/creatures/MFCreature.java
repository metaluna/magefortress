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
package magefortress.creatures;

import java.awt.Graphics2D;
import java.util.EnumSet;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.core.MFItem;
import magefortress.core.MFLocation;
import magefortress.core.MFRoom;
import magefortress.creatures.behavior.MFEMovementType;
import magefortress.creatures.behavior.MFIHoldable;
import magefortress.creatures.behavior.MFIMovable;
import magefortress.creatures.behavior.MFNullMovable;
import magefortress.creatures.behavior.MFNullHoldable;
import magefortress.graphics.MFIPaintable;
import magefortress.graphics.MFNullPaintable;
import magefortress.map.MFTile;

/**
 * Base class for all creatures
 */
public class MFCreature implements MFIMovable, MFIHoldable, MFIPaintable
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
    this.holdingBehavior = NULL_HOLDABLE;
    this.moveBehavior = NULL_MOVABLE;
    this.drawingBehavior = NULL_PAINTABLE;
    this.size = 1;
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

  public int getSize()
  {
    return this.size;
  }

  /**
   * Must be bigger than 0.
   * @param _size
   */
  public void setSize(int _size)
  {
    if (_size < 0) {
      String msg = "Creature '" + this.name + "': " +
                    "Size has to be bigger 0.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.size = _size;
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

  public void setDrawingBehavior(MFIPaintable _drawingBehavior)
  {
    if (_drawingBehavior == null) {
      String message = "Creature '" + this.name + "': " +
                        "Drawing behavior must not be null.";
      logger.severe(message);
      throw new IllegalArgumentException(message);
    }
    this.drawingBehavior = _drawingBehavior;
  }

  public MFIPaintable getDrawingBehavior()
  {
    return this.drawingBehavior;
  }

  //---vvv---     MOVABLE INTERFACE     ---vvv---
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
  public void move(MFEDirection _direction)
  {
    this.moveBehavior.move(_direction);
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
  public void setClearance(int _clearance)
  {
    this.moveBehavior.setClearance(_clearance);
  }

  @Override
  public MFLocation getLocation()
  {
    return this.moveBehavior.getLocation();
  }

  @Override
  public void setLocation(MFLocation _location)
  {
    this.moveBehavior.setLocation(_location);
  }

  //---vvv---     HOLDABLE INTERFACE    ---vvv---
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

  //---vvv---     PAINTABLE INTERFACE    ---vvv---
  public void update(long _currentTime)
  {
    this.drawingBehavior.update(_currentTime);
  }

  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    final int x = this.moveBehavior.getLocation().x * MFTile.TILESIZE + 
                    _x_translation + SPRITE_OFFSET_X;
    final int y = this.moveBehavior.getLocation().y * MFTile.TILESIZE + 
                    _y_translation + SPRITE_OFFSET_Y;

    this.drawingBehavior.paint(_g, x, y);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFCreature.class.getName());
  private static final MFIMovable NULL_MOVABLE = new MFNullMovable();
  private static final MFIHoldable NULL_HOLDABLE = new MFNullHoldable();
  private static final MFIPaintable NULL_PAINTABLE = new MFNullPaintable();
  private static final int SPRITE_OFFSET_X = 8;
  private static final int SPRITE_OFFSET_Y = 4;

  private String name;
  private int size;
  private final MFRace race;
  private MFItem itemYearnedFor;
  private MFIMovable moveBehavior;
  private MFIHoldable holdingBehavior;
  private MFIPaintable drawingBehavior;
}
