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
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
import magefortress.core.MFLocation;

/**
 * The actual path finding algorithm. It utilizes an implementation of
 * Hierarchical Annotated A*.
 */
public class MFPath extends MFAbstractPath implements Iterator<MFEDirection>
{

  public MFPath(MFMap _map, MFTile _start, MFTile _goal,
                int _clearance, EnumSet<MFEMovementType> _capabilities)
  {
    super(_map, _start, _goal, _clearance, _capabilities);
  }

  /**
   * Gets the length of the calculated path in weighted cost.
   * @return the length of the path calculated earlier
   */
  public int getPathCost()
  {
    if (this.path == null) {
      String msg = "Path: Cannot get length of path if no path was calculated yet.";
      logger.warning(msg);
      throw new IllegalStateException(msg);
    }

    return this.pathCost;
  }

  /**
   * Gets the next step of the path that was found.
   * @return The next step of the path or <code>null</code> if there are no 
   * more elements.
   * @throws IllegalStateException if no search was facilitated yet or the path
   * is invalid.
   */
  @Override
  public MFEDirection next()
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
    boolean success = false;

    // begin the search
    while (!this.getOpenList().isEmpty() && !success) {

      // get the current node and remove it from the priority open list
      MFNode currentNode = this.getOpenList().poll();

      // check if we found the target tile
      if (currentNode.tile == this.getGoal()) {
        success = true;
        this.backtracePath(currentNode);
        break;
      }

      // move node from open to closed list. Because there are
      // two open lists the node has to be removed from the second one, too
      this.getOpenListLocations().remove(currentNode.tile.getLocation());
      this.getClosedList().put(currentNode.tile.getLocation(), currentNode);

      // add surrounding tiles to the open list
      for (MFEDirection dir : MFEDirection.plain()) {

        // get the neighboring tile
        final MFTile neighbor = this.getMap().getNeighbor(currentNode.tile, dir);

        // no neighbor (edge tile) -> skip
        if (neighbor == null) {
          continue;
        }
        // skip if already processed
        if (this.getClosedList().containsKey(neighbor.getLocation())) {
          continue;
        }

        // skip if unreachable from current tile
        if (!this.getMap().canMoveTo(currentNode.tile, neighbor, dir,
                                 this.getClearance(), this.getCapabilities())) {
          continue;
        }

        processNeighbor(currentNode, neighbor);
      }
    }

    if (!success) {
      this.path = new ArrayDeque<MFEDirection>();
    }

    return success;
  }

  @Override
  int costFunction(final MFTile _start,final MFTile _goal)
  {
    final MFEDirection dir = _start.getLocation().directionOf(_goal.getLocation());
    final int cost = (MFEDirection.diagonals().contains(dir) ? getDiagonalCost() : getOrthogonalCost());
    return cost;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The path found */
  private Deque<MFEDirection> path;
  /** The length of the path in weighted cost */
  private int pathCost;

  /**
   * Backtraces from the given node and saves the direction from parent to
   * child node each step on its way.
   * @param _node the goal node
   */
  private void backtracePath(MFNode _node)
  {
    if (_node.tile != this.getGoal()) {
      String msg = "Path: Last node of backtraced path is not the target tile but " +
              _node.tile.getLocation();
      logger.warning(msg);
    }

    this.path = new ArrayDeque<MFEDirection>();
    this.pathCost = 0;

    while (_node.parent != null) {
      MFLocation currentLoc = _node.tile.getLocation();
      MFLocation targetLoc = _node.parent.tile.getLocation();
      MFEDirection dir = targetLoc.directionOf(currentLoc);
      this.path.push(dir);
      if (MFEDirection.diagonals().contains(dir)) {
        this.pathCost += getDiagonalCost();
      } else {
        this.pathCost += getOrthogonalCost();
      }
      _node = _node.parent;
    }

    if (_node.tile != this.getStart()) {
      String msg = "Path: Root of backtraced path is not the starting tile but " +
                  _node.tile.getLocation();
      logger.warning(msg);
    }
  }

}
