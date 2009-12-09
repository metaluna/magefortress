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

  //---vvv---      PRIVATE METHODS      ---vvv---

  private MFTile[][][] map;
}
