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
import magefortress.creatures.MFRace;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFRaceSqlDaoTest
{
  private MFRaceSqlDao unsavedRaceSqlDao;
  private MFRaceSqlDao savedRaceSqlDao;
  private MFRace spyRace;
  private MFRace savedMockRace;
  private MFSqlConnector mockDb;
  private static MFSqlConnector realDb;

  @BeforeClass
  public static void setUpClass()
  {
    realDb = MFSqlConnector.getInstance();
    realDb.connect("magefortress.test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");
    new MFRaceSqlDao(realDb).prepareStatements();
  }

  @Before
  public void setUp() throws ClassNotFoundException
  {
    // reset real test db
    //realDb.loadFromFile("magefortress.sql");
    //realDb.loadFromFile("test_fixtures.sql");
    //resetDb();

    mockDb = mock(MFSqlConnector.class);
    
    MFRace race = new MFRace(new MFRaceSqlDao(mockDb).getUnsavedMarker(),
            "Unsaved Spy Race", "magefortress.creatures.behavior.MFNullMovable", "magefortress.creatures.behavior.MFNullHoldable");
    spyRace = spy(race);
    unsavedRaceSqlDao = new MFRaceSqlDao(mockDb, spyRace);

    race = new MFRace(42,
            "Saved Spy Race", "magefortress.creatures.behavior.MFNullMovable", "magefortress.creatures.behavior.MFNullHoldable");
    savedMockRace = spy(race);
    savedRaceSqlDao = new MFRaceSqlDao(mockDb, savedMockRace);

  }

  @Test
  public void shouldPrepareStatements()
  {
    unsavedRaceSqlDao.prepareStatements();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
    
    verify(mockDb, times(5)).prepareQuery(queryId.capture(), query.capture());

    List<String> queryIds = queryId.getAllValues();
    List<String> queries = query.getAllValues();

    assertEquals("CREATE_MFRaceSqlDao", queryIds.get(0));
    assert(queries.get(0).startsWith("INSERT"));
    assertEquals("READ_MFRaceSqlDao", queryIds.get(1));
    assert(queries.get(1).startsWith("SELECT"));
    assertEquals("READ_ALL_MFRaceSqlDao", queryIds.get(2));
    assert(queries.get(2).startsWith("SELECT"));
    assertEquals("UPDATE_MFRaceSqlDao", queryIds.get(3));
    assert(queries.get(3).startsWith("UPDATE"));
    assertEquals("DESTROY_MFRaceSqlDao", queryIds.get(4));
    assert(queries.get(4).startsWith("DELETE"));

  }

  @Test(expected=NullPointerException.class)
  public void shouldNotSaveWithoutRace() throws DataAccessException
  {
    new MFRaceSqlDao(mockDb).save();
  }

  @Test
  public void shouldCallInsertQueryWhenUnsaved() throws DataAccessException
  {
    unsavedRaceSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).insert(queryId.capture(), anyListOf(Object.class));
    assertEquals("CREATE_MFRaceSqlDao", queryId.getValue());
  }

  @Test
  public void shouldCallUpdateQueryWhenPreviouslySaved() throws DataAccessException
  {
    savedRaceSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).update(queryId.capture(), anyListOf(Object.class));
    assertEquals("UPDATE_MFRaceSqlDao", queryId.getValue());
  }

  @Test
  public void shoudlCallGetVectorizedDataOnSave() throws DataAccessException
  {
    MFRaceSqlDao spyDao = spy(unsavedRaceSqlDao);
    spyDao.save();
    verify(spyDao).getVectorizedData();
  }

  @Test
  public void shouldUpdateRaceWithGeneratedId() throws DataAccessException
  {
    final int generatedId = 42;
    when(mockDb.insert(eq("CREATE_MFRaceSqlDao"), anyListOf(Object.class)))
            .thenReturn(generatedId);

    unsavedRaceSqlDao.save();

    verify(spyRace).setId(generatedId);
  }

  @Test
  public void shouldNotUpdateIdWhenPreviouslySaved() throws DataAccessException
  {
    savedRaceSqlDao.save();

    verify(spyRace, never()).setId(anyInt());
  }

  @Test
  public void shouldLoad() throws DataAccessException
  {
    unsavedRaceSqlDao = new MFRaceSqlDao(realDb);
    
    MFRace gotRace = unsavedRaceSqlDao.load(1);

    assertEquals(1, gotRace.getId());
    assertEquals("Martian", gotRace.getName());
    assertEquals("magefortress.creatures.behavior.MFNullHoldable", gotRace.getHoldingBehaviorClass().getName());
    assertEquals("magefortress.creatures.behavior.MFNullMovable", gotRace.getMovingBehaviorClass().getName());

  }

  @Test(expected=DataAccessException.class)
  public void shouldNotLoadNonExistingId() throws DataAccessException
  {
    unsavedRaceSqlDao.load(42);
  }

  @Test
  public void shouldLoadAll() throws DataAccessException
  {
    //resetDb();
    unsavedRaceSqlDao = new MFRaceSqlDao(realDb);
    List<? extends MFRace> gotRaces = unsavedRaceSqlDao.loadAll();
    assertEquals(2, gotRaces.size());
  }

  @Test
  public void shouldDelete() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFRaceSqlDao"), anyListOf(Object.class))).thenReturn(1);
    
    savedRaceSqlDao.delete();

    verify(mockDb).update(eq("DESTROY_MFRaceSqlDao"), anyListOf(Object.class));
    verify(savedMockRace).setId(unsavedRaceSqlDao.getUnsavedMarker());
  }

  @Test(expected=NullPointerException.class)
  public void shouldNotDeleteWithouRace() throws DataAccessException
  {
    new MFRaceSqlDao(mockDb).delete();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotDeleteUnsavedRace() throws DataAccessException
  {
    unsavedRaceSqlDao.delete();
  }

  @Test(expected=DataAccessException.class)
  public void shouldThrowExceptionWhenNothingWasDeleted() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFRaceSqlDao"), anyListOf(Object.class))).thenReturn(0);

    savedRaceSqlDao.delete();
  }

  @Test
  public void shouldGetRace()
  {
    assertEquals(spyRace, unsavedRaceSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetRaceWithoutRace()
  {
    unsavedRaceSqlDao = new MFRaceSqlDao(mockDb);
    assertNull(unsavedRaceSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetRaceAfterLoading() throws DataAccessException
  {
    unsavedRaceSqlDao = new MFRaceSqlDao(realDb);
    MFRace gotRace = unsavedRaceSqlDao.load(1);
    assertNotNull(gotRace);
    assertNull(unsavedRaceSqlDao.getPayload());
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private synchronized void resetDb()
  {
    realDb.loadFromFile("test_fixtures.sql");
  }


}