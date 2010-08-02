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
package magefortress.creatures.behavior;

import java.util.EnumSet;
import magefortress.core.Immutable;
import magefortress.core.MFEDirection;
import magefortress.core.MFIllegalMoveException;
import magefortress.core.MFLocation;

/**
 * Null implementation of MFIMovable being unable to move. Its clearance and 
 * speed is 0 and it has no movement types.
 */
public class MFNullMovable implements MFIMovable, Immutable
{

  @Override
  public boolean canMove()
  {
    return false;
  }

  @Override
  public void move(MFEDirection _direction)
  {
    throw new MFIllegalMoveException("Creature is unable to move.");
  }

  @Override
  public void setSpeed(int _speed)
  {
  }

  @Override
  public int getSpeed()
  {
    return 0;
  }

  @Override
  public void setCurrentHeading(MFLocation _heading)
  {
  }

  @Override
  public MFLocation getCurrentHeading()
  {
    return MFLocation.NOWHERE;
  }

  @Override
  public EnumSet<MFEMovementType> getCapabilities()
  {
    return EnumSet.noneOf(MFEMovementType.class);
  }

  @Override
  public int getClearance()
  {
    return 0;
  }

  @Override
  public void setClearance(int _clearance)
  {
    
  }

  @Override
  public MFLocation getLocation()
  {
    return MFLocation.NOWHERE;
  }

  @Override
  public void setLocation(MFLocation _location)
  {
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

}
