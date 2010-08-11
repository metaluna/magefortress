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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import magefortress.map.ground.MFGround;
import magefortress.storage.DataAccessException;
import magefortress.storage.MFSqlConnector;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFTileSqlDaoTest
{
  private MFTileSqlDao unsavedTileSqlDao;
  private MFTileSqlDao savedTileSqlDao;
  private MFTile unsavedMockTile;
  private MFTile savedMockTile;
  private MFSqlConnector mockDb;
  private static MFSqlConnector realDb;

  @BeforeClass
  @SuppressWarnings("unchecked")
  public static void setUpClass()
  {
    realDb = MFSqlConnector.getInstance();
    realDb.connect("magefortress.test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");
    new MFTileSqlDao(realDb, Collections.EMPTY_MAP).prepareStatements();
  }

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws ClassNotFoundException
  {
    // reset real test db
    //realDb.loadFromFile("magefortress.sql");
    //realDb.loadFromFile("test_fixtures.sql");
    //resetDb();

    mockDb = mock(MFSqlConnector.class);

    unsavedMockTile = mock(MFTile.class);
    when(unsavedMockTile.getId()).thenReturn(new MFTileSqlDao(mockDb, Collections.EMPTY_MAP).getUnsavedMarker());
    unsavedTileSqlDao = new MFTileSqlDao(mockDb, unsavedMockTile, 1);

    savedMockTile = mock(MFTile.class);
    when(savedMockTile.getId()).thenReturn(42);
    savedTileSqlDao = new MFTileSqlDao(mockDb, savedMockTile, 1);

  }

  @Test
  public void shouldPrepareStatements()
  {
    unsavedTileSqlDao.prepareStatements();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);

    verify(mockDb, times(6)).prepareQuery(queryId.capture(), query.capture());

    List<String> queryIds = queryId.getAllValues();
    List<String> queries = query.getAllValues();

    assertEquals("READ_MAP_TILES", queryIds.get(0));
    assert(queries.get(0).startsWith("SELECT"));
    assertEquals("CREATE_MFTileSqlDao", queryIds.get(1));
    assert(queries.get(1).startsWith("INSERT"));
    assertEquals("READ_MFTileSqlDao", queryIds.get(2));
    assert(queries.get(2).startsWith("SELECT"));
    assertEquals("READ_ALL_MFTileSqlDao", queryIds.get(3));
    assert(queries.get(3).startsWith("SELECT"));
    assertEquals("UPDATE_MFTileSqlDao", queryIds.get(4));
    assert(queries.get(4).startsWith("UPDATE"));
    assertEquals("DESTROY_MFTileSqlDao", queryIds.get(5));
    assert(queries.get(5).startsWith("DELETE"));
  }

  @Test(expected=NullPointerException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotSaveWithoutTile() throws DataAccessException
  {
    new MFTileSqlDao(mockDb, Collections.EMPTY_MAP).save();
  }

  @Test
  public void shouldCallInsertQueryWhenUnsaved() throws DataAccessException
  {
    unsavedTileSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).insert(queryId.capture(), anyListOf(Object.class));
    assertEquals("CREATE_MFTileSqlDao", queryId.getValue());
  }

  @Test
  public void shouldCallUpdateQueryWhenPreviouslySaved() throws DataAccessException
  {
    savedTileSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).update(queryId.capture(), anyListOf(Object.class));
    assertEquals("UPDATE_MFTileSqlDao", queryId.getValue());
  }

  @Test
  public void shoudlCallGetVectorizedDataOnSave() throws DataAccessException
  {
    MFTileSqlDao spyDao = spy(unsavedTileSqlDao);
    spyDao.save();
    verify(spyDao).getVectorizedData();
  }

  @Test
  public void shouldUpdateTileWithGeneratedId() throws DataAccessException
  {
    final int generatedId = 42;
    when(mockDb.insert(eq("CREATE_MFTileSqlDao"), anyListOf(Object.class)))
            .thenReturn(generatedId);

    unsavedTileSqlDao.save();

    verify(unsavedMockTile).setId(generatedId);
  }

  @Test
  public void shouldNotUpdateIdWhenPreviouslySaved() throws DataAccessException
  {
    savedTileSqlDao.save();

    verify(unsavedMockTile, never()).setId(anyInt());
  }

  @Test
  public void shouldLoad() throws DataAccessException
  {
    MFGround mockGround = mock(MFGround.class);
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();
    groundTypes.put(1, mockGround);

    unsavedTileSqlDao = new MFTileSqlDao(realDb, groundTypes);

    MFTile gotTile = unsavedTileSqlDao.load(7);

    assertEquals("Wrong ID was loaded;", 7, gotTile.getId());
    assertEquals("Wrong ground was loaded;", mockGround, gotTile.getGround());
    assertEquals("Wrong room id was loaded;", null, gotTile.getRoom());
    assertEquals("Wrong object id loaded;", null, gotTile.getObject());
    assertEquals("Wrong x was loaded;", 1, gotTile.getPosX());
    assertEquals("Wrong y was loaded;", 2, gotTile.getPosY());
    assertEquals("Wrong z was loaded;", 0, gotTile.getPosZ());
    assertEquals("Tile's not underground;", true, gotTile.isUnderground());
    assertEquals("Tile's not dug out;", true, gotTile.isDugOut());
    assertEquals("Tile has no north wall;", true, gotTile.hasWallNorth());
    assertEquals("Tile has no east wall;", true, gotTile.hasWallEast());
    assertEquals("Tile has no south wall;", true, gotTile.hasWallSouth());
    assertEquals("Tile has no west wall;", true, gotTile.hasWallWest());
    assertEquals("Tile has no floor;", true, gotTile.hasFloor());

  }

  @Test(expected=DataAccessException.class)
  public void shouldNotLoadNonExistingId() throws DataAccessException
  {
    unsavedTileSqlDao.load(42);
  }

  @Test
  public void shouldLoadAll() throws DataAccessException
  {
    //resetDb();
    int mapId = 2;
    MFGround mockGround = mock(MFGround.class);
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();
    groundTypes.put(1, mockGround);

    unsavedTileSqlDao = new MFTileSqlDao(realDb, groundTypes);
    List<MFTile> gotTiles = unsavedTileSqlDao.loadAllOfMap(mapId);
    assertEquals("Wrong tile count;", 16, gotTiles.size());
  }

  @Test
  public void shouldDelete() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFTileSqlDao"), anyListOf(Object.class))).thenReturn(1);

    savedTileSqlDao.delete();

    verify(mockDb).update(eq("DESTROY_MFTileSqlDao"), anyListOf(Object.class));
    verify(savedMockTile).setId(unsavedTileSqlDao.getUnsavedMarker());
  }

  @Test(expected=NullPointerException.class)
  @SuppressWarnings("unchecked")
  public void shouldNotDeleteWithouTile() throws DataAccessException
  {
    new MFTileSqlDao(mockDb, Collections.EMPTY_MAP).delete();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotDeleteUnsavedTile() throws DataAccessException
  {
    unsavedTileSqlDao.delete();
  }

  @Test(expected=DataAccessException.class)
  public void shouldThrowExceptionWhenNothingWasDeleted() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFTileSqlDao"), anyListOf(Object.class))).thenReturn(0);

    savedTileSqlDao.delete();
  }

  @Test
  public void shouldGetTile()
  {
    assertEquals(unsavedMockTile, unsavedTileSqlDao.getPayload());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotGetTileWithoutTile()
  {
    unsavedTileSqlDao = new MFTileSqlDao(mockDb, Collections.EMPTY_MAP);
    assertNull(unsavedTileSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetTileAfterLoading() throws DataAccessException
  {
    MFGround mockGround = mock(MFGround.class);
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();
    groundTypes.put(1, mockGround);

    unsavedTileSqlDao = new MFTileSqlDao(realDb, groundTypes);
    MFTile gotTile = unsavedTileSqlDao.load(1);
    assertNotNull(gotTile);
    assertNull(unsavedTileSqlDao.getPayload());
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private synchronized void resetDb()
  {
    realDb.loadFromFile("test_fixtures.sql");
  }


}