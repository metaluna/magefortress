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
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import magefortress.core.MFEMovementType;

/**
 *
 */
public class MFHierarchicalPath extends MFAbstractPath implements Iterator<MFSectionEntrance>
{
  public MFHierarchicalPath(MFMap _map, MFTile _start, MFTile _goal,
                    int _clearance, EnumSet<MFEMovementType> _capabilities)
  {
    super(_map, _start, _goal, _clearance, _capabilities);
  }

  /**
   * Probes the path to see if there are more steps in it.
   * @return <code>false</code> if there are no more steps
   * @throws IllegalStateException if no search was facilitated yet or the path
   * is invalid.
   */
  @Override
  public boolean hasNext()
  {
    if (!this.isPathValid()) {
      String msg = "Path: Have to search for a path first before testing for " +
                   "more steps. From " + this.getStart().getLocation() + " to " +
                   this.getGoal().getLocation();
      logger.warning(msg);
      throw new IllegalStateException(msg);
    }

    return !this.path.isEmpty();
  }

  /**
   * Gets the next step of the path that was found.
   * @return The next step of the path or <code>null</code> if there are no
   * more elements.
   * @throws IllegalStateException if no search was facilitated yet or the path
   * is invalid.
   */
  @Override
  public MFSectionEntrance next()
  {
   if (!this.isPathValid()) {
      String msg = "Path: Have to search for a path first before trying to get " +
              "steps. From " + this.getStart().getLocation() + " to " + this.getGoal().getLocation();
      logger.warning(msg);
      throw new IllegalStateException(msg);
    }
    return this.path.poll();
  }

  /**
   * Not supported. Don't even think about trying.
   * @throws UnsupportedOperationException when called
   */
  @Override
  public void remove()
  {
    String msg = "Path: Remove not implemented by Path. Path from "+
                  this.getStart().getLocation() + " to " + this.getGoal().getLocation();
    logger.warning(msg);
    throw new UnsupportedOperationException(msg);
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---

  @Override
  boolean runSearch()
  {
    // insert start and goal into the navi map
    final MFSectionEntrance startEntrance = this.insertTileIntoNavigationMap(this.getStart());
    // cannot reach start
    if (startEntrance == null) {
      return false;
    }

    final MFSectionEntrance goalEntrance  = this.insertTileIntoNavigationMap(this.getGoal());
    // cannot reach goal
    if (goalEntrance == null) {
      this.removeFromNavigationMap(startEntrance);
      return false;
    }

    boolean success = false;

    // begin search
    while (!this.getOpenList().isEmpty() && !success) {

      // get the next node and remove it from the priority list
      final MFNode currentNode = this.getOpenList().poll();

      // goal reached
      if (currentNode.tile == goalEntrance.getTile()) {
        success = true;
        this.backtracePath(currentNode);
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
    this.removeFromNavigationMap(startEntrance);
    this.removeFromNavigationMap(goalEntrance);

    if (!success) {
      this.path = new ArrayDeque<MFSectionEntrance>();
    }

    return success;
  }

  @Override
  int costFunction(final MFTile _start,final MFTile _goal)
  {
    final MFEdge edge = _start.getEntrance().getEdge(_goal.getEntrance());
    return edge.getCost();
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private Deque<MFSectionEntrance> path;

  /**
   * Backtraces from the given node and saves the entrances passed on the way.
   * @param _start the goal node
   */
  private void backtracePath(MFNode _node)
  {
    if (_node.tile != this.getGoal()) {
      String msg = "Path: Last node of backtraced path is not the target tile but " +
              _node.tile.getLocation();
      logger.warning(msg);
    }

    this.path = new ArrayDeque<MFSectionEntrance>();

    // skip goal entrance because it will be removed
    _node = _node.parent;

    // backtrace the path
    while (_node.parent != null) {
      this.path.push(_node.tile.getEntrance());
      _node = _node.parent;
    }

    if (_node.tile != this.getStart()) {
      String msg = "Path: Root of backtraced path is not the starting tile but " +
                  _node.tile.getLocation();
      logger.warning(msg);
    }
  }

  /**
   * Inserts the start or goal tile into the navigation map so that the following
   * search knows how to navigate from the tiles.
   * @param _tile the tile to insert
   * @return the entrance or <code>null</code> if no neighboring entrances were found
   */
  private MFSectionEntrance insertTileIntoNavigationMap(final MFTile _tile)
  {
    // create an entrance node
    final MFSectionEntrance insertedEntrance = new MFSectionEntrance(_tile);
    
    // connect to all neighboring entrances
    for (MFSectionEntrance neighbor : _tile.getParentSection().getEntrances()) {
      // create a path finder
      MFPath pathToNeighbor = new MFPath(this.getMap(), _tile,
                        neighbor.getTile(), this.getClearance(), this.getCapabilities());
      // search!
      boolean success = pathToNeighbor.runSearch();

      if (success) {
        // add edge to inserted tile
        MFEdge edgeFrom = new MFEdge(insertedEntrance, neighbor,
             pathToNeighbor.getPathCost(), this.getClearance(), this.getCapabilities());
        insertedEntrance.addEdge(edgeFrom);

        // add same edge to neighboring entrance
        MFEdge edgeTo = new MFEdge(neighbor, insertedEntrance,
             pathToNeighbor.getPathCost(), this.getClearance(), this.getCapabilities());
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
  private void removeFromNavigationMap(final MFSectionEntrance _entrance)
  {
    if (_entrance == null) {
      String msg = "Hierarchical Path: Cannot remove null entrance from map";
      logger.warning(msg);
      return;
    }

    // remove entrance by removing all connections to its neighbors
    for (MFEdge edgeFrom : _entrance.getEdges()) {
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
  }
}
