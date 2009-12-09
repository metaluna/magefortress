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

  private void processCommunicationChannels()
  {
    // process channels
    for (MFCommunicationChannel channel : this.channels) {
      channel.update();
    }
  }

}
