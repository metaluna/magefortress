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
package magefortress.map.ground;

import java.awt.Color;
import java.awt.Graphics2D;
import magefortress.core.MFEDirection;
import magefortress.graphics.MFIPaintable;
import magefortress.map.MFTile;
import magefortress.map.MFTile.Corner;

/**
 *
 */
public class MFBasicUndergroundCorner implements MFIPaintable
{
  public MFBasicUndergroundCorner(MFEDirection _direction, Corner _corner)
  {
    this.direction = _direction;
    this.corner = _corner;
  }

  @Override
  public void update()
  {
    //noop
  }

  @Override
  public void paint(Graphics2D _g, int _x, int _y)
  {
    final int TILESIZE = MFTile.TILESIZE;
    final int WALL_WIDTH = MFTile.WALL_WIDTH;
    final int wallLength = TILESIZE - WALL_WIDTH * 2;

    if (this.direction == MFEDirection.NE) {
      this.paintCorner(_g, _x + wallLength + WALL_WIDTH, _y);
    } else if (this.direction == MFEDirection.SE) {
      this.paintCorner(_g, _x + wallLength + WALL_WIDTH, _y + wallLength + WALL_WIDTH);
    } else if (this.direction == MFEDirection.SW) {
      this.paintCorner(_g, _x, _y + wallLength + WALL_WIDTH);
    } else  if (this.direction == MFEDirection.NW) {
      this.paintCorner(_g, _x, _y);
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Color CORNER_COLOR = Color.DARK_GRAY;

  private final MFEDirection direction;
  private final Corner corner;

  private void paintCorner(Graphics2D _g, int _x, int _y)
  {
    final int WALL_WIDTH = MFTile.WALL_WIDTH;

    _g.setColor(CORNER_COLOR);

    if (corner == Corner.HORIZONTAL) {
      _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
    } else if (corner == Corner.VERTICAL) {
      _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
    } else if (corner == Corner.INWARD) {
      _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
    } else if (corner == Corner.BOTH) {
      int startAngle = 0;
      if (direction == MFEDirection.NE || direction == MFEDirection.SE) {
        startAngle = 180;
      }

      int arc = 90;
      if (direction == MFEDirection.NW || direction == MFEDirection.SE) {
        arc *= -1;
      }

      if (direction == MFEDirection.NW || direction == MFEDirection.SW) {
        _x -=WALL_WIDTH;
      }
      if (direction == MFEDirection.NW || direction == MFEDirection.NE) {
        _y -=WALL_WIDTH;
      }

      _g.fillArc(_x, _y, WALL_WIDTH*2, WALL_WIDTH*2, startAngle, arc);
    }
  }

}
