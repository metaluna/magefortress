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
import java.util.logging.Level;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import magefortress.input.*;

/**
 * Represents the main game screen. It's handling the interface elements and
 * player input.
 */
public class MFGameScreen extends MFScreen implements MFIMouseListener, MFIKeyListener, MFIInputToolListener
{

  public MFGameScreen(MFInputManager _inputManager, MFScreensManager _screensManager,
                      MFGame _game, MFGameInputFactory _gameInputFactory)
  {
    super(_inputManager, _screensManager);
    if (_game == null) {
      String msg = this.getClass().getSimpleName() + ": Game must not be null. " +
                                          "Can't instantiate game screen.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    if (_gameInputFactory == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without " +
                                                        "a game input factory.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.game = _game;
    this.gameInputFactory = _gameInputFactory;
    
    this.currentLevel = 0;
    this.clippingRect = new Rectangle(0,0,1,1);

    // init input
    this.loadKeyMappings();
    this.inputActionQueue = new LinkedList<MFInputAction>();

    // set default action
    this.activeInputTool = this.getDefaultInputTool();
  }

  /**
   * Puts an action into the queue. Will be activated during the next game
   * processing phase.
   * @param _action The action to enqueue
   */
  public void enqueueInputAction(MFInputAction _action)
  {
    if (_action == null) {
      String msg = "GameScreen: Can't enqueue action if it's null.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
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

  @Override
  public void mouseClicked(int _x, int _y)
  {
    this.tileClicked = MFMap.convertToTilespace(_x, _y, this.currentLevel,
                                      this.clippingRect.x, this.clippingRect.y);

    if (this.activeInputTool != null) {
      this.activeInputTool.click(this.tileClicked);
    }

  }

  @Override
  public void mouseMoved(int _x, int _y)
  {
    this.tileMouseMoved = MFMap.convertToTilespace(_x, _y, this.currentLevel,
                                      this.clippingRect.x, this.clippingRect.y);
    
    if (this.activeInputTool != null) {
      this.tileValid = this.activeInputTool.isValid(this.tileMouseMoved);
    }
  }

  @Override
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

  //---vvv---     INPUT TOOL INTERFACE METHODS      ---vvv---
  @Override
  public void toolFinished()
  {
    if (this.activeInputTool == null) {
      String msg = this.getClass().getSimpleName() + ": Tool is reported " +
                                "to be finished, but there is no active tool.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    MFInputAction action = this.activeInputTool.buildAction();
    this.enqueueInputAction(action);
    this.activeInputTool = this.getDefaultInputTool();
  }

  @Override
  public void toolPhaseChanged()
  {
    if (this.activeInputTool == null) {
      String msg = this.getClass().getSimpleName() + ": Tool's phase is " +
                      "reported to have changed, but there is no active tool.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
  }

  //---vvv---       PACKAGE-PRIVATE METHODS        ---vvv---
  /**
   * For Testing Only. Remove ASAP by loading hotkey sets and sending
   * key pressed events.
   * @param _inputTool The input tool used
   */
  void setActiveInputTool(MFIInputTool _inputTool)
  {
    this.activeInputTool = _inputTool;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Active game */
  private final MFGame game;
  /** Factory for input tools */
  private final MFGameInputFactory gameInputFactory;
  /** Key mappings */
  private HashMap<Integer, MFInputAction> keyMappings;
  /** Player's input actions */
  private LinkedList<MFInputAction> inputActionQueue;
  /** Currently visible level */
  private int currentLevel;
  /** Currently visible part of the map */
  private Rectangle clippingRect;
  /** Currently active input tool */
  private MFIInputTool activeInputTool;

  // last tile clicked
  private MFLocation tileClicked;
  // last tile the mouse touched while moving
  private MFLocation tileMouseMoved;
  private boolean tileValid;

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
    
    Color color = null;
    if (this.tileValid) {
      color = Color.GREEN;
    } else {
      color = Color.RED;
    }
    _g.setColor(color);
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

  private MFIInputTool getDefaultInputTool()
  {
    MFIInputTool result = this.gameInputFactory.createDigTool(this);
    return result;
  }

}
