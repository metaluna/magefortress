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

import java.awt.Point;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFMapTest
{
  private MFMap map;

  public MFMapTest()
  {
  }

  @Before
  public void setUp()
  {
    this.map = new MFMap(5,5,1);
    MFTile[][] level = this.map.getLevelMap(0);
    for (MFTile[] cols : level) {
      for (MFTile tile : cols) {
        tile.setDugOut(true);
      }
    }
  }

  @Test
  public void shouldConvertToTilespace()
  {
    MFLocation expLocation;
    MFLocation gotLocation;
    int length = MFTile.TILESIZE;

    expLocation = new MFLocation(0, 0, 0);
    gotLocation = MFMap.convertToTilespace(0, 0, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(0, 0, 0);
    gotLocation = MFMap.convertToTilespace(length-1, length-1, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(1, 1, 0);
    gotLocation = MFMap.convertToTilespace(length, length, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(999, 999, 999);
    gotLocation = MFMap.convertToTilespace(length*999, length*999, 999, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(-1, -1, 0);
    gotLocation = MFMap.convertToTilespace(-1, -1, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

  }

  @Test
  public void shouldConvertFromTilespace()
  {
    Point expPoint;
    Point gotPoint;

    expPoint = new Point(0, 0);
    gotPoint = MFMap.convertFromTilespace(0, 0);
    assertEquals(expPoint, gotPoint);

    expPoint = new Point(MFTile.TILESIZE, MFTile.TILESIZE);
    gotPoint = MFMap.convertFromTilespace(1, 1);
    assertEquals(expPoint, gotPoint);

    expPoint = new Point(MFTile.TILESIZE*999, MFTile.TILESIZE*999);
    gotPoint = MFMap.convertFromTilespace(999, 999);
    assertEquals(expPoint, gotPoint);

    expPoint = new Point(-MFTile.TILESIZE, -MFTile.TILESIZE);
    gotPoint = MFMap.convertFromTilespace(-1, -1);
    assertEquals(expPoint, gotPoint);
  }

  @Test
  public void shouldCalculateClearanceValuesOnEmptyMap()
  {
    map.calculateClearanceValues(MFEMovementType.WALK);

    for (MFTile[] cols : map.getLevelMap(0)) {
      for (MFTile tile : cols) {
        //System.out.println("Testing tile " + tile.getPosX() + "/" + tile.getPosY() + "/" + tile.getPosZ());
        int expClearance = cols.length - Math.max(tile.getPosX(), tile.getPosY());
        assertEquals(expClearance, tile.getClearance(MFEMovementType.WALK));
      }
    }
  }

  @Test
  public void shouldCalculateClearanceWithObstacles()
  {
    //setup
    MFTile[][] level = map.getLevelMap(0);
    level[2][2].setDugOut(false);

    map.calculateClearanceValues(MFEMovementType.WALK);

    int expClearance = 0;
    int gotClearance = level[2][2].getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 2;
    gotClearance = level[0][0].getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 1;
    gotClearance = level[1][1].getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 2;
    gotClearance = level[3][3].getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);
  }

}