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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import magefortress.core.MFLocation;

/**
 * Represents a section of dug out, undergound tiles like a room or a tunnel.
 */
class MFSection
{
  /**
   * Constructor
   * @param _map the map this section is on
   * @param _depth the level on which this section lies
   */
  public MFSection(MFMap _map, int _depth)
  {
    this.map = _map;
    this.level = _depth;
    this.entrances = new HashSet<MFSectionEntrance>();
    this.tiles = new HashMap<MFLocation, MFTile>();
  }
  
  public int getLevel()
  {
    return this.level;
  }

  public int getSize()
  {
    return this.tiles.size();
  }

  public Set<MFSectionEntrance> getEntrances()
  {
    return Collections.unmodifiableSet(this.entrances);
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---

  /**
   * Adds an entrance that leads to this section.
   * @param _entrance the entrance to this section
   */
  void addEntrance(MFSectionEntrance _entrance)
  {
    if (_entrance == null) {
      String msg = "Section: Cannot add null entrance.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.entrances.contains(_entrance)) {
      String msg = "Section: Trying to add an entrance (" +  _entrance.getLocation() +
                   ") already present in this section.";
      logger.warning(msg);
      return;
    }
    this.entrances.add(_entrance);
  }

  /**
   * Adds a tile to this section. Its parent section will be set to this section.
   * @param _tile A tile of this section
   */
  void addTile(MFTile _tile)
  {
    if (_tile == null) {
      String msg = "Section: Cannot add null tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (this.tiles.containsKey(_tile.getLocation())) {
      String msg = "Section: Trying to add a tile (" +  _tile.getLocation() +
                   ") already present in this section.";
      logger.warning(msg);
      return;
    }
    _tile.setParentSection(this);
    this.tiles.put(_tile.getLocation(), _tile);
  }

  /**
   * Creates a union of two sections. The smaller one will be added to the bigger
   * one.
   * @param _other the other section
   * @return the union of the two sections
   * @throws IllegalArgumentException if the sections refer to the same one
   */
  MFSection uniteWith(MFSection _other)
  {
    if (_other == null) {
      String msg = "Section: Cannot unite sections without another one.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_other == this) {
      String msg = "Section: Cannot unite two sections if they are the same.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    if (_other.getSize() > this.getSize()) {
      _other.appendSection(this);
      return _other;
    } else {
      this.appendSection(_other);
      return this;
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final static Logger logger = Logger.getLogger(MFSection.class.getName());
  private final MFMap map;
  private final int level;
  private final Set<MFSectionEntrance> entrances;
  private final Map<MFLocation, MFTile> tiles;

  /**
   * Moves the tiles and entrances of the source to the target section.
   * @param _other the source section
   */
  private void appendSection(MFSection _other)
  {
    for (MFTile tile : _other.tiles.values()) {
      this.addTile(tile);
    }
    for (MFSectionEntrance entrance : _other.entrances) {
      this.addEntrance(entrance);
    }
    // empty lists
    _other.tiles.clear();
    _other.entrances.clear();
  }
}
