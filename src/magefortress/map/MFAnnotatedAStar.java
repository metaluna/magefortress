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
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.MFEMovementType;

/**
 * The actual path finding algorithm. It utilizes an implementation of
 * Hierarchical Annotated A*.
 */
public class MFAnnotatedAStar extends MFTemplateAStar
{

  /**
   * Constructor
   * @param _map the map to search
   * @param _start the starting tile
   * @param _goal the target tile
   * @param _clearance the size of the creature
   * @param _capabilities the movement modes of the creature
   */
  public MFAnnotatedAStar(MFMap _map, MFTile _start, MFTile _goal,
                int _clearance, EnumSet<MFEMovementType> _capabilities)
  {
    super(_map, _start, _goal, _clearance, _capabilities);
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---

  @Override
  MFPath runSearch()
  {
    MFPath foundPath = null;

    // begin the search
    while (!this.getOpenList().isEmpty()) {

      // get the current node and remove it from the priority open list
      MFNode currentNode = this.getOpenList().poll();

      // check if we found the target tile
      if (currentNode.tile == this.getGoal()) {
        foundPath = this.backtracePath(currentNode);
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
        if (!this.getMap().canMoveTo(currentNode.tile, neighbor,
                                 this.getClearance(), this.getCapabilities())) {
          continue;
        }

        processNeighbor(currentNode, neighbor);
      }
    }

    return foundPath;
  }

  @Override
  int costFunction(final MFTile _start,final MFTile _goal)
  {
    final MFEDirection dir = _start.getLocation().directionOf(_goal.getLocation());
    final int cost = (MFEDirection.diagonals().contains(dir) ? getDiagonalCost() : getOrthogonalCost());
    return cost;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  /**
   * Backtraces from the given node and saves the direction from parent to
   * child node each step on its way.
   * @param _node the goal node
   */
  private MFPath backtracePath(MFNode _node)
  {
    if (_node.tile != this.getGoal()) {
      String msg = "AnnotatedAStar " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Last node of backtraced " +
                   "path is not the target tile but " + _node.tile.getLocation();
      logger.warning(msg);
    }

    final Deque<MFEDirection> path = new ArrayDeque<MFEDirection>();
    int pathCost = 0;

    while (_node.parent != null) {
      MFLocation currentLoc = _node.tile.getLocation();
      MFLocation targetLoc = _node.parent.tile.getLocation();
      MFEDirection dir = targetLoc.directionOf(currentLoc);
      path.push(dir);
      if (MFEDirection.diagonals().contains(dir)) {
        pathCost += getDiagonalCost();
      } else {
        pathCost += getOrthogonalCost();
      }
      _node = _node.parent;
    }

    if (_node.tile != this.getStart()) {
      String msg = "AnnotatedAStar " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Root of backtraced path " +
                   "is not the starting tile but " + _node.tile.getLocation();
      logger.warning(msg);
    }

    final MFPath result = new MFAnnotatedPath(this.getStart(), this.getGoal(), path, pathCost);
    return result;
  }

}
