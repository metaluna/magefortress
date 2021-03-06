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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.channel.MFChannelFactory;
import magefortress.channel.MFCommunicationChannel;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.instrumentable.MFEJob;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.graphics.MFImageLibrary;
import magefortress.gui.MFScreen;
import magefortress.items.MFBlueprint;
import magefortress.jobs.MFConstructionSite;
import magefortress.jobs.MFIConstructionSiteListener;
import magefortress.jobs.MFJobFactory;
import magefortress.map.MFMap;
import magefortress.map.MFNavigationMap;
import magefortress.map.MFPathFinder;
import magefortress.map.MFTile;
import magefortress.map.ground.MFBasicUnderground;
import magefortress.map.ground.MFGround;
import magefortress.storage.MFDaoFactory;

/**
 * Single place for all game data.
 */
public class MFGame implements MFIConstructionSiteListener
{

  public static MFGame loadGame(int _mapId, MFImageLibrary _imgLib, MFDaoFactory _daoFactory)
  {
    // JUST FOR TESTING
    final Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>(1);
    final MFBlueprint blueprint = new MFBlueprint(-1, "Null Item");
    final int hardness = 150;
    final MFGround ground = new MFGround(-1, blueprint, hardness,
                                   MFBasicUnderground.getBasicSolidTile(),
                                   MFBasicUnderground.getBasicUndergroundFloor(),
                                   MFBasicUnderground.getBasicUndergroundWalls(),
                                   MFBasicUnderground.getBasicUndergroundCorners());
    groundTypes.put(1, ground);

    final MFMap map = MFMap.loadMap(_mapId, _daoFactory, groundTypes);
    final MFGame result = new MFGame(map, _imgLib, _daoFactory);
    return result;
  }

  public MFGame(MFMap _map, MFImageLibrary _imgLib, MFDaoFactory _daoFactory)
  {
    if (_map == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create game " +
              "without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_daoFactory == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create game " +
              "without a DAO factory.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.map = _map;
    this.daoFactory = _daoFactory;

    this.jobFactory = new MFJobFactory(this);
    this.gameObjectFactory = new MFGameObjectFactory(_imgLib, this.jobFactory, _map, this);

    this.channels = new LinkedList<MFCommunicationChannel>();
    initCommunicationChannels();
    this.creatures = new LinkedList<MFCreature>();
    this.constructionSites = new LinkedList<MFConstructionSite>();
    this.garbageConstructionSites = new LinkedList<MFConstructionSite>();
    
    this.pathFinder = this.gameObjectFactory.createPathFinder();
    this.naviMap = this.gameObjectFactory.createNavigationMap();
    initPathFinder();
  }

  public void update()
  {
    removeMarkedConstructionSites();
    if (mapDataChanged) {
      this.initPathFinder();
      this.mapDataChanged = false;
    }
    processCommunicationChannels();
    processCreatures();
    processConstructionSites();
    processPathFinder();
  }

  public void paint(Graphics2D _g, int _currentLevel, Rectangle _clippingRect)
  {
    this.map.paint(_g, _currentLevel, _clippingRect);

    final MFLocation start = this.map.getVisibleStart(_currentLevel, _clippingRect);
    final MFLocation end   = this.map.getVisibleEnd(_currentLevel, _clippingRect);
    
    paintCreatures(_currentLevel, start, end, _g, _clippingRect);
    paintConstructionSites(_currentLevel, start, end, _g, _clippingRect);
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

  //---vvv---     CONSTRUCTION SITE LISTENER INTERFACE      ---vvv---
  /**
   * Adds a construction site. Does not check if there's already a site at
   * the position of the new construction site.
   * @param _constructionSite The construction site to add
   */
  @Override
  public void addConstructionSite(MFConstructionSite _constructionSite)
  {
    this.constructionSites.add(_constructionSite);
  }

  /**
   * Marks a construction site for removal. Removing will take place at the
   * beginning of the next game update.
   * @param _location The location of the site to remove.
   */
  @Override
  public void removeConstructionSite(MFLocation _location)
  {
    for (MFConstructionSite site : this.constructionSites) {
      if (site.getLocation().equals(_location)) {
        // mark for removal
        this.garbageConstructionSites.add(site);
      }
    }
  }

  /**
   * Iterates over all construction sites. Do not try to remove sites directly,
   * but use {@link #removeConstructionSite}.
   * @return An iterator over all construction sites
   */
  @Override
  public Iterable<MFConstructionSite> getConstructionSites()
  {
    return Collections.unmodifiableList(this.constructionSites);
  }

  @Override
  public void constructionSiteFinished(MFConstructionSite _constructionSite)
  {
    this.removeConstructionSite(_constructionSite.getLocation());
    this.mapDataChanged = true;
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The view on the game */
  private MFScreen screen;
  /** The map */
  private MFMap map;
  /** Communications channels*/
  private final List<MFCommunicationChannel> channels;
  /** Relevant creatures */
  private final List<MFCreature> creatures;
  /** Construction sites */
  private final List<MFConstructionSite> constructionSites;
  private final List<MFConstructionSite> garbageConstructionSites;
  /** Factory for all game object like creatures, items, plants */
  private final MFGameObjectFactory gameObjectFactory;
  /** DAO factory */
  private final MFDaoFactory daoFactory;
  /** Job factory */
  private final MFJobFactory jobFactory;
  /** Navigation map containing pathfinding information */
  private final MFNavigationMap naviMap;
  /** Holds a queue of path searches */
  private final MFPathFinder pathFinder;

  private boolean mapDataChanged;

  /** The logger */
  private static final Logger logger = Logger.getLogger(MFGame.class.getName());

  private final void initCommunicationChannels()
  {
    MFChannelFactory channelFactory = this.gameObjectFactory.createChannelFactory();
    for (MFEJob channelType : MFEJob.values()) {
      this.channels.add(channelFactory.getChannel(channelType));
    }
  }

  private final void initPathFinder()
  {
    this.naviMap.updateClearanceValues(MFCapability.WALK);
    this.naviMap.calculateAllLevels();
  }

  private void processCommunicationChannels()
  {
    // process channels
    for (MFCommunicationChannel channel : this.channels) {
      channel.update();
    }
  }

  private void processCreatures()
  {
    for (MFCreature creature : this.creatures) {
      creature.update();
    }
  }

  private void processConstructionSites()
  {
    for (MFConstructionSite site: this.constructionSites) {
      site.update();
    }
  }

  private void processPathFinder()
  {
    this.pathFinder.update();
  }

  private void paintCreatures(int _currentLevel, MFLocation _start,
                        MFLocation _end, Graphics2D _g, Rectangle _clippingRect)
  {
    for (MFCreature creature : this.creatures) {
      MFLocation location = creature.getLocation();
      if (location.z == _currentLevel && 
          _start.x <= location.x && location.x <= _end.x &&
          _start.y <= location.y && location.y <= _end.y) {
        creature.paint(_g, _clippingRect.x, _clippingRect.y);
      }
    }
  }

  private void paintConstructionSites(int _currentLevel, MFLocation _start,
                        MFLocation _end, Graphics2D _g, Rectangle _clippingRect)
  {
    for (MFConstructionSite site : this.constructionSites) {
      MFLocation location = site.getLocation();
      if (location.z == _currentLevel && 
          _start.x <= location.x && location.x <= _end.x &&
          _start.y <= location.y && location.y <= _end.y) {
        site.paint(_g, _clippingRect.x, _clippingRect.y);
      }
    }
  }

  private void removeMarkedConstructionSites()
  {
    this.constructionSites.removeAll(this.garbageConstructionSites);
  }
}
