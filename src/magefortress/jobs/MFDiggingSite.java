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

import java.awt.Color;
import java.awt.Graphics2D;
import magefortress.channel.MFChannelMessage;
import magefortress.channel.MFCommunicationChannel;
import magefortress.channel.MFIChannelSubscriber;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.map.MFMap;
import magefortress.map.MFTile;

/**
 * Marks a square for digging and emits a job call over the digging channel.
 */
public class MFDiggingSite extends MFConstructionSite
{

  public MFDiggingSite(MFLocation _location, MFMap _map, MFJobFactory _jobFactory, MFCommunicationChannel _channel)
  {
    super(_location, 1, 1, _jobFactory, _channel);
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

    this.checkReachability();

    // send job offer
    if (!this.unreachable) {
      this.getChannel().enqueueMessage(this.getJobOfferMessage());
    }
  }

  @Override
  public void update(long _currentTime)
  {
    if (_currentTime >= NEXT_SWITCH) {
      PAINT_BLACK = !PAINT_BLACK;
      NEXT_SWITCH = _currentTime + BLINKING_INTERVAL;
    }
  }

  @Override
  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    // set color
    if (PAINT_BLACK) {
      _g.setColor(Color.BLACK);
    } else {
      _g.setColor(Color.lightGray);
    }
    // calculate position
    int size = MFTile.TILESIZE;
    int x = this.getLocation().x*size + _x_translation;
    int y = this.getLocation().y*size + _y_translation;

    _g.fillRect(x, y, size, size);
  }

  @Override
  public boolean isJobAvailable()
  {
    return this.jobAvailable && !this.unreachable;
  }

  @Override
  public MFAssignableJob getJob()
  {
    if (this.unreachable) {
      String msg = this.getClass().getName() + ": Someone's trying " +
            "to get a digging job for an unreachable tile@" + this.getLocation();
      logger.severe(msg);
      throw new MFPrerequisitesNotMetException(msg);
    }

    MFAssignableJob result = null;

    if (this.jobAvailable) {
      result = this.getJobFactory().createDiggingJob(this);
      this.jobAvailable = false;
    }

    return result;
  }

  @Override
  public void newSubscriber(MFIChannelSubscriber _subscriber)
  {
    if (this.jobAvailable && !this.unreachable) {
      _subscriber.update(this.getJobOfferMessage());
    }
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final int BLINKING_INTERVAL = 750;
  private static long NEXT_SWITCH;
  private static boolean PAINT_BLACK;
  private final MFMap map;
  private boolean jobAvailable;
  private boolean unreachable;

  // checks if there are any non-underground tiles around the digging site
  private void checkReachability()
  {
    this.unreachable = true;

    for (MFEDirection direction : MFEDirection.values()) {
      MFLocation loc = this.getLocation().locationOf(direction);
      if (this.map.isInsideMap(loc)) {
        MFTile tile = this.map.getTile(loc);
        if (!tile.isUnderground() || tile.isDugOut()) {
          this.unreachable = false;
          return;
        }
      }
    }
  }

  private MFChannelMessage getJobOfferMessage()
  {
    return new MFChannelMessage(this);
  }
}
