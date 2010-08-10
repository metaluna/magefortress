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

import java.util.EnumMap;
import magefortress.core.MFEDirection;
import magefortress.graphics.MFIPaintable;
import magefortress.map.MFTile.Corner;

/**
 *
 */
public class MFBasicUnderground
{
  public static MFIPaintable getBasicSolidTile()
  {
    MFIPaintable solidTile = new MFBasicUndergroundFloor(true);
    return solidTile;
  }

  public static MFIPaintable getBasicUndergroundFloor()
  {
    MFIPaintable floor = new MFBasicUndergroundFloor(false);
    return floor;
  }

  public static EnumMap<MFEDirection, MFIPaintable> getBasicUndergroundWalls()
  {
    EnumMap<MFEDirection, MFIPaintable> walls = new EnumMap<MFEDirection, MFIPaintable>(MFEDirection.class);
    for (MFEDirection dir : MFEDirection.straight()) {
      walls.put(dir, new MFBasicUndergroundWall(dir));
    }
    return walls;
  }

  public static EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>> getBasicUndergroundCorners()
  {
    EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>> corners = new EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>>(MFEDirection.class);
    for (MFEDirection dir : MFEDirection.diagonals()) {
      EnumMap<Corner, MFIPaintable> cornerTypes = new EnumMap<Corner, MFIPaintable>(Corner.class);
      corners.put(dir, cornerTypes);
      for (Corner corner : Corner.values()) {
        cornerTypes.put(corner, new MFBasicUndergroundCorner(dir, corner));
      }
    }
    return corners;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private MFBasicUnderground()
  {
    
  }
}
