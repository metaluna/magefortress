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

/**
 *
 */
public class MFBasicUndergroundWall implements MFIPaintable
{
  public MFBasicUndergroundWall(MFEDirection _direction)
  {
    this.direction = _direction;
  }

  @Override
  public void update()
  {
    //noop
  }

  @Override
  public void paint(final Graphics2D _g, final int _x, final int _y)
  {
    _g.setColor(WALL_COLOR);

    final int TILESIZE = MFTile.TILESIZE;
    final int WALL_WIDTH = MFTile.WALL_WIDTH;
    final int wallLength = TILESIZE - WALL_WIDTH * 2;

    if (this.direction == MFEDirection.N) {
      _g.fillRect(_x + WALL_WIDTH, _y, wallLength, WALL_WIDTH);
    }
    if (this.direction == MFEDirection.E) {
      _g.fillRect(_x + wallLength + WALL_WIDTH, _y + WALL_WIDTH, WALL_WIDTH, wallLength);
    }
    if (this.direction == MFEDirection.S) {
      _g.fillRect(_x + WALL_WIDTH, _y + wallLength + WALL_WIDTH, wallLength, WALL_WIDTH);
    }
    if (this.direction == MFEDirection.W) {
      _g.fillRect(_x, _y + WALL_WIDTH, WALL_WIDTH, wallLength);
    }

  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Color WALL_COLOR = Color.DARK_GRAY;
  private final MFEDirection direction;
}
