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
package magefortress.map;

import magefortress.core.MFLocation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFSectionEntranceTest
{
  private MFSectionEntrance entrance;
  private MFTile mockTile;

  @Before
  public void setUp()
  {
    this.mockTile = mock(MFTile.class);
    when(this.mockTile.getLocation()).thenReturn(new MFLocation(1, 1, 1));
    this.entrance = new MFSectionEntrance(this.mockTile);
  }

  //---------------------------- TEST CONSTRUCTOR ------------------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateEntrance()
  {
    new MFSectionEntrance(null);
  }

  @Test
  public void shouldSetEntranceOnTile()
  {
    verify(this.mockTile).setEntrance(this.entrance);
  }

  //-------------------------- addEdge() TESTS ---------------------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddNullEdge()
  {
    this.entrance.addEdge(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddEdgeWithIllegalFrom()
  {
    MFEdge edge = mock(MFEdge.class);
    MFSectionEntrance otherEntrance = mock(MFSectionEntrance.class);
    when(edge.getFrom()).thenReturn(otherEntrance);

    this.entrance.addEdge(edge);
  }

  @Test
  public void shouldAddEdge()
  {
    MFEdge expEdge = mock(MFEdge.class);
    when(expEdge.getFrom()).thenReturn(this.entrance);

    this.entrance.addEdge(expEdge);
    assertEquals(1, this.entrance.getEdges().size());
    MFEdge gotEdge = this.entrance.getEdges().get(0);
    assertEquals(expEdge, gotEdge);
  }

}