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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Queue;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
import magefortress.core.MFLocation;

/**
 * The actual path finding algorithm. It utilizes an implementation of
 * Hierarchical Annotated A*.
 */
public class MFPath implements Iterator<MFEDirection>
{

  public MFPath(MFMap _map, MFTile _start, MFTile _goal,
                int _clearance, EnumSet<MFEMovementType> _capabilities)
  {
    if (_map == null) {
      String msg = "Path: Cannot create path without map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_start == null) {
      String msg = "Path: Cannot create path without start location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_goal == null) {
      String msg = "Path: Cannot create path without target location. Start: " +
              _start.getLocation();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_clearance <= 0) {
      String msg = "Path: Cannot create path from " + _start.getLocation() +
                   " to " + _goal.getLocation() + " with an agent's size of " +
                   _clearance + ". Must be at least 1.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_capabilities == null || _capabilities.isEmpty()) {
      String msg = "Path: Cannot create path from " + _start.getLocation() +
                   " to " + _goal.getLocation() + " without knowing the " +
                   "agent's capabilities.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.map = _map;
    this.start = _start;
    this.goal = _goal;
    this.clearance = _clearance;
    this.capabilities = EnumSet.copyOf(_capabilities);
  }

  /**
   * Starts the path search algorithm. Subsequent calls will throw an exception.
   * @return <code>true</code> if a path was found
   * @throws IllegalStateException if a path was already searched for
   */
  public boolean findPath()
  {
    validateStartConditions();

    initSearchLists();

    // add start tile to the open list
    final int startH = this.estimateDistance(this.start);
    addToOpenList(this.start, null, 0, startH);

    // begin the search
    while (!this.openList.isEmpty() || this.wasPathFound == true) {

      // get the current node and remove it from the priority open list
      MFNode currentNode = this.openList.poll();

      // check if we found the target tile
      if (currentNode.tile == this.goal) {
        this.wasPathFound = true;
        backtracePath(currentNode);
        break;
      }

      // move node from open to closed list. Because there are
      // two open lists the node has to be removed from the second one, too
      this.openListLocations.remove(currentNode.tile.getLocation());
      this.closedList.put(currentNode.tile.getLocation(), currentNode);

      // add surrounding tiles to the open list
      for (MFEDirection dir : MFEDirection.plain()) {

        // get the neighboring tile
        final MFTile neighbor = this.map.getNeighbor(currentNode.tile, dir);

        // no neighbor (edge tile) -> skip
        if (neighbor == null) {
          continue;
        }
        // skip if already processed
        if (this.closedList.containsKey(neighbor.getLocation())) {
          continue;
        }

        // skip if unreachable from current tile
        if (!this.map.canMoveTo(currentNode.tile, neighbor, dir, this.clearance, this.capabilities)) {
          continue;
        }
        
        processNeighbor(currentNode, dir, neighbor);
      }
    }

    clearSearchLists();

    if (!this.wasPathFound) {
      this.path = new ArrayDeque<MFEDirection>();
    }

    return this.wasPathFound;
  }

  /**
   * Gets the next step of the path that was found.
   * @return The next step of the path or <code>null</code> if no path was found
   * or there are no more elements.
   * @throws IllegalStateException if no search was facilitated yet
   */
  @Override
  public MFEDirection next()
  {
    if (this.path == null) {
      String msg = "Path: Have to search for a path first before trying to get " +
              "steps. From " + this.start.getLocation() + " to " + this.goal.getLocation();
      logger.warning(msg);
      throw new IllegalStateException(msg);
    }
    return this.path.poll();
  }

  /**
   * Probes the path to see if there are more steps in it.
   * @return <code>false</code> if there are no more steps or no path was found
   * @throws IllegalStateException if no search was facilitated yet
   */
  @Override
  public boolean hasNext()
  {
    if (this.path == null) {
      String msg = "Path: Have to search for a path first before testing for " +
                   "more steps. From " + this.start.getLocation() + " to " +
                   this.goal.getLocation();
      logger.warning(msg);
      throw new IllegalStateException(msg);
    }
    
    return !this.path.isEmpty();
  }

  /**
   * Not supported. Don't even think about trying.
   * @throws UnsupportedOperationException when called
   */
  @Override
  public void remove()
  {
    String msg = "Path: Remove not implemented by Path. Path from "+
                  this.start.getLocation() + " to " + this.goal.getLocation();
    logger.warning(msg);
    throw new UnsupportedOperationException(msg);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFPath.class.getName());
  /** Cost for moving to a non-diagnonally adjacent tile*/
  private static final int ORTHOGONAL_COST = 10;
  /** Cost for moving to an diagonally adjacent tile */
  private static final int DIAGONAL_COST = 14;
  /** The path found */
  private Deque<MFEDirection> path;
  /** Stores if the search was unsuccessful */
  private boolean wasPathFound;
  /** The map to search */
  private final MFMap map;
  /** The starting tile */
  private final MFTile start;
  /** The target tile */
  private final MFTile goal;
  /** The size of the creature that wants to use the path */
  private final int clearance;
  /** The movement types of the creature that wants to use the path */
  private final EnumSet<MFEMovementType> capabilities;

  // used during the path search
  private Queue<MFNode> openList;
  private Map<MFLocation, MFNode> openListLocations;
  private Map<MFLocation, MFNode> closedList;

  /**
   * Instantiates the search lists. There is a {@link PriorityQueue} to gain fast
   * access to the most promising node on the open list and a {@link HashMap} to
   * store the already processed nodes and for a fast membership test.
   * <p>
   * Additionally there is a copy of the nodes on the open list organized as a
   * HashMap so that the membership test is faster than using the PriorityQueue.
   */
  private void initSearchLists()
  {
    this.openList = new PriorityQueue<MFNode>();
    this.openListLocations = new HashMap<MFLocation, MFNode>();
    this.closedList = new HashMap<MFLocation, MFNode>();
  }

  /**
   * Lets the node lists used during the search be garbage collected.
   */
  private void clearSearchLists()
  {
    // gc
    this.openList.clear();
    this.openList = null;
    this.openListLocations.clear();
    this.openListLocations = null;
    this.closedList.clear();
    this.closedList = null;
  }

  /**
   * Heuristic function to estimate the distance to the goal of this path.
   * <p>
   * Note: Copied from {@link  http://www.policyalmanac.org/games/heuristics.htm}
   * @param _tile the starting tile
   * @return the estimated cost
   */
  private int estimateDistance(MFTile _tile)
  {
    final int xDistance = Math.abs(_tile.getLocation().x - this.goal.getLocation().x);
    final int yDistance = Math.abs(_tile.getLocation().y - this.goal.getLocation().y);
    if (xDistance > yDistance) {
      return DIAGONAL_COST*yDistance + ORTHOGONAL_COST*(xDistance-yDistance);
    } else {
      return DIAGONAL_COST*xDistance + ORTHOGONAL_COST*(yDistance-xDistance);
    }
  }

  /**
   * Processes a neighbor node during the search. Puts the tile into a node with
   * associated costs.
   * @param _node the parent node
   * @param _direction the direction in which this node lies to the parent node
   * @param _tile the tile being processed
   */
  private void processNeighbor(final MFNode _node, final MFEDirection _direction, final MFTile _tile)
  {
    //　calculate costs
    final int g = _node.g +
              (MFEDirection.diagonals().contains(_direction) ? DIAGONAL_COST : ORTHOGONAL_COST);
    final int h = this.estimateDistance(_tile);

    final MFNode previouslyVisited = this.openListLocations.get(_tile.getLocation());

    // new node found -> add to open list
    if (previouslyVisited == null) {
      addToOpenList( _tile, _node, g, h);
      
    // already processed but better path to this node found -> update cost and ranking
    } else if (previouslyVisited.g > g) {
      previouslyVisited.g = g;
      previouslyVisited.f = previouslyVisited.h + g;
      previouslyVisited.parent = _node;
      // remove and re-insert into the open list to have it placed in order
      this.openList.remove(previouslyVisited);
      this.openList.add(previouslyVisited);
    }
  }

  /**
   * Adds a tile to the open list during search. 
   * @param _tile the tile to add
   * @param _node the parent node
   * @param _g the cost from start to this node
   * @param _h the estimated cost to the goal node
   */
  private void addToOpenList(final MFTile _tile, final MFNode _node, final int _g, final int _h)
  {
    final MFNode newNode = new MFNode(_tile, _node, _g, _h);
    this.openList.add(newNode);
    this.openListLocations.put(_tile.getLocation(), newNode);
  }

  /**
   * Backtraces from the given node and saves the direction from parent to
   * child node each step on its way.
   * @param _node the goal node
   */
  private void backtracePath(MFNode _node)
  {
    if (_node.tile != this.goal) {
      String msg = "Path: Last node of backtraced path is not the target tile but " +
              _node.tile.getLocation();
      logger.warning(msg);
    }

    this.path = new ArrayDeque<MFEDirection>();

    while (_node.parent != null) {
      MFLocation currentLoc = _node.tile.getLocation();
      MFLocation targetLoc = _node.parent.tile.getLocation();
      this.path.push(targetLoc.directionOf(currentLoc));
      _node = _node.parent;
    }

    if (_node.tile != this.start) {
      String msg = "Path: Root of backtraced path is not the starting tile but " +
                  _node.tile.getLocation();
      logger.warning(msg);
    }
  }

  /**
   * Validates the starting conditions for a path search.
   * @throws IllegalStateException if any of the conditions are failed to be met
   */
  private void validateStartConditions() throws IllegalStateException
  {
    if (this.path != null) {
      String msg = "Path: Cannot search for a path from" + this.start + " to " +
                    this.goal + "again. Already found one.";
      logger.warning(msg);
      throw new IllegalStateException(msg);
    }
    boolean canEnterStart = false;
    for (MFEMovementType capability : this.capabilities) {
      if (this.start.getClearance(capability) >= this.clearance && this.start.isWalkable(capability)) {
        canEnterStart = true;
        break;
      }
    }
    if (!canEnterStart) {
      String msg = "Path: Cannot search path from " + this.start.getLocation() +
                   " to " + this.goal.getLocation() + ". Given clearance and " +
                   "capabilities are insufficient to move to the starting tile.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    boolean canEnterTarget = false;
    for (MFEMovementType capability : this.capabilities) {
      if (this.goal.getClearance(capability) >= this.clearance && this.goal.isWalkable(capability)) {
        canEnterTarget = true;
        break;
      }
    }
    if (!canEnterTarget) {
      String msg = "Path: Cannot search path from " + this.start.getLocation() +
                   " to " + this.goal.getLocation() + ". Given clearance and " +
                   "capabilities are insufficient to move to the target tile.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
  }

  /**
   * Object to hold relevant data to a node of the search algorithm.
   */
  private class MFNode implements Comparable<MFNode>
  {
    final MFTile tile;
    MFNode parent;
    int f;
    int g;
    final int h;

    public MFNode(MFTile _tile, MFNode _parent, int _g, int _h)
    {
      this.tile = _tile;
      this.parent = _parent;
      this.g = _g;
      this.h = _h;
      this.f = _g + _h;
    }

    @Override
    public boolean equals(Object _other)
    {
      if (_other == null) {
        return false;
      }
      if (getClass() != _other.getClass()) {
        return false;
      }
      final MFNode otherNode = (MFNode) _other;
      if (this.f != otherNode.f) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode()
    {
      int hash = 5;
      hash = 41 * hash + this.f;
      return hash;
    }

    @Override
    public int compareTo(MFNode _other)
    {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if (this.f == _other.f) {
        return EQUAL;
      } else if (this.f < _other.f) {
        return BEFORE;
      } else {
        return AFTER;
      }
      
    }
  }
}
