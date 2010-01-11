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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFPathFinderTest
{
  MFPathFinder pathFinder;
  
  @Before
  public void setUp()
  {
    this.pathFinder = MFPathFinder.getInstance();
  }

  @Test
  public void shouldGetSingletonInstance()
  {
    MFPathFinder gotPathFinder = MFPathFinder.getInstance();
    assertEquals(this.pathFinder, gotPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotEnqueueNullPath()
  {
    this.pathFinder.enqueuePathSearch(null, mock(MFIPathFinderListener.class));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotEnqueuePathWithoutListener()
  {
    this.pathFinder.enqueuePathSearch(mock(MFHierarchicalAStar.class), null);
  }

  @Test
  public void shouldNotifyListenerOnSuccess()
  {
    final MFHierarchicalAStar mockSearch = mock(MFHierarchicalAStar.class);
    final MFIPathFinderListener mockListener = mock(MFIPathFinderListener.class);
    final MFPath mockPath = mock(MFPath.class);
    when(mockSearch.findPath()).thenReturn(mockPath);

    this.pathFinder.enqueuePathSearch(mockSearch, mockListener);

    this.pathFinder.update();
    verify(mockSearch).findPath();
    verify(mockListener).pathSearchFinished(mockPath);
  }

  @Test
  public void shouldNotifyListenerOnFailure()
  {
    final MFHierarchicalAStar mockSearch = mock(MFHierarchicalAStar.class);
    final MFIPathFinderListener mockListener = mock(MFIPathFinderListener.class);
    when(mockSearch.findPath()).thenReturn(null);

    this.pathFinder.enqueuePathSearch(mockSearch, mockListener);

    this.pathFinder.update();
    verify(mockSearch).findPath();
    verify(mockListener).pathSearchFinished(null);
  }

  @Test
  public void shouldEnqueueMultiplePaths()
  {
    final MFHierarchicalAStar mockPath1 = mock(MFHierarchicalAStar.class);
    final MFHierarchicalAStar mockPath2 = mock(MFHierarchicalAStar.class);
    final MFHierarchicalAStar mockPath3 = mock(MFHierarchicalAStar.class);
    final MFIPathFinderListener mockListener = mock(MFIPathFinderListener.class);

    this.pathFinder.enqueuePathSearch(mockPath1, mockListener);
    this.pathFinder.enqueuePathSearch(mockPath2, mockListener);
    this.pathFinder.enqueuePathSearch(mockPath3, mockListener);

    this.pathFinder.update();
    this.pathFinder.update();
    this.pathFinder.update();
    InOrder inOrder = inOrder(mockPath1, mockPath2, mockPath3);
    inOrder.verify(mockPath1).findPath();
    inOrder.verify(mockPath2).findPath();
    inOrder.verify(mockPath3).findPath();
    verify(mockListener, times(3)).pathSearchFinished(any(MFPath.class));
  }

}