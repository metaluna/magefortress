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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFNullMovableTest
{
  private MFIMovable movable;

  @Before
  public void setUp()
  {
    this.movable = new MFNullMovable();
  }

  @Test
  public void shouldNotBeAbleMove()
  {
    assertFalse(movable.canMove());
  }

  @Test(expected=MFIllegalMoveException.class)
  public void shouldNotMove()
  {
    movable.move(MFEDirection.N);
  }

  @Test
  public void shouldNotSetSpeed()
  {
    int expSpeed = movable.getSpeed();
    int setSpeed = expSpeed + 42;
    movable.setSpeed(setSpeed);
    
    int gotSpeed = movable.getSpeed();
    assertEquals(expSpeed, gotSpeed);
  }

  @Test
  public void shouldGet0Speed()
  {
    int gotSpeed = movable.getSpeed();
    assertEquals(0, gotSpeed);
  }

  @Test
  public void shouldNotSetCurrentHeading()
  {
    MFLocation setHeading = new MFLocation(42, 42, 42);
    movable.setCurrentHeading(setHeading);

    MFLocation gotHeading = movable.getCurrentHeading();
    assertEquals(MFLocation.NOWHERE, gotHeading);
  }

  @Test
  public void shouldHaveCurrentHeadingToNowhere()
  {
    MFLocation gotHeading = movable.getCurrentHeading();
    assertEquals(MFLocation.NOWHERE, gotHeading);
  }

  @Test
  public void shouldHaveNoCapabilities()
  {
    MFCapability capability = movable.getCapability();
    assertEquals(MFCapability.NONE, capability);
  }

  @Test
  public void shouldHave0Clearance()
  {
    assertEquals(0, movable.getClearance());
  }

}