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

import java.util.logging.Logger;
import magefortress.core.Immutable;
import magefortress.creatures.behavior.movable.MFCapability;

/**
 * Stores the costs to traverse the distance between two {@link MFSectionEntrance nodes} for a
 * given capability and clearance. It is immutable.
 */
class MFEdge implements Immutable
{
  /**
   * Constructor
   * @param _from the starting node
   * @param _to the end node
   * @param _cost the distance
   * @param _clearance the biggest creature that can traverse this edge
   * @param _capability the capabilites which are needed to traverse this edge
   */
  public MFEdge(MFSectionEntrance _from, MFSectionEntrance _to, int _cost, 
                                      int _clearance, MFCapability _capability)
  {
    if (_from == null) {
      String msg = "Edge: Cannot create edge without starting node.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_to == null) {
      String msg = "Edge: Cannot create edge without end node. Start node: " +
                   _from.getLocation();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_from == _to) {
      String msg = "Edge: Cannot create edge with starting node equaling end " +
              "node. Got: " + _from.getLocation();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_cost <= 0) {
      String msg = "Edge (" + _from.getLocation() + "->" + _to.getLocation() +
                   ": Cannot create edge with cost <= 0. Got: " + _cost;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_clearance <= 0) {
      String msg = "Edge (" + _from.getLocation() + "->" + _to.getLocation() +
                   ": Cannot create edge with clearance <= 0. Got: " + _clearance;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_capability == null || _capability == MFCapability.NONE) {
      String msg = "Edge (" + _from.getLocation() + "->" + _to.getLocation() + 
                   ": Cannot create edge without at least one capability.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.from = _from;
    this.to = _to;
    this.cost = _cost;
    this.clearance = _clearance;
    this.capability = _capability;
  }

  /**
   * Gets the entrance this edge starts from.
   * @return the entrance this edge starts from
   */
  public MFSectionEntrance getFrom()
  {
    return this.from;
  }

  /**
   * Gets the entrance this edge is leading to.
   * @return the entrance this edge is leading to
   */
  public MFSectionEntrance getTo()
  {
    return this.to;
  }

  /**
   * Gets the distance between the start and end node.
   * @return the distance between the end nodes
   */
  public int getCost()
  {
    return this.cost;
  }

  /**
   * Gets the size of the biggest creature that can traverse the edge
   * @return The possible size of the biggest creature for this edge
   */
  public int getClearance()
  {
    return this.clearance;
  }

  /**
   * Gets the needed capabilities to traverse this edge.
   * @return the needed capabilities to traverse this edge.
   */
  public MFCapability getCapability()
  {
    return this.capability;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFEdge.class.getName());
  private final MFSectionEntrance from;
  private final MFSectionEntrance to;
  private final int cost;
  private final int clearance;
  private final MFCapability capability;

}
