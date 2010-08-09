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
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.movable.MFCapability;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFHierarchicalPathTest
{
  private MFHierarchicalPath path;
  private MFPathFinder mockPathFinder;

  @Before
  public void setup()
  {
    this.mockPathFinder = mock(MFPathFinder.class);
  }

  //--------------------------- CONSTRUCTOR TESTS ------------------------------
  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutStart()
  {
    new MFHierarchicalPath(null, mock(MFTile.class), mock(Deque.class), 1,
                          MFCapability.WALK,
                          this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutGoal()
  {
    new MFHierarchicalPath(mock(MFTile.class), null, mock(Deque.class), 1,
                          MFCapability.WALK,
                          this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutTiles()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class), null, 1,
                          MFCapability.WALK,
                          this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroDirections()
  {
    Deque<MFTile> emptyDeque = new ArrayDeque<MFTile>();
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class), emptyDeque,
                        1, MFCapability.WALK,
                          this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutClearance()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                         mock(Deque.class), 0, MFCapability.WALK,
                         this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutCapabilities()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                            mock(Deque.class), 1, null,
                            this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroCapabilities()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                    mock(Deque.class), 1, MFCapability.NONE,
                    this.mockPathFinder, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutPathFinder()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                        mock(Deque.class), 1, MFCapability.WALK,
                        null, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroCost()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                        mock(Deque.class), 1, MFCapability.WALK,
                        this.mockPathFinder, 0);
  }

  //------------------------ ITERATOR BEHAVIOR TESTS ---------------------------
  //                           ON INVALIDATED PATH

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveNextIfInvalidated()
  {
    Deque<MFTile> stack = getMockStack(2);
    this.path = new MFHierarchicalPath(stack.getFirst(), stack.getLast(), stack, 1,
                           MFCapability.WALK,
                           this.mockPathFinder, 1);
    this.path.setPathInvalid();
    this.path.next();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveNextIfInvalidatedByFindingNoPath()
  {
    Deque<MFTile> stack = getMockStack(2);
    this.path = new MFHierarchicalPath(stack.getFirst(), stack.getLast(), stack, 1,
                           MFCapability.WALK,
                           this.mockPathFinder, 1);
    this.path.pathSearchFinished(null);
    this.path.next();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotTellHaveNextIfInvalidated()
  {
    Deque<MFTile> stack = getMockStack(2);
    this.path = new MFHierarchicalPath(stack.getFirst(), stack.getLast(), stack, 1,
                           MFCapability.WALK,
                           this.mockPathFinder, 1);
    this.path.setPathInvalid();
    this.path.hasNext();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotTellHaveNextIfInvalidatedByFindingNoPath()
  {
    Deque<MFTile> stack = getMockStack(2);
    this.path = new MFHierarchicalPath(stack.getFirst(), stack.getLast(), stack, 1,
                           MFCapability.WALK,
                           this.mockPathFinder, 1);
    this.path.pathSearchFinished(null);
    this.path.hasNext();
  }

  @Test(expected=UnsupportedOperationException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotSupportRemove()
  {
    Deque<MFTile> stack = getMockStack(2);
    this.path = new MFHierarchicalPath(stack.getFirst(), stack.getLast(), stack, 1,
                           MFCapability.WALK,
                           this.mockPathFinder, 1);
    this.path.remove();
  }

  //---------------------- ITERATOR TESTS --------------------------------------
  //                       ON VALID PATH

  @Test
  public void shouldEnqueueTwoSubpathSearchesOnCreationIfAtLeastThreeTilesLong()
  {
    MFTile mockStart  = mock(MFTile.class);
    when(mockStart.getLocation()).thenReturn(MFLocation.NOWHERE);
    MFTile mockGoal   = mock(MFTile.class);
    when(mockGoal.getLocation()).thenReturn(MFLocation.NOWHERE);
    MFTile mockMiddleTile = mock(MFTile.class);
    when(mockMiddleTile.getLocation()).thenReturn(MFLocation.NOWHERE);

    Deque<MFTile> stack = new ArrayDeque<MFTile>(3);
    stack.push(mockGoal);
    stack.push(mockMiddleTile);
    stack.push(mockStart);

    int clearance = 1;
    MFCapability capability = MFCapability.WALK;

    this.path = new MFHierarchicalPath(mockStart, mockGoal, stack, clearance,
                                    capability, this.mockPathFinder, 42);
    verify(this.mockPathFinder, times(2)).enqueuePathSearch(any(MFLocation.class), any(MFLocation.class),
            anyInt(), any(MFCapability.class), any(MFIPathFinderListener.class));

    //verify(this.mockPathFinder).enqueuePathSearch(mockStart.getLocation(), mockMiddleTile.getLocation(), clearance, capabilities, this.path);
    //verify(this.mockPathFinder).enqueuePathSearch(mockMiddleTile.getLocation(), mockGoal.getLocation(),  clearance, capabilities, this.path);
    //verifyNoMoreInteractions(this.mockPathFinder);
  }

  @Test
  public void shouldEnqueueOnlyOneSubpathSearchOnCreationIfTwoTilesLong()
  {
    // search configuration
    MFSection mockSection = mock(MFSection.class);
    MFTile mockStart  = mock(MFTile.class);
    when(mockStart.getParentSection()).thenReturn(mockSection);
    when(mockStart.getLocation()).thenReturn(MFLocation.NOWHERE);

    MFTile mockGoal   = mock(MFTile.class);
    when(mockGoal.getParentSection()).thenReturn(mockSection);
    when(mockGoal.getLocation()).thenReturn(MFLocation.NOWHERE);

    Deque<MFTile> stack = new ArrayDeque<MFTile>(2);
    stack.push(mockGoal);
    stack.push(mockStart);

    int clearance = 1;
    MFCapability capability = MFCapability.WALK;

    // start tests
    this.path = new MFHierarchicalPath(mockStart, mockGoal, stack, clearance,
                                    capability, this.mockPathFinder, 42);
    verify(this.mockPathFinder).enqueuePathSearch(any(MFLocation.class), any(MFLocation.class), 
            anyInt(), any(MFCapability.class), any(MFIPathFinderListener.class));
    //verify(this.mockPathFinder).enqueuePathSearch(mockStart.getLocation(), mockGoal.getLocation(), clearance, capabilities, this.path);
    //verifyNoMoreInteractions(this.mockPathFinder);
  }

  @Test
  public void shouldWorkWithPathThreeTilesLong()
  {
    // search configuration
    MFTile mockStart  = mock(MFTile.class);
    MFTile mockGoal   = mock(MFTile.class);
    MFTile mockInBetweenTile1 = mock(MFTile.class);
    MFTile mockInBetweenTile2 = mock(MFTile.class);

    Deque<MFTile> stack = new ArrayDeque<MFTile>(4);
    stack.push(mockGoal);
    stack.push(mockInBetweenTile1);
    stack.push(mockInBetweenTile2);
    stack.push(mockStart);

    int clearance = 1;
    MFCapability capability = MFCapability.WALK;

    // path 1
    MFPath mockPath1 = mock(MFPath.class);
    MFEDirection dir1 = MFEDirection.S;
    when(mockPath1.getGoal()).thenReturn(mockInBetweenTile1);
    when(mockPath1.hasNext()).thenReturn(true, false);
    when(mockPath1.next()).thenReturn(dir1);
    // path 2
    MFPath mockPath2 = mock(MFPath.class);
    MFEDirection dir2 = MFEDirection.W;
    when(mockPath2.getGoal()).thenReturn(mockInBetweenTile2);
    when(mockPath2.hasNext()).thenReturn(true, false);
    when(mockPath2.next()).thenReturn(dir2);
    // path 3
    MFPath mockPath3 = mock(MFPath.class);
    MFEDirection dir3 = MFEDirection.N;
    when(mockPath3.getGoal()).thenReturn(mockGoal);
    when(mockPath3.hasNext()).thenReturn(true, false);
    when(mockPath3.next()).thenReturn(dir3);

    // start tests
    this.path = new MFHierarchicalPath(mockStart, mockGoal, stack, clearance,
                                    capability, this.mockPathFinder, 42);
    verify(this.mockPathFinder, times(2)).enqueuePathSearch(
                                any(MFLocation.class), any(MFLocation.class),
                                eq(clearance), eq(capability), eq(this.path));

    this.path.pathSearchFinished(mockPath1);
    this.path.pathSearchFinished(mockPath2);

    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());
    MFEDirection gotDirection = this.path.next();
    assertEquals(dir1, gotDirection);
    
    verify(this.mockPathFinder, times(3)).enqueuePathSearch(
                                any(MFLocation.class), any(MFLocation.class),
                                eq(clearance), eq(capability), eq(this.path));
    
    this.path.pathSearchFinished(mockPath3);

    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());
    gotDirection = this.path.next();
    assertEquals(dir2, gotDirection);

    verifyNoMoreInteractions(this.mockPathFinder);

    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());
    gotDirection = this.path.next();
    assertEquals(dir3, gotDirection);

    assertFalse(this.path.hasNext());
  }

  @Test
  public void shouldWorkWhenSubpathsReturnDelayed()
  {
    // search configuration
    MFTile mockStart  = mock(MFTile.class);
    MFTile mockGoal   = mock(MFTile.class);
    MFTile mockInBetweenTile1 = mock(MFTile.class);
    MFTile mockInBetweenTile2 = mock(MFTile.class);

    Deque<MFTile> stack = new ArrayDeque<MFTile>(4);
    stack.push(mockGoal);
    stack.push(mockInBetweenTile1);
    stack.push(mockInBetweenTile2);
    stack.push(mockStart);

    int clearance = 1;
    MFCapability capability = MFCapability.WALK;

    // path 1
    MFPath mockPath1 = mock(MFPath.class);
    MFEDirection dir1 = MFEDirection.S;
    when(mockPath1.getGoal()).thenReturn(mockInBetweenTile1);
    when(mockPath1.hasNext()).thenReturn(true, false);
    when(mockPath1.next()).thenReturn(dir1);
    // path 2
    MFPath mockPath2 = mock(MFPath.class);
    MFEDirection dir2 = MFEDirection.W;
    when(mockPath2.getGoal()).thenReturn(mockInBetweenTile2);
    when(mockPath2.hasNext()).thenReturn(true, false);
    when(mockPath2.next()).thenReturn(dir2);
    // path 3
    MFPath mockPath3 = mock(MFPath.class);
    MFEDirection dir3 = MFEDirection.N;
    when(mockPath3.getGoal()).thenReturn(mockGoal);
    when(mockPath3.hasNext()).thenReturn(true, false);
    when(mockPath3.next()).thenReturn(dir3);

    // start tests
    this.path = new MFHierarchicalPath(mockStart, mockGoal, stack, clearance,
                                    capability, this.mockPathFinder, 42);
    assertTrue(this.path.isPathValid());
    assertFalse(this.path.hasNext());

    this.path.pathSearchFinished(mockPath1);
    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());

    MFEDirection gotDirection = this.path.next();
    assertEquals(dir1, gotDirection);

    assertTrue(this.path.isPathValid());
    assertFalse(this.path.hasNext());

    this.path.pathSearchFinished(mockPath2);
    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());

    gotDirection = this.path.next();
    assertEquals(dir2, gotDirection);

    assertTrue(this.path.isPathValid());
    assertFalse(this.path.hasNext());

    this.path.pathSearchFinished(mockPath3);
    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());
    
    gotDirection = this.path.next();
    assertEquals(dir3, gotDirection);

    assertFalse(this.path.hasNext());
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private Deque<MFTile> getMockStack(int _members)
  {
    Deque<MFTile> stack = new ArrayDeque<MFTile>(_members);
    for (int i = 0; i < _members; ++i) {
      MFTile mockTile = mock(MFTile.class);
      when(mockTile.getLocation()).thenReturn(MFLocation.NOWHERE);
      stack.push(mockTile);
    }
    return stack;
  }

}