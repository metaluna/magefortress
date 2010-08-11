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
package magefortress.map.ground;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.logging.Logger;
import magefortress.core.Immutable;
import magefortress.core.MFEDirection;
import magefortress.graphics.MFIPaintable;
import magefortress.items.MFBlueprint;
import magefortress.jobs.MFIProducible;
import magefortress.jobs.digging.MFIDiggable;
import magefortress.map.MFTile.Corner;

/**
 * Stores image data of various states of a specific ground type.
 */
public class MFGround implements Immutable, MFIProducible, MFIDiggable
{
  public MFGround(MFBlueprint _blueprint, int _hardness, 
                  MFIPaintable _solidTile, MFIPaintable _basicFloor,
                  EnumMap<MFEDirection, MFIPaintable> _basicWall,
                  EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>> _basicCorner)
  {
    this.blueprint = _blueprint;
    this.hardness = _hardness;
    this.solidTile = _solidTile;
    this.basicFloor = _basicFloor;
    this.basicWalls = new EnumMap<MFEDirection, MFIPaintable>(_basicWall);
    this.basicCorners = new EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>>(_basicCorner);

    validateState();
  }

  /**
   * Updates all contained paintables.
   */
  public void update()
  {
    this.basicFloor.update();
    for (MFIPaintable wall : this.basicWalls.values()) {
      wall.update();
    }
    for (EnumMap<Corner, MFIPaintable> corners : this.basicCorners.values()) {
      for (MFIPaintable corner : corners.values()) {
        corner.update();
      }
    }
  }

  //---vvv---     PRODUCIBLE INTERFACE METHODS     ---vvv---
  @Override
  public MFBlueprint getBlueprint()
  {
    return this.blueprint;
  }

  //---vvv---      DIGGABLE INTERFACE METHODS      ---vvv---
  @Override
  public boolean isDiggable()
  {
    return true;
  }

  @Override
  public int getHardness()
  {
    return this.hardness;
  }

  @Override
  public MFIPaintable getSolidTile()
  {
    return this.solidTile;
  }

  @Override
  public MFIPaintable getBasicFloor()
  {
    return this.basicFloor;
  }

  @Override
  public MFIPaintable getBasicWall(MFEDirection _direction)
  {
    return this.basicWalls.get(_direction);
  }

  @Override
  public MFIPaintable getBasicCorner(MFEDirection _direction, Corner _corner)
  {
    return this.basicCorners.get(_direction).get(_corner);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFGround.class.getName());
  
  private final MFBlueprint blueprint;
  private final int hardness;
  private final MFIPaintable solidTile;
  private final MFIPaintable basicFloor;
  private final EnumMap<MFEDirection, MFIPaintable> basicWalls;
  private final EnumMap<MFEDirection, EnumMap<Corner,MFIPaintable>> basicCorners;

  private final void validateState()
  {
    if (this.blueprint == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without " +
                                                     "a blueprint.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.hardness < 1) {
      String msg = this.getClass().getSimpleName() + ": Cannot create with " +
                                                     "hardness < 1.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.basicFloor == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without " +
                                                     "image for a basic floor";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.basicWalls == null ||
          !this.basicWalls.keySet().containsAll(MFEDirection.straight())) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without " +
                                                     "all four basic walls.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.basicCorners == null ||
          !this.basicCorners.keySet().containsAll(MFEDirection.diagonals()) ||
          !this.basicCorners.get(MFEDirection.NE).keySet().containsAll(Arrays.asList(Corner.values())) ||
          !this.basicCorners.get(MFEDirection.SE).keySet().containsAll(Arrays.asList(Corner.values())) ||
          !this.basicCorners.get(MFEDirection.SW).keySet().containsAll(Arrays.asList(Corner.values())) ||
          !this.basicCorners.get(MFEDirection.NW).keySet().containsAll(Arrays.asList(Corner.values()))) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without " +
                                                     "all basic corners.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }

}
