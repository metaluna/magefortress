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
import java.util.List;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class MFMockSqlDao extends MFSqlDao
{

  public MFMockSqlDao(MFSqlConnector _db)
  {
    this(_db, null);
  }

  public MFMockSqlDao(MFSqlConnector _db, MFISaveable _payload)
  {
    super(_db);
    this.payload = _payload;
  }

  @Override
  public MFISaveable load(int _id) throws DataAccessException
  {
    return super.load(_id);
  }

  @Override
  public List<? extends MFISaveable> loadAll() throws DataAccessException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MFISaveable getPayload()
  {
    return this.payload;
  }

  @Override
  protected EnumMap<MFESqlOperations, String> getStatements()
  {
    EnumMap<MFESqlOperations, String> queries =
            new EnumMap<MFESqlOperations, String>(MFESqlOperations.class);
    queries.put(MFESqlOperations.CREATE, "c");
    queries.put(MFESqlOperations.READ, "r");
    queries.put(MFESqlOperations.READ_ALL, "ra");
    queries.put(MFESqlOperations.UPDATE, "u");
    queries.put(MFESqlOperations.DESTROY, "d");

    return queries;
  }

  @Override
  protected List<Object> getVectorizedData()
  {
    List<Object> data = new ArrayList<Object>(1);
    data.add("mock data");

    return data;
  }

  @Override
  protected MFISaveable readVectorizedData(Map<String, Object> _data) throws DataAccessException
  {
    return null;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private MFISaveable payload;
}
