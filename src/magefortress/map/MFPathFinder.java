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
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.core.Singleton;
import magefortress.creatures.behavior.movable.MFEMovementType;

/**
 * Singleton that manages finding paths. Classes that wish to find a path
 * queue a task at the manager and get notified when the task search is
 * finished.
 * @see MFIPathFinderListener
 */
public class MFPathFinder implements Singleton
{

  public static synchronized MFPathFinder getInstance()
  {
    if (pathFinderInstance == null) {
      pathFinderInstance = new MFPathFinder();
    }

    return pathFinderInstance;
  }

  public void setMap(MFMap _map)
  {
    this.map = _map;
  }

  /**
   * Enqueues a search into the queue for later execution.
   * @param _map the map to search
   * @param _start the starting location
   * @param _goal the target location
   * @param _clearance the size of the creature
   * @param _capabilities the movement modes of the creature
   * @param _listener the listener to notify when the search was executed
   */
  public void enqueuePathSearch(final MFLocation _start,
                                final MFLocation _goal, final int _clearance,
                                final EnumSet<MFEMovementType> _capabilities,
                                final MFIPathFinderListener _listener)
  {
    if (this.map == null) {
      String msg = this.getClass().getName() + ": Map must be set before " +
              "searching for paths.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    final MFTile startTile = this.map.getTile(_start);
    final MFTile goalTile  = this.map.getTile(_goal);

    final MFHierarchicalAStar search = new MFHierarchicalAStar(this.map, startTile,
                                        goalTile, _clearance, _capabilities, this);
    this.enqueuePathSearch(search, _listener);
  }

  /**
   * Executes exactly one or zero path searches from the queue.
   */
  public void update()
  {
    if (!this.searchQueue.isEmpty()) {
      final MFTemplateAStar search = this.searchQueue.poll();
      final MFIPathFinderListener listener = this.listenerQueue.poll();
      // find a path
      final MFPath path = search.findPath();
      // notify the listener
      listener.pathSearchFinished(path);
    }
  }

  //---vvv--- PACKAGE-PRIVATE METHODS   ---vvv---
  
  /**
   * Enqueues a search into the queue for later execution.
   * @param _search the path to enqueue
   * @param _listener the listener to notify when the search was executed
   */
  void enqueuePathSearch(final MFTemplateAStar _search,
                         final MFIPathFinderListener _listener)
  {
    if (_search == null) {
      String msg = "PathFinder: Cannot search for a path without a configured search.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_listener == null) {
      String msg = "PathFinder: Cannot search for a path without a listener.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.searchQueue.add(_search);
    this.listenerQueue.add(_listener);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Logger */
  private static final Logger logger = Logger.getLogger(MFPathFinder.class.getName());
  /** Singleton instance */
  private static MFPathFinder pathFinderInstance;

  /** The map - must be set after getting the first instance */
  private MFMap map;
  /** The list of searches */
  private final Queue<MFTemplateAStar> searchQueue;
  /** The list of listeners */
  private final Queue<MFIPathFinderListener> listenerQueue;

  private MFPathFinder()
  {
    this.searchQueue = new LinkedList<MFTemplateAStar>();
    this.listenerQueue = new LinkedList<MFIPathFinderListener>();
  }

}
