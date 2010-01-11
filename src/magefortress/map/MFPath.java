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

import java.util.Iterator;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;

/**
 * Base class for paths listening to changes. If a path was changed at a
 * later point, the class that received the path will be notified of these
 * changes.
 */
public abstract class MFPath implements Iterator<MFEDirection>
{
  /**
   * Constructor
   * @param _start the starting tile
   * @param _goal the target tile
   */
  public MFPath(final MFTile _start, final MFTile _goal)
  {
    if (_start == null) {
      String msg = "Path: Cannot create path without starting tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_goal == null) {
      String msg = "Path: Cannot create path without target tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.start  = _start;
    this.goal   = _goal;
    this.isPathValid = true;
  }

  public MFTile getStart()
  {
    return this.start;
  }

  public MFTile getGoal()
  {
    return this.goal;
  }

  /**
   * Gets if the goal can be reached using this path. Has to be called before every
   * call to {@link #hasNext() hasNext()} and {@link #next() next()}.
   * <p>
   * A path is marked valid after it was created. It is valid until a change in
   * the map makes the path invalid because there is no longer a path from start
   * to goal with the current configuration.
   * <p>
   * A path can never recover from being invalid. A new search has to be started.
   * @return <code>true</code> if we can reach the goal.
   */
  public boolean isPathValid()
  {
    return this.isPathValid;
  }

  /**
   * Invalidates the path. Once set it can never be unset and the path must be
   * disposed of.
   */
  public void setPathInvalid()
  {
    this.isPathValid = false;
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Logger */
  private static Logger logger = Logger.getLogger(MFPath.class.getName());

  /** Starting tile */
  private final MFTile start;
  /** Target tile */
  private final MFTile goal;
  
  /** <code>true</code> if we can reach the goal */
  private boolean isPathValid;

}
