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
import magefortress.channel.MFChannelFactory;
import magefortress.creatures.MFRace;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.instrumentable.MFEJob;
import magefortress.creatures.behavior.movable.MFWalksOnTwoLegs;
import magefortress.graphics.MFImageLibrary;
import magefortress.graphics.MFStillPaintable;
import magefortress.jobs.digging.MFDiggingSite;
import magefortress.jobs.MFIConstructionSiteListener;
import magefortress.jobs.MFJobFactory;
import magefortress.map.MFMap;
import magefortress.map.MFPathFinder;

/**
 * In addition to the construction parameters it instantiates a path finder and
 * a channel factory (singletons).
 */
public class MFGameObjectFactory
{
  public MFGameObjectFactory(MFImageLibrary _imgLib, MFJobFactory _jobFactory, MFMap _map, MFIConstructionSiteListener _siteListener)
  {
    this.imgLib = _imgLib;
    this.jobFactory = _jobFactory;
    this.map = _map;
    this.siteListener = _siteListener;
    this.pathFinder = MFPathFinder.getInstance();
    this.pathFinder.setMap(this.map);
    this.channelFactory = MFChannelFactory.getInstance();
  }

  public MFCreature createCreature(MFRace _race)
  {
    MFCreature result = _race.createCreature();
    result.setMovingBehavior(new MFWalksOnTwoLegs());
    BufferedImage img = this.imgLib.get(DEFAULT_CREATURE_SPRITE);
    MFStillPaintable sprite = new MFStillPaintable(img);
    result.setDrawingBehavior(sprite);
    return result;
  }

  public MFPathFinder createPathFinder()
  {
    return this.pathFinder;
  }

  public MFDiggingSite createDiggingSite(MFLocation _location)
  {
    return new MFDiggingSite(_location, this.map, jobFactory, 
                            this.channelFactory.getChannel(MFEJob.DIGGING),
                            this.siteListener);
  }

  public MFChannelFactory createChannelFactory()
  {
    return this.channelFactory;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final String DEFAULT_CREATURE_SPRITE = "sticky.png";
  private final MFImageLibrary imgLib;
  private final MFJobFactory jobFactory;
  private final MFMap map;
  private final MFIConstructionSiteListener siteListener;
  private final MFPathFinder pathFinder;
  private final MFChannelFactory channelFactory;
}
