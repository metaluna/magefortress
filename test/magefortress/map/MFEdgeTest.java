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

import java.util.EnumSet;
import magefortress.creatures.behavior.movable.MFEMovementType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFEdgeTest
{

  @Before
  public void setUp()
  {
  }

  //------------------------ CONSTRUCTOR TESTS ---------------------------------
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutStartingEntrance()
  {
    new MFEdge(null, mock(MFSectionEntrance.class), 1, 1, EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutTargetEntrance()
  {
    new MFEdge(mock(MFSectionEntrance.class), null, 1, 1, EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutSameStartAndGoal()
  {
    MFSectionEntrance mockEntrance = mock(MFSectionEntrance.class);
    new MFEdge(mockEntrance, mockEntrance, 1, 1, EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutValidCost()
  {
    new MFEdge(mock(MFSectionEntrance.class), mock(MFSectionEntrance.class), 0,
            1, EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutValidClearance()
  {
    new MFEdge(mock(MFSectionEntrance.class), mock(MFSectionEntrance.class), 1,
            0, EnumSet.of(MFEMovementType.WALK));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutCapabilities()
  {
    new MFEdge(mock(MFSectionEntrance.class), mock(MFSectionEntrance.class), 1,
            1, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutValidCapabilities()
  {
    new MFEdge(mock(MFSectionEntrance.class), mock(MFSectionEntrance.class), 1,
            1, EnumSet.noneOf(MFEMovementType.class));
  }

}