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
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import magefortress.creatures.behavior.MFEMovementType;

/**
 * The main search algorithm. Hides all complexity of path finding from the
 * calling creature.
 * <p>
 * A call to {@link #findPath() findPath()} triggers the search for a new path
 * which finds a path on the abstracted hierarchical map and a path from the
 * starting tile to the first entrance.
 */
public class MFHierarchicalAStar extends MFTemplateAStar
{
  public MFHierarchicalAStar(MFMap _map, MFTile _start, MFTile _goal,
                    int _clearance, EnumSet<MFEMovementType> _capabilities,
                    MFPathFinder _pathFinder)
  {
    super(_map, _start, _goal, _clearance, _capabilities);
    if (_pathFinder == null) {
      String msg = "HierarchicalAStar " + _start.getLocation() + "->" + 
                    _goal.getLocation() + ": Cannot create search without a " +
                    "path finder.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    
    this.pathFinder = _pathFinder;
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---

  @Override
  MFPath runSearch()
  {
    MFPath foundPath = null;
    
    // start and goal are lying in the same section
    if (this.getStart().getParentSection() == this.getGoal().getParentSection()) {
      final MFAnnotatedAStar search = 
              new MFAnnotatedAStar(this.getMap(), this.getStart(),
                  this.getGoal(), this.getClearance(), this.getCapabilities());
      foundPath = search.findPath();
    } else {
      // insert start and goal into the navi map
      MFSectionEntrance startEntrance = this.getStart().getEntrance();
      MFSectionEntrance goalEntrance  = this.getGoal().getEntrance();
      // stores if we need to null the start/goal's tile's entrance attribute afterwards
      boolean resetStartEntrance  = (startEntrance == null);
      boolean resetGoalEntrance   = (goalEntrance  == null);
      if (startEntrance == null) {
        startEntrance = this.insertTileIntoNavigationMap(this.getStart());
        // cannot reach start
        if (startEntrance == null) {
          return null;
        }
      }

      if (goalEntrance == null) {
        goalEntrance = this.insertTileIntoNavigationMap(this.getGoal());
        // cannot reach goal
        if (goalEntrance == null) {
          this.removeTileFromNavigationMap(startEntrance);
          return null;
        }
      }
      // begin search
      while (!this.getOpenList().isEmpty()) {

        // get the next node and remove it from the priority list
        final MFNode currentNode = this.getOpenList().poll();

        // goal reached
        if (currentNode.tile == goalEntrance.getTile()) {
          foundPath = this.backtracePath(currentNode);
          break;
        }

        // move node to closed list
        this.getOpenListLocations().remove(currentNode.tile.getLocation());
        this.getClosedList().put(currentNode.tile.getLocation(), currentNode);

        // add connected tiles to the open list
        for (MFEdge edge : currentNode.tile.getEntrance().getEdges()) {
          // extract the entrance
          final MFSectionEntrance neighbor = edge.getTo();

          // skip if clearance is too big
          if (edge.getClearance() > this.getClearance()) {
            continue;
          }

          // skip if capabilities are not sufficient
          if (!this.getCapabilities().containsAll(edge.getCapabilities())) {
            continue;
          }

          // skip if already processed
          if (this.getClosedList().containsKey(neighbor.getLocation())) {
            continue;
          }

          this.processNeighbor(currentNode, neighbor.getTile());
        }
      }

      // remove start and goal from the map
      if (resetStartEntrance) {
        this.removeTileFromNavigationMap(startEntrance);
      }
      if (resetGoalEntrance) {
        this.removeTileFromNavigationMap(goalEntrance);
      }
    }
    
    return foundPath;
  }

  @Override
  int costFunction(final MFTile _start,final MFTile _goal)
  {
    final MFEdge edge = _start.getEntrance().getEdge(_goal.getEntrance());
    return edge.getCost();
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Path finder manager used to initiate the path found. */
  private final MFPathFinder pathFinder;

  /**
   * Backtraces from the given node and saves the entrances passed on the way plus
   * the target tile.
   * @param _start the goal node
   * @return the entrances passed on the way plus the target tile
   */
  private MFPath backtracePath(MFNode _node)
  {
    if (_node.tile != this.getGoal()) {
      String msg = "Path " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Last node of backtraced " +
                   "path is not the target tile but " + _node.tile.getLocation();
      logger.warning(msg);
    }

    final Deque<MFTile> path = new ArrayDeque<MFTile>();

    // backtrace the path
    while (_node.parent != null) {
      path.push(_node.tile);
      _node = _node.parent;
    }
    // add starting tile to the path
    path.push(_node.tile);
    
    if (_node.tile != this.getStart()) {
      String msg = "HierarchicalPath " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Root of backtraced path " +
                   "is not the starting tile but " + _node.tile.getLocation();
      logger.warning(msg);
    }

    final MFPath result = 
            new MFHierarchicalPath(this.getStart(), this.getGoal(), path,
                    this.getClearance(), this.getCapabilities(), this.getMap(),
                    this.pathFinder);
    return result;
  }

  /**
   * Inserts the start or goal tile into the navigation map so that the following
   * search knows how to navigate from/to the specified tile.
   * @param _tile the tile to insert
   * @return the entrance or <code>null</code> if no neighboring entrances were found
   */
  private MFSectionEntrance insertTileIntoNavigationMap(final MFTile _tile)
  {
    if (_tile == null) {
      String msg = "HierarchicalPath " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot insert null " +
                   "tile to map";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    
    // create an entrance node
    final MFSectionEntrance insertedEntrance = new MFSectionEntrance(_tile);
    
    // try to connect to all neighboring entrances
    for (MFSectionEntrance neighbor : _tile.getParentSection().getEntrances()) {
      // create a path finder
      MFAnnotatedAStar pathToNeighbor = new MFAnnotatedAStar(this.getMap(), _tile,
                        neighbor.getTile(), this.getClearance(), this.getCapabilities());
      // search!
      MFAnnotatedPath path = (MFAnnotatedPath) pathToNeighbor.findPath();
      boolean success = (path != null);

      if (success) {
        // add edge to inserted tile
        MFEdge edgeFrom = new MFEdge(insertedEntrance, neighbor,
             path.getCost(), this.getClearance(), this.getCapabilities());
        insertedEntrance.addEdge(edgeFrom);

        // add reverse edge to neighboring entrance
        MFEdge edgeTo = new MFEdge(neighbor, insertedEntrance,
             path.getCost(), this.getClearance(), this.getCapabilities());
        neighbor.addEdge(edgeTo);
      }
    }

    // no edges -> tile lies in isolated region of the map
    if (insertedEntrance.getEdges().size() == 0) {
      return null;
    } else {
      return insertedEntrance;
    }
  }

  /**
   * Removes the start or goal entrance node from the navigation map.
   * @param _tile the tile to remove
   */
  private void removeTileFromNavigationMap(final MFSectionEntrance _entrance)
  {
    if (_entrance == null) {
      String msg = "HierarchicalPath " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot remove null " +
                   "entrance from map";
      logger.warning(msg);
      return;
    }

    // remove entrance by removing all connections to its neighbors
    final List<MFEdge> edgesFrom = new ArrayList<MFEdge>(_entrance.getEdges());
    for (MFEdge edgeFrom : edgesFrom) {
      _entrance.removeEdge(edgeFrom);
      // find the corresponding edge at the neighbor and remove it
      final MFSectionEntrance neighbor = edgeFrom.getTo();
      for (MFEdge edgeTo : neighbor.getEdges()) {
        if (edgeTo.getTo() == _entrance) {
          neighbor.removeEdge(edgeTo);
          break;
        }
      }
    }

    _entrance.getTile().setEntrance(null);
  }

}
