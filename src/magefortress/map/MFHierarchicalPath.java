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
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.creatures.behavior.movable.MFEMovementType;

/**
 * Encapsulates an hierarchical path.
 * @see MFHierarchicalAStar
 */
public class MFHierarchicalPath extends MFPath implements MFIPathFinderListener
{
  MFHierarchicalPath(final MFTile _start, final MFTile _goal, 
                 final Deque<MFTile> _path, final int _clearance,
                 final EnumSet<MFEMovementType> _capabilities,
                 final MFPathFinder _pathFinder)
  {
    super(_start, _goal);

    if (_path == null || _path.isEmpty()) {
      String msg = "Hierarchical Path " + _start.getLocation() + "->" +
                    _goal.getLocation() + ": Cannot create hierarchical path " +
                    "without any tiles.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_clearance < 1) {
      String msg = "Hierarchical Path " + _start.getLocation() + "->" +
                    _goal.getLocation() + ": Cannot create hierarchical path " +
                    "without valid clearance. " + _clearance + " < 1";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_capabilities == null || _capabilities.isEmpty()) {
      String msg = "Hierarchical Path " + _start.getLocation() + "->" +
                    _goal.getLocation() + ": Cannot create hierarchical path " +
                    "without any capabilities needed to traverse it.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_pathFinder == null) {
      String msg = "Hierarchical Path " + _start.getLocation() + "->" +
                    _goal.getLocation() + ": Cannot create hierarchical path " +
                    "without a copy of the map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.path = _path;
    this.clearance = _clearance;
    this.capabilities = EnumSet.copyOf(_capabilities);
    this.pathFinder = _pathFinder;
    
    this.searchNextSubpath();
   // start searching for second subpath if there are at least 2 tiles left in the path
    if (this.path.size() > 1) {
      this.searchNextSubpath();
    }
  }

  /**
   * Checks if this path has currently any steps to read. If there is still
   * a search for a subpath in progress it returns <code>false</code>.
   * @return <code>false</code> if there are currently no steps to read
   */
  @Override
  public boolean hasNext()
  {
    if (!this.isPathValid()) {
      String msg = "Hierarchical Path" + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot tell if path has " +
                   "next step when it is invalid.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    return this.currentSubpath != null && this.currentSubpath.hasNext();
  }

  /**
   * Gets the next step of the path. Must be prepended by calls to
   * {@link #isPathValid() isPathValid()} and {@link #hasNext() hasNext()}.
   * @return the next step of the path
   */
  @Override
  public MFEDirection next()
  {
    if (!this.isPathValid()) {
      String msg = "Hierarchical Path" + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot get next step " +
                   "when path is invalid.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    final MFEDirection dir = this.currentSubpath.next();

    if (dir == null) {
      String msg = "Hierarchical Path " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": No more steps.";
      logger.severe(msg);
      throw new NoSuchElementException(msg);
    }

    // switch to next subpath and search for the next one if we haven't reached the goal
    if (!this.currentSubpath.hasNext()) {
      this.currentSubpath = this.nextSubpath;
      this.nextSubpath = null;
      if (this.currentSubpath != null &&
          this.currentSubpath.getGoal() != this.getGoal()) {
        this.searchNextSubpath();
      }
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
    String msg = "Hierarchical Path " + this.getStart().getLocation() + "->" +
                    this.getGoal().getLocation() + ": Remove not implemented.";
    logger.warning(msg);
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public int getLength()
  {
    return this.path.size();
  }

  @Override
  public void pathSearchFinished(MFPath _path)
  {
    // next subpath was found
    if (_path != null) {
      if (this.currentSubpath == null) {
        this.currentSubpath = _path;
      } else if (this.nextSubpath == null) {
        this.nextSubpath = _path;
      } else {
        String msg = "Hierarchical Path " + this.getStart().getLocation() + "->" +
                      this.getGoal().getLocation() + ": Received subpath but " +
                      "already have 2 subpaths stored.";
        logger.warning(msg);
      }
    // no subpath exists
    } else {
      this.setPathInvalid();
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Logger */
  private static Logger logger = Logger.getLogger(MFAnnotatedPath.class.getName());
  
  /** Path */
  private final Deque<MFTile> path;
  /** Maximum clearance allowed to traverse this path */
  private final int clearance;
  /** Capabilities needed to traverse this path */
  private final EnumSet<MFEMovementType> capabilities;
  /** Caches the path finder used to find subpaths */
  private final MFPathFinder pathFinder;
  
  /** Current subpath */
  private MFPath currentSubpath;
  /** Next subpath */
  private MFPath nextSubpath;

  /**
   * Starts searching for a path between the current head of the path queue and
   * the following node. Must not be called if there are less than 2 nodes left.
   * <p>
   * A successful call to this method removes the first element of the
   * hierarchical path.
   */
  private void searchNextSubpath()
  {
    if (this.path.size() == 1) {
      String msg = "Hierarchical  Path " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot search next " +
                   "subpath. Only one tile left.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }
    if (this.path.isEmpty()) {
      String msg = "HierarchicalPath " + this.getStart().getLocation() + "->" +
                   this.getGoal().getLocation() + ": Cannot search next " +
                   "subpath. Path is empty.";
      logger.severe(msg);
      throw new IllegalStateException(msg);
    }

    final MFTile start = this.path.poll();
    final MFTile goal  = this.path.peek();

    this.pathFinder.enqueuePathSearch(start.getLocation(), goal.getLocation(),
                                      this.clearance, this.capabilities, this);
  }

}
