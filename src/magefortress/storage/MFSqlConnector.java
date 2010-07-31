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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.Singleton;

/**
 * Handles low-level database work. This class is a singleton.
 */
public class MFSqlConnector implements Singleton
{

  public static final int INVALID_QUERY = -1;

  /**
   * Get the single instance of <code>MFSqlConnector</code>. Before you can
   * make a connection to the database, you have to <code>connect</code> to it.
   * @return An instance of MFSqlConnector.
   * @see MFSqlConnector.connect()
   */
  public static MFSqlConnector getInstance()
  {
    return MFSqlConnector.instance;
  }

  /**
   * Sets an alternative JDBC database driver.
   * @param _databaseDriver The name of the driver (e.g. "org.sqlite.JDBC")
   */
  public void setDatabaseDriver(String _databaseDriver)
  {
    this.databaseDriver = _databaseDriver;
    disconnect();

    try {
      Class.forName(_databaseDriver);
    } catch (ClassNotFoundException e) {
      String msg = "Couldn't find database driver: " + _databaseDriver;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

  }

  /**
   * The currently used JDBC driver.
   * @return The currently used JDBC driver.
   */
  public String getDatabaseDriver()
  {
    return this.databaseDriver;
  }

  /**
   * Initiates a connection to the database.
   * @param _database The name of the database file.
   * @return Was a connection made?
   */
  public boolean connect(String _database)
  {
    return connect(_database, null);
  }

  /**
   * Initiates a connection to the database with additional parameters.
   * @param _database The name of the database file.
   * @param _info Additional parameters
   * @return Was a connection made?
   */
  public boolean connect(String _database, Properties _info)
  {
    disconnect();

    try {
      if (_info != null) {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + _database, _info);
      } else {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + _database);
      }
    } catch (SQLException e) {
      logger.log(Level.SEVERE,
              "Connection to database \"" + _database + "\" couldn't be " +
              "established. Properties: " + _info, e);
      return false;
    }
    this.isConnected = true;
    return true;
  }

  public boolean isConnected()
  {
    return this.isConnected;
  }

  /**
   * Save a prepared query. The query can later be executed using
   * <code>query</code> and its id string. If there's already a query stored
   * under the same id, it will be overwritten.
   * @param _queryId The id which is used to retrieve the query later
   * @param _query The query to be saved
   * @return wether the query is valid
   */
  public boolean prepareQuery(String _queryId, String _query)
  {
    if (!this.isConnected) {
      return false;
    }

    PreparedStatement statement;

    try {
      statement = this.connection.prepareStatement(_query);
    } catch (SQLException e) {
      String msg = "Unable to save query \"" + _queryId + "\": " + _query;
      logger.log(Level.SEVERE, msg, e);
      throw new IllegalArgumentException(msg, e);
    }

    preparedStatements.put(_queryId, statement);

    return true;
  }

  /**
   * Executes a read-only query
   * @param _queryId The saved query to execute.
   * @param _values The parameters of the saved query. May be empty.
   * @return The rowset which was found
   */
  public ResultSet query(String _queryId, List<Object> _values)
          throws DataAccessException
  {
    final PreparedStatement statement = findAndPrepareStatement(_queryId, _values);

    ResultSet rs;

    try {
      // retrieve data from database
      rs = statement.executeQuery();
    } catch (SQLException e) {
      String msg = "Couldn't execute prepared query \"" + _queryId + 
              "\". Values: " + _values;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    return rs;
  }

  /**
   * Updates records in the database. Returns the number of updated rows.
   * @param _queryId The saved query to execute.
   * @param _values The parameters of the saved query. May be empty.
   * @return The number of updated rows
   */
  public int update(String _queryId, List<Object> _values)
          throws DataAccessException
  {
    final PreparedStatement statement = findAndPrepareStatement(_queryId, _values);

    int rowCount = 0;

    try {
      // retrieve data from database
      rowCount = statement.executeUpdate();
    } catch (SQLException e) {
      String msg = "Couldn't execute prepared update \"" + _queryId + 
              "\". Values: " + _values;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    return rowCount;
  }

  /**
   * Stores a new record into the database.
   * @param _queryId The saved query to execute.
   * @param _values The parameters of the saved query. May be empty.
   * @return The generated key
   * @throws DataAccessException
   */
  public int insert(String _queryId, List<Object> _values)
          throws DataAccessException
  {
    PreparedStatement statement = findAndPrepareStatement(_queryId, _values);

    int rowCount;
    int generatedKey = INVALID_QUERY;

    try {
      // retrieve data from database
      rowCount = statement.executeUpdate();
    } catch (SQLException e) {
      String msg = "Couldn't execute prepared insert \"" + _queryId + 
              "\". Values: " + _values;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    try {
      // get auto-generated id if any
      ResultSet keys = statement.getGeneratedKeys();
      if (keys.next() && keys != null) {
        generatedKey = keys.getInt(1);
      }
      keys.close();
    } catch (SQLException e) {
      String msg = "Unable to retrieve id of last insert of \"" + _queryId +
              "\". Values: " + _values;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    return generatedKey;
  }

  /**
   * Loads SQL statements from a file.
   * @param _filename The file to read from.
   */
  public boolean loadFromFile(String _filename)
  {
    if (!this.isConnected) {
      return false;
    }

    boolean success = true;

    try {
      Statement statement = this.connection.createStatement();

      success = readFile(_filename, statement);
      if (!success) {
        return false;
      }

      // execute queries
      logger.fine("Batch committing read SQL commands...");
      this.connection.setAutoCommit(false);
      int results[] = statement.executeBatch();
      this.connection.setAutoCommit(true);

      // check results for errors
      for (int result : results) {
        if (result < 0) {
          success = false;
        }
      }
    } catch (SQLException e) {
      logger.log(Level.SEVERE,
              "Couldn't execute queries read from file \"" + _filename + "\".", e);
      success = false;
    }

    return success;
  }

  private boolean readFile(String _filename, Statement _statement)
          throws SQLException
  {
    boolean success = true;
    try
    {
      // get a file reader
      BufferedReader in = new BufferedReader(new FileReader(_filename));
      try {
        String line;
        // read the file, one sql query per line
        logger.fine("Reading SQL statements from file \"" + _filename + "\"...");
        while ((line = in.readLine()) != null) {
          logger.fine(line);
          // check if it's not empty or a comment
          if (line.startsWith("//") || line.equals("")) {
            continue;
          }
          _statement.addBatch(line);
        }
      } finally {
        in.close();
        logger.fine("Done.");
      }
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE,
              "Couldn't find database file \"" + _filename + "\".", e);
      success = false;
    } catch (IOException e) {
      logger.log(Level.SEVERE,
              "Couldn't read from file \"" + _filename + "\".", e);
      success = false;
    }
    return success;
  }

  // ---vvv--- PRIVATE METHODS ---vvv---
  
  /** Default database driver */
  private static final String DEFAULT_DB_DRIVER = "org.sqlite.JDBC";
  /** Singleton instance */
  private static final MFSqlConnector instance = new MFSqlConnector();
  /** Logger */
  private static final Logger logger = Logger.getLogger(MFSqlConnector.class.getName());
  private String databaseDriver;
  private Connection connection;
  private boolean isConnected;
  private HashMap<String, PreparedStatement> preparedStatements;

  /**
   * Hidden standard constructor. (cf. Singleton pattern)
   */
  private MFSqlConnector()
  {
    this.setDatabaseDriver(DEFAULT_DB_DRIVER);
    this.isConnected = false;
    this.preparedStatements = new HashMap<String, PreparedStatement>();
  }

  private void disconnect()
  {
    if (!this.isConnected) {
      return;
    }

    try {
      this.connection.close();
      this.isConnected = false;
    } catch (SQLException e) {
      String msg = "Couldn't disconnect from database.";
      logger.log(Level.SEVERE, msg, e);
    }
  }

  /**
   * Loads a previously stored statement and fills in the values if needed.
   * @param _queryId The id which is used to identify the query
   * @param _values The values to insert into the query. May be an empty list.
   * @return <code>null</code> if no query was saved or a connection error
   *          is encountered
   */
  private PreparedStatement findAndPrepareStatement(String _queryId, List<Object> _values)
          throws DataAccessException
  {
    if (!this.isConnected) {
      String msg = "Couldn't connect to database during preparation of query \"" +
              _queryId + "\". Values: " + _values;
      logger.severe(msg);
      throw new DataAccessException(msg);
    }

    // find saved query
    final PreparedStatement statement = this.preparedStatements.get(_queryId);

    // was the query found?
    if (statement == null) {
      String msg = "Couldn't find prepared statement identified by \"" +
              _queryId + "\". Values: " + _values;
      logger.severe(msg);
      throw new DataAccessException(msg);
    }

    // check parameter count
    try
    {
      final int parameterCount = statement.getParameterMetaData().getParameterCount();
      if (_values.size() != parameterCount) {
        String msg = "Parameter count mismatch during preparation of query \"" +
                _queryId + "\". Needed " + parameterCount + " but got " + _values.size() +
                ". Values: " + _values;
        logger.severe(msg);
        throw new DataAccessException(msg);
      }
    } catch (SQLException e) {
      String msg = "Couldn't compare parameter count during preparation of query \"" +
              _queryId + "\". Values: " + _values;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    if (!_values.isEmpty()) {
      prepareStatement(_queryId, _values, statement);
    }

    return statement;
  }

  /**
   * Inserts values into a prepared statement.
   * @param _queryId The id of the query used for error messages
   * @param _values The values to fill in
   * @param _statement The prepared statement
   * @throws DataAccessException
   */
  private void prepareStatement(String _queryId, List<Object> _values, PreparedStatement _statement)
          throws DataAccessException
  {
    try {

      // insert values
      for (int i = 0; i < _values.size(); ++i) {
        _statement.setObject(i + 1, _values.get(i));
      }

    } catch (SQLException e) {
      String msg = "Couldn't fill in values during preparation of query \"" +
              _queryId + "\". Values: " + _values;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }
  }
}
