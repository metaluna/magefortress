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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFTileTest
{
  MFTile[][] tiles;

  @Before
  public void setUp()
  {
  }

  @Test
  public void shouldHaveClearanceValue()
  {
    MFTile tile = new MFTile(0,0,0);

    int walkValue = 1;
    int flyValue = 2;
    tile.setClearance(MFEMovementType.WALK, walkValue);
    tile.setClearance(MFEMovementType.FLY, flyValue);

    assertEquals(walkValue, tile.getClearance(MFEMovementType.WALK));
    assertEquals(flyValue, tile.getClearance(MFEMovementType.FLY));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotHaveClearanceValue()
  {
    MFTile tile = new MFTile(0,0,0);

    tile.getClearance(MFEMovementType.WALK);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  
  /**
   * Creates a dug out underground tile with no walls.
   * @param x Position on the map
   * @param y Position on the map
   * @return The created tile
   */
  private MFTile createUndergroundTile(int x, int y, int z)
  {
    boolean isDugOut = true;
    boolean hasWallN = y == 0 || false;
    boolean hasWallE = x == tiles.length || false;
    boolean hasWallS = y == tiles[0].length || false;
    boolean hasWallW = x == 0 || false;
    boolean hasFloor = true;
    boolean isUnderground = true;

    return new MFTile(x, y, z, isDugOut, hasWallN, hasWallE, hasWallS, hasWallW,
                                  hasFloor, isUnderground);
  }
}