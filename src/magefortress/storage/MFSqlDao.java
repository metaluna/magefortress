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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Superclass for all objects that need to be saved to a relational database.
 */
abstract class MFSqlDao<T extends MFISaveable> implements MFIDao<T>
{
  /**
   * Constructor taking the database connection.
   * @param _db The database connection
   */
  public MFSqlDao(MFSqlConnector _db, T _payload)
  {
    if (_db == null) {
      String msg = "Couldn't create DAO " + this.getClass().getName() +
              " without a database connector.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.db = _db;
    this.payload = _payload;
  }

  @Override
  public void save() throws DataAccessException
  {

    if (this.getPayload() == null) {
      String msg = "Unable to save " + this.getClass().getSimpleName() +
              " without payload.";
      logger.severe(msg);
      throw new NullPointerException(msg);
    }

    final List<Object> vectorizedData = getVectorizedData();

    try {

      if (this.getPayload().getId() == this.getUnsavedMarker()) {
        createRecord(vectorizedData);
      } else {
        updateRecord(vectorizedData);
      }
      
    } catch (DataAccessException e) {
      String payloadClass = this.getPayload().getClass().getSimpleName();
      String msg = "Unable to save " + payloadClass + " #" + this.getPayload().getId();
      logger.log(Level.SEVERE, msg, e);
      throw e;
    }
  }

  @Override
  public T load(int _id) throws DataAccessException
  {
    final ArrayList<Object> args = new ArrayList<Object>(1);
    args.add(_id);

    ResultSet rs = this.getDb().query("READ_" + this.getClass().getSimpleName(), args);

    if (rs == null) {
      String msg = "Couldn't find database entry with id " + _id;
      logger.severe(msg);
      throw new DataAccessException(msg);
    }

    T gotPayload = this.parseRecord(rs);
    return gotPayload;
  }

  @Override
  public List<? extends T> loadAll() throws DataAccessException
  {
    return loadAll("READ_ALL_" + this.getClass().getSimpleName(),
                    Collections.emptyList());
  }

  public List<? extends T> loadAll(String _queryId, List<Object> _parameters)
          throws DataAccessException
  {
    final List<T> gotPayloads = new ArrayList<T>();

    ResultSet rs = this.getDb().query(_queryId, _parameters);

    if (rs == null) {
      String msg = "Error during loading of "+ this.getPayload().getClass().getSimpleName() +
              " from the database";
      logger.severe(msg);
      throw new DataAccessException(msg);
    }

    try {

      while (rs.next()) {
        T gotPayload = this.parseRecord(rs);
        gotPayloads.add(gotPayload);
      }

    } catch (SQLException e) {
      String msg = "Couldn't load records of class " +
              this.getPayload().getClass().getSimpleName();
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    return gotPayloads;
  }

  @Override
  public void delete() throws DataAccessException
  {
    if (this.getPayload() == null) {
      String msg = "Unable to delete " + this.getClass().getSimpleName() +
                      " without payload";
      logger.severe(msg);
      throw new NullPointerException(msg);
    }

    final String payloadClass = this.getPayload().getClass().getSimpleName();

    if (this.getPayload().getId() == this.getUnsavedMarker()) {
      String msg = "Unable to delete " + payloadClass + " #" + this.getPayload().getId() +
              " because it hasn't been saved before.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    int rowCount = this.getDb().update(
            "DESTROY_" + this.getClass().getSimpleName(), new ArrayList<Object>(1));
    
    if (rowCount == 1) {
      this.getPayload().setId(this.getUnsavedMarker());
    } else if (rowCount == 0) {
      String msg = "Unable to delete " + payloadClass + " #" +
              this.getPayload().getId() + ". Nothing found.";
      logger.severe(msg);
      throw new DataAccessException(msg);
    } else {
      String msg = "Unable to delete " + payloadClass + " #" +
              this.getPayload().getId() + ". " + rowCount + " matches found.";
      logger.severe(msg);
      throw new DataAccessException(msg);
    }

  }

  @Override
  public int getUnsavedMarker()
  {
    return UNSAVED_MARKER;
  }

  @Override
  public T getPayload()
  {
    return this.payload;
  }

  //---vvv---      PROTECTED METHODS      ---vvv---
  /** Marks an unsaved object */
  protected static final int UNSAVED_MARKER = -1;
  /** Logger */
  protected static final Logger logger = Logger.getLogger(MFSqlDao.class.getName());

  /**
   * Prepares the statements used to access the database by putting all
   * SQL query into an {@link EnumMap}.
   *
   * <p>Hook method used in template method {@link #prepareStatements}.
   * @return The queries -one per operation- as strings.
   */
  protected abstract EnumMap<MFESqlOperations, String> getStatements();

  /**
   * Hook method used in template method {@link #save}. ID last or at least
   * should be when the ID's position in the query is last (where it should be).
   * @return The object's data to save
   */
  protected abstract List<Object> getVectorizedData();

  /**
   * Hook method used in template method {@link #load} and {@link #loadAll}.
   *
   * <p>This should probably be overloaded in subclasses to return a
   * specific type.
   * 
   * @param _data The raw data from the database
   * @return The finished object
   */
  protected abstract T readVectorizedData(Map<String, Object> _data)
          throws DataAccessException;


  /**
   * To be called once after the database was initialized.
   * Enters all prepared statements into the database (driver). Template method
   * which utilizes {@link #getStatements()} in subclasses.
   */
  protected void prepareStatements()
  {
    EnumMap<MFESqlOperations, String> queries = this.getStatements();
    
    for (MFESqlOperations queryId : queries.keySet()) {
      db.prepareQuery(queryId.toString() + "_" + this.getClass().getSimpleName(),
              queries.get(queryId));
    }

  }

  protected MFSqlConnector getDb()
  {
    return this.db;
  }

  //---vvv---       PRIVATE METHODS       ---vvv---
  /** Database connection */
  private final MFSqlConnector db;
  /** Data which can be saved or deleted */
  private final T payload;

  /**
   * Template method making use of readVectorizedData(). Here raw data from the
   * database is transformed to a hash map (column names -> data) and then sent
   * to the implementing subclass via readVectorizedData.
   * @param _rs The raw data
   * @return The parse record
   */
  private T parseRecord(final ResultSet _rs) throws DataAccessException
  {
    T gotPayload = null;
    int id = 0;

    try {

      id = _rs.getInt("id");

      final Map<String, Object> data = new HashMap<String, Object>();

      final ResultSetMetaData metadata = _rs.getMetaData();

      for (int i=1; i<=metadata.getColumnCount(); ++i) {
        data.put(metadata.getColumnName(i), _rs.getObject(i));
      }

      gotPayload = this.readVectorizedData(data);

    } catch (SQLException e) {
      String msg = "Unable to load " + gotPayload.getClass().getName() + " #" + id;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }
    return gotPayload;
  }

  private void updateRecord(final List<Object> vectorizedData)
          throws DataAccessException
  {
    this.getDb().update(
            "UPDATE_" + this.getClass().getSimpleName(), vectorizedData);
  }

  private void createRecord(final List<Object> vectorizedData)
          throws DataAccessException
  {
    // delete last entry which should be the id
    assert (Integer) vectorizedData.get(vectorizedData.size()-1) == UNSAVED_MARKER;
    vectorizedData.remove(vectorizedData.size()-1);
    
    int generatedId = this.getDb().insert(
            "CREATE_" + this.getClass().getSimpleName(), vectorizedData);
    this.getPayload().setId(generatedId);
  }

}
