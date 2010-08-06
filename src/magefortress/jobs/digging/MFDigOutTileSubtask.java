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
package magefortress.jobs.digging;

import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.instrumentable.MFEJob;
import magefortress.jobs.subtasks.MFSubtask;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;
import magefortress.map.MFMap;

/**
 * Digs out a single tile.
 */
public class MFDigOutTileSubtask extends MFSubtask
{

  public MFDigOutTileSubtask(MFCreature _owner, MFMap _map, MFLocation _location)
  {
    super(_owner);
    this.map = _map;
    this.location = _location;
    this.timeLeft = 0;
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    boolean subtaskDone;

    if (this.timeLeft == 0) {
      // check if the tile can be dug out
      if (this.map.getTile(this.location).isDugOut()) {
        String msg = "Tile@" + this.location + " is already dug out.";
        logger.warning(msg);
        throw new MFSubtaskCanceledException(msg);
      }
      // check if creature's position is adjacent to the tile
      MFLocation pos = this.getOwner().getLocation();
      if (!pos.isNeighborOf(this.location)) {
        String msg = "Miner does not stand close enough to tile@" + this.location;
        logger.warning(msg);
        throw new MFSubtaskCanceledException(msg);
      }
      this.timeLeft = calculateDiggingTime();
      subtaskDone = false;
    } else if (this.timeLeft == 1) {
      this.map.digOut(this.location);
      this.getOwner().gainJobExperience(JOB_SKILL_USED, 2);
      subtaskDone = true;
    } else {
      --this.timeLeft;
      subtaskDone = false;
    }

    return subtaskDone;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final int MIN_DIGGING_TIME = 10;
  private static final MFEJob JOB_SKILL_USED = MFEJob.DIGGING;
  private final MFMap map;
  private final MFLocation location;
  private int timeLeft;

  /**
   * Calculates the time it will take the creature to dig out the tile. Takes
   * the stone's hardness and the miner's skill into account.
   * @return The time left in ticks until the tile is dug out
   */
  private int calculateDiggingTime()
  {
    //hardness should be between 200 and 800 (1st draft)
    final int hardness = 500;
    final int jobSkill = this.getOwner().getJobSkill(JOB_SKILL_USED);

    int result = MIN_DIGGING_TIME;
    result += Math.max(0, hardness-jobSkill);

    return result;
  }
}
