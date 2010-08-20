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
package magefortress.map;

import magefortress.map.ground.MFGround;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.items.placeable.MFIPlaceable;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.core.MFRoom;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.creatures.behavior.movable.MFEMovementType;
import magefortress.graphics.MFIPaintable;
import magefortress.storage.MFISaveable;

/**
 *
 * 
 */
public class MFTile implements MFIPaintable, MFISaveable
{
  /** Saves the type of corner */
  public enum Corner {NONE,VERTICAL,HORIZONTAL,BOTH,INWARD};
  /** Size of one tile. */
  public final static int TILESIZE = 48;
  public static final int WALL_WIDTH = 12;

  /**
   * Convenience constructor creating an underground tile with no walls.
   * @param _id Id in the data storage
   * @param _posX Position on the map
   * @param _posY Position on the map
   * @param _posZ Position on the map
   */
  public MFTile(int _id, int _posX, int _posY, int _posZ, MFGround _ground)
  {
    this(_id, _posX, _posY, _posZ, false,false,false,false,false,true,true, _ground);
  }
  /**
   * Full constructor
   * @param _id Id in the data storage
   * @param _posX Position on the map
   * @param _posY Position on the map
   * @param _posZ Position on the map
   * @param _dugOut Was it dug out or is it still solid?
   * @param _wallN Wall status
   * @param _wallE Wall status
   * @param _wallS Wall status
   * @param _wallW Wall status
   * @param _floor Floor status
   * @param _isUnderground Is or was it underground?
   */
  public MFTile(int _id, int _posX, int _posY, int _posZ, boolean _isDugOut,
          boolean _wallN, boolean _wallE, boolean _wallS, boolean _wallW,
          boolean _floor, boolean _isUnderground, MFGround _ground)
  {
    if (_ground == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without " +
                                                     "ground type.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.id = _id;
    this.posX = _posX;
    this.posY = _posY;
    this.posZ = _posZ;
    this.isDugOut = _isDugOut;
    this.isUnderground = _isUnderground;
    this.ground = _ground;

    // init listener lists
    this.constructionsListeners = new LinkedList<MFITileConstructionsListener>();
    this.room = null;

    setWalls(_wallN, _wallE, _wallS, _wallW);
    this.floor = _floor;
    this.corners = new EnumMap<MFEDirection, Corner>(MFEDirection.class);
    this.setCorner(MFEDirection.NE, Corner.NONE);
    this.setCorner(MFEDirection.SE, Corner.NONE);
    this.setCorner(MFEDirection.SW, Corner.NONE);
    this.setCorner(MFEDirection.NW, Corner.NONE);

    // init placed objects
    this.placedObject = null;

    // init clearance values
    this.naviInfo = new MFNavigationTile();

  }


  @Override
  public int getId()
  {
    return this.id;
  }

  @Override
  public void setId(int _id)
  {
    this.id = _id;
  }

  public int getPosX()
  {
    return posX;
  }

  public int getPosY()
  {
    return posY;
  }

  public int getPosZ()
  {
    return posZ;
  }

  public MFLocation getLocation()
  {
    return new MFLocation(this.posX, this.posY, this.posZ);
  }

  public MFGround getGround()
  {
    return this.ground;
  }

  public boolean isDugOut()
  {
    return isDugOut;
  }

  public boolean isUnderground()
  {
    return this.isUnderground;
  }

  public boolean hasWallNorth()
  {
    return wallN;
  }

  public boolean hasWallEast()
  {
    return wallE;
  }

  public boolean hasWallSouth()
  {
    return wallS;
  }

  public boolean hasWallWest()
  {
    return wallW;
  }

  public boolean hasWall(MFEDirection _direction)
  {
    if (!MFEDirection.straight().contains(_direction)) {
      String msg = "Tile: Cannot get wall. Illegal direction: " + _direction;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    switch (_direction) {
      case N: return this.hasWallNorth();
      case E: return this.hasWallEast();
      case S: return this.hasWallSouth();
      case W: return this.hasWallWest();
      default: return false;
    }
  }

  public boolean hasFloor()
  {
    return floor;
  }

  public boolean isWalkable(MFEMovementType _movementType)
  {
    switch (_movementType) {
      case WALK : return isDugOut() && hasFloor();
      case FLY  : return isDugOut();
      default   : return false;
    }
  }

  public boolean isWalkable(MFCapability _capability)
  {
    for (MFEMovementType movementType : _capability) {
      if (!this.isWalkable(movementType)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the room this tile currently belongs to. Set to <code>null</code> to detach
   * the tile from the room.
   * @param _room The new room or <code>null</code>
   */
  public void setRoom(MFRoom _room)
  {
    this.room = _room;
  }

  /**
   * Gets the room the tile currently belongs to. May be <code>null</code> if
   * the tile belongs to no room.
   * @return The parent room or <code>null</code>
   */
  public MFRoom getRoom()
  {
    return this.room;
  }

  /**
   * Adds a listener who wants to be notified when walls or floors have been
   * built or were removed from the tile. Supposed to be used mainly by entrance
   * nodes and creatures following a path over this node, so that they are able
   * to re-calculate paths.
   * @param _listener The new listener
   */
  public void subscribeConstructionsListener(MFITileConstructionsListener _listener)
  {
    this.constructionsListeners.add(_listener);
  }

  /**
   * Removes a listener
   * @param _listener The listener to remove
   */
  public void unsubscribeConstructionsListener(MFITileConstructionsListener _listener)
  {
    this.constructionsListeners.remove(_listener);
  }

  /**
   * Sets the placed object. May be set to <code>null</code> to remove the object.
   * @param _placeable The object placed on the tile. May be set to <code>null</code>
   *              to remove it.
   */
  public void setObject(MFIPlaceable _placeable)
  {
    if (this.placedObject != null && _placeable != null) {
      String msg = "Cannot put " + _placeable + " on tile@" + this.toString() +
                                  ". Already occupied by " + this.placedObject;
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }

    boolean objectRemoved = (this.placedObject != null && 
                              this.placedObject.isPlaceable() &&
                              _placeable == null);

    this.placedObject = _placeable;

    if (objectRemoved || (_placeable != null && _placeable.isPlaceable())) {
      this.notifyRoom();
    }
  }

  /**
   * Gets the object that is currently placed on the tile.
   * @return The object placed on the tile. May be <code>null</code>.
   */
  public MFIPlaceable getObject()
  {
    return this.placedObject;
  }

  //---vvv---        PAINTABLE INTERFACE        ---vvv---
  @Override
  public void update()
  {
    this.ground.update();
  }

  @Override
  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    final int x = posX * TILESIZE + _x_translation;
    final int y = posY * TILESIZE + _y_translation;

    paintFloor(_g, x, y);

    if (this.isDugOut()) {
      paintWalls(_g, x, y);
      paintCorners(_g, x, y);
    }

    if (PRINT_CLEARANCE) {
      int clearance = this.naviInfo.getClearance(MFCapability.WALK);
      _g.drawString("" + clearance, x+TILESIZE/2, y+TILESIZE/2);
    }

    //TODO paint objects placed on the tile
  }

  @Override
  public String toString()
  {
    return "" + posX + "/" + posY + "/" + posZ;
  }

  /**
   * Gets the corner type of the specified direction.
   * @param _direction The direction of the corner
   * @return The type of corner
   * @throws IllegalArgumentException If the direction is not diagonal
   */
  public Corner getCorner(MFEDirection _direction)
  {
    if (!MFEDirection.diagonals().contains(_direction)) {
      String msg = _direction + " is not a valid corner direction.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    return this.corners.get(_direction);
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---
  void setWallNorth(boolean wallNorth)
  {
    setWalls(wallNorth, wallE, wallS, wallW);
  }

  void setWallEast(boolean wallEast)
  {
    setWalls(wallN, wallEast, wallS, wallW);
  }

  void setWallSouth(boolean wallSouth)
  {
    setWalls(wallN, wallE, wallSouth, wallW);
  }

  void setWallWest(boolean wallWest)
  {
    setWalls(wallN, wallE, wallS, wallWest);
  }

  void setWalls(boolean n, boolean e, boolean s, boolean w)
  {
    boolean oldWallN = this.wallN;
    boolean oldWallE = this.wallE;
    boolean oldWallS = this.wallS;
    boolean oldWallW = this.wallW;
    this.wallN = n;
    this.wallE = e;
    this.wallS = s;
    this.wallW = w;

    if (oldWallN != n || oldWallE != e || oldWallS != s || oldWallW != w) {
      this.notifyConstructionsListeners();
    }
  }

  void setFloor(boolean floor)
  {
    boolean oldFloor = this.floor;
    this.floor = floor;
    if (oldFloor != floor) {
      this.notifyConstructionsListeners();
    }
  }

  void setDugOut(boolean dugOut)
  {
    this.isDugOut = dugOut;
  }

  /**
   * Sets the corner type of the specified direction.
   * @param _direction The direction of the corner
   * @param corner The type of corner
   */
  void setCorner(MFEDirection _direction, Corner _corner)
  {
    if (!MFEDirection.diagonals().contains(_direction)) {
      String msg = _direction + " is not a valid corner direction.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    this.corners.put(_direction, _corner);
  }

  Corner getCornerNW()
  {
    return this.getCorner(MFEDirection.NW);
  }

  Corner getCornerNE()
  {
    return this.getCorner(MFEDirection.NE);
  }

  Corner getCornerSE()
  {
    return this.getCorner(MFEDirection.SE);
  }

  Corner getCornerSW()
  {
    return this.getCorner(MFEDirection.SW);
  }

  void setClearance(MFCapability _capability, int _clearance)
  {
    this.naviInfo.setClearance(_capability, _clearance);
  }

  int getClearance(MFCapability _capability)
  {
    return this.naviInfo.getClearance(_capability);
  }

  void setParentSection(MFSection _section)
  {
    this.naviInfo.setParentSection(_section);
  }

  MFSection getParentSection()
  {
    return this.naviInfo.getParentSection();
  }
  
  void setEntrance(MFSectionEntrance _entrance)
  {
    this.naviInfo.setEntrance(_entrance);
  }

  MFSectionEntrance getEntrance()
  {
    return this.naviInfo.getEntrance();
  }

  boolean isEntrance()
  {
    return this.naviInfo.isEntrance();
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final static boolean PRINT_CLEARANCE = false;
  private static final Logger logger = Logger.getLogger(MFTile.class.getName());

  private final MFNavigationTile naviInfo;
  private int id;
  private int posX, posY, posZ;
  private boolean isUnderground;
  private boolean isDugOut;
  private boolean wallN, wallE, wallS, wallW;
  private boolean floor;
  private final MFGround ground;
  /**The types of corners. Automatically calculated
   * @Transient */
  private EnumMap<MFEDirection, Corner> corners;
  /** The room to which the tile belongs, if any.*/
  private MFRoom room;
  /** Wall and floor construction listeners */
  private LinkedList<MFITileConstructionsListener> constructionsListeners;
  /** Items placed on the tile like furniture, food or dropped clothes */
  private MFIPlaceable placedObject;

  /**
   * Paints the floor
   * @param _g The canvas
   * @param x The x-coordinate of the top-left position of the tile
   * @param y The y-coordinate of the top-left position of the tile
   */
  private void paintFloor(final Graphics2D _g, final int x, final int y)
  {
    if (!this.hasFloor()) {
      _g.setColor(Color.BLUE);
      _g.fillRect(x, y, TILESIZE, TILESIZE);
    } else if (this.isDugOut()) {
      this.ground.getBasicFloor().paint(_g, x, y);
    } else {
      this.ground.getSolidTile().paint(_g, x, y);
    }
  }

  /**
   * Paints the walls
   * @param _g The canvas
   * @param _x The x-coordinate of the top-left position of the tile
   * @param _y The y-coordinate of the top-left position of the tile
   */
  private void paintWalls(final Graphics2D _g, final int _x, final int _y)
  {
    for (MFEDirection dir : MFEDirection.straight()) {
      if (this.hasWall(dir)) {
        this.ground.getBasicWall(dir).paint(_g, _x, _y);
      }
    }
  }

  /**
   * Paints the corners
   * @param _g The canvas
   * @param x The x-coordinate of the top-left position of the tile
   * @param y The y-coordinate of the top-left position of the tile
   */
  private void paintCorners(final Graphics2D _g, final int _x, final int _y)
  {
    for (MFEDirection dir : MFEDirection.diagonals()) {
      this.ground.getBasicCorner(dir, this.getCorner(dir)).paint(_g, _x, _y);
    }
  }

  private void notifyRoom()
  {
    if (this.room != null) {
      this.room.tileObjectsChanged();
    }
  }

  private void notifyConstructionsListeners()
  {
    for (MFITileConstructionsListener listener : this.constructionsListeners) {
      listener.tileConstructionsChanged(this);
    }
  }
}
