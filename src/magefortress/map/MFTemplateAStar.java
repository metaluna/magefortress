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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Logger;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.movable.MFEMovementType;

/**
 * Base class for path finding algorithms.
 */
public abstract class MFTemplateAStar
{
  /**
   * Constructor
   * @param _map the map
   * @param _start the starting tile
   * @param _goal the target tile
   * @param _clearance the size of the moving creature
   * @param _capabilities the movement types the moving creature can employ
   */
  public MFTemplateAStar(MFMap _map, MFTile _start, MFTile _goal,
                int _clearance, EnumSet<MFEMovementType> _capabilities)
  {
    validateConstructorParams(_map, _start, _goal, _clearance, _capabilities);

    this.map = _map;
    this.start = _start;
    this.goal = _goal;
    this.clearance = _clearance;
    this.capabilities = EnumSet.copyOf(_capabilities);
  }

  public final int getOrthogonalCost()
  {
    return ORTHOGONAL_COST;
  }

  public final int getDiagonalCost()
  {
    return DIAGONAL_COST;
  }

  public final MFMap getMap()
  {
    return map;
  }

  public final MFTile getStart()
  {
    return start;
  }

  public final MFTile getGoal()
  {
    return goal;
  }

  public final int getClearance()
  {
    return clearance;
  }

  public final EnumSet<MFEMovementType> getCapabilities()
  {
    return capabilities;
  }

  /**
   * Starts a search for a path between the tile set in during object
   * construction.
   * <p>
   * Uses the subclasses' implementation of {@link #runSearch() runSearch()}.
   * Initialization and pushing the start node onto the open list is taken care
   * of here.
   * @return <code>true</code> if a was path found
   */
  public MFPath findPath()
  {
    validateStartConditions();

    initSearchLists();

    // add start tile to the open list
    final int startH = this.estimateDistance(this.start);
    this.addToOpenList(this.start, null, 0, startH);

    final MFPath result = this.runSearch();

    clearSearchLists();

    return result;
  }

  /**
   * Do not call directly! Used during the path search in {@link #findPath() findPath()}.
   * @return <code>true</code> if a path was found
   */
  abstract MFPath runSearch();
  /**
   * Do not call directly! Used during insertion of a new node.
   * @param _start the parent node
   * @param _goal the neighboring tile
   * @return the cost of moving from parent node to neighboring node
   */
  abstract int costFunction(MFTile _start, MFTile _goal);

  //---vvv---      PACKAGE-PRIVATE METHODS      ---vvv---
  /** Logger */
  static final Logger logger = Logger.getLogger(MFTemplateAStar.class.getName());

  final Queue<MFNode> getOpenList()
  {
    return this.openList;
  }

  final Map<MFLocation, MFNode> getOpenListLocations()
  {
    return this.openListLocations;
  }

  final Map<MFLocation, MFNode> getClosedList()
  {
    return this.closedList;
  }

  /**
   * Processes a neighbor node during the search. Packages the tile into a node
   * with associated costs and puts it onto the open list.
   * <p>
   * Uses the subclass' implementation of
   * {@link #costFunction(magefortress.map.MFAbstractPath.MFNode, magefortress.map.MFTile) costFunction()}
   * to calculate the distance from the parent node to the neighbor.
   * @param _parentNode the parent node
   * @param _neighbor the tile being processed
   */
  final void processNeighbor(final MFNode _parentNode, final MFTile _neighbor)
  {
    //　calculate costs
    final int g = _parentNode.g + this.costFunction(_parentNode.tile, _neighbor);
    final int h = this.estimateDistance(_neighbor);
    final MFNode previouslyVisited = this.openListLocations.get(_neighbor.getLocation());

    // new node found -> add to open list
    if (previouslyVisited == null) {
      this.addToOpenList(_neighbor, _parentNode, g, h);

    // already processed but better path to this node found -> update cost and ranking
    } else if (previouslyVisited.g > g) {
      previouslyVisited.g = g;
      previouslyVisited.f = previouslyVisited.h + g;
      previouslyVisited.parent = _parentNode;

      // remove and re-insert into the open list to have it placed in order
      this.openList.remove(previouslyVisited);
      this.openList.add(previouslyVisited);
    }
  }

  //---vvv---      PRIVATE METHODS        ---vvv---

  /** Cost for moving to a non-diagonally adjacent tile */
  private static final int ORTHOGONAL_COST = 1;
  /** Cost for moving to a diagonally adjacent tile */
  private static final int DIAGONAL_COST = 1;

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

  /** Stores the processed nodes */
  private Map<MFLocation, MFNode> closedList;
  /** Stores the nodes as a priority queue */
  private Queue<MFNode> openList;
  /** Stores the nodes for faster access */
  private Map<MFLocation, MFNode> openListLocations;

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
      return MFTemplateAStar.DIAGONAL_COST * yDistance +
             MFTemplateAStar.ORTHOGONAL_COST * (xDistance - yDistance);
    } else {
      return MFTemplateAStar.DIAGONAL_COST * xDistance +
             MFTemplateAStar.ORTHOGONAL_COST * (yDistance - xDistance);
    }
  }

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
   * Validates the starting conditions for a path search.
   * @throws IllegalStateException if any of the conditions are failed to be met
   */
  private void validateStartConditions()
  {
    boolean canEnterStart = false;
    for (MFEMovementType capability : this.capabilities) {
      if (this.start.getClearance(capability) >= this.clearance &&
          this.start.isWalkable(capability)) {
        canEnterStart = true;
        break;
      }
    }
    if (!canEnterStart) {
      String msg = "AStar: Cannot search path from " + this.start.getLocation() +
                   " to " + this.goal.getLocation() + ". Given clearance and " +
                   "capabilities are insufficient to move to the starting tile.";
      MFTemplateAStar.logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    boolean canEnterTarget = false;
    for (MFEMovementType capability : this.capabilities) {
      if (this.goal.getClearance(capability) >= this.clearance &&
          this.goal.isWalkable(capability)) {
        canEnterTarget = true;
        break;
      }
    }
    if (!canEnterTarget) {
      String msg = "AStar: Cannot search path from " + this.start.getLocation() +
                   " to " + this.goal.getLocation() + ". Given clearance and " +
                   "capabilities are insufficient to move to the target tile.";
      MFTemplateAStar.logger.severe(msg);
      throw new IllegalStateException(msg);
    }
  }

  /**
   * Validates the parameters given to the constructor.
   * @param _map the map
   * @param _start the starting tile
   * @param _goal the target tile
   * @param _clearance the size of the moving creature
   * @param _capabilities the movement types the moving creature can employ
   * @throws IllegalArgumentException if any of the parameters fails to meet
   *                                  the conditions.
   */
  private void validateConstructorParams(final MFMap _map, final MFTile _start,
                                   final MFTile _goal, final int _clearance,
                                   final EnumSet<MFEMovementType> _capabilities)
  {
    if (_map == null) {
      String msg = "AStar: Cannot create A-Star without map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_start == null) {
      String msg = "AStar: Cannot create A-Star without start location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_goal == null) {
      String msg = "AStar: Cannot create A-Star without target location. Start: " +
                                                           _start.getLocation();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_clearance <= 0) {
      String msg = "AStar: Cannot create A-Star from " + _start.getLocation() +
                    " to " + _goal.getLocation() + " with an agent's size of " +
                    _clearance + ". Must be at least 1.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_capabilities == null || _capabilities.isEmpty()) {
      String msg = "AStar: Cannot create A-Star from " + _start.getLocation() +
                    " to " + _goal.getLocation() + " without knowing the " +
                    "agent's capabilities.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }


  /**
   * Object to hold relevant data to a node of the search algorithm.
   */
  final class MFNode implements Comparable<MFNode>
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
