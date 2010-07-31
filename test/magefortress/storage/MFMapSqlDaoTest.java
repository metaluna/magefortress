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
package magefortress.storage;

import java.util.List;
import magefortress.map.MFMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFMapSqlDaoTest
{
  private MFMapSqlDao unsavedMapSqlDao;
  private MFMapSqlDao savedMapSqlDao;
  private MFMap unsavedMockMap;
  private MFMap savedMockMap;
  private MFSqlConnector mockDb;
  private static MFSqlConnector realDb;

  @BeforeClass
  public static void setUpClass()
  {
    realDb = MFSqlConnector.getInstance();
    realDb.connect("test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");
    new MFMapSqlDao(realDb).prepareStatements();
  }

  @Before
  public void setUp() throws ClassNotFoundException
  {
    // reset real test db
    //realDb.loadFromFile("magefortress.sql");
    //realDb.loadFromFile("test_fixtures.sql");
    //resetDb();

    mockDb = mock(MFSqlConnector.class);

    unsavedMockMap = mock(MFMap.class);
    when(unsavedMockMap.getId()).thenReturn(new MFMapSqlDao(mockDb).getUnsavedMarker());
    unsavedMapSqlDao = new MFMapSqlDao(mockDb, unsavedMockMap);

    savedMockMap = mock(MFMap.class);
    when(savedMockMap.getId()).thenReturn(42);
    savedMapSqlDao = new MFMapSqlDao(mockDb, savedMockMap);

  }

  @Test
  public void shouldPrepareStatements()
  {
    unsavedMapSqlDao.prepareStatements();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);

    verify(mockDb, times(5)).prepareQuery(queryId.capture(), query.capture());

    List<String> queryIds = queryId.getAllValues();
    List<String> queries = query.getAllValues();

    assertEquals("CREATE_MFMapSqlDao", queryIds.get(0));
    assert(queries.get(0).startsWith("INSERT"));
    assertEquals("READ_MFMapSqlDao", queryIds.get(1));
    assert(queries.get(1).startsWith("SELECT"));
    assertEquals("READ_ALL_MFMapSqlDao", queryIds.get(2));
    assert(queries.get(2).startsWith("SELECT"));
    assertEquals("UPDATE_MFMapSqlDao", queryIds.get(3));
    assert(queries.get(3).startsWith("UPDATE"));
    assertEquals("DESTROY_MFMapSqlDao", queryIds.get(4));
    assert(queries.get(4).startsWith("DELETE"));

  }

  @Test(expected=NullPointerException.class)
  public void shouldNotSaveWithoutMap() throws DataAccessException
  {
    new MFMapSqlDao(mockDb).save();
  }

  @Test
  public void shouldCallInsertQueryWhenUnsaved() throws DataAccessException
  {
    unsavedMapSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).insert(queryId.capture(), anyListOf(Object.class));
    assertEquals("CREATE_MFMapSqlDao", queryId.getValue());
  }

  @Test
  public void shouldCallUpdateQueryWhenPreviouslySaved() throws DataAccessException
  {
    savedMapSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).update(queryId.capture(), anyListOf(Object.class));
    assertEquals("UPDATE_MFMapSqlDao", queryId.getValue());
  }

  @Test
  public void shoudlCallGetVectorizedDataOnSave() throws DataAccessException
  {
    MFSqlDao spyDao = spy(unsavedMapSqlDao);
    spyDao.save();
    verify(spyDao).getVectorizedData();
  }

  @Test
  public void shouldUpdateMapWithGeneratedId() throws DataAccessException
  {
    final int generatedId = 42;
    when(mockDb.insert(eq("CREATE_MFMapSqlDao"), anyListOf(Object.class)))
            .thenReturn(generatedId);

    unsavedMapSqlDao.save();

    verify(unsavedMockMap).setId(generatedId);
  }

  @Test
  public void shouldNotUpdateIdWhenPreviouslySaved() throws DataAccessException
  {
    savedMapSqlDao.save();

    verify(unsavedMockMap, never()).setId(anyInt());
  }

  @Test
  public void shouldLoad() throws DataAccessException
  {
    unsavedMapSqlDao = new MFMapSqlDao(realDb);

    MFMap gotMap = unsavedMapSqlDao.load(1);

    assertEquals("Wrong ID was loaded;", 1, gotMap.getId());
    assertEquals("Wrong width was loaded;", 13, gotMap.getWidth());
    assertEquals("Wrong height was loaded;", 14, gotMap.getHeight());
    assertEquals("Wrong depth loaded;", 15, gotMap.getDepth());

  }

  @Test(expected=DataAccessException.class)
  public void shouldNotLoadNonExistingId() throws DataAccessException
  {
    unsavedMapSqlDao.load(42);
  }

  @Test
  public void shouldLoadAll() throws DataAccessException
  {
    //resetDb();
    unsavedMapSqlDao = new MFMapSqlDao(realDb);
    List<MFMap> gotMaps = unsavedMapSqlDao.loadAll();
    assertEquals("Wrong map count;", 2, gotMaps.size());
  }

  @Test
  public void shouldDelete() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFMapSqlDao"), anyListOf(Object.class))).thenReturn(1);

    savedMapSqlDao.delete();

    verify(mockDb).update(eq("DESTROY_MFMapSqlDao"), anyListOf(Object.class));
    verify(savedMockMap).setId(unsavedMapSqlDao.getUnsavedMarker());
  }

  @Test(expected=NullPointerException.class)
  public void shouldNotDeleteWithouMap() throws DataAccessException
  {
    new MFMapSqlDao(mockDb).delete();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotDeleteUnsavedMap() throws DataAccessException
  {
    unsavedMapSqlDao.delete();
  }

  @Test(expected=DataAccessException.class)
  public void shouldThrowExceptionWhenNothingWasDeleted() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFMapSqlDao"), anyListOf(Object.class))).thenReturn(0);

    savedMapSqlDao.delete();
  }

  @Test
  public void shouldGetMap()
  {
    assertEquals(unsavedMockMap, unsavedMapSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetMapWithoutMap()
  {
    unsavedMapSqlDao = new MFMapSqlDao(mockDb);
    assertNull(unsavedMapSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetMapAfterLoading() throws DataAccessException
  {
    unsavedMapSqlDao = new MFMapSqlDao(realDb);
    MFMap gotMap = unsavedMapSqlDao.load(1);
    assertNotNull(gotMap);
    assertNull(unsavedMapSqlDao.getPayload());
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private synchronized void resetDb()
  {
    realDb.loadFromFile("test_fixtures.sql");
  }


}