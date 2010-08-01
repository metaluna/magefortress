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
import java.util.logging.Level;
import magefortress.core.Immutable;
import magefortress.core.MFRace;

/**
 *
 */
class MFRaceSqlDao extends MFSqlDao<MFRace> implements MFIRaceDao, Immutable
{
  // QUERIES
  private static final String CREATE    = "INSERT INTO races (name, hold_behavior, move_behavior) VALUES (?,?,?);";
  private static final String READ      = "SELECT id, name, hold_behavior, move_behavior FROM races WHERE id=?;";
  private static final String READ_ALL  = "SELECT id, name, hold_behavior, move_behavior FROM races ORDER BY name ASC;";
  private static final String UPDATE    = "UPDATE races SET name=?, hold_behavior=?, move_behavior=? WHERE id=?;";
  private static final String DESTROY   = "DELETE FROM races WHERE id=?";

  /**
   * Most basic constructor
   * @param _db
   */
  public MFRaceSqlDao(MFSqlConnector _db)
  {
    this(_db, null);
  }

  /**
   * Constructor 
   * @param _db
   * @param _race
   */
  public MFRaceSqlDao(MFSqlConnector _db, MFRace _race)
  {
    super(_db, _race);
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
  protected MFRace readVectorizedData(final Map<String, Object> _data)
          throws DataAccessException
  {
    assert _data != null && !_data.isEmpty();

    int id = (Integer) _data.get("id");
    String name = (String) _data.get("name");
    String holdBehaviorName = (String) _data.get("hold_behavior");
    String moveBehaviorName = (String) _data.get("move_behavior");

    MFRace gotRace = null;

    try {

      gotRace = new MFRace(id, name, moveBehaviorName, holdBehaviorName);

    } catch (ClassNotFoundException e) {
      String msg = "Unable to find desired behavior during loading of race " + name;
      logger.log(Level.SEVERE, msg, e);
      throw new DataAccessException(msg, e);
    }

    return gotRace;
  }

  @Override
  protected List<Object> getVectorizedData()
  {
    final List<Object> vectorizedData = new ArrayList<Object>(4);
    vectorizedData.add(this.getPayload().getName());
    vectorizedData.add(this.getPayload().getHoldingBehaviorClass().getName());
    vectorizedData.add(this.getPayload().getMovingBehaviorClass().getName());
    vectorizedData.add(this.getPayload().getId());
    return vectorizedData;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

}
