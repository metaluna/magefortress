/*
 *  Copyright (c) 2010 Simon Hardijanto
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

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;

/**
 * Encapsulates an annotated path.
 * @see MFAnnotatedAStar
 */
public class MFAnnotatedPath extends MFPath
{

  /**
   * Constructor
   * @param _start the starting tile
   * @param _goal the target tile
   * @param _directions the list of directions to reach the target tile
   * @param _pathCost the cost to traverse the path in weighted units
   */
  MFAnnotatedPath(final MFTile _start, final MFTile _goal,
                  final Deque<MFEDirection> _directions, final int _pathCost)
  {
    super(_start, _goal);
    if (_directions == null || _directions.isEmpty()) {
      String msg = "Annotated Path " + this.getStart().getLocation() + "->" +
                    this.getGoal().getLocation() + ": Cannot create path " +
                    "without at least one direction.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_pathCost < 1) {
      String msg = "Annotated Path " + this.getStart().getLocation() + "->" +
                    this.getGoal().getLocation() + ": Cannot create path " +
                    "with path cost of " + _pathCost + ". Has to be at least 1.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.path = _directions;
    this.pathCost = _pathCost;
  }

  /**
   * Checks if this path has any steps left.
   * @return <code>false</code> if there are no steps left
   */
  @Override
  public boolean hasNext()
  {
    if (!this.isPathValid()) {
      String msg = "Annotated Path" + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot tell if path has " +
                   "next step when it is invalid.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    return !this.path.isEmpty();
  }

  /**
   * Gets the next step of the path.
   * @return the next step of the path
   */
  @Override
  public MFEDirection next()
  {
    if (!this.isPathValid()) {
      String msg = "Annotated Path" + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot get next step " +
                   "when path is invalid.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    final MFEDirection dir = this.path.poll();

    if (dir == null) {
      String msg = "Annotated Path " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": No more steps.";
      logger.severe(msg);
      throw new NoSuchElementException(msg);
    }

    return dir;
  }

  /**
   * Not supported. Don't even think about trying.
   * @throws UnsupportedOperationException when called
   */
  @Override
  public final void remove()
  {
    String msg = "Annotated Path " + this.getStart().getLocation() + "->" +
                    this.getGoal().getLocation() + ": Remove not implemented.";
    logger.warning(msg);
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public int getLength()
  {
    return this.path.size();
  }

  //---vvv---      PACKAGE-PRIVATE METHODS      ---vvv---

  /**
   * Gets the cost of traversing the path in weighted units. Currently every
   * tile moved equals one unit.
   * @return the length of the path
   */
  int getCost()
  {
    return this.pathCost;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Logger */
  private static Logger logger = Logger.getLogger(MFAnnotatedPath.class.getName());

  /** The steps of the path */
  private final Deque<MFEDirection> path;
  /** Cost of traversing the path in weighted cost */
  private final int pathCost;

}
