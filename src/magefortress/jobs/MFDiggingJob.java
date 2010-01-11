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

import magefortress.jobs.subtasks.MFLocateWalkableNeighorSubtask;
import magefortress.jobs.subtasks.MFSubtask;
import magefortress.jobs.subtasks.MFDigOutTileSubtask;
import magefortress.jobs.subtasks.MFGotoLocationSubtask;
import magefortress.core.MFLocation;
import magefortress.map.MFMap;

/**
 * Digs out a single tile.
 */
public class MFDiggingJob extends MFJob
{

  public MFDiggingJob(MFDiggingSite _sender, MFMap _map, MFLocation _location)
  {
    super(_sender);
    this.map = _map;
    this.location = _location;
  }

  @Override
  void initJob()
  {
    MFSubtask findNeighbor = new MFLocateWalkableNeighorSubtask(this.getOwner(), this.location);
    MFSubtask gotoTile  = new MFGotoLocationSubtask(this.map, this.getOwner());
    MFSubtask digTile   = new MFDigOutTileSubtask(this.getOwner(), this.map, this.location);
    this.subtaskQueue.add(findNeighbor);
    this.subtaskQueue.add(gotoTile);
    this.subtaskQueue.add(digTile);
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

}
