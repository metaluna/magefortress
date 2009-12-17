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
package magefortress.core;

import magefortress.map.MFTile;
import java.util.Set;
import magefortress.channel.MFIChannelSender;
import magefortress.channel.MFIChannelSubscriber;
import magefortress.jobs.MFJob;

/**
 * A workshop is a place where items are produced. Before anything can be
 * manufactured, blueprints for the products have to be placed inside the room.
 * The player then can choose an object he wants to have produced. The workshop
 * then sends out calls to creatures that it has a job available.
 *
 * At a workshop multiple persons can work according to the room's size. If more
 * than one person is working in the workshop they have to work on different
 * items.
 *
 * Efficiency of the workshop can be improved by placing furniture in the
 * room. Every kind of workshop gets best results with different kinds of
 * furniture. Furnitur can be used by all workers in the workshop simultanously.
 */
public class MFWorkshop extends MFRoom implements MFIChannelSender
{
  public MFWorkshop(String _name, Set<MFTile> _tiles)
  {
    super(_name, _tiles);
  }

  @Override
  public boolean isJobAvailable()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public MFJob getJob()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public MFLocation getLocation()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void newSubscriber(MFIChannelSubscriber subscriber)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void tileAdded(MFTile _tile)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void tileRemoved(MFTile _tile)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void tileUpdated()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

}
