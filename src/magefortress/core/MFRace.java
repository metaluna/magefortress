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
package magefortress.core;

import java.util.logging.Logger;
import magefortress.storage.MFISaveable;

/**
 * Contains attributes equal across all creatures of the same race, namely:
 * <ul>
 * <li>moving behavior</li>
 * <li>holding behavior</li>
 * <li>working behavior</li>
 * <li>...</li>
 * </ul>
 *
 * Instances of this class are <strong>immutable</strong> except regarding their id.
 */
public class MFRace implements Immutable, MFISaveable
{
  /**
   * Constructor with behavior classes defined by a string. It converts the
   * strings to Class objects and calls the other constructor method. Used by
   * the persistence adapter.
   * @param _id The database id of the object
   * @param _name The name of the race
   * @param _movingBehaviorClassName Class name of the moving behavior strategy
   * @param _holdingBehaviorClassName Class name of the holding behavior strategy
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public MFRace(int _id, String _name, String _movingBehaviorClassName, String _holdingBehaviorClassName)
          throws ClassNotFoundException
  {
    this(_id, _name, (Class<MFIMovable>) Class.forName(_movingBehaviorClassName), (Class<MFIHoldable>) Class.forName(_holdingBehaviorClassName));    
  }

  /**
   * Constructor with behavior classes defined by Class objects.
   * @param _id The database id of the object
   * @param _name The name of the race
   * @param _movingBehavior Class object of the moving behavior strategy
   * @param _holdingBehavior Class object of the holding behavior strategy
   */
  public MFRace(int _id, String _name, Class<? extends MFIMovable> _movingBehavior, Class<? extends MFIHoldable> _holdingBehavior)
  {
    if (_name == null) {
      String msg = "MFRace: Name must not be null. Cannot create race";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_movingBehavior == null) {
      String msg = "MFRace \"" + _name + "\": moving behavior must not be null. " +
              "Cannot create race";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_holdingBehavior == null) {
      String msg = "MFRace \"" + _name + "\": holding behavior must not be null. " +
              "Cannot create race";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    if (!MFIMovable.class.isAssignableFrom(_movingBehavior)) {
      String msg = "MFRace \"" + _name + "\": moving behavior " +
              _movingBehavior.getName() + " does not implement MFIMovable. " +
              "Cannot create race.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (!MFIHoldable.class.isAssignableFrom(_holdingBehavior)) {
      String msg = "MFRace \"" + _name + "\": holding behavior " +
              _holdingBehavior.getName() + " does not implement MFIHoldable. " +
              "Cannot create race.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.id = _id;
    this.name = _name;
    this.movingBehavior = _movingBehavior;
    this.holdingBehavior = _holdingBehavior;
  }

  @Override
  public int getId()
  {
    return this.id;
  }

  @Override
  public void setId(int _id)
  {
    this.id = _id;
  }

  public String getName()
  {
    return this.name;
  }

  public Class<? extends MFIMovable> getMovingBehaviorClass()
  {
    return this.movingBehavior;
  }

  public Class<? extends MFIHoldable> getHoldingBehaviorClass()
  {
    return this.holdingBehavior;
  }

  public MFCreature createCreature()
  {
    MFCreature creature = new MFCreature(this.name, this);
    
    try {
      creature.setHoldingBehavior(holdingBehavior.newInstance());
    } catch (InstantiationException ex) {
      logger.severe("Unable to instantiate holding behavior \""+
              holdingBehavior.getName()+"\" for " + this.name);
    } catch (IllegalAccessException ex) {
      logger.severe("Unable to instantiate holding behavior \""+
              holdingBehavior.getName()+"\" for " + this.name +
              ". Cannot access class or no-parameter constructor.");
    }

    try {
      creature.setMovingBehavior(movingBehavior.newInstance());
    } catch (InstantiationException ex) {
      logger.severe("Unable to instantiate moving behavior \""+
              holdingBehavior.getName()+"\" while creating a " + this.name);
    } catch (IllegalAccessException ex) {
      logger.severe("Unable to instantiate moving behavior \""+
              holdingBehavior.getName()+"\" while creating a " + this.name +
              ". Cannot access class or no-parameter constructor.");
    }

    return creature;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private int id;
  private final String name;
  private final Class<? extends MFIMovable> movingBehavior;
  private final Class<? extends MFIHoldable> holdingBehavior;
  private static final Logger logger = Logger.getLogger(MFRace.class.getName());

}
