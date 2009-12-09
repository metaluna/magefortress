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

import java.awt.Point;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFMapTest
{

  public MFMapTest()
  {
  }

  @Test
  public void shouldConvertToTilespace()
  {
    MFLocation expLocation;
    MFLocation gotLocation;
    int length = MFTile.TILESIZE;

    expLocation = new MFLocation(0, 0, 0);
    gotLocation = MFMap.convertToTilespace(0, 0, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(0, 0, 0);
    gotLocation = MFMap.convertToTilespace(length-1, length-1, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(1, 1, 0);
    gotLocation = MFMap.convertToTilespace(length, length, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(999, 999, 999);
    gotLocation = MFMap.convertToTilespace(length*999, length*999, 999, 0, 0);
    assertEquals(expLocation, gotLocation);

    expLocation = new MFLocation(-1, -1, 0);
    gotLocation = MFMap.convertToTilespace(-1, -1, 0, 0, 0);
    assertEquals(expLocation, gotLocation);

  }

  @Test
  public void shouldConvertFromTilespace()
  {
    Point expPoint;
    Point gotPoint;

    expPoint = new Point(0, 0);
    gotPoint = MFMap.convertFromTilespace(0, 0);
    assertEquals(expPoint, gotPoint);

    expPoint = new Point(MFTile.TILESIZE, MFTile.TILESIZE);
    gotPoint = MFMap.convertFromTilespace(1, 1);
    assertEquals(expPoint, gotPoint);

    expPoint = new Point(MFTile.TILESIZE*999, MFTile.TILESIZE*999);
    gotPoint = MFMap.convertFromTilespace(999, 999);
    assertEquals(expPoint, gotPoint);

    expPoint = new Point(-MFTile.TILESIZE, -MFTile.TILESIZE);
    gotPoint = MFMap.convertFromTilespace(-1, -1);
    assertEquals(expPoint, gotPoint);
  }

}