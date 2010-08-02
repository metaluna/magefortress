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
package magefortress.jobs.subtasks;

import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;
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
    this.timeLeft = -1;
  }

  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    boolean subtaskDone;

    if (this.timeLeft == -1) {
      // check if the tile can be dug out
      if (this.map.getTile(location.x, location.y, location.z).isDugOut()) {
        throw new MFSubtaskCanceledException("Tile is already dug out.");
      }
      // check if creature's position is adjacent to the tile
      MFLocation pos = this.getOwner().getLocation();
      if (!pos.isNeighborOf(this.location)) {
        throw new MFSubtaskCanceledException("Miner does not stand close enough to tile.");
      }
      this.timeLeft = calculateDiggingTime();
      subtaskDone = false;
    } else if (this.timeLeft == 0) {
      this.map.digOut(this.location);
      subtaskDone = true;
    } else {
      --this.timeLeft;
      subtaskDone = false;
    }

    return subtaskDone;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFMap map;
  private final MFLocation location;
  private int timeLeft;

  /**
   * Calculates the time it will take the creature to dig out the tile. Takes
   * into account how hard the stone is and how skilled the miner is.
   * @return The time left until the tile is dug out
   */
  private int calculateDiggingTime()
  {
    return 75;
  }
}
