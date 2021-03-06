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
package magefortress.creatures.behavior.movable;

import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;

/**
 *
 */
public class MFMockMovable implements MFIMovable
{

  public boolean canMove()
  {
    return true;
  }

  public void move(MFEDirection _direction)
  {
  }

  public void setSpeed(int _speed)
  {
  }

  public int getSpeed()
  {
    return 1;
  }

  public void setCurrentHeading(MFLocation _heading)
  {
  }

  public MFLocation getCurrentHeading()
  {
    return new MFLocation(1,1,1);
  }

  public MFCapability getCapability()
  {
    return MFCapability.WALK;
  }

  public int getClearance()
  {
    return 1;
  }

  public void setClearance(int _clearance)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MFLocation getLocation()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void setLocation(MFLocation _location)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

}
