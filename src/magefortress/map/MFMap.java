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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
import magefortress.core.MFLocation;
import magefortress.map.MFTile.Corner;

/**
 * Contains the tiles.
 */
public class MFMap
{

  public MFMap(int _width, int _height, int _depth)
  {
    this.width = _width;
    this.height = _height;
    this.depth = _depth;
    
    this.map = new MFTile[_depth][_width][_height];

    for (int z = 0; z < _depth; ++z) {
      for (int x = 0; x < _width; ++x) {
        for (int y = 0; y < _height; ++y) {
          this.map[z][x][y] = new MFTile(x,y,z);
        }
      }
    }
  }

  //---vvv---      STATIC METHODS      ---vvv---

  public static MFLocation convertToTilespace(int _x,int _y, int _z,
                                        int _x_translation, int _y_translation)
  {
    int tileX = (int) Math.floor((_x-_x_translation) / (double)MFTile.TILESIZE);
    int tileY = (int) Math.floor((_y-_y_translation) / (double)MFTile.TILESIZE);
    int tileZ = _z;

    return new MFLocation(tileX, tileY, tileZ);
  }

  public static Point convertFromTilespace(int _x,int _y)
  {
    int screenX = _x * MFTile.TILESIZE;
    int screenY = _y * MFTile.TILESIZE;

    return new Point(screenX, screenY);
  }

  public static MFMap createRandomMap(int _width, int _height, int _depth)
  {
    Random r = new Random();
    MFMap result = new MFMap(_width, _height, _depth);

    for (MFTile[][] level : result.map) {
      for (MFTile[] cols : level) {
        for (MFTile tile : cols) {
          tile.setDugOut(r.nextBoolean());
        }
      }
    }
    
    for (MFTile[][] level : result.map) {
      for (MFTile[] cols : level) {
        for (MFTile tile : cols) {
          if (tile.isDugOut()) {
            boolean wallN = r.nextBoolean();
            boolean wallE = r.nextBoolean();
            boolean wallS = r.nextBoolean();
            boolean wallW = r.nextBoolean();
            if (tile.getPosX() == 0) {
              wallW = true;
            } else if (tile.getPosX() == level.length - 1) {
              wallE = true;
            }
            if (tile.getPosY() == 0) {
              wallN = true;
            } else if (tile.getPosY() == cols.length - 1) {
              wallS = true;
            }
            MFTile neighborN = level[tile.getPosX()][Math.max(tile.getPosY() - 1, 0)];
            MFTile neighborE = level[Math.min(tile.getPosX() + 1, 29)][tile.getPosY()];
            MFTile neighborS = level[tile.getPosX()][Math.min(tile.getPosY() + 1, 29)];
            MFTile neighborW = level[Math.max(tile.getPosX() - 1, 0)][tile.getPosY()];
            if (neighborN.isDugOut()) {
              neighborN.setWallSouth(wallN);
            } else {
              wallN = true;
            }
            if (neighborE.isDugOut()) {
              neighborE.setWallWest(wallE);
            } else {
              wallE = true;
            }
            if (neighborS.isDugOut()) {
              neighborS.setWallNorth(wallS);
            } else {
              wallS = true;
            }
            if (neighborW.isDugOut()) {
              neighborW.setWallEast(wallW);
            } else {
              wallW = true;
            }
            tile.setWalls(wallN, wallE, wallS, wallW);
          }
        }
      }
    }
    
    for (MFTile[][] level : result.map) {
      for (MFTile[] cols : level) {
        for (MFTile tile : cols) {
          if (tile.isDugOut()) {
            result.calculateCorners(tile);
          }
        }
      }
    }

    return result;
  }

  //---vvv---      PUBLIC METHODS      ---vvv---

  /**
   * The width of a level
   * @return The width of a level
   */
  public int getWidth()
  {
    return this.width;
  }

  /**
   * The height of a level
   * @return The height of a level
   */
  public int getHeight()
  {
    return this.height;
  }

  /**
   * The number of levels
   * @return The number of levels
   */
  public int getDepth()
  {
    return this.depth;
  }

  /**
   * Gets a tile of this map.
   * @param x x position
   * @param y y position
   * @param z z position
   * @return The tile
   * @throws IndexOutOfBoundsException If the given coordinates are not on the map
   */
  public MFTile getTile(int x, int y, int z)
  {
    MFTile result = null;
    try {
      result = this.map[z][x][y];
    } catch (IndexOutOfBoundsException e) {
      logger.severe("Coordinates have to be inside the map: " +
                                x + "/" + y + "/" + z);
      throw e;
    }
    return result;
  }

  /**
   * Gets a tile of this map. Overloaded {@link getTile(int, int, int)}.
   * @param _location the location of the tile
   * @return the tile
   */
  public MFTile getTile(MFLocation _location)
  {
    return this.getTile(_location.x, _location.y, _location.z);
  }

  /**
   * Updates all animated sprites.
   */
  public void update()
  {
  }

  /**
   * Paints the map.
   * @param _g The canvas
   * @param _level The currently visible level of the map
   * @param _clippingRect The clipping rectangle.
   */
  public void paint(Graphics2D _g, int _level, Rectangle _clippingRect)
  {
    // select which tiles are visible
    // start of visible tiles
    MFLocation start = convertToTilespace(0, 0, _level,
                                          _clippingRect.x, _clippingRect.y);
    // end of visible tiles
    MFLocation end = convertToTilespace(_clippingRect.width-1, _clippingRect.height-1,
                                      _level, _clippingRect.x, _clippingRect.y);

    // only necessary if we don't stop scrolling at the edges of the map
    int startX = Math.max(start.x, 0);
    int startY = Math.max(start.y, 0);
    int endX = Math.min(end.x, this.width-1);
    int endY = Math.min(end.y, this.height-1);

    // render all visible tiles
    for (int y = startY; y <= endY; ++y) {
      for (int x = startX; x <= endX; ++x) {
        map[_level][x][y].paint(_g, _clippingRect.x, _clippingRect.y);
      }
    }
  }

  /**
   * Digs out the tile at the specified location. Currently all walls to
   * neighboring dug-out tiles will be removed.
   * @param _location the location of not yet dug-out tile
   * @throws IllegalArgumentException if the specified tile is not solid rock
   */
  public void digOut(MFLocation _location)
  {
    // error if location is null
    if (_location == null) {
      String msg = "Map: Location to dig must not be null.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    // error if location isn't inside the map bounds
    if (0 > _location.z || _location.z >= this.depth ||
        0 > _location.x || _location.x >= this.width ||
        0 > _location.y || _location.y >= this.height) {
      String msg = "Map@" + _location + ": Illegal location to dig.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    final MFTile tile = this.map[_location.z][_location.x][_location.y];

    // error if tile is not underground
    if (!tile.isUnderground()) {
      String msg = "Map@" + _location + ": Tile to dig out has to be underground.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    // error if the tile is already clear
    if (tile.isDugOut()) {
      String msg = "Map@" + _location + ": Tile to dig out must not be dug out";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    // dig out
    tile.setDugOut(true);
    // place walls if neighbor isn't dug out else remove wall of neighbor
    MFTile neighborN = this.getNeighbor(tile, MFEDirection.N);
    MFTile neighborNE = this.getNeighbor(tile, MFEDirection.NE);
    MFTile neighborE = this.getNeighbor(tile, MFEDirection.E);
    MFTile neighborSE = this.getNeighbor(tile, MFEDirection.SE);
    MFTile neighborS = this.getNeighbor(tile, MFEDirection.S);
    MFTile neighborSW = this.getNeighbor(tile, MFEDirection.SW);
    MFTile neighborW = this.getNeighbor(tile, MFEDirection.W);
    MFTile neighborNW = this.getNeighbor(tile, MFEDirection.NW);
    boolean wallN = neighborN == null || !neighborN.isUnderground() || !neighborN.isDugOut();
    boolean wallE = neighborE == null || !neighborE.isUnderground() || !neighborE.isDugOut();
    boolean wallS = neighborS == null || !neighborS.isUnderground() || !neighborS.isDugOut();
    boolean wallW = neighborW == null || !neighborW.isUnderground() || !neighborW.isDugOut();
    tile.setWalls(wallN, wallE, wallS, wallW);
    if (!wallN){
      neighborN.setWallSouth(false);
      this.calculateCorners(neighborN);
    }
    if (!wallE){
      neighborE.setWallWest(false);
      this.calculateCorners(neighborE);
    }
    if (!wallS){
      neighborS.setWallNorth(false);
      this.calculateCorners(neighborS);
    }
    if (!wallW){
      neighborW.setWallEast(false);
      this.calculateCorners(neighborW);
    }

    // remove center-facing corners of diagonal neighbors if all walls
    // have been removed
    if (!wallN && !wallE && neighborNE.isDugOut() &&
        !neighborNE.hasWallWest() && !neighborNE.hasWallSouth()) {
      this.calculateCorners(neighborNE);
    }
    if (!wallS && !wallE && neighborSE.isDugOut() &&
        !neighborSE.hasWallWest() && !neighborSE.hasWallNorth()) {
      this.calculateCorners(neighborSE);
    }
    if (!wallS && !wallW && neighborSW.isDugOut() &&
        !neighborSW.hasWallEast() && !neighborSW.hasWallNorth()) {
      this.calculateCorners(neighborSW);
    }
    if (!wallN && !wallW && neighborNW.isDugOut() &&
        !neighborNW.hasWallEast() && !neighborNW.hasWallSouth()) {
      this.calculateCorners(neighborNW);
    }

    this.calculateCorners(tile);
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---

  /**
   * Gets the tiles of an entire level.
   * @return the tile map
   */
  MFTile[][] getLevelMap(int _depth)
  {
    return this.map[_depth];
  }

  /**
   * Gets the neighbor of a tile. Returns <code>null</code> if the neighbor
   * is not inside the map because the tile lies on an edge.
   * @param _tile The tile to look from
   * @param _direction Which neighboring tile to get
   * @return The neighboring tile or <code>null</code> if not on the map
   */
  MFTile getNeighbor(MFTile _tile, MFEDirection _direction)
  {
    if (_tile == null) {
      String msg = "Map: Tile must not be null.";
      logger.logp(Level.SEVERE, MFMap.class.getName(), "getNeighbor()", msg);
      throw new IllegalArgumentException(msg);
    }

    MFTile result = null;
    final int x = _tile.getPosX();
    final int y = _tile.getPosY();
    final int z = _tile.getPosZ();

    if (_direction == MFEDirection.N && y > 0) {
      result = this.map[z][x][y-1];
    } else if (_direction == MFEDirection.NE && x < this.width-1 && y > 0) {
      result = this.map[z][x+1][y-1];
    } else if (_direction == MFEDirection.E && x < this.width-1) {
      result = this.map[z][x+1][y];
    } else if (_direction == MFEDirection.SE && x < this.width-1 && y < this.height-1) {
      result = this.map[z][x+1][y+1];
    } else if (_direction == MFEDirection.S && y < this.height-1) {
      result = this.map[z][x][y+1];
    } else if (_direction == MFEDirection.SW && x > 0 && y < this.height-1) {
      result = this.map[z][x-1][y+1];
    } else if (_direction == MFEDirection.W && x > 0) {
      result = this.map[z][x-1][y];
    } else if (_direction == MFEDirection.NW && x > 0 && y > 0) {
      result = this.map[z][x-1][y-1];
    }
    return result;
  }

  /**
   * Calculates the types of corner.
   * <p>Horizontal and vertical wall on the tile -> inward-facing corner piece
   * </p><p>Only horizontal wall on the tile -> horizontal corner piece
   * </p><p>Only vertical wall on the tile -> vertical corner piece
   * </p><p>No adjacent walls on the tile
   * AND no adjacent walls on surrounding tiles -> no corner piece
   * </p><p>Otherwise -> outward-facing corner piece
   * </p>
   */
  void calculateCorners(MFTile _tile)
  {

    if (!_tile.isDugOut()) {
      logger.log(Level.WARNING, "Tile " + _tile.toString() +
                                ": Trying to calculate corners on solid tile");
      return;
    }

    MFTile neighborN = this.getNeighbor(_tile, MFEDirection.N);
    MFTile neighborE = this.getNeighbor(_tile, MFEDirection.E);
    MFTile neighborS = this.getNeighbor(_tile, MFEDirection.S);
    MFTile neighborW = this.getNeighbor(_tile, MFEDirection.W);

    boolean _wallN = _tile.getPosY() == 0 || _tile.hasWallNorth();
    boolean _wallE = _tile.getPosX() == this.width-1 || _tile.hasWallEast();
    boolean _wallS = _tile.getPosY() == this.height-1 || _tile.hasWallSouth();
    boolean _wallW = _tile.getPosX() == 0 || _tile.hasWallWest();

    boolean wallAboveW = neighborN == null ||
                          (neighborN.isDugOut() && neighborN.hasWallWest());
    boolean wallAboveE = neighborN == null ||
                          (neighborN.isDugOut() && neighborN.hasWallEast());
    boolean wallRightN = neighborE == null ||
                          (neighborE.isDugOut() && neighborE.hasWallNorth());
    boolean wallRightS = neighborE == null ||
                          (neighborE.isDugOut() && neighborE.hasWallSouth());
    boolean wallBelowE = neighborS == null ||
                          (neighborS.isDugOut() && neighborS.hasWallEast());
    boolean wallBelowW = neighborS == null ||
                          (neighborS.isDugOut() && neighborS.hasWallWest());
    boolean wallLeftS  = neighborW == null ||
                          (neighborW.isDugOut() && neighborW.hasWallSouth());
    boolean wallLeftN  = neighborW == null ||
                          (neighborW.isDugOut() && neighborW.hasWallNorth());

    this.calculateCornerType(_tile, MFEDirection.NW, _wallN, _wallW, wallLeftN, wallAboveW);
    this.calculateCornerType(_tile, MFEDirection.NE, _wallN, _wallE, wallAboveE, wallRightN);
    this.calculateCornerType(_tile, MFEDirection.SE, _wallS, _wallE, wallRightS, wallBelowE);
    this.calculateCornerType(_tile, MFEDirection.SW, _wallS, _wallW, wallLeftS, wallBelowW);
  }

  /**
   * Checks if there is a path from two adjacent tiles for a 1-tile sized,
   * walking creature. This is done by testing if the neighboring tile is
   * walkable for the giving clearance and capabilities and there are no
   * walls in the way. If the target tile is <code>null</code> it returns
   * <code>false</code>.
   * @param _start the start tile
   * @param _goal the end tile
   * @param _direction the direction as seen from the start tile
   * @param _clearance the size of the moving creature
   * @param _capabilities the movement types the creature can use
   * @return <code>true</code> if a creature can walk from start to goal
   * @throws IllegalArgumentException if the start tile is <code>null</code> or
   *        if the tiles are not adjacent or if the clearance is smaller one or
   *        if there are no capabilities given.
   */
  boolean canMoveTo(MFTile _start, MFTile _goal, MFEDirection _direction,
          int _clearance, EnumSet<MFEMovementType> _capabilities)
  {
    if (_start == null) {
      String msg = "Map: Cannot test reachability of target tile without start tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    if (_goal == null) {
      return false;
    }

    if (!_start.getLocation().isNeighborOf(_goal.getLocation())) {
      String msg = "Map: Cannot test reachability between non-adjacent tiles from " +
                    _start.getLocation() + " towards " + _direction;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    if (_clearance < 1) {
      String msg = "Map: Cannot test reachability for a clearance < 1 from " +
              _start.getLocation() + " towards " + _direction;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    if (_capabilities == null || _capabilities.size() == 0) {
      String msg = "Map: Cannot test reachability without knowing the capabilities " +
              _start.getLocation() + " towards " + _direction;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    boolean walled = false;
    if (MFEDirection.straight().contains(_direction)) {
      walled = _start.hasWall(_direction);
    } else if (MFEDirection.diagonals().contains(_direction)) {
      switch (_direction) {
        case NE: walled = _start.hasWallNorth() || _start.hasWallEast() ||
                          _goal.hasWallSouth() || _goal.hasWallWest();
                 break;
        case SE: walled = _start.hasWallSouth() || _start.hasWallEast() ||
                          _goal.hasWallNorth() || _goal.hasWallWest();
                 break;
        case SW: walled = _start.hasWallSouth() || _start.hasWallWest() ||
                          _goal.hasWallNorth() || _goal.hasWallEast();
                 break;
        case NW: walled = _start.hasWallNorth() || _start.hasWallWest() ||
                          _goal.hasWallSouth() || _goal.hasWallEast();
                 break;
      }
    }

    boolean walkable = false;

    // only need to check walkability if there are no walls
    if (!walled) {
      for (MFEMovementType movement : _capabilities) {
        if (_goal.getClearance(movement) >= _clearance && _goal.isWalkable(movement)) {
          walkable = true;
          break;
        }
      }
    }

    if (walled || !walkable) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Checks if there is a path from two adjacent tiles for a 1-tile sized, 
   * walking creature. This is done by testing if the neighboring tile is
   * walkable and there are no walls in the way. If the target tile is
   * <code>null</code> it returns <code>false</code>.
   * @param _start the start tile
   * @param _goal the end tile
   * @param _direction the direction as seen from the start tile
   * @return <code>true</code> if a creature can walk from start to goal
   * @throws IllegalArgumentException if the start tile is <code>null</code> or
   *        if the tiles are not adjacent.
   */
  boolean canWalkTo(MFTile _start, MFTile _goal, MFEDirection _direction)
  {
    return this.canMoveTo(_start, _goal, _direction, 1, EnumSet.of(MFEMovementType.WALK));
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Width of a level */
  private final int width;
  /** Height of a level */
  private final int height;
  /** Number of levels */
  private final int depth;
  /** The map. The array has the following order: z -> x -> y */
  private MFTile[][][] map;
  /** The logger */
  private static final Logger logger = Logger.getLogger(MFMap.class.getName());

  /**
   * Calculates the type of specified corner.
   * @param _direction
   * @param _horizontalWall
   * @param _verticalWall
   * @param _adjacentLeft
   * @param _adjacentRight
   */
  private void calculateCornerType(MFTile _tile,MFEDirection _direction, boolean _horizontalWall, boolean _verticalWall, boolean _adjacentLeft, boolean _adjacentRight)
  {
    Corner corner = Corner.NONE;
    if (_horizontalWall && _verticalWall) {
      corner = Corner.INWARD;
    } else if (_horizontalWall) {
      corner = Corner.HORIZONTAL;
    } else if (_verticalWall) {
      corner = Corner.VERTICAL;
    } else if (_adjacentLeft || _adjacentRight) {
      corner = Corner.BOTH;
    }
    _tile.setCorner(_direction, corner);
  }

}
