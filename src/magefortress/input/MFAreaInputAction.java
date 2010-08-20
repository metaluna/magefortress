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
import java.util.Collections;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;

/**
 *
 */
public abstract class MFAreaInputAction extends MFInputAction
{

  public MFAreaInputAction(MFGame _game, Collection<MFLocation> _designatedArea)
  {
    super(_game);
    if (_designatedArea == null || _designatedArea.size() == 0) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                          "without locations.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    for (MFLocation location : _designatedArea) {
      if (location == null) {
        String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                        "if locations contains null elements.";
        logger.severe(msg);
        throw new IllegalArgumentException(msg);
      }
    }

    this.designatedArea = _designatedArea;
  }

  //---vvv---      PROTECTED METHODS      ---vvv---
  protected Collection<MFLocation> getArea()
  {
    return Collections.unmodifiableCollection(this.designatedArea);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final Collection<MFLocation> designatedArea;
}
