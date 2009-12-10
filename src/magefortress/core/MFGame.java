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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import magefortress.channel.MFCommunicationChannel;
import magefortress.gui.MFScreensManager;

/**
 * Single place for all game data.
 */
public class MFGame
{
  
  public MFGame()
  {
    // init channels
    this.channels = new ArrayList<MFCommunicationChannel>();
    
    // init map
    this.map = new MFMap(30,30,1);
    createRandomMap();
  }

  public void update()
  {
    processCommunicationChannels();

    // TODO process creatures
  }

  public void paint(Graphics2D _g, int _currentLevel, Rectangle _clippingRect)
  {
    this.map.paint(_g, _currentLevel, _clippingRect);
    // TODO paint objects - move to map.paint()?
    // TODO paint creatures - move to map.paint()?
  }

  /**
   * The size of a map tile
   * @return The size of a map tile
   */
  public int getTileSize()
  {
    return MFTile.TILESIZE;
  }

  /**
   * One level of the map.
   * @param z The depth
   * @return The level
   */
  public MFTile[][] getLevelMap(int z)
  {
    return this.map.getLevelMap(z);
  }

  /**
   * Removes the newest screen from the screens stack, which should be an
   * instance of this game's game screen.
   */
  public void quit()
  {
    MFScreensManager.getInstance().pop();
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  /** The map */
  private MFMap map;
  /** Communications channels*/
  private final ArrayList<MFCommunicationChannel> channels;

  private void createRandomMap()
  {
    Random r = new Random();
    MFTile[][] level = this.map.getLevelMap(0);
    for (MFTile[] cols : level) {
      for (MFTile tile : cols) {
        tile.setDugOut(r.nextBoolean());
      }
    }
    for (MFTile[] cols : level) {
      for (MFTile tile : cols) {
        if (tile.isDugOut()) {
          boolean wallN = r.nextBoolean();
          boolean wallE = r.nextBoolean();
          boolean wallS = r.nextBoolean();
          boolean wallW = r.nextBoolean();
          if (tile.getPosX() == 0) {
            wallW = true;
          } else if (tile.getPosX() == level.length - 1) {
            wallE = true;
          }
          if (tile.getPosY() == 0) {
            wallN = true;
          } else if (tile.getPosY() == cols.length - 1) {
            wallS = true;
          }
          MFTile neighborN = level[tile.getPosX()][Math.max(tile.getPosY() - 1, 0)];
          MFTile neighborE = level[Math.min(tile.getPosX() + 1, 29)][tile.getPosY()];
          MFTile neighborS = level[tile.getPosX()][Math.min(tile.getPosY() + 1, 29)];
          MFTile neighborW = level[Math.max(tile.getPosX() - 1, 0)][tile.getPosY()];
          if (neighborN.isDugOut()) {
            neighborN.setWallSouth(wallN);
          } else {
            wallN = true;
          }
          if (neighborE.isDugOut()) {
            neighborE.setWallWest(wallE);
          } else {
            wallE = true;
          }
          if (neighborS.isDugOut()) {
            neighborS.setWallNorth(wallS);
          } else {
            wallS = true;
          }
          if (neighborW.isDugOut()) {
            neighborW.setWallEast(wallW);
          } else {
            wallW = true;
          }
          tile.setWalls(wallN, wallE, wallS, wallW);
        }
      }
    }
    for (MFTile[] cols : level) {
      for (MFTile tile : cols) {
        if (tile.isDugOut()) {
          tile.calculateCorners(level);
        }
      }
    }
  }

  private void processCommunicationChannels()
  {
    // process channels
    for (MFCommunicationChannel channel : this.channels) {
      channel.update();
    }
  }

}
