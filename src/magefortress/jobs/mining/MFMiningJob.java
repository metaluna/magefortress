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
package magefortress.jobs.mining;

import magefortress.core.MFRoom;
import magefortress.jobs.MFAssignableJob;
import magefortress.jobs.subtasks.MFGotoLocationSubtask;
import magefortress.jobs.subtasks.MFISubtask;
import magefortress.map.MFMap;
import magefortress.map.MFPathFinder;

/**
 * Job created when a quarry calls for creatures to mine for ore or extract
 * a stone.
 */
public class MFMiningJob extends MFAssignableJob
{

  public MFMiningJob(MFQuarry _sender, MFMap _map, MFPathFinder _pathFinder)
  {
    super(_sender);
    this.map = _map;
    this.pathFinder = _pathFinder;
  }

  @Override
  protected void initJob()
  {
    MFISubtask findJobSlot   = new MFLocateJobSlotSubtask(this.getOwner(),
                                    (MFRoom) this.getSender(), this.pathFinder);
    MFISubtask gotoTile   = new MFGotoLocationSubtask(this.getOwner(),
                                                              this.pathFinder);
    MFISubtask mineTile   = new MFMineSubtask(this.getOwner(), this.map);
    this.addSubtask(findJobSlot);
    this.addSubtask(gotoTile);
    this.addSubtask(mineTile);
  }

  @Override
  public void pauseJob()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void cancelJob()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFMap map;
  private final MFPathFinder pathFinder;
}
