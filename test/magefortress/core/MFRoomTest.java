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

import magefortress.map.MFTile;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFRoomTest
{
  private MFRoom room;
  private MFTile mockTile;

  @Before
  public void setUp()
  {
    // creates a room with one empty tile
    Set<MFTile> tiles = new HashSet<MFTile>(1);
    this.mockTile = mock(MFTile.class);
    tiles.add(this.mockTile);
    this.room = new MFRoomMock(tiles);
  }

  @Test
  public void shouldGetLivingValue()
  {
    int expValue = 0;
    int gotValue = this.room.getLivingValue();
    assertEquals(expValue, gotValue);
  }

  @Test
  public void shouldGetTiles()
  {
    Set<MFTile> gotSet = this.room.getTiles();
    int expSize = 1;
    int gotSize = gotSet.size();
    assertEquals(expSize, gotSize);
    assertTrue(gotSet.contains(this.mockTile));
  }

  @Test
  public void shouldGetSize()
  {
    int expSize = 1;
    int gotSize = this.room.getSize();
    assertEquals(expSize, gotSize);
  }

  @Test
  public void shouldAddAllTiles()
  {
    Set<MFTile> addedTiles = new HashSet<MFTile>(2);
    MFTile mockTile2 = mock(MFTile.class);
    MFTile mockTile3 = mock(MFTile.class);
    addedTiles.add(mockTile2);
    addedTiles.add(mockTile3);
    int prevSize = this.room.getSize();

    this.room.addTiles(addedTiles);
    Set<MFTile> gotSet = this.room.getTiles();
    int expSize = prevSize + 2;
    int gotSize = this.room.getSize();
    assertEquals(expSize, gotSize);
    assertTrue(gotSet.contains(mockTile2));
    assertTrue(gotSet.contains(mockTile3));
  }

  @Test
  public void shouldNotAddTiles()
  {
    Set<MFTile> addedTiles = new HashSet<MFTile>(1);
    addedTiles.add(this.mockTile);
    int prevSize = this.room.getSize();

    assertTrue(this.room.getTiles().contains(this.mockTile));

    this.room.addTiles(addedTiles);
    assertEquals(prevSize, this.room.getSize());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddEmptyTilesList()
  {
    this.room.addTiles(new HashSet<MFTile>(1));
  }

  @Test
  public void shouldNotAddAllTiles()
  {
    Set<MFTile> addedTiles = new HashSet<MFTile>(2);
    MFTile mockTile2 = mock(MFTile.class);
    MFTile mockTile3 = mock(MFTile.class);
    addedTiles.add(mockTile2);
    addedTiles.add(mockTile3);
    
    assertTrue(this.room.getTiles().contains(this.mockTile));
    addedTiles.add(this.mockTile);

    int prevSize = this.room.getSize();

    this.room.addTiles(addedTiles);
    Set<MFTile> gotSet = this.room.getTiles();
    int expSize = prevSize + 2;
    int gotSize = this.room.getSize();
    assertEquals(expSize, gotSize);
    assertTrue(gotSet.contains(mockTile2));
    assertTrue(gotSet.contains(mockTile3));
  }

  @Test
  public void shouldRemoveAllTiles()
  {
    Set<MFTile> removedTiles = new HashSet<MFTile>(1);
    removedTiles.add(this.mockTile);
    int prevSize = this.room.getSize();

    assertTrue(this.room.getTiles().contains(this.mockTile));

    this.room.removeTiles(removedTiles);
    int expSize = prevSize - 1;
    int gotSize = this.room.getSize();
    assertEquals(expSize, gotSize);
    assertTrue(this.room.getTiles().isEmpty());
  }

  @Test
  public void shouldNotRemoveTiles()
  {
    Set<MFTile> removedTiles = new HashSet<MFTile>(1);
    MFTile mockTile2 = mock(MFTile.class);
    removedTiles.add(mockTile2);
    int prevSize = this.room.getSize();

    this.room.removeTiles(removedTiles);
    int expSize = prevSize;
    int gotSize = this.room.getSize();
    assertEquals(expSize, gotSize);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotRemoveEmptyTilesList()
  {
    this.room.removeTiles(new HashSet<MFTile>(1));
  }

  @Test
  public void shouldNotRemoveAllTiles()
  {
    Set<MFTile> removedTiles = new HashSet<MFTile>(2);
    MFTile mockTile2 = mock(MFTile.class);
    removedTiles.add(mockTile2);
    assertTrue(this.room.getTiles().contains(this.mockTile));
    removedTiles.add(this.mockTile);

    int prevSize = this.room.getSize();

    this.room.removeTiles(removedTiles);
    int expSize = prevSize - 1;
    int gotSize = this.room.getSize();
    assertEquals(expSize, gotSize);
    assertTrue(this.room.getTiles().isEmpty());
  }

  @Test
  public void shouldSubscribeToTileChanges()
  {
    MFTile mockTile2 = mock(MFTile.class);
    Set<MFTile> addedTiles = new HashSet<MFTile>(1);
    addedTiles.add(mockTile2);

    this.room.addTiles(addedTiles);
    assertTrue(this.room.getTiles().contains(mockTile2));
    verify(mockTile2).subscribeConstructionsListener(this.room);
    verify(mockTile2).setRoom(this.room);
  }

  @Test
  public void shouldUnsubscribeFromTileChanges()
  {
    MFTile mockTile2 = mock(MFTile.class);
    Set<MFTile> removedTiles = new HashSet<MFTile>(1);
    removedTiles.add(mockTile2);

    this.room.addTiles(removedTiles);
    this.room.removeTiles(removedTiles);
    assertFalse(this.room.getTiles().contains(mockTile2));
    verify(mockTile2).unsubscribeConstructionsListener(this.room);
    verify(mockTile2).setRoom(null);
  }

  @Test
  public void shouldNotifySubclassOfAddedTile()
  {
    // setup spy
    MFRoom spiedRoom = createSpiedRoom();

    // add tiles to spied-on room
    Set<MFTile> addedTiles = new HashSet<MFTile>(1);
    MFTile mockTile2 = mock(MFTile.class);
    MFTile mockTile3 = mock(MFTile.class);
    addedTiles.add(mockTile2);
    addedTiles.add(mockTile3);

    // verify
    spiedRoom.addTiles(addedTiles);
    verify(spiedRoom).tileAdded(mockTile2);
    verify(spiedRoom).tileAdded(mockTile3);
  }

  @Test
  public void shouldNotNotifySubclassOfAddedTile()
  {
    // setup spy
    MFRoom spiedRoom = createSpiedRoom();

    // add tiles to spied-on room
    Set<MFTile> addedTiles = new HashSet<MFTile>(1);
    MFTile mockTile2 = mock(MFTile.class);
    MFTile mockTile3 = mock(MFTile.class);
    addedTiles.add(mockTile2);
    addedTiles.add(mockTile3);

    // verify tileAdded() was only called once for each added tile
    spiedRoom.addTiles(addedTiles);
    spiedRoom.addTiles(addedTiles);
    verify(spiedRoom).tileAdded(mockTile2);
    verify(spiedRoom).tileAdded(mockTile3);
  }

  @Test
  public void shouldNotifySubclassOfRemovedTile()
  {
    // setup spy
    MFRoom spiedRoom = createSpiedRoom();

    // add tiles to spied-on room
    Set<MFTile> addedTiles = new HashSet<MFTile>(1);
    MFTile mockTile2 = mock(MFTile.class);
    MFTile mockTile3 = mock(MFTile.class);
    addedTiles.add(mockTile2);
    addedTiles.add(mockTile3);
    spiedRoom.addTiles(addedTiles);

    // verify
    spiedRoom.removeTiles(addedTiles);
    verify(spiedRoom).tileRemoved(mockTile2);
    verify(spiedRoom).tileRemoved(mockTile3);
  }

  @Test
  public void shouldNotNotifySubclassOfRemovedTile()
  {
    // setup spy
    MFRoom spiedRoom = createSpiedRoom();

    // add tiles to spied-on room
    Set<MFTile> addedTiles = new HashSet<MFTile>(1);
    MFTile mockTile2 = mock(MFTile.class);
    MFTile mockTile3 = mock(MFTile.class);
    addedTiles.add(mockTile2);
    addedTiles.add(mockTile3);
    spiedRoom.addTiles(addedTiles);

    // verify tileRemoved() was only called once for each removed tile
    spiedRoom.removeTiles(addedTiles);
    spiedRoom.removeTiles(addedTiles);
    verify(spiedRoom).tileRemoved(mockTile2);
    verify(spiedRoom).tileRemoved(mockTile3);
  }

  @Test
  public void shouldNotifySubclassOfUpdatedTile()
  {
    MFRoom spiedRoom = createSpiedRoom();
    spiedRoom.tileObjectsChanged();
    verify(spiedRoom).tileUpdated();
  }

  @Test
  public void shouldUpdateLivingValueWhenTilesAreAdded()
  {
    final int ADDED_LIVING_VALUE = 1;

    // setup item & tile
    MFIPlaceable mockItem = mock(MFIPlaceable.class);
    when(mockItem.getLivingValue()).thenReturn(ADDED_LIVING_VALUE);
    MFTile mockTile2 = mock(MFTile.class);
    when(mockTile2.getObject()).thenReturn(mockItem);
    Set<MFTile> newTiles = new HashSet<MFTile>(1);
    newTiles.add(mockTile2);

    int expLivingValue = this.room.getLivingValue() + ADDED_LIVING_VALUE;
    this.room.addTiles(newTiles);
    int gotLivingValue = this.room.getLivingValue();
    assertEquals(expLivingValue, gotLivingValue);
  }

  @Test
  public void shouldUpdateLivingValueWhenTilesAreRemoved()
  {
    final int REMOVED_LIVING_VALUE = 1;
    // setup item & tile
    MFIPlaceable mockItem = mock(MFIPlaceable.class);
    when(mockItem.getLivingValue()).thenReturn(REMOVED_LIVING_VALUE);
    MFTile mockTile2 = mock(MFTile.class);
    when(mockTile2.getObject()).thenReturn(mockItem);
    Set<MFTile> newTiles = new HashSet<MFTile>(1);
    newTiles.add(mockTile2);

    int expLivingValue = this.room.getLivingValue();
    this.room.addTiles(newTiles);
    this.room.removeTiles(newTiles);
    int gotLivingValue = this.room.getLivingValue();
    assertEquals(expLivingValue, gotLivingValue);
  }

  @Test
  public void shouldUpdateLivingValueWhenObjectsAreAdded()
  {
    final int ADDED_LIVING_VALUE = 1;
    MFIPlaceable mockItem = mock(MFIPlaceable.class);
    when(mockItem.getLivingValue()).thenReturn(ADDED_LIVING_VALUE);
    when(mockTile.getObject()).thenReturn(mockItem);

    int expLivingValue = this.room.getLivingValue() + ADDED_LIVING_VALUE;
    this.room.tileObjectsChanged();
    int gotLivingValue = this.room.getLivingValue();
    assertEquals(expLivingValue, gotLivingValue);
  }

  @Test
  public void shouldUpdateLivingValueWhenObjectsAreRemoved()
  {
    final int REMOVED_LIVING_VALUE = 1;
    MFIPlaceable mockItem = mock(MFIPlaceable.class);
    when(mockItem.getLivingValue()).thenReturn(REMOVED_LIVING_VALUE);
    when(mockTile.getObject()).thenReturn(mockItem);

    // add item
    int expLivingValue = this.room.getLivingValue();
    this.room.tileObjectsChanged();

    // remove item
    when(mockTile.getObject()).thenReturn(null);
    this.room.tileObjectsChanged();
    int gotLivingValue = this.room.getLivingValue();
    assertEquals(expLivingValue, gotLivingValue);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private static MFRoom createSpiedRoom()
  {
    MFTile tile = mock(MFTile.class);
    Set<MFTile> newTiles = new HashSet<MFTile>(1);
    newTiles.add(tile);
    MFRoom realRoom = new MFRoomMock(newTiles);
    MFRoom result = spy(realRoom);
    return result;
  }

}