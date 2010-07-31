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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import magefortress.map.MFMap;

/**
 *
 */
public class MFMapSqlDao extends MFSqlDao implements MFIMapDao
{
  // QUERIES
  private static final String CREATE    = "INSERT INTO maps (width, height, depth) VALUES (?,?,?);";
  private static final String READ      = "SELECT id, width, height, depth FROM maps WHERE id=?;";
  private static final String READ_ALL  = "SELECT id, width, height, depth FROM maps;";
  private static final String UPDATE    = "UPDATE maps SET width=?, height=?, depth=? WHERE id=?;";
  private static final String DESTROY   = "DELETE FROM maps WHERE id=?";

  /**
   * Most basic constructor
   * @param _db
   */
  public MFMapSqlDao(MFSqlConnector _db)
  {
    this(_db, null);
  }

  /**
   * Constructor
   * @param _db
   */
  public MFMapSqlDao(MFSqlConnector _db, MFMap _map)
  {
    super(_db);
    this.map = _map;
  }

  @Override
  public MFMap load(int _id) throws DataAccessException
  {
    MFMap gotMap = (MFMap) super.load(_id);

    return gotMap;
  }

  @Override
  public List<MFMap> loadAll() throws DataAccessException
  {
    List<MFMap> gotMaps = new ArrayList<MFMap>();
    for (MFISaveable payload : super.loadAll()) {
      gotMaps.add((MFMap) payload);
    }
    return gotMaps;
  }

  @Override
  public MFMap getPayload()
  {
    return this.map;
  }

  //---vvv---       PROTECTED METHODS        ---vvv---
  @Override
  protected EnumMap<MFESqlOperations, String> getStatements()
  {
    EnumMap<MFESqlOperations, String> queries =
            new EnumMap<MFESqlOperations, String>(MFESqlOperations.class);
    queries.put(MFESqlOperations.CREATE, CREATE);
    queries.put(MFESqlOperations.READ, READ);
    queries.put(MFESqlOperations.READ_ALL, READ_ALL);
    queries.put(MFESqlOperations.UPDATE, UPDATE);
    queries.put(MFESqlOperations.DESTROY, DESTROY);
    return queries;
  }

  @Override
  protected MFMap readVectorizedData(final Map<String, Object> _data)
          throws DataAccessException
  {
    int id = (Integer) _data.get("id");
    int width = (Integer) _data.get("width");
    int height = (Integer) _data.get("height");
    int depth = (Integer) _data.get("depth");

    MFMap gotMap = new MFMap(id, width, height, depth);

    return gotMap;
  }

  @Override
  protected List<Object> getVectorizedData()
  {
    final List<Object> vectorizedData = new ArrayList<Object>(4);
    vectorizedData.add(this.map.getWidth());
    vectorizedData.add(this.map.getHeight());
    vectorizedData.add(this.map.getDepth());
    vectorizedData.add(this.map.getId());
    return vectorizedData;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The represented map. May be null if this object is used to load data */
  private final MFMap map;
}
