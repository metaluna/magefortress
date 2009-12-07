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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import magefortress.channel.MFCommunicationChannel;
import magefortress.gui.MFScreensManager;
import magefortress.input.*;

/**
 * Single place for all game data.
 */
public class MFGame implements MFIMouseListener, MFIKeyListener
{

  public MFGame()
  {
    // init input
    this.loadKeyMappings();
    this.inputActionQueue = new LinkedList<MFInputAction>();
    MFInputManager.getInstance().addMouseListener(this);
    MFInputManager.getInstance().addKeyListener(this);
    
    // init channels
    this.channels = new ArrayList<MFCommunicationChannel>();
    
    // init map
    this.map = new MFMap(30,30,1);
  }

  public void update()
  {
    processInput();
    processCommunicationChannels();

    // TODO process creatures
  }

  public void paint(Graphics2D _g)
  {
    // TODO paint map
    // TODO paint objects - move to map.paint()?
    // TODO paint creatures - move to map.paint()?
    paintMovedTile(_g);
    paintClickedTile(_g);
  }

  /**
   * The size of a map tile
   * @return The size of a map tile
   */
  public int getTilesize()
  {
    return MFMap.TILESIZE;
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

  /**
   * Removes the newest screen from the screens stack, which should be an
   * instance of this game's game screen.
   */
  public void quit()
  {
    MFScreensManager.getInstance().pop();
  }

  public void mouseClicked(int _x, int _y)
  {
    this.clicked = this.map.convertToTilespace(_x, _y);
  }

  public void mouseMoved(int _x, int _y)
  {
    this.mouseMoved = this.map.convertToTilespace(_x, _y);
  }

  public void keyPressed(int _keyCode)
  {
    // beware of teh b0xing
    final MFInputAction action = this.keyMappings.get(_keyCode);
    if (action != null) {
      this.enqueueInputAction(action);
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  /** The map */
  private MFMap map;
  /** Key mappings */
  private HashMap<Integer, MFInputAction> keyMappings;
  /** Player's input actions */
  private Queue<MFInputAction> inputActionQueue;
  /** Communications channels*/
  private final ArrayList<MFCommunicationChannel> channels;

  // last tile clicked
  private MFLocation clicked;
  // last tile the mouse touched while moving
  private MFLocation mouseMoved;


  private void loadKeyMappings()
  {
    this.keyMappings = new HashMap<Integer, MFInputAction>();
    this.keyMappings.put(KeyEvent.VK_Q, new MFQuitInputAction(this));
  }

  private void paintClickedTile(Graphics2D _g)
  {
    if (this.clicked == null)
      return;

    Point pos = this.map.convertFromTilespace(this.clicked.x, this.clicked.y);

    _g.setColor(Color.GREEN);
    _g.fillRect(pos.x, pos.y, MFMap.TILESIZE, MFMap.TILESIZE);
  }

  private void paintMovedTile(Graphics2D _g)
  {
    if (this.mouseMoved == null)
      return;

    Point pos = this.map.convertFromTilespace(this.mouseMoved.x, this.mouseMoved.y);
    _g.setColor(Color.MAGENTA);
    _g.fillRect(pos.x, pos.y, MFMap.TILESIZE, MFMap.TILESIZE);
    _g.setColor(Color.GRAY);
    _g.drawString("" + this.mouseMoved.x + "/" + this.mouseMoved.y, 2, 13);
  }

  private void processCommunicationChannels()
  {
    // process channels
    for (MFCommunicationChannel channel : this.channels) {
      channel.update();
    }
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
