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
    final int WIDTH = 5;
    final int HEIGHT = 5;

    tiles = new MFTile[WIDTH][HEIGHT];

    int z = 0;

    for (int x = 0; x<WIDTH; ++x)
      for (int y = 0; y<HEIGHT; ++y)
      {
        tiles[x][y] = createUndergroundTile(x, y, z);
      }
  }

  @Test
  public void shouldHaveNoCornersWhenThereAreNoWalls()
  {
    MFTile testedTile = tiles[1][1];
    
    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveNoCornersWhenAllWallsArePresent()
  {
    MFTile testedTile = tiles[1][1];
    testedTile.setWalls(true, true, true, true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveHorizontalCorners()
  {
    MFTile testedTile = tiles[1][1];

    /*
     *  _
     *  X
     *  -
     *
     */
    testedTile.setWallNorth(true);
    testedTile.setWallSouth(true);

    testedTile.calculateCorners(tiles);
    
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveVerticalCorners()
  {
    MFTile testedTile = tiles[1][1];

    /*
     *  |X|
     *
     */
    testedTile.setWallEast(true);
    testedTile.setWallWest(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveBothCornersWhenAdjacentWallsArePresent()
  {
    MFTile above = tiles[1][0];
    MFTile left = tiles[0][1];
    MFTile testedTile = tiles[1][1];
    MFTile right = tiles[2][1];
    MFTile below = tiles[1][2];

    /*
     * _| |_
     * _ X _
     *  | |
     *
     */
    above.setWallEast(true);
    above.setWallWest(true);
    left.setWallNorth(true);
    left.setWallSouth(true);
    right.setWallNorth(true);
    right.setWallSouth(true);
    below.setWallEast(true);
    below.setWallWest(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveBothCornersWhenRightAndLeftWallsArePresent()
  {
    MFTile left = tiles[0][1];
    MFTile testedTile = tiles[1][1];
    MFTile right = tiles[2][1];

    /*
     * _   _
     * _ X _
     *   
     * */
    left.setWallNorth(true);
    left.setWallSouth(true);
    right.setWallNorth(true);
    right.setWallSouth(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveBothCornersWhenWallsAboveAndBelowArePresent()
  {
    MFTile above = tiles[1][0];
    MFTile testedTile = tiles[1][1];
    MFTile below = tiles[1][2];

    /*
     *  | |
     *   X
     *  | |
     * */
    above.setWallEast(true);
    above.setWallWest(true);
    below.setWallEast(true);
    below.setWallWest(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveCornersWhenOnFirstRow()
  {
    MFTile testedTile = tiles[1][0];

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNE());

    /*
     * _____
     * | |X|
     *  
     */
    testedTile.setWallEast(true);
    testedTile.setWallWest(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNE());

  }

  @Test
  public void shouldHaveCornersWhenOnLastRow()
  {
    MFTile testedTile = tiles[1][4];

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSW());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSE());

    /*
     * 
     * |_|x_|_
     *
     */
    testedTile.setWallEast(true);
    testedTile.setWallWest(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSE());
  }

  @Test
  public void shouldHaveCornersWhenOnFirstColumn()
  {
    MFTile testedTile = tiles[0][1];

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerSW());

    /*
     * _____
     * |_
     * |X
     * |-
     * |
     */
    testedTile.setWallNorth(true);
    testedTile.setWallSouth(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveCornersWhenOnLastColumn()
  {
    MFTile testedTile = tiles[4][1];

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerSE());

    /*
     * __
     *  _|
     *  X|
     *  -|
     *   |
     *
     */
    testedTile.setWallNorth(true);
    testedTile.setWallSouth(true);

    testedTile.calculateCorners(tiles);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSE());
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