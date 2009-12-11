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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.core.MFMap;
import magefortress.core.MFTile;
import magefortress.input.*;

/**
 * Represents the main game screen. It's handling the interface elements and
 * player input.
 */
public class MFGameScreen extends MFScreen implements MFIMouseListener, MFIKeyListener
{

  public MFGameScreen(MFGame _game)
  {
    this.game = _game;
    this.currentLevel = 0;
    this.clippingRect = new Rectangle(0,0,1,1);

    // init input
    this.loadKeyMappings();
    this.inputActionQueue = new LinkedList<MFInputAction>();
  }

  /**
   * Puts an action into the queue. Will be activated during the next game
   * processing phase.
   * @param _action The action to enqueue
   */
  public void enqueueInputAction(MFInputAction _action)
  {
    this.inputActionQueue.add(_action);
  }

  @Override
  public void initialize()
  {
    this.getInputManager().addMouseListener(this);
    this.getInputManager().addKeyListener(this);
  }

  @Override
  public void deinitialize()
  {
    this.getInputManager().removeMouseListener(this);
    this.getInputManager().removeKeyListener(this);
  }

  @Override
  public void update()
  {
    this.processInput();
    this.game.update();
  }

  @Override
  public void paint(Graphics2D _g, int _width, int _height)
  {
    this.clippingRect.width  = _width;
    this.clippingRect.height = _height;
    
    this.paintRaster(_g);
    //_g.fillRect(0,0,_width,_height);

    Rectangle _clippingRectCopy = new Rectangle(this.clippingRect);
    this.game.paint(_g, this.currentLevel, _clippingRectCopy);

    paintMovedTile(_g);
    paintClickedTile(_g);
  }

  public void mouseClicked(int _x, int _y)
  {
    this.tileClicked = MFMap.convertToTilespace(_x, _y, this.currentLevel,
                                      this.clippingRect.x, this.clippingRect.y);

    // dig out tile if it's inside the map and not dug out
    MFMap map = this.game.getMap();
    if (0 <= tileClicked.x && tileClicked.x < map.getWidth() &&
        0 <= tileClicked.y && tileClicked.y < map.getHeight() &&
        map.getTile(tileClicked.x, tileClicked.y, tileClicked.z).isDugOut() == false) {
      MFLocation[] locations = {new MFLocation(this.tileClicked)};
      MFDigInputAction digAction = new MFDigInputAction(this.game, locations);
      this.enqueueInputAction(digAction);
    }
  }

  public void mouseMoved(int _x, int _y)
  {
    this.tileMouseMoved = MFMap.convertToTilespace(_x, _y, this.currentLevel,
                                      this.clippingRect.x, this.clippingRect.y);
  }

  public void keyPressed(int _keyCode)
  {
    // beware of teh b0xing
    final MFInputAction action = this.keyMappings.get(_keyCode);
    if (action != null) {
      this.enqueueInputAction(action);
    }

    final int scrollSpeed = MFTile.TILESIZE/2;
    if (_keyCode == KeyEvent.VK_LEFT || _keyCode == KeyEvent.VK_KP_LEFT) {
      this.clippingRect.x+=scrollSpeed;
    } else if (_keyCode == KeyEvent.VK_RIGHT || _keyCode == KeyEvent.VK_KP_RIGHT) {
      this.clippingRect.x-=scrollSpeed;
    } else if (_keyCode == KeyEvent.VK_UP || _keyCode == KeyEvent.VK_KP_UP) {
      this.clippingRect.y+=scrollSpeed;
    } else if (_keyCode == KeyEvent.VK_DOWN || _keyCode == KeyEvent.VK_KP_DOWN) {
      this.clippingRect.y-=scrollSpeed;
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Active game */
  private MFGame game;
  /** Key mappings */
  private HashMap<Integer, MFInputAction> keyMappings;
  /** Player's input actions */
  private LinkedList<MFInputAction> inputActionQueue;
  /** Currently visible level */
  private int currentLevel;
  /** Currently visible part of the map */
  private Rectangle clippingRect;

  // last tile clicked
  private MFLocation tileClicked;
  // last tile the mouse touched while moving
  private MFLocation tileMouseMoved;

  private void paintClickedTile(Graphics2D _g)
  {
    if (this.tileClicked == null)
      return;

    Point pos = MFMap.convertFromTilespace(this.tileClicked.x, this.tileClicked.y);
    pos.translate(this.clippingRect.x, this.clippingRect.y);

    _g.setColor(Color.GREEN);
    _g.drawRect(pos.x, pos.y, MFTile.TILESIZE, MFTile.TILESIZE);
  }

  private void paintMovedTile(Graphics2D _g)
  {
    if (this.tileMouseMoved == null)
      return;

    Point pos = MFMap.convertFromTilespace(this.tileMouseMoved.x, this.tileMouseMoved.y);
    pos.translate(this.clippingRect.x, this.clippingRect.y);
    
    _g.setColor(Color.MAGENTA);
    _g.drawRect(pos.x, pos.y, MFTile.TILESIZE, MFTile.TILESIZE);
    _g.setColor(Color.GRAY);
    _g.drawString("" + this.tileMouseMoved.x + "/" + this.tileMouseMoved.y, 2, 13);
  }

  private void paintRaster(Graphics2D _g)
  {
    final int tilesize = this.game.getTileSize();
    final int startx = this.clippingRect.x%MFTile.TILESIZE;
    final int starty = this.clippingRect.y%MFTile.TILESIZE;
    final int endx = this.clippingRect.width;
    final int endy = this.clippingRect.height;

    // fill background
    _g.setColor(Color.BLACK);
    _g.fillRect(0, 0, clippingRect.width, clippingRect.height);

    // draw grid
    _g.setColor(Color.GRAY);
    // draw vertical lines
    for (int x=startx; x < endx; x+=tilesize) {
      _g.drawLine(x, 0, x, clippingRect.height-1);
    }
    // draw horizontal lines
    for (int y=starty; y < endy; y+=tilesize) {
      _g.drawLine(0, y, clippingRect.width-1, y);
    }
  }

  private void loadKeyMappings()
  {
    this.keyMappings = new HashMap<Integer, MFInputAction>();
    this.keyMappings.put(KeyEvent.VK_ESCAPE, new MFQuitInputAction(this.game));
  }

  private void processInput()
  {
    // process enqueued actions
    for (MFInputAction action : this.inputActionQueue) {
      action.execute();
    }
    // clear queue
    this.inputActionQueue.clear();
  }

}
