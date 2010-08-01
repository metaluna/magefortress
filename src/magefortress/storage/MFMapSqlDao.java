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
import magefortress.core.Immutable;
import magefortress.map.MFMap;
import magefortress.map.MFTile;

/**
 *
 */
class MFMapSqlDao extends MFSqlDao<MFMap> implements MFIMapDao, Immutable
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
   * @param _daoFactory
   */
  public MFMapSqlDao(MFSqlConnector _db, MFDaoFactory _daoFactory)
  {
    this(_db, null, _daoFactory);
  }

  /**
   * Constructor
   * @param _db The database connection
   * @param _map The map that has to be saved/deleted
   * @param _daoFactory The factory used to save and load the map's tiles
   */
  public MFMapSqlDao(MFSqlConnector _db, MFMap _map, MFDaoFactory _daoFactory)
  {
    super(_db, _map);
    this.daoFactory = _daoFactory;
  }

  @Override
  public void save() throws DataAccessException
  {
    // save the map
    super.save();

    // additionally save all tiles
    this.saveTiles();
  }

  @Override
  public MFMap load(int _id) throws DataAccessException
  {
    // load the map
    MFMap gotMap = super.load(_id);

    // additionally load all tiles
    this.loadTiles(gotMap);

    return gotMap;
  }

  @Override
  public List<? extends MFMap> loadAll() throws DataAccessException
  {
    // load maps
    List<? extends MFMap> gotMaps = super.loadAll();

    // additionally load all tiles of all maps
    for (MFMap map : gotMaps) {
      this.loadTiles(map);
    }
    return gotMaps;
  }

  @Override
  public void delete() throws DataAccessException
  {
    if (this.getPayload() == null) {
      String msg = "MFMapSqlDao: Can't delete null map. " +
                      "Please initialize DAO with map object.";
      logger.severe(msg);
      throw new NullPointerException(msg);
    }
    // delete tiles before deletion of map
    this.deleteTiles();

    // delete the map
    super.delete();
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
    assert _data != null && !_data.isEmpty();
    
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
    vectorizedData.add(this.getPayload().getWidth());
    vectorizedData.add(this.getPayload().getHeight());
    vectorizedData.add(this.getPayload().getDepth());
    vectorizedData.add(this.getPayload().getId());
    return vectorizedData;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Used for loading of tiles */
  private final MFDaoFactory daoFactory;

  private void saveTiles() throws DataAccessException
  {
    MFMap map = this.getPayload();
    assert map != null : "MFMapSqlDao: Can't save tiles of null map";
    assert map.getId() != MFSqlDao.UNSAVED_MARKER : "MFMapSqlDao: Map has " +
                                                    "to be saved before tiles.";

    for (int x=0; x<map.getWidth(); ++x) {
      for (int y=0; y<map.getHeight(); ++y) {
        for (int z=0; z<map.getDepth(); ++z) {
          saveTile(map.getTile(x, y, z), map.getId());
        }
      }
    }

  }

  private void saveTile(MFTile _tile, int _mapId) throws DataAccessException
  {
    assert _tile != null : "MFMapSqlDao: No tile to save.";
    
    MFITileDao tileDao = this.daoFactory.getTileDao(_tile, _mapId);
    tileDao.save();
  }

  private void loadTiles(MFMap _map)  throws DataAccessException
  {
    assert _map != null : "MFMapSqlDao: Can't load tiles of null map.";

    MFITileDao tileDao = this.daoFactory.getTileDao();

    List<MFTile> tiles = tileDao.loadAllOfMap(_map.getId());

    for (MFTile tile : tiles) {
      _map.setTile(tile);
    }

    for (MFTile tile : tiles) {
      _map.calculateCorners(tile);
    }
  }

  private void deleteTiles() throws DataAccessException
  {
    MFMap map = this.getPayload();
    assert map != null : "MFMapSqlDao: Cannot delete tiles of null map.";

    for (int x=0; x<map.getWidth(); ++x) {
      for (int y=0; y<map.getHeight(); ++y) {
        for (int z=0; z<map.getDepth(); ++z) {
          MFTile tile = map.getTile(x, y, z);
          MFITileDao tileDao = this.daoFactory.getTileDao(tile, map.getId());
          tileDao.delete();
        }
      }
    }
    
  }
}
