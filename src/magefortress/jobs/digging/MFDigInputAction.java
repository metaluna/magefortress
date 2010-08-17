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

import magefortress.input.MFAreaInputAction;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.jobs.MFConstructionSite;

/**
 * Digs out a tile
 */
public class MFDigInputAction extends MFAreaInputAction
{
  public MFDigInputAction(MFGame _game, MFLocation[] _markedForDigging)
  {
    super(_game, _markedForDigging);
  }

  @Override
  public void execute()
  {
    for (MFLocation location : this.getArea()) {
      if (!alreadyMarked(location)) {
        this.game.addConstructionSite(this.game.getGameObjectFactory().createDiggingSite(location));
      } else {
        this.game.removeConstructionSite(location);
      }
    }

  }

  //---vvv---      PRIVATE METHODS      ---vvv---
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
