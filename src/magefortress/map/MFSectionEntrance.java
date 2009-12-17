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

  public MFSectionEntrance(MFLocation _location)
  {
    if (_location == null) {
      String msg = "SectionEntrance: Cannot create entrance without a location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.location = _location;
    this.edges = new LinkedList<MFEdge>();
  }

  /**
   * Adds a connection to another entrance.
   * @param _edge The connecting edge
   */
  public void addEdge(MFEdge _edge)
  {
    if (_edge == null) {
      String msg = "SectionEntrance " + this.getLocation() + ": Cannot add null edge";
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
   * Gets the location of the tile this entrance is positioned on.
   * @return The location of the entrance tile
   */
  public MFLocation getLocation()
  {
    return this.location;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final static Logger logger = Logger.getLogger(MFSectionEntrance.class.getName());
  private final MFLocation location;
  private final List<MFEdge> edges;
}
