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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFLocationTest
{
  private MFLocation location;
  private final int X = 1;
  private final int Y = 2;
  private final int Z = 3;
  
  @Before
  public void setUp()
  {
    location = new MFLocation(X, Y, Z);
  }

  @Test
  public void shouldBeNeighbors()
  {
    MFLocation neighbor = new MFLocation(X, Y-1, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+1, Y-1, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+1, Y, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+1, Y+1, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X, Y+1, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-1, Y+1, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-1, Y, Z);
    assertTrue(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-1, Y-1, Z);
    assertTrue(location.isNeighborOf(neighbor));
  }

  @Test
  public void shouldNotBeNeighbors()
  {
    MFLocation neighbor = new MFLocation(X, Y-2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+1, Y-2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+2, Y-2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+2, Y-1, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+2, Y, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+2, Y+1, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+2, Y+2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X+1, Y+2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X, Y+2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-1, Y+2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-2, Y+2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-2, Y+1, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-2, Y, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-2, Y-1, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-2, Y-2, Z);
    assertFalse(location.isNeighborOf(neighbor));
    neighbor = new MFLocation(X-1, Y-2, Z);
    assertFalse(location.isNeighborOf(neighbor));
  }

  @Test
  public void shouldNeverBeNeighborOfNowhere()
  {
    MFLocation neighbor = new MFLocation(0, 0, 0);
    assertFalse(MFLocation.NOWHERE.isNeighborOf(neighbor));
    neighbor = new MFLocation(1, 1, 0);
    assertFalse(MFLocation.NOWHERE.isNeighborOf(neighbor));

    MFLocation origin = new MFLocation(0, 0, 0);
    assertFalse(origin.isNeighborOf(MFLocation.NOWHERE));
    origin = new MFLocation(1, 1, 0);
    assertFalse(origin.isNeighborOf(MFLocation.NOWHERE));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetDirection()
  {
    location.directionOf(null);
  }

  @Test
  public void shouldReturnNoDirectionWhenIdentical()
  {
    assertNull(location.directionOf(location));
  }

  @Test
  public void shouldReturnNoDirectionToNowhere()
  {
    assertNull(location.directionOf(MFLocation.NOWHERE));
    assertNull(MFLocation.NOWHERE.directionOf(MFLocation.NOWHERE));
  }

  @Test
  public void shouldReturnNoDirectionFromNowhere()
  {
    assertNull(MFLocation.NOWHERE.directionOf(location));
  }

  @Test
  public void shouldFindDirections()
  {
    MFLocation n = new MFLocation(location.x, location.y-1, location.z);
    MFEDirection expDir = MFEDirection.N;
    MFEDirection gotDir = location.directionOf(n);
    assertEquals(expDir, gotDir);

    MFLocation ne = new MFLocation(location.x+1, location.y-1, location.z);
    expDir = MFEDirection.NE;
    gotDir = location.directionOf(ne);
    assertEquals(expDir, gotDir);

    MFLocation e = new MFLocation(location.x+1, location.y, location.z);
    expDir = MFEDirection.E;
    gotDir = location.directionOf(e);
    assertEquals(expDir, gotDir);

    MFLocation se = new MFLocation(location.x+1, location.y+1, location.z);
    expDir = MFEDirection.SE;
    gotDir = location.directionOf(se);
    assertEquals(expDir, gotDir);

    MFLocation s = new MFLocation(location.x, location.y+1, location.z);
    expDir = MFEDirection.S;
    gotDir = location.directionOf(s);
    assertEquals(expDir, gotDir);

    MFLocation sw = new MFLocation(location.x-1, location.y+1, location.z);
    expDir = MFEDirection.SW;
    gotDir = location.directionOf(sw);
    assertEquals(expDir, gotDir);

    MFLocation w = new MFLocation(location.x-1, location.y, location.z);
    expDir = MFEDirection.W;
    gotDir = location.directionOf(w);
    assertEquals(expDir, gotDir);

    MFLocation nw = new MFLocation(location.x-1, location.y-1, location.z);
    expDir = MFEDirection.NW;
    gotDir = location.directionOf(nw);
    assertEquals(expDir, gotDir);
  }

  @Test
  public void shouldPrintString()
  {
    String expString = "1/2/3";
    String gotString = location.toString();
    assertEquals(expString, gotString);
  }

  @Test
  public void shouldEqual()
  {
    MFLocation eqlLocation = new MFLocation(X, Y, Z);
    MFLocation eql2Location = new MFLocation(X, Y, Z);

    // reflexive
    assertEquals(location, location);
    // symmetric
    assertEquals(location, eqlLocation);
    assertEquals(eqlLocation, location);
    // transitive
    assertEquals(location, eqlLocation);
    assertEquals(eqlLocation, eql2Location);
    assertEquals(location, eql2Location);
    // null is false
    assertFalse(location.equals(null));
  }

  @Test
  public void shouldNotEqual()
  {
    MFLocation uneqlLocation = new MFLocation(X+1, Y+2, Z+3);

    assertFalse(location.equals(uneqlLocation));
  }
}