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
package magefortress.core;

import java.awt.image.BufferedImage;
import magefortress.creatures.MFRace;
import magefortress.creatures.MFCreature;
import magefortress.graphics.MFImageLibrary;
import magefortress.graphics.MFStillPaintable;
import magefortress.jobs.MFJobFactory;
import magefortress.map.MFMap;
import magefortress.map.MFPathFinder;

/**
 *
 */
public class MFGameObjectFactory
{
  public MFGameObjectFactory(MFImageLibrary _imgLib, MFJobFactory _jobFactory, MFMap _map)
  {
    this.imgLib = _imgLib;
    this.jobFactory = _jobFactory;
    this.pathFinder = MFPathFinder.getInstance();
    this.pathFinder.setMap(_map);
  }

  public MFCreature createCreature(MFRace _race)
  {
    MFCreature result = _race.createCreature();
    BufferedImage img = this.imgLib.get(DEFAULT_CREATURE_SPRITE);
    MFStillPaintable sprite = new MFStillPaintable(img);
    result.setDrawingBehavior(sprite);
    return result;
  }

  public MFPathFinder createPathFinder()
  {
    return this.pathFinder;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final String DEFAULT_CREATURE_SPRITE = "sticky.png";
  private final MFImageLibrary imgLib;
  private final MFJobFactory jobFactory;
  private final MFPathFinder pathFinder;
}
