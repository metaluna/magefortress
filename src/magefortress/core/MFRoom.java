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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for all player-defined rooms. A room consists of a variable-shaped
 * number of connected tiles. At its base a room has only one attribute: living
 * value. It is the sum of the living values of the surrounding walls, the floor,
 * and the furniture placed in the room. Sub-classes may introduce new
 * attributes calculated in a similar manner.
 */
public abstract class MFRoom implements MFITileConstructionsListener
{
  public MFRoom(Set<MFTile> _tiles)
  {
    if (_tiles == null || _tiles.size() == 0) {
      String msg = "Room: Cannot create room without tiles.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    this.livingValue = 0;
    this.roomTiles = new LinkedHashSet<MFTile>(_tiles.size());
    this.addTiles(_tiles);
  }

  /**
   * Gets the living value
   * @return The living value
   */
  final public int getLivingValue()
  {
    return this.livingValue;
  }

  /**
   * Gets the room's tiles. The returned <code>Set</code> is unmodifiable.
   * @return The room tiles as an unmodifiable set
   * @see Collections unmodifiableSet()
   */
  final public Set<MFTile> getTiles()
  {
    return Collections.unmodifiableSet(this.roomTiles);
  }

  /**
   * The number of tiles belonging to the room.
   * @return The number of tiles
   */
  final public int getSize()
  {
    return this.roomTiles.size();
  }

  /**
   * Used to add a tile to the room. The list may contain tiles already in the
   * room. Sub-classes will only be notified of the addition of truly new
   * tiles.
   * @param _tiles The new tiles
   */
  final public void addTiles(Set<MFTile> _tiles)
  {
    if (_tiles == null || _tiles.size() == 0) {
      String msg = "Room: Cannot add zero tiles.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    for (MFTile tile : _tiles) {
      boolean notDuplicate = this.roomTiles.add(tile);
      if (notDuplicate) {
        // notify sub-classes
        this.tileAdded(tile);
        // update living value
        this.calculateLivingValue();
        // let us be notified of changes
        this.subscribeToTileChanges(tile);
      }
    }
  }

  /**
   * Used to remove a tile from the room. The list may contain tiles not in the
   * room. Sub-classes will only be notified of the removal of truly removed
   * tiles.
   * @param _tiles The removed tiles
   */
  final public void removeTiles(Set<MFTile> _tiles)
  {
    if (_tiles == null || _tiles.size() == 0) {
      String msg = "Room: Cannot remove zero tiles.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    for (MFTile tile : _tiles) {
      boolean removed = this.roomTiles.remove(tile);
      if (removed) {
        // notify sub-classes
        this.tileRemoved(tile);
        // update living value
        this.calculateLivingValue();
        // don't notify us of changes
        this.unsubscribeToTileChanges(tile);
      }
    }
  }

  /**
   * Callback used by a tile of the room when an object was placed or removed
   * or a wall was polished. It will trigger a re-calculation of all attribute
   * values of all tiles. Expensive but ok, because this won't happen often.
   * <p>
   * Calling this will trigger a call to {@link tileUpdated()} in the sub-classes.
   * </p>
   */
  final public void tileObjectsChanged()
  {
    // notify sub-classes
    this.tileUpdated();
    this.calculateLivingValue();
  }

  final public void tileConstructionsChanged(MFTile _tile)
  {
    // notify sub-classes
    this.tileUpdated();
    this.calculateLivingValue();
  }

  /**
   * Called when a tile was added to the room. Can be used to re-calculate the
   * attribute values of the sub-classed room.
   * @param _tile The new tile
   */
  abstract void tileAdded(MFTile _tile);
  /**
   * Called when a tile was removed from the room. Can be used to re-calculate the
   * attribute values of the sub-classed room.
   * @param _tile The removed tile
   */
  abstract void tileRemoved(MFTile _tile);
  /**
   * Called when the contents of a tile of the room changed or a wall/floor was
   * built or removed. Should be followed by a re-calculation of all values
   * of all tiles in the room.
   */
  abstract void tileUpdated();
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private int livingValue;
  private Set<MFTile> roomTiles;
  private static final Logger logger = Logger.getLogger(MFRoom.class.getName());

  /**
   * Calculates the living value by iterating over all tiles.
   */
  private void calculateLivingValue()
  {
    this.livingValue = 0;
    for (MFTile tile : this.roomTiles) {
      MFIPlaceable item = tile.getObject();
      // only add living value if there's an item on the tile
      if (item != null) {
        this.livingValue += tile.getObject().getLivingValue();
      }
    }

  }

  /**
   * Starts listening to changes to the specified tile.
   * @param _tile The tile to subscribe to
   */
  private void subscribeToTileChanges(MFTile _tile)
  {
    _tile.setRoom(this);
    _tile.subscribeConstructionsListener(this);
  }

  /**
   * Stops listening to changes to the specified tile
   * @param _tile The tile to unsubscribe from
   */
  private void unsubscribeToTileChanges(MFTile _tile)
  {
    _tile.setRoom(null);
    _tile.unsubscribeConstructionsListener(this);
  }
}
