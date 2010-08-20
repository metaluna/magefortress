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

import java.util.logging.Logger;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.input.MFGameInputFactory;
import magefortress.input.MFIInputTool;
import magefortress.input.MFIInputToolListener;
import magefortress.input.MFInputAction;
import magefortress.map.MFMap;

/**
 * First checks wether selected {@link MFTile tiles} are all dug out and
 * underground. Then a number of job slots have to be placed according to the
 * size of the room.
 */
public class MFBuildQuarryInputTool implements MFIInputTool
{
  public MFBuildQuarryInputTool(MFMap _map, MFGameInputFactory _inputFactory,
                                            MFIInputToolListener _toolListener)
  {
    this.validateConstructorParams(_map, _inputFactory, _toolListener);
    
    this.map = _map;
    this.inputFactory = _inputFactory;
    this.toolListener = _toolListener;
  }

  @Override
  public boolean isValid(MFLocation _location)
  {
    if (_location == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot test " +
                                                  "validity of null location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    // Room selection phase
    if (this.inputAction == null) {

      // tile is not inside map or not dug out or not underground
      if (this.map.isInsideMap(_location) &&
          this.map.getTile(_location).isUnderground() &&
          this.map.getTile(_location).isDugOut()) {
        return true;
      }

    // Job slot placement phase
    } else if (!this.allJobSlotsPlaced()) {

      if (this.inputAction.isValidJobSlotLocation(_location)) {
        return true;
      }

    // All done - should not happen
    } else {

      String msg = this.getClass().getSimpleName() + ": Cannot test validity " +
                          "of work place location if all places have been set.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
      
    }

    return false;
  }

  @Override
  public void click(MFLocation _location)
  {
    // Room selection phase
    if (this.inputAction == null) {

      if (this.isValid(_location)) {
        MFLocation[] locations = { _location };
        this.inputAction = this.inputFactory.createQuarryAction(locations);
        this.toolListener.toolPhaseChanged();
      }

    // Job slot placement phase
    } else if (!this.allJobSlotsPlaced()) {

      if (this.isValid(_location)) {
        ++this.placedJobSlots;
        if (this.allJobSlotsPlaced()) {
          this.toolListener.toolFinished();
        }
      }

    // All done - should not happen
    } else if (this.allJobSlotsPlaced()) {

      String msg = this.getClass().getSimpleName() + ": Cannot add any more work " +
                                  "places than this. All places have been set.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);

    }
    
  }

  @Override
  public MFInputAction buildAction()
  {
    // Room selection phase
    if (this.inputAction == null) {

      String msg = this.getClass().getSimpleName() + ": Cannot build quarry " +
                                  "input action when no room has been marked.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);

    // Job slot placement phase
    } else if (!this.allJobSlotsPlaced()) {

      String msg = this.getClass().getSimpleName() + ": Cannot build quarry " +
            "input cation when not all job slots have been placed. Still " +
            (this.inputAction.getJobSlotCount()-this.placedJobSlots) + " more to go.";
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
      
    }

    return this.inputAction;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFBuildQuarryInputTool.class.getName());

  private final MFMap map;
  private final MFGameInputFactory inputFactory;
  private final MFIInputToolListener toolListener;

  private MFBuildQuarryInputAction inputAction;
  private int placedJobSlots;

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
                                                              "without a game.";
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

  private boolean allJobSlotsPlaced()
  {
    assert this.inputAction != null : "Cannot check if all job slots were " +
                                  "placed without a room.";

    if (this.placedJobSlots < this.inputAction.getJobSlotCount()) {
      return false;
    }

    return true;
  }

}
