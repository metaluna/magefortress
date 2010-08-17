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
package magefortress.jobs.digging;

import java.util.logging.Logger;
import magefortress.core.Immutable;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.input.MFIInputTool;
import magefortress.input.MFInputAction;
import magefortress.map.MFMap;

/**
 * Checks wether a selected tile is underground and wether it has not been
 * dug out yet.
 *
 * <p>Objects of this class are immutable.
 */
public class MFDigInputTool implements MFIInputTool, Immutable
{
  /**
   * Constructor
   * @param _map The map is needed to check wether a tile is valid.
   * @param _game The game is used during the construction of the dig input action. Replace with factory!
   */
  public MFDigInputTool(MFMap _map, MFGame _game)
  {
    if (_map == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_game == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a game.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.map = _map;
    this.game = _game;
  }

  //--vvv---      INPUT TOOL INTERFACE METHODS      ---vvv---
  @Override
  public boolean isValid(MFLocation _location)
  {
    if (_location == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot test " +
                                                  "validity of null location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    
    // tile is not inside map or already dug out
    if (!this.map.isInsideMap(_location) ||
        this.map.getTile(_location).isDugOut()) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public MFInputAction click(MFLocation _location)
  {
    if (_location == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot generate " +
                                          "dig input action for null location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (!this.isValid(_location)) {
      String msg = this.getClass().getSimpleName() + ": Cannot generate " +
                          "dig input action for invalid location@" + _location +
                          ". Check validity before getting the input action!";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }
    
    final MFLocation[] locations = {_location};
    final MFDigInputAction result = new MFDigInputAction(this.game, locations);
    return result;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Log */
  private static final Logger logger = Logger.getLogger(MFDigInputTool.class.getName());

  /** The map */
  private final MFMap map;
  /** The game - used for constructing the dig input action*/
  private final MFGame game;

}
