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

import magefortress.items.placeable.MFIPlaceable;
import magefortress.map.MFTile;
import magefortress.map.MFITileConstructionsListener;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.channel.MFIChannelSender;
import magefortress.items.MFBlueprint;
import magefortress.jobs.MFJobSlot;

/**
 * Base class for all player-defined rooms. A room consists of a variable-shaped
 * number of connected tiles. At its base a room has only one attribute: living
 * value. It is the sum of the living values of the surrounding walls, the floor,
 * and the furniture placed in the room. Sub-classes may introduce new
 * attributes calculated in a similar manner.
 */
public abstract class MFRoom implements MFITileConstructionsListener, MFIChannelSender
{
  public MFRoom(String _name, Set<MFTile> _tiles)
  {
    if (_tiles == null || _tiles.size() == 0) {
      String msg = "Room: Cannot create room without tiles.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }

    this.name = _name;
    this.livingValue = 0;
    this.products = new LinkedList<MFBlueprint>();
    this.roomTiles = new LinkedHashSet<MFTile>(_tiles.size());
    this.jobSlots = new LinkedList<MFJobSlot>();
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
   * The name of the type of room
   * @return The name of the type of room
   */
  final public String getName()
  {
    return this.name;
  }

  /**
   * Adds a number of tiles to the room. The list may contain tiles already in the
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
   * Removes a number of tiles from the room. The list may contain tiles not in the
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
   * Adds the blueprint of product to the list of producible items in this room.
   * If the blueprint is already present, this will fail silently.
   * @param _blueprint The blueprint of the product to add
   */
  public void addProduct(MFBlueprint _blueprint)
  {
    if (!this.products.contains(_blueprint)) {
      this.products.add(_blueprint);
    } else {
      logger.fine(this.getClass().getSimpleName() + " '" + this.name +
              "': Trying to add a product already stored in this room.");
    }
  }

  /**
   * Removes the blueprint of a product from the list of producible items
   * in this room. If the blueprint is not present, this will fail silently.
   * @param _blueprint The blueprint of the product to remove
   */
  public void removeProduct(MFBlueprint _blueprint)
  {
    if (this.products.contains(_blueprint)) {
      this.products.remove(_blueprint);
    } else {
      logger.fine(this.getClass().getSimpleName() + " '" + this.name +
              "': Trying to remove a product not stored in this room.");
    }
  }

  /**
   * Returns a list of products that can be produced in this room.
   * @return An unmodifiable list of products
   */
  public List<MFBlueprint> getProducts()
  {
    return Collections.unmodifiableList(this.products);
  }

  /**
   * Returns a list of slots where creatures can work.
   * @return An unmodifiable list of job slots
   */
  public List<MFJobSlot> getJobSlots()
  {
    return Collections.unmodifiableList(this.jobSlots);
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

  @Override
  final public void tileConstructionsChanged(MFTile _tile)
  {
    // notify sub-classes
    this.tileUpdated();
    this.calculateLivingValue();
  }

  //---vvv---        PROTECTED METHODS       ---vvv---
  protected void addJobSlot(MFJobSlot _slot)
  {
    this.jobSlots.add(_slot);
  }

  protected void removeJobSlot(MFJobSlot _slot)
  {
    this.jobSlots.remove(_slot);
  }

  /**
   * Called when a tile was added to the room. Can be used to re-calculate the
   * attribute values of the sub-classed room.
   * @param _tile The new tile
   */
  protected abstract void tileAdded(MFTile _tile);
  /**
   * Called when a tile was removed from the room. Can be used to re-calculate the
   * attribute values of the sub-classed room.
   * @param _tile The removed tile
   */
  protected abstract void tileRemoved(MFTile _tile);
  /**
   * Called when the contents of a tile of the room changed or a wall/floor was
   * built or removed. Should be followed by a re-calculation of all values
   * of all tiles in the room.
   */
  protected abstract void tileUpdated();
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private int livingValue;
  private Set<MFTile> roomTiles;
  private final String name;
  private final List<MFBlueprint> products;
  private final List<MFJobSlot> jobSlots;
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
