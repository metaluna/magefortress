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
package magefortress.map;

import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.creatures.behavior.movable.MFEMovementType;

/**
 * Represents data used for navigation specific to a tile.
 */
public class MFNavigationTile
{

  public MFNavigationTile(int _posX, int _posY, int _posZ)
  {
    this.posX = _posX;
    this.posY = _posY;
    this.posZ = _posZ;
    this.clearanceValues = new EnumMap<MFEMovementType,Integer>(MFEMovementType.class);
  }
  
  /**
   * Sets the size of the biggest creature which can pass the tile.
   * @param _movementType The type of movement
   * @param _clearance The size of the creature
   */
  void setClearance(MFEMovementType _movementType, int _clearance)
  {
    this.clearanceValues.put(_movementType, _clearance);
  }

  /**
   * Gets the size of the biggest creature which can pass the tile.
   * @param _movementType The movement type
   * @return The size of the creature or 0 if inaccessible
   */
  int getClearance(MFEMovementType _movementType)
  {
    Integer result = this.clearanceValues.get(_movementType);
    if (result == null) {
      result = 0;
    }
    return result;
  }

  /**
   * Sets the section the tile belongs to. For navigational use.
   * @param _section The parent section.
   */
  void setParentSection(MFSection _section)
  {
    if (_section == null) {
      String msg = "Tile: Cannot set parent section to null.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.parentSection = _section;
  }

  /**
   * Gets the section the tile belongs to. For navigational use. May be
   * <code>null</code> if it was not set yet.
   * @return The parent section
   */
  MFSection getParentSection()
  {
    return this.parentSection;
  }

  /**
   * Marks the tile as a section entrance. If set to <code>null</code> the tile
   * stops being an entrance. For navigational use.
   * @param _entrance the entrance
   */
  void setEntrance(MFSectionEntrance _entrance)
  {
    this.entrance = _entrance;
  }

  MFSectionEntrance getEntrance()
  {
    return this.entrance;
  }

  boolean isEntrance()
  {
    return this.entrance != null;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Log */
  private static final Logger logger = Logger.getLogger(MFNavigationTile.class.getName());

  /** Position of the tile */
  private final int posX, posY, posZ;

  /** Saves how big a creature can stand on this and the surrounding tiles.
   * @Transient */
  private EnumMap<MFEMovementType, Integer> clearanceValues;
  /** The parent navigational section
   * @Transient */
  private MFSection parentSection;
  /** Marks the tile as being an entrance
   * @Transient */
  private MFSectionEntrance entrance;

}
