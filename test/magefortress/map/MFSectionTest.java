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

public class MFSectionTest
{
  private MFSection section;
  private MFMap mockMap;

  @Before
  public void setUp()
  {
    this.mockMap = mock(MFMap.class);
    this.section = new MFSection(this.mockMap, 0);
  }

  //------------------------ addTile() TESTS -----------------------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddNullTile()
  {
    this.section.addTile(null);
  }

  @Test
  public void shouldNotAddTileTwice()
  {
    MFTile mockTile = mock(MFTile.class);
    when(mockTile.getLocation()).thenReturn(new MFLocation(0, 0, 0));

    int expTileCount = this.section.getSize() + 1;

    this.section.addTile(mockTile);
    this.section.addTile(mockTile);

    verify(mockTile).setParentSection(this.section);
    int gotTileCount = this.section.getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  @Test
  public void shouldAddTile()
  {
    MFTile mockTile = mock(MFTile.class);
    when(mockTile.getLocation()).thenReturn(new MFLocation(1, 1, 1));

    int expTileCount = this.section.getSize() + 1;

    this.section.addTile(mockTile);

    verify(mockTile).setParentSection(this.section);
    int gotTileCount = this.section.getSize();
    assertEquals(expTileCount, gotTileCount);
  }

  //-------------------------- addEntrance() TESTS -----------------------------

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddNullEntrance()
  {
    this.section.addEntrance(null);
  }

  @Test
  public void shouldNotAddEntranceTwice()
  {
    MFSectionEntrance mockEntrance = mock(MFSectionEntrance.class);
    when(mockEntrance.getLocation()).thenReturn(new MFLocation(2, 2, 2));

    int expEntranceCount = this.section.getEntrances().size() + 1;

    this.section.addEntrance(mockEntrance);
    this.section.addEntrance(mockEntrance);

    int gotEntranceCount = this.section.getEntrances().size();
    assertEquals(expEntranceCount, gotEntranceCount);
    assertTrue(this.section.getEntrances().contains(mockEntrance));
  }

  @Test
  public void shouldAddEntrance()
  {
    MFSectionEntrance mockEntrance = mock(MFSectionEntrance.class);
    when(mockEntrance.getLocation()).thenReturn(new MFLocation(3, 3, 3));

    int expEntranceCount = this.section.getEntrances().size() + 1;

    this.section.addEntrance(mockEntrance);

    int gotEntranceCount = this.section.getEntrances().size();
    assertEquals(expEntranceCount, gotEntranceCount);
    assertTrue(this.section.getEntrances().contains(mockEntrance));
  }

  //-------------------------------- uniteWith() TESTS -------------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotUniteNullSection()
  {
    this.section.uniteWith(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotUniteSameSection()
  {
    this.section.uniteWith(this.section);
  }

  @Test
  public void shouldUniteSections()
  {
    // set up section 1 with size 2
    MFTile mockTile1 = mock(MFTile.class);
    when(mockTile1.getLocation()).thenReturn(new MFLocation(4, 4, 0));

    MFTile mockTile2 = mock(MFTile.class);
    when(mockTile2.getLocation()).thenReturn(new MFLocation(4, 5, 0));

    MFSectionEntrance mockEntrance1 = mock(MFSectionEntrance.class);
    when(mockEntrance1.getLocation()).thenReturn(new MFLocation(5, 5, 0));

    this.section.addTile(mockTile1);
    this.section.addTile(mockTile2);
    this.section.addEntrance(mockEntrance1);

    // set up section 2 with size 1
    MFSection otherSection = new MFSection(mock(MFMap.class), 0);

    MFTile mockTile3 = mock(MFTile.class);
    when(mockTile3.getLocation()).thenReturn(new MFLocation(6, 6, 0));

    MFSectionEntrance mockEntrance2 = mock(MFSectionEntrance.class);
    when(mockEntrance2.getLocation()).thenReturn(new MFLocation(7, 7, 0));

    otherSection.addTile(mockTile3);
    otherSection.addEntrance(mockEntrance2);

    // set up test parameters
    int expEntranceCount = this.section.getEntrances().size() + otherSection.getEntrances().size();
    int expTileCount = this.section.getSize() + otherSection.getSize();
    MFSection expSection = this.section;

    MFSection gotSection = this.section.uniteWith(otherSection);

    // check correct section was added
    assertEquals(expSection, gotSection);

    // check 1st section
    int gotEntranceCount = this.section.getEntrances().size();
    int gotTileCount = this.section.getSize();
    assertEquals(expEntranceCount, gotEntranceCount);
    assertTrue(this.section.getEntrances().contains(mockEntrance2));
    assertEquals(expTileCount, gotTileCount);

    // check 2nd section
    assertEquals(0, otherSection.getEntrances().size());
    assertEquals(0, otherSection.getSize());

    // check tiles
    verify(mockTile1).setParentSection(this.section);
    verify(mockTile2).setParentSection(this.section);
    verify(mockTile3).setParentSection(otherSection);
    verify(mockTile3).setParentSection(this.section);
  }

  //------------------------------- contains() TESTS ---------------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotContainNoLocation()
  {
    this.section.contains(null);
  }

  @Test
  public void shouldContainLocation()
  {
    MFLocation location = new MFLocation(1, 1, 0);
    MFTile mockTile = mock(MFTile.class);
    when(mockTile.getLocation()).thenReturn(location);

    this.section.addTile(mockTile);

    boolean gotResult = this.section.contains(location);
    assertTrue(gotResult);
  }

  @Test
  public void shouldNotContainLocation()
  {
    MFLocation location = new MFLocation(1, 1, 0);

    boolean gotResult = this.section.contains(location);
    assertFalse(gotResult);
  }

}