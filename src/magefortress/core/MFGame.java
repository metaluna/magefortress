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
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.channel.MFCommunicationChannel;
import magefortress.gui.MFScreen;

/**
 * Single place for all game data.
 */
public class MFGame
{
  
  public MFGame(MFMap _map)
  {
    // init channels
    this.channels = new ArrayList<MFCommunicationChannel>();
    
    // init map
    this.map = _map;
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
   * The currently active map
   * @return The map
   */
  public MFMap getMap()
  {
    return this.map;
  }

  /**
   * Important! Set this after you've initialized the view.
   * @param _screen The view of the game
   */
  public void setScreen(MFScreen _screen)
  {
    this.screen = _screen;
  }
  /**
   * Removes the newest screen from the screens stack, which should be an
   * instance of this game's game screen.
   */
  public void quit()
  {
    if (this.screen == null) {
      String msg = "Game: Can't call close() without a screen. Set it!";
      logger.log(Level.SEVERE, msg);
      throw new NullPointerException(msg);
    }
    this.screen.close();
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The view on the game */
  private MFScreen screen;
  /** The map */
  private MFMap map;
  /** Communications channels*/
  private final ArrayList<MFCommunicationChannel> channels;
  /** The logger */
  private static final Logger logger = Logger.getLogger(MFGame.class.getName());

  private void processCommunicationChannels()
  {
    // process channels
    for (MFCommunicationChannel channel : this.channels) {
      channel.update();
    }
  }

}
