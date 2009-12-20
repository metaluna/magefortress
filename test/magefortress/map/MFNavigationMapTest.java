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

import java.util.LinkedList;
import java.util.List;
import magefortress.core.MFEMovementType;
import magefortress.core.MFLocation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFNavigationMapTest
{
  private MFNavigationMap naviMap;
  private MFMap map;
  private static final int WIDTH  = 5;
  private static final int HEIGHT = 5;
  private static final int DEPTH  = 1;


  @Before
  public void setUp()
  {
    this.naviMap = createMap(WIDTH, HEIGHT, DEPTH);
  }

  @Test
  public void shouldCalculateClearanceValuesOnEmptyMap()
  {
    this.naviMap.updateClearanceValues(MFEMovementType.WALK);

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

    this.naviMap.updateClearanceValues(MFEMovementType.WALK);

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
  public void shouldNotFindEntrances()
  {
    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(0, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(1, sections.size());
    int expTileCount = WIDTH*HEIGHT;
    int gotTileCount = sections.get(0).getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  @Test
  public void shouldFindCollapsableEntrance()
  {
    /* _________
     * |  |/|  |
     * |  |/|  |
     * |   _   |
     * |  |/|  |
     * |__|/|__|
     */
    // build central wall
    this.map.getTile(1, 0, 0).setWallEast(true);
    this.map.getTile(1, 1, 0).setWallEast(true);
    this.map.getTile(1, 3, 0).setWallEast(true);
    this.map.getTile(1, 4, 0).setWallEast(true);
    this.map.getTile(2, 0, 0).setDugOut(false);
    this.map.getTile(2, 1, 0).setDugOut(false);
    this.map.getTile(2, 3, 0).setDugOut(false);
    this.map.getTile(2, 4, 0).setDugOut(false);
    this.map.getTile(3, 0, 0).setWallWest(true);
    this.map.getTile(3, 1, 0).setWallWest(true);
    this.map.getTile(3, 3, 0).setWallWest(true);
    this.map.getTile(3, 4, 0).setWallWest(true);
    // build corridor
    MFTile entrance = this.map.getTile(2, 2, 0);
    entrance.setWalls(true, false, true, false);

    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(1, entrances.size());
    MFLocation expLocation = entrance.getLocation();
    MFLocation gotLocation = entrances.get(0).getLocation();
    assertEquals(expLocation, gotLocation);
    assertEquals(entrances.get(0), entrance.getEntrance());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(2, sections.size());
    int expTileCount = 2*5+1;
    int gotTileCount = sections.get(0).getSize();
    assertEquals(expTileCount, gotTileCount);
    expTileCount = 2*5;
    gotTileCount = sections.get(1).getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  @Test
  public void shouldFindTwoEntrances()
  {
    this.naviMap = createMap(7, 5, 1);
    /*  _________
     * |  |///|  |
     * |  |///|  |
     * |   ___   |
     * |  |///|  |
     * |__|///|__|
     */
    // build central wall
    this.map.getTile(1, 0, 0).setWallEast(true);
    this.map.getTile(1, 1, 0).setWallEast(true);
    this.map.getTile(1, 3, 0).setWallEast(true);
    this.map.getTile(1, 4, 0).setWallEast(true);
    for (int x=2; x < 5; ++x) {
     this.map.getTile(x, 0, 0).setDugOut(false);
     this.map.getTile(x, 1, 0).setDugOut(false);
     this.map.getTile(x, 3, 0).setDugOut(false);
     this.map.getTile(x, 4, 0).setDugOut(false);
    }
    this.map.getTile(5, 0, 0).setWallWest(true);
    this.map.getTile(5, 1, 0).setWallWest(true);
    this.map.getTile(5, 3, 0).setWallWest(true);
    this.map.getTile(5, 4, 0).setWallWest(true);
    // build corridor
    MFTile entrance1 = this.map.getTile(2, 2, 0);
    entrance1.setWalls(true, false, true, false);
    MFTile tunnel = this.map.getTile(3, 2, 0);
    tunnel.setWalls(true, false, true, false);
    MFTile entrance2 = this.map.getTile(4, 2, 0);
    entrance2.setWalls(true, false, true, false);

    List<MFLocation> possibleEntrances = new LinkedList<MFLocation>();
    possibleEntrances.add(entrance1.getLocation());
    possibleEntrances.add(entrance2.getLocation());
    // other possible entrances
    possibleEntrances.add(new MFLocation(1,2,0));
    possibleEntrances.add(new MFLocation(5,2,0));

    //this.map.calculateClearanceValues(MFEMovementType.WALK);
    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals("Number of entrances does not match - ", 2, entrances.size());
    for (MFSectionEntrance sectionEntrance : entrances) {
      assertTrue("Incorrect location. Found " + sectionEntrance.getLocation(),
                  possibleEntrances.contains(sectionEntrance.getLocation()));
      assertEquals(sectionEntrance, this.map.getTile(sectionEntrance.getLocation()).getEntrance());
    }

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals("Number of sections does not match - ", 3, sections.size());
  }

  @Test
  public void shouldNotFindEntranceDiagonally()
  {
    this.naviMap = createMap(4, 4, 1);
    /*
     *  _____
     * |  |//|
     * |__|//|
     * |//|  |
     * |//|__|
     */
    // build top left room
    this.map.getTile(1, 0, 0).setWallEast(true);
    this.map.getTile(1, 1, 0).setWallEast(true);
    this.map.getTile(1, 1, 0).setWallSouth(true);
    this.map.getTile(0, 1, 0).setWallSouth(true);
    // build bottom right room
    this.map.getTile(2, 2, 0).setWallWest(true);
    this.map.getTile(2, 3, 0).setWallWest(true);
    this.map.getTile(2, 2, 0).setWallNorth(true);
    this.map.getTile(3, 2, 0).setWallNorth(true);
    // filll other area
    for (int x=2; x < 4; ++x) {
     this.map.getTile(x, 0, 0).setDugOut(false);
     this.map.getTile(x, 1, 0).setDugOut(false);
    }
    for (int x=0; x < 2; ++x) {
     this.map.getTile(x, 2, 0).setDugOut(false);
     this.map.getTile(x, 3, 0).setDugOut(false);
    }

    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(0, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(2, sections.size());
    int expTileCount = 2*2;
    int gotTileCount = sections.get(0).getSize();
    assertEquals(expTileCount, gotTileCount);
    gotTileCount = sections.get(1).getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  @Test
  public void shouldFindOneEntrance()
  {
    this.naviMap = createMap(4, 4, 1);
    /*
     *  ______
     * |  |//|
     * |__   |
     * |//|  |
     * |//|__|
     */
    // build top left room
    this.map.getTile(1, 0, 0).setWallEast(true);
    this.map.getTile(1, 1, 0).setWallSouth(true);
    this.map.getTile(0, 1, 0).setWallSouth(true);
    // build bottom right room
    this.map.getTile(2, 2, 0).setWallWest(true);
    this.map.getTile(2, 3, 0).setWallWest(true);
    this.map.getTile(2, 1, 0).setWallNorth(true);
    this.map.getTile(3, 1, 0).setWallNorth(true);
    // filll other area
    for (int x=2; x < 4; ++x) {
     this.map.getTile(x, 0, 0).setDugOut(false);
    }
    for (int x=0; x < 2; ++x) {
     this.map.getTile(x, 2, 0).setDugOut(false);
     this.map.getTile(x, 3, 0).setDugOut(false);
    }

    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(1, entrances.size());

    MFLocation entrance = new MFLocation(1, 1, 0);
    assertEquals(entrance, entrances.get(0).getLocation());
    assertEquals(entrances.get(0), this.map.getTile(entrances.get(0).getLocation()).getEntrance());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(2, sections.size());
    int expTileCount = 2*2;
    int gotTileCount = sections.get(0).getSize();
    assertEquals(expTileCount, gotTileCount);
    expTileCount = 2*3;
    gotTileCount = sections.get(1).getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  @Test
  public void shouldFindEntranceBetweenTilesDividedByWalls()
  {
    /*
     *  _______
     * |   |   |
     * |   |   |
     * |       |
     * |   |   |
     * |___|___|
     */
    // build wall
    this.map.getTile(2, 0, 0).setWallEast(true);
    this.map.getTile(2, 1, 0).setWallEast(true);
    this.map.getTile(2, 3, 0).setWallEast(true);
    this.map.getTile(2, 4, 0).setWallEast(true);
    this.map.getTile(3, 0, 0).setWallWest(true);
    this.map.getTile(3, 1, 0).setWallWest(true);
    this.map.getTile(3, 3, 0).setWallWest(true);
    this.map.getTile(3, 4, 0).setWallWest(true);

    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(1, entrances.size());

    MFLocation possibleEntrance1 = new MFLocation(2, 2, 0);
    MFLocation possibleEntrance2 = new MFLocation(3, 2, 0);
    assertTrue("Incorrect location. Found " + entrances.get(0).getLocation(),
               entrances.get(0).getLocation().equals(possibleEntrance1) ||
               entrances.get(0).getLocation().equals(possibleEntrance2));
    assertEquals(entrances.get(0), this.map.getTile(entrances.get(0).getLocation()).getEntrance());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(2, sections.size());
  }

  @Test
  public void shouldFindNoEntranceAroundIsland()
  {
    this.naviMap = createMap(6, 6, 1);
    /*
     *  ________
     * |        |
     * |   __   |
     * |  |//|  |
     * |  |//|  |
     * |        |
     * |________|
     */

    // build island
    this.map.getTile(2, 2, 0).setDugOut(false);
    this.map.getTile(2, 3, 0).setDugOut(false);
    this.map.getTile(3, 2, 0).setDugOut(false);
    this.map.getTile(3, 3, 0).setDugOut(false);
    // build walls around it
    this.map.getTile(2, 1, 0).setWallSouth(true);
    this.map.getTile(3, 1, 0).setWallSouth(true);
    this.map.getTile(4, 2, 0).setWallWest(true);
    this.map.getTile(4, 3, 0).setWallWest(true);
    this.map.getTile(3, 4, 0).setWallNorth(true);
    this.map.getTile(2, 4, 0).setWallNorth(true);
    this.map.getTile(1, 3, 0).setWallEast(true);
    this.map.getTile(1, 2, 0).setWallEast(true);

    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(0, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(1, sections.size());
  }

  @Test
  public void shouldFindOneSection()
  {
    this.naviMap = createMap(5, 5, 1);
    /*
     *  _______
     * |  |/|  |
     * |  |/|  |
     * |       |
     * |_______|
     */

    this.map.getTile(2, 0, 0).setDugOut(false);
    this.map.getTile(2, 1, 0).setDugOut(false);

    this.map.getTile(1, 0, 0).setWallEast(true);
    this.map.getTile(1, 1, 0).setWallEast(true);
    this.map.getTile(2, 2, 0).setWallNorth(true);
    this.map.getTile(3, 0, 0).setWallWest(true);
    this.map.getTile(3, 1, 0).setWallWest(true);

    this.naviMap.updateAllEntrances();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(0, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(1, sections.size());
    assertEquals(5*5-2, sections.get(0).getSize());
  }

  //---vvv---     PRIVATE METHODS    ---vvv---

  private MFNavigationMap createMap(int _width, int _height, int _depth)
  {
    this.map = new MFMap(_width, _height, _depth);
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
    MFNavigationMap result = new MFNavigationMap(this.map);
    result.updateClearanceValues(MFEMovementType.WALK);
    return new MFNavigationMap(map);
  }
}