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
package magefortress.input;

import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.jobs.MFConstructionSite;
import magefortress.jobs.MFDiggingSite;

/**
 * Digs out a tile
 */
public class MFDigInputAction extends MFInputAction
{
  public MFDigInputAction(MFGame _game, MFLocation[] _markedForDigging)
  {
    super(_game);
    if (_markedForDigging == null || _markedForDigging.length == 0) {
      String msg = "MFDigInputAction: Can't create action without locations.";
      Logger.getLogger(MFDigInputAction.class.getName()).log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    this.markedForDigging = _markedForDigging;
  }

  @Override
  public void execute()
  {
    for (MFLocation location : markedForDigging) {
      if (!alreadyMarked(location)) {
        this.game.addConstructionSite(this.game.getGameObjectFactory().createDiggingSite(location));
      } else {
        this.game.removeConstructionSite(location);
      }
    }

  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private final MFLocation[] markedForDigging;

  /**
   * Checks for the presence of a digging site.
   * @param _location The location
   * @return if there's previous digging site
   */
  private boolean alreadyMarked(MFLocation _location)
  {
    boolean marked = false;
    // check for presence
    for (MFConstructionSite site : this.game.getConstructionSites()) {
      if (site instanceof MFDiggingSite && site.getLocation().equals(_location)) {
        return true;
      }
    }

    return marked;
  }
}
