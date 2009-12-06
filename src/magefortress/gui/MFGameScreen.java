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
package magefortress.gui;

import java.awt.Color;
import magefortress.core.MFGame;
import java.awt.Graphics2D;

/**
 * Represents the main game screen. It's mainly handling the interface elements.
 */
public class MFGameScreen extends MFScreen
{

  public MFGameScreen(MFGame _game)
  {
    this.game = _game;
  }

  @Override
  public void initialize()
  {
  }

  @Override
  public void deinitialize()
  {
  }

  @Override
  public void update()
  {
    this.game.update();
  }

  @Override
  public void paint(Graphics2D _g)
  {
    this.paintRaster(_g);
    this.game.paint(_g);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private MFGame game;

  private void paintRaster(Graphics2D _g)
  {
    final int tilesize = this.game.getTilesize();
    final int startx = 0;
    final int starty = 0;
    final int width = _g.getDeviceConfiguration().getBounds().width;
    final int height = _g.getDeviceConfiguration().getBounds().height;

    // fill background
    _g.setColor(Color.BLACK);
    _g.fillRect(startx, starty, width, height);

    // draw grid
    _g.setColor(Color.GRAY);
    // draw vertical lines
    for (int x=startx; x < width; x+=tilesize) {
      _g.drawLine(x, starty, x, starty+height-1);
    }
    // draw horizontal lines
    for (int y=starty; y < height; y+=tilesize) {
      _g.drawLine(startx, y, startx+width-1, y);
    }
  }
}
