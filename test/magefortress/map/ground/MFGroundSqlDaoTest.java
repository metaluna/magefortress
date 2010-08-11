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
package magefortress.map.ground;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import magefortress.core.MFEDirection;
import magefortress.graphics.MFIPaintable;
import magefortress.graphics.MFImageLibrary;
import magefortress.items.MFBlueprint;
import magefortress.map.MFTile.Corner;
import magefortress.storage.DataAccessException;
import magefortress.storage.MFSqlConnector;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGroundSqlDaoTest
{
  private MFGroundSqlDao unsavedGroundSqlDao;
  private MFGroundSqlDao savedGroundSqlDao;
  private MFGround spyGround;
  private MFGround savedMockGround;
  private MFSqlConnector mockDb;
  private MFBlueprint blueprint;

  private MFImageLibrary imgLib = MFImageLibrary.getInstance();
  private Map<Integer, MFBlueprint> blueprints;
  private MFIPaintable solid;
  private MFIPaintable floor;
  private EnumMap<MFEDirection, MFIPaintable> walls;
  private EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>> corners;

  private static MFSqlConnector realDb;

  @BeforeClass
  public static void setUpClass()
  {
    realDb = MFSqlConnector.getInstance();
    realDb.connect("magefortress.test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");
    new MFGroundSqlDao(realDb).prepareStatements();
  }

  @Before
  public void setUp() throws ClassNotFoundException
  {
    mockDb = mock(MFSqlConnector.class);
    blueprint = mock(MFBlueprint.class);

    solid = mock(MFIPaintable.class);
    floor = mock(MFIPaintable.class);
    walls = new EnumMap<MFEDirection, MFIPaintable>(MFEDirection.class);
    for (MFEDirection dir : MFEDirection.straight()) {
      walls.put(dir, mock(MFIPaintable.class));
    }
    corners = new EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>>(MFEDirection.class);
    for (MFEDirection dir : MFEDirection.diagonals()) {
      EnumMap<Corner, MFIPaintable> dirCorners= new EnumMap<Corner, MFIPaintable>(Corner.class);
      corners.put(dir, dirCorners);

      for (Corner c : Corner.values()) {
        dirCorners.put(c, mock(MFIPaintable.class));
      }
    }

    MFGround ground = new MFGround(new MFGroundSqlDao(mockDb).getUnsavedMarker(), this.blueprint, 999, solid, floor, walls, corners);
    spyGround = spy(ground);
    unsavedGroundSqlDao = new MFGroundSqlDao(mockDb, spyGround);

    ground = new MFGround(42, this.blueprint, 23, solid, floor, walls, corners);
    savedMockGround = spy(ground);
    savedGroundSqlDao = new MFGroundSqlDao(mockDb, savedMockGround);

    this.blueprints = new HashMap<Integer, MFBlueprint>();
    when(blueprint.getName()).thenReturn("Rubinite");
    this.blueprints.put(1, blueprint);
    this.blueprints.put(2, blueprint);
  }

  @Test
  public void shouldPrepareStatements()
  {
    unsavedGroundSqlDao.prepareStatements();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);

    verify(mockDb, times(5)).prepareQuery(queryId.capture(), query.capture());

    List<String> queryIds = queryId.getAllValues();
    List<String> queries = query.getAllValues();

    assertEquals("CREATE_MFGroundSqlDao", queryIds.get(0));
    assert(queries.get(0).startsWith("INSERT"));
    assertEquals("READ_MFGroundSqlDao", queryIds.get(1));
    assert(queries.get(1).startsWith("SELECT"));
    assertEquals("READ_ALL_MFGroundSqlDao", queryIds.get(2));
    assert(queries.get(2).startsWith("SELECT"));
    assertEquals("UPDATE_MFGroundSqlDao", queryIds.get(3));
    assert(queries.get(3).startsWith("UPDATE"));
    assertEquals("DESTROY_MFGroundSqlDao", queryIds.get(4));
    assert(queries.get(4).startsWith("DELETE"));

  }

  @Test(expected=NullPointerException.class)
  public void shouldNotSaveWithoutGround() throws DataAccessException
  {
    new MFGroundSqlDao(mockDb).save();
  }

  @Test
  public void shouldCallInsertQueryWhenUnsaved() throws DataAccessException
  {
    unsavedGroundSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).insert(queryId.capture(), anyListOf(Object.class));
    assertEquals("CREATE_MFGroundSqlDao", queryId.getValue());
  }

  @Test
  public void shouldCallUpdateQueryWhenPreviouslySaved() throws DataAccessException
  {
    savedGroundSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).update(queryId.capture(), anyListOf(Object.class));
    assertEquals("UPDATE_MFGroundSqlDao", queryId.getValue());
  }

  @Test
  public void shoudlCallGetVectorizedDataOnSave() throws DataAccessException
  {
    MFGroundSqlDao spyDao = spy(unsavedGroundSqlDao);
    spyDao.save();
    verify(spyDao).getVectorizedData();
  }

  @Test
  public void shouldUpdateWithGeneratedId() throws DataAccessException
  {
    final int generatedId = 42;
    when(mockDb.insert(eq("CREATE_MFGroundSqlDao"), anyListOf(Object.class)))
            .thenReturn(generatedId);

    unsavedGroundSqlDao.save();

    verify(spyGround).setId(generatedId);
  }

  @Test
  public void shouldNotUpdateIdWhenPreviouslySaved() throws DataAccessException
  {
    savedGroundSqlDao.save();

    verify(spyGround, never()).setId(anyInt());
  }

  @Test
  public void shouldLoad() throws DataAccessException
  {
    unsavedGroundSqlDao = new MFGroundSqlDao(realDb, blueprints, imgLib);

    MFGround gotGround = unsavedGroundSqlDao.load(1);

    assertEquals(1, gotGround.getId());
    assertEquals("Rubinite", gotGround.getBlueprint().getName());
    assertEquals(255, gotGround.getHardness());
  }

  @Test(expected=DataAccessException.class)
  public void shouldNotLoadNonExistingId() throws DataAccessException
  {
    unsavedGroundSqlDao.load(42);
  }

  @Test
  public void shouldLoadAll() throws DataAccessException
  {
    unsavedGroundSqlDao = new MFGroundSqlDao(realDb, blueprints, imgLib);
    List<? extends MFGround> gotBlueprints = unsavedGroundSqlDao.loadAll();
    assertEquals(2, gotBlueprints.size());
  }

  @Test
  public void shouldDelete() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFGroundSqlDao"), anyListOf(Object.class))).thenReturn(1);

    savedGroundSqlDao.delete();

    verify(mockDb).update(eq("DESTROY_MFGroundSqlDao"), anyListOf(Object.class));
    verify(savedMockGround).setId(unsavedGroundSqlDao.getUnsavedMarker());
  }

  @Test(expected=NullPointerException.class)
  public void shouldNotDeleteWithouBlueprint() throws DataAccessException
  {
    new MFGroundSqlDao(mockDb).delete();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotDeleteUnsavedBlueprint() throws DataAccessException
  {
    unsavedGroundSqlDao.delete();
  }

  @Test(expected=DataAccessException.class)
  public void shouldThrowExceptionWhenNothingWasDeleted() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFGroundSqlDao"), anyListOf(Object.class))).thenReturn(0);

    savedGroundSqlDao.delete();
  }

  @Test
  public void shouldGetBlueprint()
  {
    assertEquals(spyGround, unsavedGroundSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetWithoutBlueprint()
  {
    unsavedGroundSqlDao = new MFGroundSqlDao(mockDb, blueprints, imgLib);
    assertNull(unsavedGroundSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetAfterLoading() throws DataAccessException
  {
    unsavedGroundSqlDao = new MFGroundSqlDao(realDb, blueprints, imgLib);
    MFGround gotBlueprint = unsavedGroundSqlDao.load(1);
    assertNotNull(gotBlueprint);
    assertNull(unsavedGroundSqlDao.getPayload());
  }

}