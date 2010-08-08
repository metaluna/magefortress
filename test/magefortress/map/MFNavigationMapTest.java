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
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.movable.MFCapability;
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
  public void shouldNotFindEntrances()
  {
    this.naviMap.calculateAllLevels();
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

    this.naviMap.calculateAllLevels();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(1, entrances.size());
    MFLocation expLocation = entrance.getLocation();
    MFLocation gotLocation = entrances.get(0).getLocation();
    assertEquals(expLocation, gotLocation);
    assertEquals(entrances.get(0), entrance.getEntrance());

    int expEdgeCount = 0;
    int gotEdgeCount = entrances.get(0).getEdges().size();
    assertEquals(expEdgeCount, gotEdgeCount);

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
    this.naviMap.calculateAllLevels();

    // check entrances
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals("Number of entrances does not match - ", 2, entrances.size());
    for (MFSectionEntrance sectionEntrance : entrances) {
      assertTrue("Incorrect location. Found " + sectionEntrance.getLocation(),
                  possibleEntrances.contains(sectionEntrance.getLocation()));
      assertEquals(sectionEntrance, this.map.getTile(sectionEntrance.getLocation()).getEntrance());
    }
    // check edges on entrance 1
    MFSectionEntrance gotEntrance1 = entrances.get(0);
    MFSectionEntrance gotEntrance2 = entrances.get(1);
    int expEdgeCount = 1;
    int gotEdgeCount = gotEntrance1.getEdges().size();
    assertEquals(expEdgeCount, gotEdgeCount);

    final MFEdge edge1 = gotEntrance1.getEdges().get(0);
    assertEquals(gotEntrance2, edge1.getTo());

    // check edges on entrance 2
    gotEdgeCount = gotEntrance2.getEdges().size();
    assertEquals(expEdgeCount, gotEdgeCount);

    final MFEdge edge2 = gotEntrance2.getEdges().get(0);
    assertEquals(gotEntrance1, edge2.getTo());

    // check sections
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

    this.naviMap.calculateAllLevels();
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

    this.naviMap.calculateAllLevels();
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

    this.naviMap.calculateAllLevels();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(1, entrances.size());

    MFLocation possibleEntrance1 = new MFLocation(2, 2, 0);
    MFLocation possibleEntrance2 = new MFLocation(3, 2, 0);
    assertTrue("Incorrect location. Found " + entrances.get(0).getLocation(),
               entrances.get(0).getLocation().equals(possibleEntrance1) ||
               entrances.get(0).getLocation().equals(possibleEntrance2));
    assertEquals(entrances.get(0), this.map.getTile(entrances.get(0).getLocation()).getEntrance());

    assertEquals(0, entrances.get(0).getEdges().size());

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

    this.naviMap.calculateAllLevels();
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

    this.naviMap.calculateAllLevels();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(0, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(1, sections.size());
    assertEquals(5*5-2, sections.get(0).getSize());
  }

  @Test
  public void shouldFindNoEntranceInCorneredShape()
  {
    this.naviMap = createMap(4, 4, 1);
    /*
     *  _____
     * |     |
     * |    _|
     * |___|/|
     * |/////|
     */
    // build top space room
    this.map.getTile(0, 2, 0).setWallSouth(true);
    this.map.getTile(1, 2, 0).setWallSouth(true);
    this.map.getTile(2, 2, 0).setWallSouth(true);
    this.map.getTile(2, 2, 0).setWallEast(true);
    this.map.getTile(3, 1, 0).setWallSouth(true);
    // build bottom earth room
    // fill other area
    for (int x=0; x < 4; ++x) {
     this.map.getTile(x, 3, 0).setDugOut(false);
    }
    this.map.getTile(3, 2, 0).setDugOut(false);

    this.naviMap.calculateAllLevels();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(0, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(1, sections.size());
    int expTileCount = 11;
    int gotTileCount = sections.get(0).getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  @Test
  public void shouldFindEntranceAtTunnelBeginning()
  {
    this.naviMap = createMap(5, 5, 1);
    /*
     *  _______
     * |       |
     * |       |
     * |E _____|
     * |_|/////|
     */

    this.map.getTile(0, 4, 0).setWallEast(true);
    
    for (int x = 1; x < 5; ++x) {
      this.map.getTile(x, 4, 0).setDugOut(false);
      this.map.getTile(x, 3, 0).setWallSouth(true);
    }
    
    this.naviMap.calculateAllLevels();
    List<MFSectionEntrance> entrances = this.naviMap.getEntrances();
    assertEquals(1, entrances.size());

    List<MFSection> sections = this.naviMap.getSections();
    assertEquals(2, sections.size());
  }

  @Test
  public void shouldFindEntranceAfterDigging()
  {
    // given a map with 1 section
    this.naviMap = createMap(5, 5, 1);
    /*
     *  _______
     * |       |
     * |       |
     * |E _____|
     * |X|/////|
     */

    for (int x = 0; x < 5; ++x) {
      this.map.getTile(x, 4, 0).setDugOut(false);
      this.map.getTile(x, 3, 0).setWallSouth(true);
    }

    this.naviMap.calculateAllLevels();

    int expSectionCount = 1;
    int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    // when i dig out a tile
    final MFLocation diggingLocation = new MFLocation(0,4,0);
    this.map.digOut(diggingLocation);
    this.naviMap.calculateAllLevels();

    // then 2 sections should be found
    expSectionCount = 2;
    gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    // and 1 entrance should be found
    int expEntranceCount = 1;
    int gotEntranceCount = this.naviMap.getEntrances().size();
    assertEquals(expEntranceCount, gotEntranceCount);

    MFLocation expLocation = new MFLocation(0,3,0);
    MFLocation gotLocation = this.naviMap.getEntrances().get(0).getLocation();
    assertEquals(expLocation, gotLocation);
  }

  //--------------------- addMovementCombination() TESTS -----------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddCombinationWithIllegalClearance()
  {
    this.naviMap.addMovementCombination(0, MFCapability.WALK_FLY);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddCombinationWithoutCapabilities()
  {
    this.naviMap.addMovementCombination(1, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddCombinationWithZeroCapabilities()
  {
    this.naviMap.addMovementCombination(1, MFCapability.NONE);
  }

  @Test
  public void shouldAddMovementCombination()
  {
    this.naviMap.addMovementCombination(1, MFCapability.WALK_FLY);
  }

  @Test
  public void shouldAddDuplicateMovementCombinationWithoutError()
  {
    this.naviMap.addMovementCombination(2, MFCapability.WALK_FLY);
    this.naviMap.addMovementCombination(2, MFCapability.WALK_FLY);
  }

  //---vvv---     PRIVATE METHODS    ---vvv---

  private MFNavigationMap createMap(int _width, int _height, int _depth)
  {
    this.map = new MFMap(-1, _width, _height, _depth);
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