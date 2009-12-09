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
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Contains the tiles.
 */
public class MFMap
{

  public MFMap(int _width, int _height, int _levels)
  {
    this.map = new MFTile[_levels][_width][_height];

    for (int z = 0; z < _levels; ++z) {
      for (int x = 0; x < _width; ++x) {
        for (int y = 0; y < _height; ++y) {
          this.map[z][x][y] = new MFTile(x,y,z);
        }
      }
    }
  }

  //---vvv---      STATIC METHODS      ---vvv---

  public static MFLocation convertToTilespace(int _x,int _y, int _z,
                                        int _x_translation, int _y_translation)
  {
    int tileX = (int) Math.floor((_x-_x_translation) / (double)MFTile.TILESIZE);
    int tileY = (int) Math.floor((_y-_y_translation) / (double)MFTile.TILESIZE);
    int tileZ = _z;

    return new MFLocation(tileX, tileY, tileZ);
  }

  public static Point convertFromTilespace(int _x,int _y)
  {
    int screenX = _x * MFTile.TILESIZE;
    int screenY = _y * MFTile.TILESIZE;

    return new Point(screenX, screenY);
  }

  //---vvv---      PUBLIC METHODS      ---vvv---

  /**
   * The tiles of the specified level
   * @param _level The level
   * @return The map
   */
  MFTile[][] getLevelMap(int _level)
  {
    return this.map[_level];
  }

  /**
   * Updates all animated sprites.
   */
  public void update()
  {
  }

  /**
   * Paints the map.
   * @param _g The canvas
   * @param _level The currently visible level of the map
   * @param _clippingRect The clipping rectangle.
   */
  public void paint(Graphics2D _g, int _level, Rectangle _clippingRect)
  {
    // select which tiles are visible
    // start of visible tiles
    MFLocation start = convertToTilespace(0, 0, _level,
                                          _clippingRect.x, _clippingRect.y);
    // end of visible tiles
    MFLocation end = convertToTilespace(_clippingRect.width-1, _clippingRect.height-1,
                                      _level, _clippingRect.x, _clippingRect.y);

    // only necessary if we don't stop scrolling at the edges of the map
    int startX = Math.max(start.x, 0);
    int startY = Math.max(start.y, 0);
    int endX = Math.min(end.x, this.map[_level].length-1);
    int endY = Math.min(end.y, this.map[_level][0].length-1);

    // render all visible tiles
    for (int y = startY; y <= endY; ++y) {
      for (int x = startX; x <= endX; ++x) {
        map[_level][x][y].paint(_g, _clippingRect.x, _clippingRect.y);
      }
    }
  }

  void calculateClearanceValues(MFEMovementType _movementType)
  {
    for (MFTile[][] levels : this.map) {
      for (MFTile[] rows : levels) {
        for (MFTile tile : rows) {
          int clearance = calculateClearance(tile, _movementType, 1);
          tile.setClearance(_movementType, clearance);
        }
      }
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private MFTile[][][] map;

  private int calculateClearance(MFTile _tile, MFEMovementType _movementType,
                                                                  int _clearance)
  {
    // abort if clearance is the size of the map
    if (_clearance > this.map[0].length - _tile.getPosX() ||
        _clearance > this.map[0][0].length - _tile.getPosY()) {
      return _clearance-1;
    }

    if (_clearance == 1) {
      if (_tile.isWalkable(_movementType))
        return calculateClearance(_tile, _movementType, _clearance+1);
      else
        return 0;
    }

    // test new row of tiles below the current one
    final int startx = _tile.getPosX();
    final int endx   = startx + _clearance;
    int y = _tile.getPosY() + _clearance - 1;
    for (int x = startx; x < endx; ++x) {
      MFTile neighbor = this.map[_tile.getPosZ()][x][y];
      if (!neighbor.isWalkable(_movementType) ||
           neighbor.hasWallNorth()) {
        return _clearance - 1;
      }
    }
    // test new column of tiles
    final int starty = _tile.getPosY();
    final int endy   = starty + _clearance;
    int x = _tile.getPosX() + _clearance - 1;
    for (y = starty; y < endy; ++y) {
      MFTile neighbor = this.map[_tile.getPosZ()][x][y];
      if (!neighbor.isWalkable(_movementType) ||
           neighbor.hasWallWest()) {
        return _clearance - 1;
      }
    }

    return calculateClearance(_tile, _movementType, _clearance+1);
  }
}
