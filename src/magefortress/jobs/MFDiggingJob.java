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
package magefortress.jobs;

import magefortress.jobs.subtasks.MFLocateNearestNeighorSubtask;
import magefortress.jobs.subtasks.MFSubtask;
import magefortress.jobs.subtasks.MFDigOutTileSubtask;
import magefortress.jobs.subtasks.MFGotoLocationSubtask;
import magefortress.core.MFLocation;
import magefortress.map.MFMap;
import magefortress.map.MFPathFinder;

/**
 * Digs out a single tile.
 */
public class MFDiggingJob extends MFAssignableJob
{

  public MFDiggingJob(MFDiggingSite _sender, MFMap _map, MFLocation _location,
                                                       MFPathFinder _pathFinder)
  {
    super(_sender);
    this.map = _map;
    this.location = _location;
    this.pathFinder = _pathFinder;
  }

  @Override
  protected void initJob()
  {
    MFSubtask findNeighbor = new MFLocateNearestNeighorSubtask(
                    this.getOwner(), this.location, this.map, this.pathFinder);
    MFSubtask gotoTile  = new MFGotoLocationSubtask(
                                this.getOwner(), this.pathFinder);
    MFSubtask digTile   = new MFDigOutTileSubtask(
                                this.getOwner(), this.map, this.location);
    this.addSubtask(findNeighbor);
    this.addSubtask(gotoTile);
    this.addSubtask(digTile);
  }

  @Override
  public void pauseJob()
  {
  }

  @Override
  public void cancelJob()
  {
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFMap map;
  private final MFLocation location;
  private final MFPathFinder pathFinder;

}
