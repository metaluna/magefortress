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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
import magefortress.core.MFIPaintable;
import magefortress.core.MFIPlaceable;
import magefortress.core.MFLocation;
import magefortress.core.MFRoom;

/**
 *
 * 
 */
public class MFTile implements MFIPaintable
{
  /** Saves the type of corner */
  public enum Corner {NONE,VERTICAL,HORIZONTAL,BOTH,INWARD};
  /** Size of one tile. */
  public final static int TILESIZE = 48;

  /**
   * Convenience constructor creating an underground tile with no walls.
   * @param _posX Position on the map
   * @param _posY Position on the map
   * @param _posZ Position on the map
   */
  public MFTile(int _posX, int _posY, int _posZ)
  {
    this(_posX, _posY, _posZ, false,false,false,false,false,true,true);
  }
  /**
   * Full constructor
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
  public MFTile(int _posX, int _posY, int _posZ, boolean _isDugOut,
          boolean _wallN, boolean _wallE, boolean _wallS, boolean _wallW,
          boolean _floor, boolean _isUnderground)
  {
    this.posX = _posX;
    this.posY = _posY;
    this.posZ = _posZ;
    this.isDugOut = _isDugOut;
    this.isUnderground = _isUnderground;

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
    this.naviInfo = new MFNavigationTile(_posX, _posY, _posZ);

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
    this.placedObject = _placeable;
    this.notifyRoom();
  }

  /**
   * Gets the object that is currently placed on the tile.
   * @return The object placed on the tile. May be <code>null</code>.
   */
  public MFIPlaceable getObject()
  {
    return this.placedObject;
  }

  public void update()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    final int x = posX * TILESIZE + _x_translation;
    final int y = posY * TILESIZE + _y_translation;
    // paint floor
    if (this.isUnderground()) {
      if (!this.hasFloor()) {
        _g.setColor(Color.BLUE); 
      } else if (this.isDugOut()) {
        _g.setColor(Color.LIGHT_GRAY);
      } else {
        _g.setColor(Color.BLACK);
      }
    } else {
      _g.setColor(Color.GREEN);
    }

    _g.fillRect(x, y, TILESIZE, TILESIZE);

    if (this.isDugOut()) {
      // paint walls
      _g.setColor(Color.DARK_GRAY);
      final int wallLength = TILESIZE-WALL_WIDTH*2;

      if (this.hasWallNorth()) {
        _g.fillRect(x + WALL_WIDTH, y, wallLength, WALL_WIDTH);
      }
      if (this.hasWallEast()) {
        _g.fillRect(x + wallLength + WALL_WIDTH, y + WALL_WIDTH, WALL_WIDTH, wallLength);
      }
      if (this.hasWallSouth()) {
        _g.fillRect(x + WALL_WIDTH, y + wallLength + WALL_WIDTH, wallLength, WALL_WIDTH);
      }
      if (this.hasWallWest()) {
        _g.fillRect(x, y + WALL_WIDTH, WALL_WIDTH, wallLength);
      }

      // paint corners
      this.paintCorner(_g, MFEDirection.NE, x + wallLength + WALL_WIDTH, y);
      this.paintCorner(_g, MFEDirection.SE, x + wallLength + WALL_WIDTH, y + wallLength + WALL_WIDTH);
      this.paintCorner(_g, MFEDirection.SW, x, y + wallLength + WALL_WIDTH);
      this.paintCorner(_g, MFEDirection.NW, x, y);

    }


    //TODO paint objects placed on the tile
  }

  @Override
  public String toString()
  {
    return "" + posX + "/" + posY + "/" + posZ;
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

  /**
   * Gets the corner type of the specified direction.
   * @param _direction The direction of the corner
   * @return The type of corner
   * @throws IllegalArgumentException If the direction is not diagonal
   */
  Corner getCorner(MFEDirection _direction)
  {
    if (!MFEDirection.diagonals().contains(_direction)) {
      String msg = _direction + " is not a valid corner direction.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    return this.corners.get(_direction);
  }

  void setClearance(MFEMovementType _movementType, int _clearance)
  {
    this.naviInfo.setClearance(_movementType, _clearance);
  }

  int getClearance(MFEMovementType _movementType)
  {
    return this.naviInfo.getClearance(_movementType);
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
  private static final int WALL_WIDTH = 12;
  private static final Logger logger = Logger.getLogger(MFTile.class.getName());

  private final MFNavigationTile naviInfo;
  private int posX, posY, posZ;
  private boolean isUnderground;
  private boolean isDugOut;
  private boolean wallN, wallE, wallS, wallW;
  private boolean floor;
  /**The types of corners. Automatically calculated
   * @Transient */
  private EnumMap<MFEDirection, Corner> corners;
  /** The room to which the tile belongs, if any.*/
  private MFRoom room;
  /** Wall and floor construction listeners */
  private LinkedList<MFITileConstructionsListener> constructionsListeners;
  /** Items placed on the tile like furniture, food or dropped clothes */
  private MFIPlaceable placedObject;

  private void paintCorner(Graphics2D _g, MFEDirection _direction, int _x, int _y)
  {
    final Corner corner = this.getCorner(_direction);
    if (corner == Corner.HORIZONTAL) {
      _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
    } else if (corner == Corner.VERTICAL) {
      _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
    } else if (corner == Corner.INWARD) {
      _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
    } else if (corner == Corner.BOTH) {
      int startAngle = 0;
      if (_direction == MFEDirection.NE || _direction == MFEDirection.SE) {
        startAngle = 180;
      }

      int arc = 90;
      if (_direction == MFEDirection.NW || _direction == MFEDirection.SE) {
        arc *= -1;
      }

      if (_direction == MFEDirection.NW || _direction == MFEDirection.SW) {
        _x -=WALL_WIDTH;
      }
      if (_direction == MFEDirection.NW || _direction == MFEDirection.NE) {
        _y -=WALL_WIDTH;
      }

      _g.fillArc(_x, _y, WALL_WIDTH*2, WALL_WIDTH*2, startAngle, arc);
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
