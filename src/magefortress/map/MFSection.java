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

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import magefortress.core.MFLocation;

/**
 * Represents a section of dug out, undergound tiles like a room or a tunnel.
 */
class MFSection
{
  public MFSection(MFMap _map)
  {
    this.map = _map;
    this.entrances = new LinkedList<MFSectionEntrance>();
    this.tiles = new HashMap<MFLocation, MFTile>();
  }
  
  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---

  /**
   * Adds an entrance that leads to this section.
   * @param _entrance the entrance to this section
   */
  public void addEntrance(MFSectionEntrance _entrance)
  {
    if (_entrance == null) {
      String msg = "Section: Cannot add null entrance.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.entrances.add(_entrance);
  }

  /**
   * Adds a tile to this section. Its parent section will be set to this section.
   * @param _tile A tile of this section
   */
  public void addTile(MFTile _tile)
  {
    if (_tile == null) {
      String msg = "Section: Cannot add null tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    _tile.setParentSection(this);
    this.tiles.put(_tile.getLocation(), _tile);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final static Logger logger = Logger.getLogger(MFSection.class.getName());
  private final MFMap map;
  private final List<MFSectionEntrance> entrances;
  private final Map<MFLocation, MFTile> tiles;
}
