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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import magefortress.core.MFLocation;

/**
 * Stores the tile which was detected as the entrance to a 
 * {@link MFSection} and the {@link MFEdge}s connecting it to other
 * entrances.
 */
class MFSectionEntrance
{

  public MFSectionEntrance(MFTile _tile)
  {
    if (_tile == null) {
      String msg = "SectionEntrance: Cannot create entrance without a tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.tile = _tile;
    _tile.setEntrance(this);
    this.edges = new LinkedList<MFEdge>();
  }

  /**
   * Adds a connection to another entrance.
   * @param _edge The connecting edge
   */
  public void addEdge(MFEdge _edge)
  {
    if (_edge == null) {
      String msg = "SectionEntrance " + this.getLocation() + ": Cannot add null edge.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);      
    }
    if (_edge.getFrom() != this) {
      String msg = "SectionEntrance " + this.getLocation() + ": Cannot add an edge " +
                    "whose origin is not this entrance. " +
                    "Got: " + _edge.getFrom().getLocation();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.edges.add(_edge);
  }

  /**
   * Removes a connection to another entrance.
   * @param _edge the edge to remove
   */
  public void removeEdge(MFEdge _edge)
  {
    if (_edge == null) {
      String msg = "SectionEntrance " + this.getLocation() + ": Cannot remove null edge.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_edge.getFrom() != this) {
      String msg = "SectionEntrance " + this.getLocation() + ": Cannot remove an edge " +
                    "whose target is not this entrance. " +
                    "Got: " + _edge.getTo().getLocation();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.edges.remove(_edge);
  }

  public MFTile getTile()
  {
    return this.tile;
  }

  /**
   * Gets an unmodifiable copy of the list of the edges that connect this
   * entrance to others. Returns a list with no elements if this entrance
   * has no connections.
   * @return The unmodifiable list of edges
   * @see Collections#unmodifiableList(java.util.List)
   */
  public List<MFEdge> getEdges()
  {
    return Collections.unmodifiableList(edges);
  }

  /**
   * Gets the edge which points towards the specified target entrance. Returns
   * <code>null</code> if no edge was found.
   * @param _to the target entrance
   * @return the edge pointing towards the target or <code>null</code> if no edge
   * like that exists.
   */
  public MFEdge getEdge(MFSectionEntrance _to)
  {
    for (MFEdge edge : this.edges) {
      if (edge.getTo() == _to) {
        return edge;
      }
    }

    return null;
  }

  /**
   * Gets the location of the tile this entrance is positioned on.
   * @return The location of the entrance tile
   */
  public MFLocation getLocation()
  {
    return this.tile.getLocation();
  }

  @Override
  public String toString()
  {
    return this.tile.getLocation().toString();
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final static Logger logger = Logger.getLogger(MFSectionEntrance.class.getName());
  private final MFTile tile;
  private final List<MFEdge> edges;
}
