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
  private final int WIDTH = 5;
  private final int HEIGHT = 5;
  private final int DEPTH = 1;

  public MFMapTest()
  {
  }

  @Before
  public void setUp()
  {
    this.map = new MFMap(WIDTH, HEIGHT, DEPTH);
    for (int x = 0; x < WIDTH; ++x) {
      for (int y = 0; y < HEIGHT; ++y) {
        for (int z = 0; z < DEPTH; ++z) {
          boolean hasWallN = y == 0 || false;
          boolean hasWallE = x == WIDTH || false;
          boolean hasWallS = y == HEIGHT || false;
          boolean hasWallW = x == 0 || false;
          MFTile tile = this.map.getTile(x, y, z);
          tile.setDugOut(true);
          tile.setWalls(hasWallN, hasWallE, hasWallS, hasWallW);
        }
      }
    }
  }

  @Test
  public void shouldGetDimensions()
  {
    assertEquals(WIDTH, this.map.getWidth());
    assertEquals(HEIGHT, this.map.getHeight());
    assertEquals(DEPTH, this.map.getDepth());
  }

  @Test
  public void shouldGetTile()
  {
    MFTile tile = this.map.getTile(0, 0, 0);
    assertNotNull(tile);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void shouldNotGetTileBeneathMap()
  {
    this.map.getTile(WIDTH+1, HEIGHT, DEPTH);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void shouldNotGetTileBelowMap()
  {
    this.map.getTile(WIDTH, HEIGHT+1, DEPTH);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void shouldNotGetTileUnderMap()
  {
    this.map.getTile(WIDTH, HEIGHT, DEPTH+1);
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

    for (int x = 0; x < WIDTH; ++x) {
      for (int y = 0; y < HEIGHT; ++y) {
        for (int z = 0; z < DEPTH; ++z) {
        //System.out.println("Testing tile " + tile.getPosX() + "/" + tile.getPosY() + "/" + tile.getPosZ());
        int expClearance = Math.min(WIDTH, HEIGHT) - Math.max(x, y);
        assertEquals(expClearance, this.map.getTile(x, y, z).getClearance(MFEMovementType.WALK));
        }
      }
    }
  }

  @Test
  public void shouldCalculateClearanceWithObstacles()
  {
    //setup
    this.map.getTile(2, 2, 0).setDugOut(false);

    map.calculateClearanceValues(MFEMovementType.WALK);

    int expClearance = 0;
    int gotClearance = this.map.getTile(2, 2, 0).getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 2;
    gotClearance = this.map.getTile(0, 0, 0).getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 1;
    gotClearance = this.map.getTile(1, 1, 0).getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = Math.max(WIDTH, HEIGHT) - 3;
    gotClearance = this.map.getTile(3, 3, 0).getClearance(MFEMovementType.WALK);
    assertEquals(expClearance, gotClearance);
  }

  @Test
  public void shouldHaveNoCornersWhenThereAreNoWalls()
  {
    MFTile testedTile = this.map.getTile(1, 1, 0);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveInwardCornersWhenAllWallsArePresent()
  {
    MFTile testedTile = this.map.getTile(1, 1, 0);
    testedTile.setWalls(true, true, true, true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveHorizontalCorners()
  {
    MFTile testedTile = this.map.getTile(1, 1, 0);

    /*
     *  _
     *  X
     *  -
     *
     */
    testedTile.setWallNorth(true);
    testedTile.setWallSouth(true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveVerticalCorners()
  {
    MFTile testedTile = this.map.getTile(1, 1, 0);

    /*
     *  |X|
     *
     */
    testedTile.setWallEast(true);
    testedTile.setWallWest(true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.VERTICAL, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveBothCornersWhenAdjacentWallsArePresent()
  {
    MFTile above      = this.map.getTile(1, 0, 0);
    MFTile left       = this.map.getTile(0, 1, 0);
    MFTile testedTile = this.map.getTile(1, 1, 0);
    MFTile right      = this.map.getTile(2, 1, 0);
    MFTile below      = this.map.getTile(1, 2, 0);

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

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveBothCornersWhenRightAndLeftWallsArePresent()
  {
    MFTile left       = this.map.getTile(0, 1, 0);
    MFTile testedTile = this.map.getTile(1, 1, 0);
    MFTile right      = this.map.getTile(2, 1, 0);

    /*
     * _   _
     * _ X _
     *
     * */
    left.setWallNorth(true);
    left.setWallSouth(true);
    right.setWallNorth(true);
    right.setWallSouth(true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveBothCornersWhenWallsAboveAndBelowArePresent()
  {
    MFTile above      = this.map.getTile(1, 0, 0);
    MFTile testedTile = this.map.getTile(1, 1, 0);
    MFTile below      = this.map.getTile(1, 2, 0);

    /*
     *  | |
     *   X
     *  | |
     * */
    above.setWallEast(true);
    above.setWallWest(true);
    below.setWallEast(true);
    below.setWallWest(true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.BOTH, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveCornersWhenOnFirstRow()
  {
    MFTile testedTile = this.map.getTile(1, 0, 0);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerNE());

    /*
     * _____
     * | |X|
     *
     */
    testedTile.setWallEast(true);
    testedTile.setWallWest(true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerNE());

  }

  @Test
  public void shouldHaveCornersWhenOnLastRow()
  {
    MFTile testedTile = this.map.getTile(1, HEIGHT-1, 0);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSW());
    assertEquals(MFTile.Corner.HORIZONTAL, testedTile.getCornerSE());

    /*
     *
     * |_|x_|_
     *
     */
    testedTile.setWallEast(true);
    testedTile.setWallWest(true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerSW());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerSE());
  }

  @Test
  public void shouldHaveCornersWhenOnFirstColumn()
  {
    MFTile testedTile = this.map.getTile(0, 1, 0);

    this.map.calculateCorners(testedTile);

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

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerNW());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerSW());
  }

  @Test
  public void shouldHaveCornersWhenOnLastColumn()
  {
    MFTile testedTile = this.map.getTile(WIDTH-1, 1, 0);

    this.map.calculateCorners(testedTile);

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

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.INWARD, testedTile.getCornerSE());
  }

  @Test
  public void shouldNotCalculateCornersOnSolidTile()
  {
    MFTile testedTile = this.map.getTile(2, 2, 0);

    testedTile.setDugOut(false);
    testedTile.setWalls(true, true, true, true);

    this.map.calculateCorners(testedTile);

    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSE());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerSW());
    assertEquals(MFTile.Corner.NONE, testedTile.getCornerNW());
  }
}