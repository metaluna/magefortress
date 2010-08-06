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
package magefortress.map;

import java.util.EnumSet;
import magefortress.creatures.behavior.movable.MFEMovementType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFTemplateAStarTest
{
  private MFTemplateAStar search;

  @Before
  public void setUp()
  {
  }

  //--------------------------- INSTANTIATION TESTS ----------------------------

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFMockAStar(null, mock(MFTile.class), mock(MFTile.class), 1,
                      EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutStartTile()
  {
    new MFMockAStar(mock(MFMap.class), null, mock(MFTile.class), 1,
                      EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutTargetTile()
  {
    new MFMockAStar(mock(MFMap.class), mock(MFTile.class), null, 1,
                      EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutClearance()
  {
    new MFMockAStar(mock(MFMap.class), mock(MFTile.class), mock(MFTile.class),
                      0, EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutCapabilities()
  {
    new MFMockAStar(mock(MFMap.class), mock(MFTile.class), mock(MFTile.class),
                      1, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithZeroCapabilities()
  {
    new MFMockAStar(mock(MFMap.class), mock(MFTile.class), mock(MFTile.class),
                      1, EnumSet.noneOf(MFEMovementType.class));
  }

  //-------------------- PATH START CONDITIONS TESTS ---------------------------

  @Test(expected=IllegalStateException.class)
  public void shouldNotSearchPathStartingOnSolidTile()
  {
    MFTile mockStart = mock(MFTile.class);
    when(mockStart.getClearance(MFEMovementType.WALK)).thenReturn(1);
    when(mockStart.isWalkable(MFEMovementType.WALK)).thenReturn(false);
    MFTile mockGoal = mock(MFTile.class);
    when(mockGoal.getClearance(MFEMovementType.WALK)).thenReturn(1);
    when(mockGoal.isWalkable(MFEMovementType.WALK)).thenReturn(true);

    this.search = new MFMockAStar(mock(MFMap.class), mockStart, mockGoal, 1,
                                  EnumSet.of(MFEMovementType.WALK));
    search.findPath();
  }

  @Test(expected=IllegalStateException.class)
  public void shouldNotSearchPathStartingOnNarrowPassage()
  {
    MFTile mockStart = mock(MFTile.class);
    when(mockStart.getClearance(MFEMovementType.WALK)).thenReturn(1);
    when(mockStart.isWalkable(MFEMovementType.WALK)).thenReturn(true);
    MFTile mockGoal = mock(MFTile.class);
    when(mockGoal.getClearance(MFEMovementType.WALK)).thenReturn(2);
    when(mockGoal.isWalkable(MFEMovementType.WALK)).thenReturn(true);

    this.search = new MFMockAStar(mock(MFMap.class), mockStart, mockGoal, 2,
                                  EnumSet.of(MFEMovementType.WALK));
    search.findPath();
  }

  @Test(expected=IllegalStateException.class)
  public void shouldNotSearchPathEndingOnSolidTile()
  {
    MFTile mockStart = mock(MFTile.class);
    when(mockStart.getClearance(MFEMovementType.WALK)).thenReturn(1);
    when(mockStart.isWalkable(MFEMovementType.WALK)).thenReturn(true);
    MFTile mockGoal = mock(MFTile.class);
    when(mockGoal.getClearance(MFEMovementType.WALK)).thenReturn(1);
    when(mockGoal.isWalkable(MFEMovementType.WALK)).thenReturn(false);

    this.search = new MFMockAStar(mock(MFMap.class), mockStart, mockGoal, 1,
                                  EnumSet.of(MFEMovementType.WALK));
    search.findPath();
  }

  @Test(expected=IllegalStateException.class)
  public void shouldNotSearchPathEndingOnNarrowPassage()
  {
    MFTile mockStart = mock(MFTile.class);
    when(mockStart.getClearance(MFEMovementType.WALK)).thenReturn(2);
    when(mockStart.isWalkable(MFEMovementType.WALK)).thenReturn(true);
    MFTile mockGoal = mock(MFTile.class);
    when(mockGoal.getClearance(MFEMovementType.WALK)).thenReturn(1);
    when(mockGoal.isWalkable(MFEMovementType.WALK)).thenReturn(true);

    this.search = new MFMockAStar(mock(MFMap.class), mockStart, mockGoal, 2,
                                  EnumSet.of(MFEMovementType.WALK));
    search.findPath();
  }

}