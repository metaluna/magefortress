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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFSqlDaoTest
{
  private MFSqlDao sqlDao;
  private MFSqlConnector mockDb;
  private MFISaveable mockPayload;
  private static MFSqlConnector realDb;

  @BeforeClass
  public static void setUpClass()
  {
    realDb = MFSqlConnector.getInstance();
    realDb.connect("test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");
    new MFRaceSqlDao(realDb).prepareStatements();
  }

  @Before
  public void setUp()
  {
    mockPayload = mock(MFISaveable.class);
    mockDb = mock(MFSqlConnector.class);
    sqlDao = new MFMockSqlDao(mockDb, mockPayload);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutConnector()
  {
    new MFMockSqlDao(null);
  }

  @Test
  public void shouldPrepareStatements()
  {
    sqlDao.prepareStatements();
    verify(mockDb).prepareQuery("CREATE_MFMockSqlDao", "c");
    verify(mockDb).prepareQuery("READ_MFMockSqlDao", "r");
    verify(mockDb).prepareQuery("READ_ALL_MFMockSqlDao", "ra");
    verify(mockDb).prepareQuery("UPDATE_MFMockSqlDao", "u");
    verify(mockDb).prepareQuery("DESTROY_MFMockSqlDao", "d");
  }

  @Test
  public void shouldCallReadVectorizedData() throws DataAccessException
  {
    MFSqlDao spyDao = spy(new MFMockSqlDao(realDb, mockPayload));
    spyDao.load(1);
    

  }

  @Test
  public void shoudlCallGetVectorizedData() throws DataAccessException
  {
    MFSqlDao spyDao = spy(sqlDao);
    spyDao.save();
    verify(spyDao).getVectorizedData();
  }

}