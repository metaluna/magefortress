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
package magefortress.map;

import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.map.ground.MFGround;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFAnnotatedAStarTest
{
  private MFAnnotatedAStar search;
  private MFMap map;
  private MFNavigationMap naviMap;
  private static final int WIDTH  = 5;
  private static final int HEIGHT = 5;
  private static final int DEPTH  = 1;


  @Before
  public void setUp()
  {
    this.naviMap = createMap(WIDTH, HEIGHT, DEPTH);
  }

  //---------------------------- PATH TESTS ------------------------------------
  //                         WITH CLEARANCE OF 1

  @Test
  public void shouldFindPath0()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
    final int expPathLength = 4;
    /*
     *  _______
     * |       |
     * |       |
     * |A     B|
     * |       |
     * |_______|
     */

    MFAnnotatedAStar testPath = new MFAnnotatedAStar(this.map, startTile, goalTile, 1, MFCapability.WALK);

    MFPath path = testPath.findPath();
    assertNotNull(path);

    int gotPathLength = 0;
    while(path.hasNext()) {
      path.next();
      ++gotPathLength;
    }
    assertEquals(expPathLength, gotPathLength);
  }

  @Test
  public void shouldFindPath1()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
    final int expPathLength = 6;
    /*
     *  _______
     * |   _   |
     * |  |/|  |
     * |A |/| B|
     * |  |/|  |
     * |__|/|__|
     */
    // build wall
    this.map.getTile(2, 0, 0).setWallSouth(true);
    for (int y=1; y<this.map.getHeight(); ++y) {
      this.map.getTile(1, y, 0).setWallEast(true);
      this.map.getTile(2, y, 0).setDugOut(false);
      this.map.getTile(3, y, 0).setWallWest(true);
    }
    this.naviMap.updateClearanceValues(MFCapability.WALK);

    MFAnnotatedAStar testPath = new MFAnnotatedAStar(this.map, startTile, goalTile, 1, MFCapability.WALK);

    MFPath path = testPath.findPath();
    assertNotNull(path);

    int gotPathLength = 0;
    while(path.hasNext()) {
      path.next();
      ++gotPathLength;
    }
    assertEquals(expPathLength, gotPathLength);
  }

  @Test
  public void shouldFindPath2()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
    final int expPathLength = 8;
    /*
     *  _______
     * | ___   |
     * |    |  |
     * |A   | B|
     * | ___|  |
     * |_______|
     */
    // build wall
    for (int x=1; x < 4; ++x) {
      this.map.getTile(x, 0, 0).setWallSouth(true);
      this.map.getTile(x, 1, 0).setWallNorth(true);
      this.map.getTile(x, 3, 0).setWallSouth(true);
      this.map.getTile(x, 4, 0).setWallNorth(true);
    }
    for (int y=1; y<this.map.getHeight(); ++y) {
      this.map.getTile(3, y, 0).setWallEast(true);
      this.map.getTile(4, y, 0).setWallWest(true);
    }
    this.naviMap.updateClearanceValues(MFCapability.WALK);

    MFAnnotatedAStar testPath = new MFAnnotatedAStar(this.map, startTile, goalTile, 1, MFCapability.WALK);

    MFPath path = testPath.findPath();
    assertNotNull(path);

    int gotPathLength = 0;
    while(path.hasNext()) {
      path.next();
      ++gotPathLength;
    }
    assertEquals(expPathLength, gotPathLength);
  }

  //------------------------- NO PATH FOUND TESTS ------------------------------

  @Test
  public void shouldNotFindPathBehindSolidTiles()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
    /*
     *  _______
     * |  |/|  |
     * |  |/|  |
     * |A |/| B|
     * |  |/|  |
     * |__|/|__|
     */
    // build wall
    for (int y=0; y<this.map.getHeight(); ++y) {
      this.map.getTile(1, y, 0).setWallEast(true);
      this.map.getTile(2, y, 0).setDugOut(false);
      this.map.getTile(3, y, 0).setWallWest(true);
    }
    this.naviMap.updateClearanceValues(MFCapability.WALK);

    MFAnnotatedAStar testPath = new MFAnnotatedAStar(this.map, startTile, goalTile, 1, MFCapability.WALK);

    MFPath path = testPath.findPath();
    assertNull(path);
  }

  @Test
  public void shouldNotFindPathBehindWall()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
    /*
     *  _______
     * |  ||   |
     * |  ||   |
     * |A ||  B|
     * |  ||   |
     * |__||___|
     */
    // build wall
    for (int y=0; y<this.map.getHeight(); ++y) {
      this.map.getTile(1, y, 0).setWallEast(true);
      this.map.getTile(2, y, 0).setWallWest(true);
    }
    this.naviMap.updateClearanceValues(MFCapability.WALK);

    MFAnnotatedAStar testPath = new MFAnnotatedAStar(this.map, startTile, goalTile, 1, MFCapability.WALK);

    MFPath path = testPath.findPath();
    assertNull(path);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private MFNavigationMap createMap(int _width, int _height, int _depth)
  {
    this.map = new MFMap(-1, _width, _height, _depth, mock(MFGround.class));
    for (int x = 0; x < _width; ++x) {
      for (int y = 0; y < _height; ++y) {
        for (int z = 0; z < _depth; ++z) {
          boolean hasWallN = y == 0 || false;
          boolean hasWallE = x == _width-1 || false;
          boolean hasWallS = y == _height-1 || false;
          boolean hasWallW = x == 0 || false;
          MFTile tile = this.map.getTile(x, y, z);
          tile.setDugOut(true);
          tile.setWalls(hasWallN, hasWallE, hasWallS, hasWallW);
        }
      }
    }
    MFClearanceCalculator clearanceCalc = new MFClearanceCalculator(this.map);
    MFNavigationMap result = new MFNavigationMap(this.map, clearanceCalc);
    result.updateClearanceValues(MFCapability.WALK);
    return result;
  }

}