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

import magefortress.map.MFTile;
import java.util.HashSet;
import magefortress.creatures.behavior.MFEMovementType;
import magefortress.core.MFIPlaceable;
import magefortress.core.MFRoom;
import magefortress.core.MFRoomMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFTileTest
{
  private MFTile tile;

  @Before
  public void setUp()
  {
    this.tile = new MFTile(-1, 0, 0, 0);
  }

  @Test
  public void shouldHaveClearanceValue()
  {
    int walkValue = 1;
    int flyValue = 2;
    tile.setClearance(MFEMovementType.WALK, walkValue);
    tile.setClearance(MFEMovementType.FLY, flyValue);

    assertEquals(walkValue, tile.getClearance(MFEMovementType.WALK));
    assertEquals(flyValue, tile.getClearance(MFEMovementType.FLY));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotHaveClearanceValue()
  {
    tile.getClearance(MFEMovementType.WALK);
  }

  @Test
  public void shouldAddObject()
  {
    MFIPlaceable mockItem = mock(MFIPlaceable.class);

    tile.setObject(mockItem);
    MFIPlaceable gotItem = tile.getObject();
    assertEquals(mockItem, gotItem);
  }

  @Test
  public void shouldRemoveObject()
  {
    MFIPlaceable mockItem = mock(MFIPlaceable.class);
    tile.setObject(mockItem);

    tile.setObject(null);
    MFIPlaceable gotItem = tile.getObject();
    assertNull(gotItem);
  }

  @Test
  public void shouldBuildWall()
  {
    assertFalse(tile.hasWallNorth());
    assertFalse(tile.hasWallEast());
    assertFalse(tile.hasWallSouth());
    assertFalse(tile.hasWallWest());

    tile.setWallNorth(true);
    assertTrue(tile.hasWallNorth());
    assertFalse(tile.hasWallEast());
    assertFalse(tile.hasWallSouth());
    assertFalse(tile.hasWallWest());

    tile.setWallEast(true);
    assertTrue(tile.hasWallNorth());
    assertTrue(tile.hasWallEast());
    assertFalse(tile.hasWallSouth());
    assertFalse(tile.hasWallWest());

    tile.setWallSouth(true);
    assertTrue(tile.hasWallNorth());
    assertTrue(tile.hasWallEast());
    assertTrue(tile.hasWallSouth());
    assertFalse(tile.hasWallWest());
    
    tile.setWallWest(true);
    assertTrue(tile.hasWallNorth());
    assertTrue(tile.hasWallEast());
    assertTrue(tile.hasWallSouth());
    assertTrue(tile.hasWallWest());
  }

  @Test
  public void shouldRemoveWall()
  {
    tile.setWalls(true, true, true, true);
    assertTrue(tile.hasWallNorth());
    assertTrue(tile.hasWallEast());
    assertTrue(tile.hasWallSouth());
    assertTrue(tile.hasWallWest());

    tile.setWallNorth(false);
    assertFalse(tile.hasWallNorth());
    assertTrue(tile.hasWallEast());
    assertTrue(tile.hasWallSouth());
    assertTrue(tile.hasWallWest());

    tile.setWallEast(false);
    assertFalse(tile.hasWallNorth());
    assertFalse(tile.hasWallEast());
    assertTrue(tile.hasWallSouth());
    assertTrue(tile.hasWallWest());

    tile.setWallSouth(false);
    assertFalse(tile.hasWallNorth());
    assertFalse(tile.hasWallEast());
    assertFalse(tile.hasWallSouth());
    assertTrue(tile.hasWallWest());

    tile.setWallWest(false);
    assertFalse(tile.hasWallNorth());
    assertFalse(tile.hasWallEast());
    assertFalse(tile.hasWallSouth());
    assertFalse(tile.hasWallWest());
  }

  @Test
  public void shouldBuildFloor()
  {
    tile.setFloor(false);
    assertFalse(tile.hasFloor());

    tile.setFloor(true);
    assertTrue(tile.hasFloor());
  }

  @Test
  public void shouldRemoveFloor()
  {
    tile.setFloor(false);
    assertFalse(tile.hasFloor());
  }

  @Ignore
  @Test
  public void shouldPolishWalls()
  {

  }
  
  @Test
  public void shouldSetRoom()
  {
    assertNull(tile.getRoom());

    MFRoom mockRoom = mock(MFRoom.class);
    tile.setRoom(mockRoom);
    MFRoom gotRoom = tile.getRoom();
    assertEquals(mockRoom, gotRoom);
  }

  @Test
  public void shouldUnsetRoom()
  {
    MFRoom mockRoom = mock(MFRoom.class);

    tile.setRoom(mockRoom);
    MFRoom gotRoom = tile.getRoom();
    assertEquals(mockRoom, gotRoom);

    tile.setRoom(null);
    assertNull(tile.getRoom());
  }

  @Test
  public void shouldNotifyRoomOfChangedObject()
  {
    MFIPlaceable item = mock(MFIPlaceable.class);
    when(item.getLivingValue()).thenReturn(0);
    MFRoom spiedRoom = createSpiedRoom();
    tile.setRoom(spiedRoom);

    tile.setObject(item);
    verify(spiedRoom).tileObjectsChanged();
  }

  @Ignore
  @Test
  public void shouldNotifyRoomOfPolishedWalls()
  {
    
  }

  @Test
  public void shouldNotifySubscribersOfWallsBuilt()
  {
    MFRoom room = createSpiedRoom();
    
    tile.setWallNorth(true);
    verify(room).tileConstructionsChanged(tile);
  }

  @Test
  public void shouldNotNotifySubscribersOfAlreadyBuiltWalls()
  {
    tile.setWallNorth(true);
    assertTrue(tile.hasWallNorth());
    MFRoom room = createSpiedRoom();

    tile.setWallNorth(true);
    verify(room, never()).tileConstructionsChanged(tile);
  }

  @Test
  public void shouldNotifySubscribersOfWallsRemoved()
  {
    tile.setWallEast(true);
    assertTrue(tile.hasWallEast());
    MFRoom room = createSpiedRoom();

    tile.setWallEast(false);
    verify(room).tileConstructionsChanged(tile);
  }

  @Test
  public void shouldNotifySubscribersOfFloorBuilt()
  {
    tile.setFloor(false);
    assertFalse(tile.hasFloor());
    MFRoom room = createSpiedRoom();

    tile.setFloor(true);
    verify(room).tileConstructionsChanged(tile);
  }

  @Test
  public void shouldNotNotifySubscribersOfAlreadyBuiltFloor()
  {
    assertTrue(tile.hasFloor());
    MFRoom room = createSpiedRoom();

    tile.setFloor(true);
    verify(room, never()).tileConstructionsChanged(tile);
  }

  @Test
  public void shouldNotifySubscribersOfFloorRemoved()
  {
    assertTrue(tile.hasFloor());
    MFRoom room = createSpiedRoom();

    tile.setFloor(false);
    verify(room).tileConstructionsChanged(tile);
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---

  private MFRoom createSpiedRoom()
  {
    HashSet<MFTile> newTiles = new HashSet<MFTile>(1);
    newTiles.add(mock(MFTile.class));
    MFRoom realRoom = new MFRoomMock(newTiles);
    realRoom.addTiles(newTiles);
    MFRoom result = spy(realRoom);
    HashSet<MFTile> tiles = new HashSet<MFTile>(1);
    tiles.add(tile);
    result.addTiles(tiles);
    return result;
  }

}