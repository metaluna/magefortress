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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.items.placeable.MFIPlaceable;
import magefortress.items.placeable.MFUnplaceable;
import magefortress.storage.MFISaveable;

/**
 * The class holds information about materials needed to produce an item.
 */
public class MFBlueprint implements MFISaveable
{
  /**
   * Constructor
   * @param _name The name of the item produced
   */
  public MFBlueprint(int _id, String _name)
  {
    if (_name == null || _name.trim().isEmpty()) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                  "blueprint without name.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.id = _id;
    this.name = _name;
    this.materials = new ArrayList<MFBlueprint>();

    // set default behaviors
    this.placingBehavior = UNPLACEABLE;
  }

  /**
   * Returns the name of the blueprint's product.
   * @return The name of the blueprint's product
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * An ingredient needed to produce the item.
   * @param _material The ingredient
   */
  public void addMaterial(MFBlueprint _material)
  {
    if (_material == null) {
      String msg = this.getClass().getSimpleName() + " '" + this.name +
                                                "': Cannot add null material.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.materials.add(_material);
  }

  public List<MFBlueprint> getMaterials()
  {
    return Collections.unmodifiableList(this.materials);
  }

  public MFItem createItem()
  {
    MFItem result = new MFItem(this);

    try {
      result.setPlacingBehavior(this.placingBehavior.newInstance());
    } catch (InstantiationException ex) {
      String msg = "Unable to instantiate placing behavior \""+
                          placingBehavior.getName()+"\" for " + this.name;
      logger.log(Level.SEVERE, msg, ex);
    } catch (IllegalAccessException ex) {
      String msg =  "Unable to instantiate placing behavior \""+
                          placingBehavior.getName()+"\" for " + this.name +
                          ". Cannot access class or no-parameter constructor.";
      logger.log(Level.SEVERE, msg, ex);
    }

    return result;
  }

  //---vvv---          SAVEABLE INTERFACE METHODS         ---vvv---
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
  
  //---vvv---         PLACING BEHAVIOR GETTER/SETTER         ---vvv---
  @SuppressWarnings("unchecked")
  public void setPlacingBehavior(String _placingBehaviorClassName) throws ClassNotFoundException
  {
    Class<? extends MFIPlaceable> placingBehaviorClass =
      (Class<? extends MFIPlaceable>) Class.forName(_placingBehaviorClassName);

    if (!MFIPlaceable.class.isAssignableFrom(placingBehaviorClass)) {
      String msg = this.getClass().getSimpleName() + " '" + this.name + "': " +
                "Cannot set placing behavior to " + _placingBehaviorClassName +
                ". Interface mismatch.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.setPlacingBehavior(placingBehaviorClass);
  }

  public void setPlacingBehavior(Class<? extends MFIPlaceable> _placingBehavior)
  {
    this.placingBehavior = _placingBehavior;
  }

  Class<? extends MFIPlaceable> getPlacingBehavior()
  {
    return this.placingBehavior;
  }


  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Log */
  private static final Logger logger = Logger.getLogger(MFBlueprint.class.getName());
  // Default behaviors
  private static final Class<? extends MFIPlaceable> UNPLACEABLE = MFUnplaceable.class;

  /** The product */
  private final String name;
  /** The ingredients */
  private final ArrayList<MFBlueprint> materials;

  /** ID used for storage */
  private int id;
  // Configured behaviors
  private Class<? extends MFIPlaceable> placingBehavior;

}
