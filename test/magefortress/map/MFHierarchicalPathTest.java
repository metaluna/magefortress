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
import magefortress.core.MFEDirection;
import magefortress.core.MFEMovementType;
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
                          EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                          this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutGoal()
  {
    new MFHierarchicalPath(mock(MFTile.class), null, mock(Deque.class), 1,
                          EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                          this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutTiles()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class), null, 1,
                          EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                          this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroDirections()
  {
    Deque<MFTile> emptyDeque = new ArrayDeque<MFTile>();
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class), emptyDeque,
                        1, EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                          this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutClearance()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                         mock(Deque.class), 0, EnumSet.of(MFEMovementType.WALK),
                         mock(MFMap.class), this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutCapabilities()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                            mock(Deque.class), 1, null, mock(MFMap.class),
                            this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithZeroCapabilities()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                    mock(Deque.class), 1, EnumSet.noneOf(MFEMovementType.class),
                    mock(MFMap.class), this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutMap()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                  mock(Deque.class), 1, EnumSet.of(MFEMovementType.WALK), null,
                          this.mockPathFinder);
  }

  @Test(expected=IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotCreateHierarchicalPathWithoutPathFinder()
  {
    new MFHierarchicalPath(mock(MFTile.class), mock(MFTile.class),
                        mock(Deque.class), 1, EnumSet.of(MFEMovementType.WALK),
                        mock(MFMap.class), null);
  }

  //------------------------ ITERATOR BEHAVIOR TESTS ---------------------------
  //                           ON INVALIDATED PATH

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveNextIfInvalidated()
  {
    this.path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                           this.mockPathFinder);
    this.path.setPathInvalid();
    this.path.next();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotHaveNextIfInvalidatedByFindingNoPath()
  {
    this.path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                           this.mockPathFinder);
    this.path.pathSearchFinished(null);
    this.path.next();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotTellHaveNextIfInvalidated()
  {
    this.path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                           this.mockPathFinder);
    this.path.setPathInvalid();
    this.path.hasNext();
  }

  @Test(expected=IllegalStateException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotTellHaveNextIfInvalidatedByFindingNoPath()
  {
    this.path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                           this.mockPathFinder);
    this.path.pathSearchFinished(null);
    this.path.hasNext();
  }

  @Test(expected=UnsupportedOperationException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotSupportRemove()
  {
    this.path = new MFHierarchicalPath(mock(MFTile.class),
                           mock(MFTile.class), mock(Deque.class), 1,
                           EnumSet.of(MFEMovementType.WALK), mock(MFMap.class),
                           this.mockPathFinder);
    this.path.remove();
  }

  //---------------------- ITERATOR TESTS --------------------------------------
  //                       ON VALID PATH

  @Test
  public void shouldEnqueueTwoSubpathSearchesOnCreationIfAtLeastThreeTilesLong()
  {
    MFTile mockStart  = mock(MFTile.class);
    MFTile mockGoal   = mock(MFTile.class);
    MFTile mockMiddleTile = mock(MFTile.class);

    Deque<MFTile> stack = new ArrayDeque<MFTile>(3);
    stack.push(mockGoal);
    stack.push(mockMiddleTile);
    stack.push(mockStart);

    int clearance = 1;
    EnumSet<MFEMovementType> capabilities = EnumSet.of(MFEMovementType.WALK);
    MFMap mockMap = mock(MFMap.class);


    this.path = new MFHierarchicalPath(mockStart, mockGoal, stack, clearance,
                                    capabilities, mockMap, this.mockPathFinder);
    verify(this.mockPathFinder).enqueuePathSearch(mockMap, mockStart, mockMiddleTile, clearance, capabilities, this.path);
    verify(this.mockPathFinder).enqueuePathSearch(mockMap, mockMiddleTile, mockGoal,  clearance, capabilities, this.path);
    verifyNoMoreInteractions(this.mockPathFinder);
  }

  @Test
  public void shouldEnqueueOnlyOneSubpathSearchOnCreationIfTwoTilesLong()
  {
    // search configuration
    MFSection mockSection = mock(MFSection.class);
    MFTile mockStart  = mock(MFTile.class);
    when(mockStart.getParentSection()).thenReturn(mockSection);

    MFTile mockGoal   = mock(MFTile.class);
    when(mockGoal.getParentSection()).thenReturn(mockSection);

    Deque<MFTile> stack = new ArrayDeque<MFTile>(2);
    stack.push(mockGoal);
    stack.push(mockStart);

    int clearance = 1;
    EnumSet<MFEMovementType> capabilities = EnumSet.of(MFEMovementType.WALK);
    MFMap mockMap = mock(MFMap.class);

    // start tests
    this.path = new MFHierarchicalPath(mockStart, mockGoal, stack, clearance,
                                    capabilities, mockMap, this.mockPathFinder);
    verify(this.mockPathFinder).enqueuePathSearch(mockMap, mockStart, mockGoal, clearance, capabilities, this.path);
    verifyNoMoreInteractions(this.mockPathFinder);
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
    EnumSet<MFEMovementType> capabilities = EnumSet.of(MFEMovementType.WALK);
    MFMap mockMap = mock(MFMap.class);

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
                                    capabilities, mockMap, this.mockPathFinder);
    verify(this.mockPathFinder, times(2)).enqueuePathSearch(eq(mockMap),
                                any(MFTile.class), any(MFTile.class),
                                eq(clearance), eq(capabilities), eq(this.path));

    this.path.pathSearchFinished(mockPath1);
    this.path.pathSearchFinished(mockPath2);

    assertTrue(this.path.isPathValid());
    assertTrue(this.path.hasNext());
    MFEDirection gotDirection = this.path.next();
    assertEquals(dir1, gotDirection);
    
    verify(this.mockPathFinder, times(3)).enqueuePathSearch(eq(mockMap),
                                any(MFTile.class), any(MFTile.class),
                                eq(clearance), eq(capabilities), eq(this.path));
    
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
    EnumSet<MFEMovementType> capabilities = EnumSet.of(MFEMovementType.WALK);
    MFMap mockMap = mock(MFMap.class);

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
                                    capabilities, mockMap, this.mockPathFinder);
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

}