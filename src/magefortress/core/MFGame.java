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
import magefortress.creatures.MFCreature;
import magefortress.graphics.MFImageLibrary;
import magefortress.gui.MFScreen;
import magefortress.jobs.MFJobFactory;
import magefortress.map.MFMap;
import magefortress.map.MFNavigationMap;
import magefortress.map.MFTile;
import magefortress.storage.MFDaoFactory;

/**
 * Single place for all game data.
 */
public class MFGame
{

  public static MFGame loadGame(int _mapId, MFImageLibrary _imgLib, MFDaoFactory _daoFactory)
  {
    final MFMap map = MFMap.loadMap(_mapId, _daoFactory);
    final MFGame result = new MFGame(map, _imgLib, _daoFactory);
    return result;
  }

  public MFGame(MFMap _map, MFImageLibrary _imgLib, MFDaoFactory _daoFactory)
  {
    // init channels
    this.channels = new ArrayList<MFCommunicationChannel>();
    this.creatures = new ArrayList<MFCreature>();
    
    // init map
    this.map = _map;

    this.daoFactory = _daoFactory;

    this.jobFactory = new MFJobFactory(this);
    this.gameObjectFactory = new MFGameObjectFactory(_imgLib, this.jobFactory, _map);

    this.naviMap = new MFNavigationMap(this.map);
    this.naviMap.updateAllEntrances();
  }

  public void update(long _currentTime)
  {
    processCommunicationChannels();

    for (MFCreature creature : this.creatures) {
      creature.update(_currentTime);
    }
  }

  public void paint(Graphics2D _g, int _currentLevel, Rectangle _clippingRect)
  {
    this.map.paint(_g, _currentLevel, _clippingRect);

    MFLocation start = this.map.getVisibleStart(_currentLevel, _clippingRect);
    MFLocation end   = this.map.getVisibleEnd(_currentLevel, _clippingRect);
    
    // render all visible creatures
    for (MFCreature creature : this.creatures) {
      MFLocation location = creature.getLocation();
      if (location.z == _currentLevel &&
          start.x <= location.x && location.x <= end.x &&
          start.y <= location.y && location.y <= end.y) {
        creature.paint(_g, _clippingRect.x, _clippingRect.y);
      }
    }

    // TODO paint objects - move to map.paint()?
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
   * The game object producer
   * @return The game object factory
   */
  public MFGameObjectFactory getGameObjectFactory()
  {
    return this.gameObjectFactory;
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

  /**
   * Adds a creature to the game. It does not have to be placed on the map.
   * It will be processed regardless.
   * @param _creature The creature to add.
   */
  public void addCreature(MFCreature _creature)
  {
    this.creatures.add(_creature);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The view on the game */
  private MFScreen screen;
  /** The map */
  private MFMap map;
  /** Communications channels*/
  private final ArrayList<MFCommunicationChannel> channels;
  /** Relevant creatures */
  private final ArrayList<MFCreature> creatures;
  /** Factory for all game object like creatures, items, plants */
  private final MFGameObjectFactory gameObjectFactory;
  /** DAO factory */
  private final MFDaoFactory daoFactory;
  /** Job factory */
  private final MFJobFactory jobFactory;
  /** Navigation map containing pathfinding information */
  private final MFNavigationMap naviMap;
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
