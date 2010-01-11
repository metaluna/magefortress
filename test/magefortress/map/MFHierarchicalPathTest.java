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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import magefortress.core.MFEMovementType;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFHierarchicalPathTest
{

  //--------------------------- CONSTRUCTOR TESTS ------------------------------
  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutStart()
  {
    new MFHierarchicalPath(null, mock(MFTile.class), mock(Deque.class), 1,
                          EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutGoal()
  {
    new MFHierarchicalPath(mock(MFTile.class), null, mock(Deque.class), 1,
                          EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutTiles()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class), null, 1,
                          EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroDirections()
  {
    Deque<MFTile> emptyDeque = new ArrayDeque<MFTile>();
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class), emptyDeque,
                        1, EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutClearance()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
     mock(Deque.class), 0, EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutCapabilities()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
     mock(Deque.class), 1, null, mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroCapabilities()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
     mock(Deque.class), 1, EnumSet.noneOf(MFEMovementType.class), mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutMap()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
     mock(Deque.class), 1, EnumSet.of(MFEMovementType.WALK), null);
  }


  //------------------------ ITERATOR BEHAVIOR TESTS ---------------------------
  //                           ON INVALIDATED PATH

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveNextIfInvalidated()
  {
    MFHierarchicalPath path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
    path.setPathInvalid();
    path.next();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotTellHaveNextIfInvalidated()
  {
    MFHierarchicalPath path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));
    path.setPathInvalid();
    path.hasNext();
  }

  @Test(expected=UnsupportedOperationException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotSupportRemove()
  {
    MFHierarchicalPath path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class));

    path.remove();
  }

  //---------------------- ITERATOR TESTS --------------------------------------
  //                       ON VALID PATH

}