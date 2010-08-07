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

import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.creatures.behavior.movable.MFCapability;

/**
 * Calculates clearance values for a map
 */
public class MFClearanceCalculator
{

  public MFClearanceCalculator(MFMap _map)
  {
    if (_map == null) {
      String msg = "Cannot create without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.map = _map;
  }

  public void calculateAllLevels(MFCapability _capability)
  {
    for (int z = 0; z < this.map.getDepth(); ++z) {
      this.calculateLevel(z, _capability);
    }
  }

  public void calculateLevel(int _z, MFCapability _capability)
  {
    MFTile[][] level = this.map.getLevelMap(_z);
    
    for (int x=this.map.getWidth()-1; x >= 0; --x) {
      for (int y=this.map.getHeight()-1; y>=0; --y) {

        final MFTile tile = level[x][y];
        final int clearance = calculateClearance(tile, _capability);
        tile.setClearance(_capability, clearance);

      }
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFClearanceCalculator.class.getName());
  private final MFMap map;

  /**
   * Calculates the clearance for one tile
   * @param _tile the tile to update
   * @param _capability the capability of the creature
   * @return the highest possible clearance for this tile
   */
  private int calculateClearance(MFTile _tile, MFCapability _capability)
  {
    int result;

    if (!_tile.isWalkable(_capability)) {
      result = 0;
    } else {
      final MFTile neighborE  = this.map.getNeighbor(_tile, MFEDirection.E);
      final MFTile neighborS  = this.map.getNeighbor(_tile, MFEDirection.S);
      final MFTile neighborSE = this.map.getNeighbor(_tile, MFEDirection.SE);

      boolean accessible = canAccessNeighbors(_tile, _capability,
                                                neighborE, neighborS, neighborSE);

      // not placed on edge and neighbors are all accessible
      if (accessible) {
          // calculate clearance
          final int clearanceE  = neighborE.getClearance(_capability);
          final int clearanceS  = neighborS.getClearance(_capability);
          final int clearanceSE = neighborSE.getClearance(_capability);
          result = Math.min(clearanceE, Math.min(clearanceS, clearanceSE)) + 1;
      // placed on edge or no access to neighboring tiles
      } else {
        result = 1;
      }
    }

    return result;
  }

  private boolean canAccessNeighbors(MFTile _tile, MFCapability _capability,
                          MFTile _neighborE, MFTile _neighborS, MFTile _neighborSE)
  {
    // Do we have neighbors?
    if (_neighborE == null || _neighborS == null || _neighborSE == null) {
      return false;
    }

    boolean result = true;

    // Can we move to every neighboring tile?
    if (!_neighborE.isWalkable(_capability) ||
        !_neighborS.isWalkable(_capability) ||
        !_neighborSE.isWalkable(_capability)) {
      result = false;
    }

    // no walls placed between neighbors and tile
    if (result && (_tile.hasWallEast() || _tile.hasWallSouth())) {
      result = false;
    }

    return result;
  }

}
