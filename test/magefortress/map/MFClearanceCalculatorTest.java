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
package magefortress.map;

import java.util.HashSet;
import java.util.Set;
import magefortress.creatures.behavior.movable.MFCapability;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFClearanceCalculatorTest
{
  private static final int WIDTH  = 5;
  private static final int HEIGHT = 5;
  private static final int DEPTH  = 1;

  private final Set<MFCapability> capabilities;

  private MFClearanceCalculator clearanceCalc;
  private MFMap map;

  public MFClearanceCalculatorTest()
  {
    this.capabilities = new HashSet<MFCapability>();
    this.capabilities.add(MFCapability.WALK);
    this.capabilities.add(MFCapability.FLY);
    this.capabilities.add(MFCapability.WALK_FLY);
  }

  @Before
  public void setUp()
  {
    
    this.map = this.createMap(WIDTH, HEIGHT, DEPTH);
    this.clearanceCalc = new MFClearanceCalculator(map);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFClearanceCalculator(null);
  }

  @Test
  public void shouldCalculateClearanceValuesOnEmptyMap()
  {
    // given an emtpy map

    // when clearances are calculated
    this.clearanceCalc.calculateAllLevels(MFCapability.WALK);

    // then every tile should have a clearance equal to the distance to the right/bottom border
    for (int x = 0; x < WIDTH; ++x) {
      for (int y = 0; y < HEIGHT; ++y) {
        for (int z = 0; z < DEPTH; ++z) {
        int expClearance = Math.min(WIDTH, HEIGHT) - Math.max(x, y);
        assertEquals(expClearance, this.map.getTile(x, y, z).getClearance(MFCapability.WALK));
        }
      }
    }
  }

  @Test
  public void shouldCalculateClearanceWithObstacles()
  {
    //setup
    this.map.getTile(2, 2, 0).setDugOut(false);

    this.clearanceCalc.calculateAllLevels(MFCapability.WALK);

    int expClearance = 0;
    int gotClearance = this.map.getTile(2, 2, 0).getClearance(MFCapability.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 2;
    gotClearance = this.map.getTile(0, 0, 0).getClearance(MFCapability.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = 1;
    gotClearance = this.map.getTile(1, 1, 0).getClearance(MFCapability.WALK);
    assertEquals(expClearance, gotClearance);

    expClearance = Math.max(WIDTH, HEIGHT) - 3;
    gotClearance = this.map.getTile(3, 3, 0).getClearance(MFCapability.WALK);
    assertEquals(expClearance, gotClearance);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private MFMap createMap(int _width, int _height, int _depth)
  {
    MFMap resultMap = new MFMap(-1, _width, _height, _depth);
    for (int x = 0; x < _width; ++x) {
      for (int y = 0; y < _height; ++y) {
        for (int z = 0; z < _depth; ++z) {
          boolean hasWallN = y == 0 || false;
          boolean hasWallE = x == _width-1 || false;
          boolean hasWallS = y == _height-1 || false;
          boolean hasWallW = x == 0 || false;
          MFTile tile = resultMap.getTile(x, y, z);
          tile.setDugOut(true);
          tile.setWalls(hasWallN, hasWallE, hasWallS, hasWallW);
        }
      }
    }

    return resultMap;
  }
}