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
package magefortress.items;

import java.util.List;
import magefortress.items.placeable.MFUnplaceable;
import magefortress.storage.DataAccessException;
import magefortress.storage.MFSqlConnector;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFBlueprintSqlDaoTest
{

  private MFBlueprintSqlDao unsavedBlueprintSqlDao;
  private MFBlueprintSqlDao savedBlueprintSqlDao;
  private MFBlueprint spyBlueprint;
  private MFBlueprint savedMockBlueprint;
  private MFSqlConnector mockDb;
  private static MFSqlConnector realDb;

  @BeforeClass
  public static void setUpClass()
  {
    realDb = MFSqlConnector.getInstance();
    realDb.connect("magefortress.test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");
    new MFBlueprintSqlDao(realDb).prepareStatements();
  }

  @Before
  public void setUp() throws ClassNotFoundException
  {
    // reset real test db
    //realDb.loadFromFile("magefortress.sql");
    //realDb.loadFromFile("test_fixtures.sql");
    //resetDb();

    mockDb = mock(MFSqlConnector.class);

    MFBlueprint blueprint = new MFBlueprint(new MFBlueprintSqlDao(mockDb).getUnsavedMarker(),
            "Unsaved Spy Blueprint");
    spyBlueprint = spy(blueprint);
    unsavedBlueprintSqlDao = new MFBlueprintSqlDao(mockDb, spyBlueprint);

    blueprint = new MFBlueprint(42, "Saved Spy Blueprint");
    savedMockBlueprint = spy(blueprint);
    savedBlueprintSqlDao = new MFBlueprintSqlDao(mockDb, savedMockBlueprint);

  }

  @Test
  public void shouldPrepareStatements()
  {
    unsavedBlueprintSqlDao.prepareStatements();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);

    verify(mockDb, times(5)).prepareQuery(queryId.capture(), query.capture());

    List<String> queryIds = queryId.getAllValues();
    List<String> queries = query.getAllValues();

    assertEquals("CREATE_MFBlueprintSqlDao", queryIds.get(0));
    assert(queries.get(0).startsWith("INSERT"));
    assertEquals("READ_MFBlueprintSqlDao", queryIds.get(1));
    assert(queries.get(1).startsWith("SELECT"));
    assertEquals("READ_ALL_MFBlueprintSqlDao", queryIds.get(2));
    assert(queries.get(2).startsWith("SELECT"));
    assertEquals("UPDATE_MFBlueprintSqlDao", queryIds.get(3));
    assert(queries.get(3).startsWith("UPDATE"));
    assertEquals("DESTROY_MFBlueprintSqlDao", queryIds.get(4));
    assert(queries.get(4).startsWith("DELETE"));

  }

  @Test(expected=NullPointerException.class)
  public void shouldNotSaveWithoutBlueprint() throws DataAccessException
  {
    new MFBlueprintSqlDao(mockDb).save();
  }

  @Test
  public void shouldCallInsertQueryWhenUnsaved() throws DataAccessException
  {
    unsavedBlueprintSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).insert(queryId.capture(), anyListOf(Object.class));
    assertEquals("CREATE_MFBlueprintSqlDao", queryId.getValue());
  }

  @Test
  public void shouldCallUpdateQueryWhenPreviouslySaved() throws DataAccessException
  {
    savedBlueprintSqlDao.save();

    ArgumentCaptor<String> queryId = ArgumentCaptor.forClass(String.class);

    verify(mockDb).update(queryId.capture(), anyListOf(Object.class));
    assertEquals("UPDATE_MFBlueprintSqlDao", queryId.getValue());
  }

  @Test
  public void shoudlCallGetVectorizedDataOnSave() throws DataAccessException
  {
    MFBlueprintSqlDao spyDao = spy(unsavedBlueprintSqlDao);
    spyDao.save();
    verify(spyDao).getVectorizedData();
  }

  @Test
  public void shouldUpdateWithGeneratedId() throws DataAccessException
  {
    final int generatedId = 42;
    when(mockDb.insert(eq("CREATE_MFBlueprintSqlDao"), anyListOf(Object.class)))
            .thenReturn(generatedId);

    unsavedBlueprintSqlDao.save();

    verify(spyBlueprint).setId(generatedId);
  }

  @Test
  public void shouldNotUpdateIdWhenPreviouslySaved() throws DataAccessException
  {
    savedBlueprintSqlDao.save();

    verify(spyBlueprint, never()).setId(anyInt());
  }

  @Test
  public void shouldLoad() throws DataAccessException
  {
    unsavedBlueprintSqlDao = new MFBlueprintSqlDao(realDb);

    MFBlueprint gotBlueprint = unsavedBlueprintSqlDao.load(1);

    assertEquals(1, gotBlueprint.getId());
    assertEquals("Green Nail", gotBlueprint.getName());
    assertEquals(MFUnplaceable.class.getName(), gotBlueprint.getPlacingBehavior().getName());
  }

  @Test(expected=DataAccessException.class)
  public void shouldNotLoadNonExistingId() throws DataAccessException
  {
    unsavedBlueprintSqlDao.load(42);
  }

  @Test
  public void shouldLoadAll() throws DataAccessException
  {
    unsavedBlueprintSqlDao = new MFBlueprintSqlDao(realDb);
    List<? extends MFBlueprint> gotBlueprints = unsavedBlueprintSqlDao.loadAll();
    assertEquals(2, gotBlueprints.size());
  }

  @Test
  public void shouldDelete() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFBlueprintSqlDao"), anyListOf(Object.class))).thenReturn(1);

    savedBlueprintSqlDao.delete();

    verify(mockDb).update(eq("DESTROY_MFBlueprintSqlDao"), anyListOf(Object.class));
    verify(savedMockBlueprint).setId(unsavedBlueprintSqlDao.getUnsavedMarker());
  }

  @Test(expected=NullPointerException.class)
  public void shouldNotDeleteWithouBlueprint() throws DataAccessException
  {
    new MFBlueprintSqlDao(mockDb).delete();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotDeleteUnsavedBlueprint() throws DataAccessException
  {
    unsavedBlueprintSqlDao.delete();
  }

  @Test(expected=DataAccessException.class)
  public void shouldThrowExceptionWhenNothingWasDeleted() throws DataAccessException
  {
    when(mockDb.update(eq("DESTROY_MFBlueprintSqlDao"), anyListOf(Object.class))).thenReturn(0);

    savedBlueprintSqlDao.delete();
  }

  @Test
  public void shouldGetBlueprint()
  {
    assertEquals(spyBlueprint, unsavedBlueprintSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetWithoutBlueprint()
  {
    unsavedBlueprintSqlDao = new MFBlueprintSqlDao(mockDb);
    assertNull(unsavedBlueprintSqlDao.getPayload());
  }

  @Test
  public void shouldNotGetAfterLoading() throws DataAccessException
  {
    unsavedBlueprintSqlDao = new MFBlueprintSqlDao(realDb);
    MFBlueprint gotBlueprint = unsavedBlueprintSqlDao.load(1);
    assertNotNull(gotBlueprint);
    assertNull(unsavedBlueprintSqlDao.getPayload());
  }

}