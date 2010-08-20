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
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.input.MFGameInputFactory;
import magefortress.input.MFIInputTool;
import magefortress.input.MFIInputToolListener;
import magefortress.input.MFInputAction;
import magefortress.map.MFMap;

/**
 * Checks wether a selected tile is underground and wether it has not been
 * dug out yet.
 */
public class MFDigInputTool implements MFIInputTool
{
  /**
   * Constructor
   * @param _map The map is needed to check wether a tile is valid.
   * @param _inputFactory The game input factory used during the construction
   *                      of the dig input action.
   */
  public MFDigInputTool(MFMap _map, MFGameInputFactory _inputFactory, MFIInputToolListener _toolListener)
  {
    validateConstructorParams(_map, _inputFactory, _toolListener);

    this.map = _map;
    this.inputFactory = _inputFactory;
    this.toolListener = _toolListener;
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

    if (this.inputAction == null) {
      // tile is not inside map or already dug out
      if (this.map.isInsideMap(_location) &&
          !this.map.getTile(_location).isDugOut()) {
        return true;
      }
    } else {

      String msg = this.getClass().getSimpleName() + ": Cannot test validity " +
                  "if input action was built. Please use a new dig input tool.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);

    }

    return false;
  }

  @Override
  public void click(MFLocation _location)
  {
    if (_location == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot generate " +
                                          "dig input action for null location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    
    if (this.inputAction == null && this.isValid(_location)) {

      final MFLocation[] locations = {_location};
      this.inputAction = this.inputFactory.createDigAction(locations);
      this.toolListener.toolFinished();

    } else if (this.inputAction != null) {
      
      String msg = this.getClass().getSimpleName() + ": Cannot dig any more " +
                                  "than this. Please use a new dig input tool.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
      
    }

  }

  @Override
  public MFInputAction buildAction()
  {
    if (this.inputAction == null) {

      String msg = this.getClass().getSimpleName() + ": Cannot generate " +
                                    "input action when no tiles were selected.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);

    }

    return this.inputAction;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Log */
  private static final Logger logger = Logger.getLogger(MFDigInputTool.class.getName());

  /** The map */
  private final MFMap map;
  /** The game input factory used for constructing the dig input action*/
  private final MFGameInputFactory inputFactory;
  /** The listener to notify when a phase change takes place */
  private final MFIInputToolListener toolListener;

  private MFInputAction inputAction;

  private void validateConstructorParams(MFMap _map, MFGameInputFactory _inputFactory,
          MFIInputToolListener _toolListener)
  {
    if (_map == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_inputFactory == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                "without a game input factory.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_toolListener == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without" +
                                                      "an input tool listener.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }

}
