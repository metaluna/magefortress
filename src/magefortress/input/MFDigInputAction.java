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
package magefortress.input;

import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.core.MFTile;

/**
 * Digs out a tile
 */
public class MFDigInputAction extends MFInputAction
{
  public MFDigInputAction(MFGame _game, MFLocation[] _markedForDigging)
  {
    super(_game);
    if (_markedForDigging == null || _markedForDigging.length == 0) {
      String msg = "MFDigInputAction: Can't create action without locations.";
      Logger.getLogger(MFDigInputAction.class.getName()).log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    this.markedForDigging = _markedForDigging;
  }

  @Override
  public void execute()
  {
    int level = markedForDigging[0].z;
    MFTile[][] tiles = this.game.getLevelMap(level);
    for (MFLocation location : markedForDigging) {
      MFTile tile = tiles[location.x][location.y];
      if (!tile.isDugOut()) {
        // dig out
        tile.setDugOut(true);
        // place walls if neighbor isn't dug out else remove wall of neighbor
        MFTile neighborN = tiles[tile.getPosX()][Math.max(tile.getPosY()-1,0)];
        MFTile neighborNE = tiles[Math.min(tile.getPosX()+1,tiles.length-1)][Math.max(tile.getPosY()-1,0)];
        MFTile neighborE = tiles[Math.min(tile.getPosX()+1,tiles.length-1)][tile.getPosY()];
        MFTile neighborSE = tiles[Math.min(tile.getPosX()+1,tiles.length-1)][Math.min(tile.getPosY()+1, tiles.length-1)];
        MFTile neighborS = tiles[tile.getPosX()][Math.min(tile.getPosY()+1,tiles.length-1)];
        MFTile neighborSW = tiles[Math.max(tile.getPosX()-1,0)][Math.min(tile.getPosY()+1,tiles.length-1)];
        MFTile neighborW = tiles[Math.max(tile.getPosX()-1,0)][tile.getPosY()];
        MFTile neighborNW = tiles[Math.max(tile.getPosX()-1,0)][Math.max(tile.getPosY()-1,0)];
        boolean wallN = neighborN == tile || !neighborN.isDugOut();
        boolean wallE = neighborE == tile || !neighborE.isDugOut();
        boolean wallS = neighborS == tile || !neighborS.isDugOut();
        boolean wallW = neighborW == tile || !neighborW.isDugOut();
        tile.setWalls(wallN, wallE, wallS, wallW);
        if (!wallN){
          neighborN.setWallSouth(false);
          neighborN.calculateCorners(tiles);
        }
        if (!wallE){
          neighborE.setWallWest(false);
          neighborE.calculateCorners(tiles);
        }
        if (!wallS){
          neighborS.setWallNorth(false);
          neighborS.calculateCorners(tiles);
        }
        if (!wallW){
          neighborW.setWallEast(false);
          neighborW.calculateCorners(tiles);
        }

        if (!wallN && !wallE && neighborNE.isDugOut() &&
            !neighborNE.hasWallWest() && !neighborNE.hasWallSouth()) {
          neighborNE.calculateCorners(tiles);
        }
        if (!wallS && !wallE && neighborSE.isDugOut() &&
            !neighborSE.hasWallWest() && !neighborSE.hasWallNorth()) {
          neighborSE.calculateCorners(tiles);
        }
        if (!wallS && !wallW && neighborSW.isDugOut() &&
            !neighborSW.hasWallEast() && !neighborSW.hasWallNorth()) {
          neighborSW.calculateCorners(tiles);
        }
        if (!wallN && !wallW && neighborNW.isDugOut() &&
            !neighborNW.hasWallEast() && !neighborNW.hasWallSouth()) {
          neighborNW.calculateCorners(tiles);
        }


        tile.calculateCorners(tiles);
      }
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private MFLocation[] markedForDigging;
}
