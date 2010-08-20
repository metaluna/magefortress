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
package magefortress.input;

import java.util.Collection;
import java.util.logging.Logger;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.jobs.digging.MFDigInputAction;
import magefortress.jobs.digging.MFDigInputTool;
import magefortress.jobs.mining.MFBuildQuarryInputAction;
import magefortress.jobs.mining.MFBuildQuarryInputTool;

/**
 * Produces input actions and tools concerning a specific game/map.
 */
public class MFGameInputFactory
{

  public MFGameInputFactory(MFGame _game)
  {
    if (_game == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a game.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.game = _game;
  }

  public MFIInputTool createQuarryTool(MFIInputToolListener _toolListener)
  {
    return new MFBuildQuarryInputTool(this.game.getMap(), this, _toolListener);
  }

  public MFBuildQuarryInputAction createQuarryAction(Collection<MFLocation> _locations)
  {
    return new MFBuildQuarryInputAction(this.game, _locations);
  }

  public MFIInputTool createDigTool(MFIInputToolListener _toolListener)
  {
    return new MFDigInputTool(this.game.getMap(), this, _toolListener);
  }

  public MFInputAction createDigAction(Collection<MFLocation> _locations)
  {
    return new MFDigInputAction(this.game, _locations);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFGameInputFactory.class.getName());
  
  private final MFGame game;

}
