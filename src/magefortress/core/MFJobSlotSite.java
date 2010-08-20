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

import java.awt.Graphics2D;
import magefortress.channel.MFCommunicationChannel;
import magefortress.channel.MFIChannelSubscriber;
import magefortress.jobs.MFAssignableJob;
import magefortress.jobs.MFConstructionSite;
import magefortress.jobs.MFIConstructionSiteListener;
import magefortress.jobs.MFJobFactory;
import magefortress.map.MFMap;

/**
 *
 */
public class MFJobSlotSite extends MFConstructionSite
{

  MFJobSlotSite(MFLocation _location, MFMap _map, MFJobFactory _jobFactory,
          MFCommunicationChannel _channel, MFIConstructionSiteListener _siteListener)
  {
    super(_location, 1, 1, _jobFactory, _channel, _siteListener);
    if (_map == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

  }

  @Override
  public void update()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isJobAvailable()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MFAssignableJob getJob()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void newSubscriber(MFIChannelSubscriber subscriber)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void jobDone(MFAssignableJob _finishedJob)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

}
