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

import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.movable.MFCapability;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFHierarchicalAStarTest
{
  private MFHierarchicalAStar search;
  private MFMap map;
  private MFNavigationMap naviMap;
  private MFPathFinder pathFinder;
  
  private static final int WIDTH  = 5;
  private static final int HEIGHT = 5;
  private static final int DEPTH  = 1;

  public MFHierarchicalAStarTest()
  {
    this.pathFinder = mock(MFPathFinder.class);
  }

  @Before
  public void setUp()
  {
    this.naviMap = this.createMap(WIDTH, HEIGHT, DEPTH);
  }

  //-------------------------- CONSTRUCTOR TESTS -------------------------------

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateHierarchicalSearchWithoutPathFinder()
  {
    new MFHierarchicalAStar(mock(MFMap.class), mock(MFTile.class),
                            mock(MFTile.class), 1,
                            MFCapability.WALK, null);
  }

  //-------------------- NODE INSERTION AND REMOVAL TESTS ----------------------

  @Test
  public void shouldInsertAndRemoveTileInNaviMap()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
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
    this.naviMap.calculateAllLevels();

    // check entrances
    final int expSectionCount = 2;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    final MFSectionEntrance gateway = this.naviMap.getEntrances().get(0);

    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile, 1,
                            MFCapability.WALK, this.pathFinder);

    MFPath path = search.findPath();
    assertNotNull(path);

    assertNull(startTile.getEntrance());
    assertNull(goalTile.getEntrance());
    int expEdgeCount = 0;
    int gotEdgeCount = gateway.getEdges().size();
    assertEquals(expEdgeCount, gotEdgeCount);
  }

  @Test
  public void shouldNotInsertAndRemoveTileInNaviMapWhenStartOrGoalIsEntrance()
  {
    this.naviMap = this.createMap(7, 5, 1);
    /*  _________
     * |  |///|  |
     * |  |///|  |
     * |A  ___  B|
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

    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    // check entrances
    final int expSectionCount = 3;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    // set the start and goal tile to the entrances' tiles
    final MFTile startTile;
    final MFTile goalTile;
    startTile = this.naviMap.getEntrances().get(0).getTile();
    goalTile  = this.naviMap.getEntrances().get(1).getTile();

    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile, 1,
                            MFCapability.WALK, this.pathFinder);

    MFPath path = search.findPath();
    assertNotNull(path);

    assertNotNull(startTile.getEntrance());
    assertNotNull(goalTile.getEntrance());
  }

  //------------------------- SEARCH TESTS -------------------------------------
  //                       WITH NO VALID PATH
  
  @Test
  public void shouldNotFindPathBetweenIsolatedRegions()
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
    this.naviMap.calculateAllLevels();
    
    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile, 1, 
                            MFCapability.WALK, this.pathFinder);

    MFPath path = search.findPath();
    assertNull(path);
  }

  //------------------------- SEARCH TESTS -------------------------------------
  //                        WITH VALID PATHS

  @Test
  public void shouldFindPathInSameSection()
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
    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile, 1,
                            MFCapability.WALK, this.pathFinder);

    final MFPath path = search.findPath();
    assertNotNull(path);
    verify(this.pathFinder, never()).enqueuePathSearch(any(MFLocation.class),
                            any(MFLocation.class), anyInt(), any(MFCapability.class),
                            any(MFIPathFinderListener.class));

    int gotPathLength = 0;
    while (path.hasNext()) {
      path.next();
      ++gotPathLength;
    }
    assertEquals(expPathLength, gotPathLength);
  }

  @Test
  public void shouldFindPathWithinTwoSections()
  {
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(4, 2, 0);
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
    this.naviMap.calculateAllLevels();

    // check entrances
    final int expSectionCount = 2;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    final MFSectionEntrance gateway = this.naviMap.getEntrances().get(0);

    final int clearance = 1;
    final MFCapability capability = MFCapability.WALK;
    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile,
                                      clearance, capability, this.pathFinder);

    final MFPath path = this.search.findPath();
    assertNotNull(path);
    assertTrue(path instanceof MFHierarchicalPath);

    verify(this.pathFinder).enqueuePathSearch(startTile.getLocation(), 
            gateway.getLocation(), clearance, capability, (MFHierarchicalPath) path);
    verify(this.pathFinder).enqueuePathSearch(gateway.getLocation(), 
            goalTile.getLocation(), clearance, capability, (MFHierarchicalPath) path);
    verifyNoMoreInteractions(this.pathFinder);
  }

  @Test
  public void shouldFindPathWithinThreeSections()
  {
    this.naviMap = this.createMap(7, 5, 1);
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(6, 2, 0);
    /*  _________
     * |  |///|  |
     * |  |///|  |
     * |A  ___  B|
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

    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    // check entrances
    final int expSectionCount = 3;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    // get the entrances passed on the way in correct order
    MFTile gateway1 = null;
    MFTile gateway2 = null;
    assertEquals(1, startTile.getParentSection().getEntrances().size());
    assertEquals(1,  goalTile.getParentSection().getEntrances().size());
    for (MFSectionEntrance entrance : startTile.getParentSection().getEntrances()) {
      gateway1 = entrance.getTile();
    }
    for (MFSectionEntrance entrance : goalTile.getParentSection().getEntrances()) {
      gateway2 = entrance.getTile();
    }

    // search!
    final int clearance = 1;
    final MFCapability capability = MFCapability.WALK;
    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile,
                                      clearance, capability, this.pathFinder);
    final MFPath path = this.search.findPath();
    assertNotNull(path);
    assertTrue(path instanceof MFHierarchicalPath);

    final MFHierarchicalPath hierarchicalPath = (MFHierarchicalPath) path;
    verify(this.pathFinder).enqueuePathSearch(startTile.getLocation(), 
            gateway1.getLocation(), clearance, capability, hierarchicalPath);
    verify(this.pathFinder).enqueuePathSearch(gateway1.getLocation(), 
            gateway2.getLocation(), clearance, capability, hierarchicalPath);
    verifyNoMoreInteractions(this.pathFinder);

    // return some 1-tile-long long
    hierarchicalPath.pathSearchFinished(createMockPath(1, startTile, gateway1));
    hierarchicalPath.pathSearchFinished(createMockPath(1, gateway1,  gateway2));

    hierarchicalPath.next();

    verify(this.pathFinder).enqueuePathSearch(gateway2.getLocation(), 
            goalTile.getLocation(), clearance, capability, hierarchicalPath);
    verifyNoMoreInteractions(this.pathFinder);


  }

  @Test
  public void shouldFindPathStartingAtEntrance()
  {
    // given a map with 2 sections
    this.naviMap = createMap(5, 5, 1);
    /*
     *  _______
     * |       |
     * |       |
     * |A _B___|
     * |_|/////|
     */

    this.map.getTile(0, 4, 0).setWallEast(true);

    for (int x = 1; x < 5; ++x) {
      this.map.getTile(x, 4, 0).setDugOut(false);
      this.map.getTile(x, 3, 0).setWallSouth(true);
    }

    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    final int expSectionCount = 2;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    // and i am standing on an entrance tile
    final MFTile startTile = this.map.getTile(0, 3, 0);
    assertNotNull(startTile.getEntrance());

    // when i want to go to a normal tile
    final MFTile goalTile  = this.map.getTile(2, 3, 0);
    assertNull(goalTile.getEntrance());

    final int clearance = 1;
    final MFCapability capability = MFCapability.WALK;
    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile,
                                      clearance, capability, this.pathFinder);
    final MFPath path = this.search.findPath();

    // then a direct path should be found
    assertNotNull(path);
    assertTrue(path instanceof MFAnnotatedPath);
    assertEquals(2, path.getCost());
  }

  @Test
  public void shouldFindPathFromEntranceFoundAfterDigging()
  {
    // given a map with 1 section
    this.naviMap = createMap(5, 5, 1);
    /*
     *  _______
     * |       |
     * |       |
     * |A _B___|
     * |X|/////|
     */

    for (int x = 0; x < 5; ++x) {
      this.map.getTile(x, 4, 0).setDugOut(false);
      this.map.getTile(x, 3, 0).setWallSouth(true);
    }

    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    final int expSectionCount = 1;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);


    // and i am standing on a non-entance tile
    final MFTile startTile = this.map.getTile(0, 3, 0);
    assertNull(startTile.getEntrance());
    assertNotNull(startTile.getParentSection());

    // when i dig out a tile
    final MFLocation diggingLocation = new MFLocation(0,4,0);
    this.map.digOut(diggingLocation);
    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    // then i should be standing on an entrance tile
    assertNotNull(startTile.getEntrance());
    assertNotNull(startTile.getParentSection());

    // when i want to go to a normal tile
    final MFTile goalTile  = this.map.getTile(2, 3, 0);
    assertNull(goalTile.getEntrance());

    final int clearance = 1;
    final MFCapability capability = MFCapability.WALK;
    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile,
                                      clearance, capability, this.pathFinder);
    final MFPath path = this.search.findPath();

    // then a direct path should be found
    assertNotNull(path);
    assertTrue(path instanceof MFAnnotatedPath);
    assertEquals(2, path.getCost());
  }

  @Test
  public void shouldReturnHierarchicalPathWithEstimatedCostAtCalculation()
  {
    // given a map with 3 sections
    this.naviMap = this.createMap(7, 5, 1);
    final MFTile startTile = this.map.getTile(0, 2, 0);
    final MFTile goalTile  = this.map.getTile(6, 2, 0);
    /*  _________
     * |  |///|  |
     * |  |///|  |
     * |A  ___  B|
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

    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();

    // check entrances
    final int expSectionCount = 3;
    final int gotSectionCount = this.naviMap.getSections().size();
    assertEquals(expSectionCount, gotSectionCount);

    // when i search for a path from a to b
    final int clearance = 1;
    final MFCapability capability = MFCapability.WALK;
    this.search = new MFHierarchicalAStar(this.map, startTile, goalTile,
                                      clearance, capability, this.pathFinder);

    // then a hierarchical path should be returned with a length of 6
    final MFPath path = this.search.findPath();
    assertNotNull(path);
    assertTrue(path instanceof MFHierarchicalPath);

    final int expCost = 6;
    final int gotCost = path.getCost();
    assertEquals(expCost, gotCost);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

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

  private MFPath createMockPath(final int _length, final MFTile _start, final MFTile _goal)
  {
    MFPath mockPath = mock(MFPath.class);
    when(mockPath.getStart()).thenReturn(_start);
    when(mockPath.getGoal()).thenReturn(_goal);
    for (int i = 0; i < _length; ++i) {
      MFEDirection dir = MFEDirection.values()[i % MFEDirection.values().length];
      when(mockPath.hasNext()).thenReturn(true);
      when(mockPath.next()).thenReturn(dir);
    }
    when(mockPath.hasNext()).thenReturn(false);

    return mockPath;
  }
}