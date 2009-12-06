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

import java.awt.Point;

/**
 * Contains the tiles.
 */
public class MFMap
{
  public final static int TILESIZE = 48;

  public MFMap(int width, int height, int levels)
  {
    this.map = new MFTile[levels][width][height];
  }

  public MFLocation convertToTilespace(final int _x, final int _y)
  {
    int tileX = _x / TILESIZE;
    int tileY = _y / TILESIZE;
    int tileZ = 0;

    return new MFLocation(tileX, tileY, tileZ);
  }

  public Point convertFromTilespace(final int _x, final int _y)
  {
    int screenX = _x * TILESIZE;
    int screenY = _y * TILESIZE;

    return new Point(screenX, screenY);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private MFTile[][][] map;
}
