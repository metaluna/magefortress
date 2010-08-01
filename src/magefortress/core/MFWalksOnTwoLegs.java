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

import java.util.EnumSet;

/**
 *
 */
public class MFWalksOnTwoLegs implements MFIMovable
{

  public MFWalksOnTwoLegs()
  {
    this.speed = 1;
    this.clearance = 1;
    this.location = MFLocation.NOWHERE;
    this.heading = MFLocation.NOWHERE;
    this.capabilities = EnumSet.of(MFEMovementType.WALK);
  }

  public boolean canMove()
  {
    return true;
  }

  public void move(MFEDirection _direction)
  {
    this.location = this.location.locationOf(_direction);
  }

  public void setSpeed(int _speed)
  {
    this.speed = _speed;
  }

  public int getSpeed()
  {
    return this.speed;
  }

  public void setCurrentHeading(MFLocation _heading)
  {
    this.heading = _heading;
  }

  public MFLocation getCurrentHeading()
  {
    return this.heading;
  }

  public EnumSet<MFEMovementType> getCapabilities()
  {
    return this.capabilities;
  }

  public int getClearance()
  {
    return this.clearance;
  }

  public void setClearance(int _clearance)
  {
    this.clearance = _clearance;
  }

  public MFLocation getLocation()
  {
    return this.location;
  }

  public void setLocation(MFLocation _location)
  {
    this.location = _location;
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private EnumSet<MFEMovementType> capabilities;
  private int                      clearance;
  private MFLocation               heading;
  private MFLocation               location;
  private int                      speed;
}
