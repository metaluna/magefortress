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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
import magefortress.core.MFLocation;

/**
 * Stores all found entrances and sections.
 */
public class MFNavigationMap
{

  /**
   * Constructor
   * @param _map the underlying real map
   */
  public MFNavigationMap(MFMap _map)
  {
    this.map = _map;
    this.entrances = new LinkedList<MFSectionEntrance>();
    this.sections = new LinkedList<MFSection>();
  }

  /**
   * Clears the list of entrances and re-calculates them for all depth levels
   * of the map.
   */
  public void updateAllEntrances()
  {
    this.entrances.clear();
    for (int depth = 0; depth < this.map.getDepth(); ++depth) {
      updateEntrances(depth);
    }
  }

  /**
   * Removes all entrances of the depth level and re-calculates them.
   * @param _depth the depth level
   */
  public void updateEntrances(int _depth)
  {
    // remove all entrances of the specified level before adding new ones
    Iterator<MFSectionEntrance> it = this.entrances.iterator();
    while(it.hasNext()) {
      MFSectionEntrance entrance = it.next();
      if (entrance.getLocation().z == _depth) {
        it.remove();
      }
    }
    // find and store the entrances
    this.entrances.addAll(findEntrances(this.map.getLevelMap(_depth)));
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---
  /**
   * Gets the list of entrances on the map. May return a zero-sized list if
   * there has not been an update triggered to calculate the entrances or there
   * are no detectable entrances on the map yet.
   * @return the unmodifiable list of entrances
   */
  List<MFSectionEntrance> getEntrances()
  {
    return Collections.unmodifiableList(this.entrances);
  }

  /**
   * Gets the list of sections on the map. May return a zero-sized list if
   * there has not been an update triggered or there are no sections on the map
   * yet.
   * @return the unmodifiable list of sections
   */
  List<MFSection> getSections()
  {
    return Collections.unmodifiableList(this.sections);
  }

  /**
   * Calculates all clearance values and stores them inside the tiles.
   * @param _movementType The type of movement
   */
  void updateClearanceValues(MFEMovementType _movementType)
  {
    for (int z = 0; z < this.map.getDepth(); ++z) {
      for (MFTile[] rows : this.map.getLevelMap(z)) {
        for (MFTile tile : rows) {
          int clearance = calculateClearance(tile, _movementType, 1);
          tile.setClearance(_movementType, clearance);
        }
      }
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFNavigationMap.class.getName());
  private final MFMap map;
  private final List<MFSectionEntrance> entrances;
  private final List<MFSection> sections;

  /**
   * Calculates the clearance for one tile recursively
   * @param _tile the tile to update
   * @param _movementType the capability of the creature
   * @param _clearance the size of the creature
   * @return the highest possible clearance for this tile
   */
  private int calculateClearance(final MFTile _tile, 
                      final MFEMovementType _movementType, final int _clearance)
  {
    // abort if clearance is the size of the map
    if (_clearance > this.map.getWidth() - _tile.getPosX() ||
        _clearance > this.map.getHeight() - _tile.getPosY()) {
      return _clearance-1;
    }

    if (_clearance == 1) {
      if (_tile.isWalkable(_movementType))
        return calculateClearance(_tile, _movementType, _clearance+1);
      else
        return 0;
    }

    // test new row of tiles below the current one
    final int startx = _tile.getPosX();
    final int endx   = startx + _clearance;
    int y = _tile.getPosY() + _clearance - 1;
    for (int x = startx; x < endx; ++x) {
      MFTile neighbor = this.map.getTile(x, y, _tile.getPosZ());
      if (!neighbor.isWalkable(_movementType) ||
           neighbor.hasWallNorth()) {
        return _clearance - 1;
      }
    }
    // test new column of tiles
    final int starty = _tile.getPosY();
    final int endy   = starty + _clearance;
    int x = _tile.getPosX() + _clearance - 1;
    for (y = starty; y < endy; ++y) {
      MFTile neighbor = this.map.getTile(x, y, _tile.getPosZ());
      if (!neighbor.isWalkable(_movementType) ||
           neighbor.hasWallWest()) {
        return _clearance - 1;
      }
    }

    return calculateClearance(_tile, _movementType, _clearance+1);
  }

  /**
   * Scans the map and detects all tiles which might define an entrance. For
   * navigational use.
   * @return the list of entrances found
   */
  private List<MFSectionEntrance> findEntrances(final MFTile[][] _tiles)
  {
    final HashMap<MFLocation, MFSectionEntrance> potentialEntrances =
                                  new HashMap<MFLocation, MFSectionEntrance>();

    for (final MFTile[] rows : _tiles) {
      for (final MFTile tile : rows) {
        // skip if tile is not underground or not dug out or without a floor
        if (!tile.isUnderground() || !tile.isDugOut() || !tile.hasFloor()) {
          continue;
        }
        // skip if clearance > 1
        //if (tile.getClearance(MFEMovementType.WALK) > 1){
        //  continue;
        //}
        // skip if a neighbor is an entrance
        if (hasNeighboringEntrance(tile, potentialEntrances)) {
          continue;
        }
        // skip this tile if it doesn't divide two groups
        if (!dividesGroups(tile)) {
          continue;
        }
        // all tests passed - entrance found!
        potentialEntrances.put(tile.getLocation(), new MFSectionEntrance(tile.getLocation()));
      }
    }
    List<MFSectionEntrance> result = this.collapseCloseEntrances(potentialEntrances);
    return result;
  }

  /**
   * Checks if a neighbor is already defined as an entrance.
   * @param _tile the tile to check
   * @param _entrances the list of entrances
   * @return <code>true</true> if a neighbor is an entrance
   */
  private boolean hasNeighboringEntrance(final MFTile _tile,
                        final HashMap<MFLocation, MFSectionEntrance> _entrances)
  {
    for (MFEDirection direction : MFEDirection.values()) {
      MFTile neighbor = this.map.getNeighbor(_tile, direction);
      if (neighbor != null && _entrances.containsKey(neighbor.getLocation())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a tile divides at least two groups of tiles, one consisting of
   * at least 2 tiles.
   * @param _tile the tile to check
   * @return <code>true</true> if the tile divides multiple groups
   */
  private boolean dividesGroups(final MFTile _tile)
  {
    int biggestGroupSize = 0;
    int currentGroupSize = 0;
    int blockedStretchesCount = 0;
    boolean blockedStretch = false;
    boolean blockedN = false;
    boolean blockedNW = false;

    for (MFEDirection direction : MFEDirection.values()) {
      // get the neighboring tile
      MFTile neighbor = this.map.getNeighbor(_tile, direction);
      // walls?
      boolean walled = false;
      if (MFEDirection.straight().contains(direction)) {
        walled = _tile.hasWall(direction);
      } else if (MFEDirection.diagonals().contains(direction)) {
        switch (direction) {
          case NE: walled = _tile.hasWallNorth() || _tile.hasWallEast() ||
                            neighbor.hasWallSouth() || neighbor.hasWallWest();
                   break;
          case SE: walled = _tile.hasWallSouth() || _tile.hasWallEast() ||
                            neighbor.hasWallNorth() || neighbor.hasWallWest();
                   break;
          case SW: walled = _tile.hasWallSouth() || _tile.hasWallWest() ||
                            neighbor.hasWallNorth() || neighbor.hasWallEast();
                   break;
          case NW: walled = _tile.hasWallNorth() || _tile.hasWallWest() ||
                            neighbor.hasWallSouth() || neighbor.hasWallEast();
                   break;
        }
      }
      // unreachable or irrelevant or unwalkable tile
      if (walled || neighbor == null || !neighbor.isUnderground() ||
                   !neighbor.isWalkable(MFEMovementType.WALK)) {
        // switch to empty stretch
        if (blockedStretch == false) {
          if (currentGroupSize > biggestGroupSize) {
            biggestGroupSize = currentGroupSize;
          }
          currentGroupSize = 0;
          ++blockedStretchesCount;
          blockedStretch = true;
        }
      // neighbor is walkable and reachable
      } else if (neighbor.isWalkable(MFEMovementType.WALK)) {
        ++currentGroupSize;
        blockedStretch = false;
      } else {
        String msg = "Map: error calculating entrance for " + _tile + "/neighbor: " + neighbor;
        logger.warning(msg);
      }

      // save for wrap test
      if (direction == MFEDirection.N) {
        blockedN = blockedStretch;
      } else if (direction == MFEDirection.NW) {
        blockedNW = blockedStretch;
      }
    }
    // final check for biggest group
    if (currentGroupSize > biggestGroupSize) {
      biggestGroupSize = currentGroupSize;
    }
    // wrap empty stretches blocks
    if (blockedN && blockedNW) {
      --blockedStretchesCount;
    }

    if (blockedStretchesCount < 2 || biggestGroupSize < 2) {
      return false;
    }
    return true;
  }

  /**
   * Moves all entrances lying two tiles apart into one if possible.
   * @param _entrances list of entrances
   * @return collapsed list of entrances
   */
  private List<MFSectionEntrance> collapseCloseEntrances(
                        final HashMap<MFLocation, MFSectionEntrance> _entrances)
  {
    List<MFSectionEntrance> result = new LinkedList<MFSectionEntrance>();
    List<MFSectionEntrance> removedEntrances = new LinkedList<MFSectionEntrance>();

    Iterator<MFSectionEntrance> it1 = _entrances.values().iterator();
    while (it1.hasNext()) {

      MFSectionEntrance start = it1.next();
      if (removedEntrances.contains(start))
        continue;

      MFSectionEntrance add = start;
      for (MFSectionEntrance goal : _entrances.values()) {

        MFLocation startLoc = start.getLocation();
        MFLocation goalLoc = goal.getLocation();
        // found potentially collapsable locations
        if (startLoc.distanceTo(goalLoc) == 2) {

          MFEDirection dir = startLoc.directionOf(goalLoc);
          MFTile middle = this.map.getNeighbor(this.map.getTile(startLoc), dir);
          // check if tile can be connected
          if (middle.isWalkable(MFEMovementType.WALK) && middle.isUnderground()) {
            add = new MFSectionEntrance(middle.getLocation());
            removedEntrances.add(goal);
            break;

          } //-- connectable
        } //-- potentially collapsable
      } //-- inner loop

      result.add(add);
      if (start != add) {
        it1.remove();
      }
    } //-- outer loop
    return result;
  }

}
