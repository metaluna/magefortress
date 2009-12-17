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
package magefortress.jobs;

import java.awt.Graphics2D;
import magefortress.channel.MFChannelMessage;
import magefortress.channel.MFIChannelSubscriber;
import magefortress.core.MFLocation;
import magefortress.map.MFMap;

/**
 * Marks a square for digging and emits a job call over the digging channel.
 */
public class MFDiggingSite extends MFConstructionSite
{

  public MFDiggingSite(MFLocation _location, MFMap _map)
  {
    super(_location, 1, 1);
    if (_map == null) {
      String msg = "DiggingSite: Map must not be null. Cannot instantiate " +
              "DiggingSite";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_location.x >= _map.getWidth() || _location.y >= _map.getHeight() ||
            _location.z >= _map.getDepth()) {
      String msg = "DiggingSite: Illegal location. Cannot instantiate " +
              "DiggingSite. Got: " + _location;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.map = _map;
    this.jobAvailable = true;
  }

  @Override
  public void update()
  {
  }

  @Override
  public void paint(Graphics2D _g, int _x_translation, int _y_translation, MFLocation _location)
  {
  }

  @Override
  public boolean isJobAvailable()
  {
    return this.jobAvailable;
  }

  @Override
  public MFJob getJob()
  {
    MFDiggingJob result = null;

    if (this.jobAvailable) {
      result = new MFDiggingJob(this, this.map, this.getLocation());
      this.jobAvailable = false;
    }

    return result;
  }

  @Override
  public void newSubscriber(MFIChannelSubscriber _subscriber)
  {
    if (this.jobAvailable) {
      MFChannelMessage jobOfferMsg = new MFChannelMessage(this);
      _subscriber.update(jobOfferMsg);
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFMap map;
  private boolean jobAvailable;
}
