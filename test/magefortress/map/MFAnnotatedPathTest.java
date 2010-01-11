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
import java.util.NoSuchElementException;
import magefortress.core.MFEDirection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFAnnotatedPathTest
{
  @Before
  public void setUp()
  {
  }

  //--------------------------- CONSTRUCTOR TESTS ------------------------------
  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateAnnotatedPathWithoutStart()
  {
    new MFAnnotatedPath(null, mock(MFTile.class), mock(Deque.class), 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateAnnotatedPathWithoutGoal()
  {
    new MFAnnotatedPath(mock(MFTile.class), null, mock(Deque.class), 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateAnnotatedPathWithoutDirections()
  {
    new MFAnnotatedPath(mock(MFTile.class), mock(MFTile.class), null, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateAnnotatedPathWithZeroDirections()
  {
    Deque<MFEDirection> emptyDeque = new ArrayDeque<MFEDirection>();
    new MFAnnotatedPath(mock(MFTile.class), mock(MFTile.class), emptyDeque, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateAnnotatedPathWithoutCost()
  {
    new MFAnnotatedPath(mock(MFTile.class), mock(MFTile.class), mock(Deque.class), 0);
  }

  //------------------------ ITERATOR BEHAVIOR TESTS ---------------------------
  //                           ON INVALIDATED PATH

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveNextIfInvalidated()
  {
    MFAnnotatedPath path = new MFAnnotatedPath(mock(MFTile.class),
            mock(MFTile.class), mock(Deque.class), 1);
    path.setPathInvalid();
    path.next();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotTellHaveNextIfInvalidated()
  {
    MFAnnotatedPath path = new MFAnnotatedPath(mock(MFTile.class),
                      mock(MFTile.class), mock(Deque.class), 1);
    path.setPathInvalid();
    path.hasNext();
  }

  @Test(expected=UnsupportedOperationException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotSupportRemove()
  {
    MFAnnotatedPath path = new MFAnnotatedPath(mock(MFTile.class),
                      mock(MFTile.class), mock(Deque.class), 1);

    path.remove();
  }

  //---------------------- ITERATOR TESTS --------------------------------------
  //                       ON VALID PATH

  @Test(expected=NoSuchElementException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveAnyMoreElements()
  {
    Deque<MFEDirection> mockDeque = mock(Deque.class);
    MFEDirection direction        = MFEDirection.S;
    when(mockDeque.isEmpty()).thenReturn(false, true);
    when(mockDeque.poll()).thenReturn(direction, (MFEDirection) null);
    
    MFAnnotatedPath path = new MFAnnotatedPath(mock(MFTile.class),
            mock(MFTile.class), mockDeque, 1);

    MFEDirection gotDirection = path.next();
    assertEquals(direction, gotDirection);

    boolean gotHasNext = path.hasNext();
    assertFalse(gotHasNext);

    path.next();
  }

  @Test
  public void shouldIterateOverSteps()
  {
    Deque<MFEDirection> deque = new ArrayDeque<MFEDirection>(3);
    MFEDirection dir1 = MFEDirection.S;
    MFEDirection dir2 = MFEDirection.W;
    MFEDirection dir3 = MFEDirection.N;
    deque.push(dir3);
    deque.push(dir2);
    deque.push(dir1);

    MFAnnotatedPath path = new MFAnnotatedPath(mock(MFTile.class),
            mock(MFTile.class), deque, 3);

    assertTrue(path.hasNext());
    MFEDirection gotDir = path.next();
    assertEquals(dir1, gotDir);

    assertTrue(path.hasNext());
    gotDir = path.next();
    assertEquals(dir2, gotDir);

    assertTrue(path.hasNext());
    gotDir = path.next();
    assertEquals(dir3, gotDir);

    assertFalse(path.hasNext());
  }
}