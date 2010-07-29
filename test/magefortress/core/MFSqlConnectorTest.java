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
package magefortress.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class MFSqlConnectorTest
{

  final static MFSqlConnector dbinstance = MFSqlConnector.getInstance();
  final static String DATABASE = "test.db";
  final static String DATABASE_SCHEMA = "magefortress.sql";
  final static String FIXTURES = "test_fixtures.sql";

  @Before
  public void setUp()
  {
    dbinstance.setDatabaseDriver("org.sqlite.JDBC");
  }
  @Test
  public void shouldGetUnconnectedInstance()
  {
    MFSqlConnector result = MFSqlConnector.getInstance();
    assertEquals(dbinstance, result);
    assertEquals(false, dbinstance.isConnected());
  }

  @Test
  public void shouldSetDatabaseDriver()
  {
    String databaseDriver = "org.sqlite.JDBC";
    dbinstance.setDatabaseDriver(databaseDriver);
    assertEquals(false, dbinstance.isConnected());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotSetInvalidDatabaseDriver()
  {
    String faultyDriver = "invalid.sqldriver";
    dbinstance.setDatabaseDriver(faultyDriver);
  }

  @Test
  public void shouldGetDefaultDatabaseDriver()
  {
    String expResult = "org.sqlite.JDBC";
    String result = dbinstance.getDatabaseDriver();
    // Test default driver
    assertEquals(expResult, result);

  }

  @Test
  public void shouldGetDatabaseDriver()
  {
    String expResult = "org.sqlite.JDBC";
    dbinstance.setDatabaseDriver(expResult);
    String result = dbinstance.getDatabaseDriver();
    assertEquals(expResult, result);
  }

  @Test
  public void shouldConnectWithoutProperties()
  {
    assertEquals(false, dbinstance.isConnected());
    boolean expResult = true;
    boolean result = dbinstance.connect(DATABASE);
    assertEquals(expResult, result);
  }

  @Test
  public void shouldPrepareQuery()
  {
    setupConnection();

    // Test valid query
    String queryId = "READ_ALL_RACES";
    String query = "SELECT * FROM races;";

    boolean success = dbinstance.prepareQuery(queryId, query);

    assertTrue(success);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotPrepareInvalidQuery()
  {
    setupConnection();

    // Test invalid query
    String queryId = "INVALID_READ_ALL_RACES";
    String query = "races FROM * SELECT;";

    boolean success = dbinstance.prepareQuery(queryId, query);

    assertFalse(success);
  }

  @Test
  public void shouldExecuteQuery() throws DataAccessException
  {
    setupFixtures();

    final String queryId = "READ_ALL_RACES";
    final String query = "SELECT * FROM races;";
    
    final boolean success = dbinstance.prepareQuery(queryId, query);
    assertTrue(success);

    final int expRowCount = 2;
    ResultSet rs = dbinstance.query(queryId, new ArrayList<Object>(0));
    assertTrue(rs != null);
    try {
      int rowCount = 0;
      while (rs.next()) {
        ++rowCount;
      }
      rs.close();
      assertEquals(expRowCount, rowCount);

    } catch (SQLException e) {
      assertFalse(true);
    }

  }

  @Test(expected=DataAccessException.class)
  public void shouldNotExecuteQueryMissingAllFillInValues()
          throws DataAccessException, SQLException
  {
    setupFixtures();

    final String queryId = "READ_ALL_RACES";
    final String query = "SELECT * FROM races WHERE hold_behavior = ? AND move_behavior = ?;";

    final boolean success = dbinstance.prepareQuery(queryId, query);
    assertTrue(success);

    ResultSet rs = dbinstance.query(queryId, new ArrayList<Object>());
    rs.close();
  }

  @Test(expected=DataAccessException.class)
  public void shouldNotExecuteQueryMissingOneFillInValue()
          throws DataAccessException, SQLException
  {
    setupFixtures();
    
    final String queryId = "READ_ALL_RACES";
    final String query = "SELECT * FROM races WHERE hold_behavior = ? AND move_behavior = ?;";

    final boolean success = dbinstance.prepareQuery(queryId, query);
    assertTrue(success);

    final ArrayList<Object> values = new ArrayList<Object>(1);
    values.add("magefortress.core.MFNullHoldable");
    ResultSet rs = dbinstance.query(queryId, values);
    rs.close();
  }

  @Test(expected=DataAccessException.class)
  public void shouldNotExecuteNonExistingQuery() throws DataAccessException
  {
    setupSchema();

    final String queryId = "READ_NON_EXISTING_QUERY";
    dbinstance.query(queryId, new ArrayList<Object>(0));

  }

  @Test
  public void shouldUpdate() throws DataAccessException
  {
    setupFixtures();

    final String queryId = "UPDATE_RACE";
    final String query = "UPDATE races SET name = ?, hold_behavior = ?, move_behavior = ? WHERE id = ?;";

    boolean success = dbinstance.prepareQuery(queryId, query);
    assertTrue(success);

    ArrayList<Object> values = new ArrayList<Object>(3);
    values.add("Test Race");
    values.add("magefortress.core.MFNullHoldable");
    values.add("magefortress.core.MFNullMovable");
    values.add(1);

    int expRowCount = 1;
    int rowCount = dbinstance.update(queryId, values);
    assertEquals(expRowCount, rowCount);
  }

  @Test
  public void shouldInsert() throws DataAccessException
  {
    setupFixtures();

    final String queryId = "INSERT_RACE";
    final String query = "INSERT INTO races (name, hold_behavior, move_behavior) VALUES (?, ?, ?);";

    boolean success = dbinstance.prepareQuery(queryId, query);
    assertTrue(success);

    ArrayList<Object> values = new ArrayList<Object>(3);
    values.add("Test Race");
    values.add("magefortress.core.MFNullHoldable");
    values.add("magefortress.core.MFNullMovable");

    int expId = 3;
    int generatedId = dbinstance.insert(queryId, values);
    assertEquals(expId, generatedId);
  }

  @Test
  public void shouldLoadFromFile()
  {
    setupConnection();

    boolean result = dbinstance.loadFromFile(DATABASE_SCHEMA);
    assertTrue(result);

    result = dbinstance.loadFromFile(FIXTURES);
    assertTrue(result);
  }

  @Test
  public void shouldLoadEmptyFile()
  {
    setupConnection();
    boolean result = dbinstance.loadFromFile("test_empty.sql");
    assertTrue(result);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

  private void setupConnection()
  {
    boolean connected = dbinstance.connect(DATABASE, null);
    assertTrue(connected);
  }

  private void setupSchema()
  {
    setupConnection();
    boolean success = dbinstance.loadFromFile(DATABASE_SCHEMA);
    assertTrue(success);
  }

  private void setupFixtures()
  {
    setupSchema();
    boolean success = dbinstance.loadFromFile(FIXTURES);
    assertTrue(success);
  }

}
