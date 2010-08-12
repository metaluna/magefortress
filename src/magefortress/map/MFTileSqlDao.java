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
package magefortress.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import magefortress.core.Immutable;
import magefortress.map.ground.MFGround;
import magefortress.storage.DataAccessException;
import magefortress.storage.MFESqlOperations;
import magefortress.storage.MFSqlConnector;
import magefortress.storage.MFSqlDao;

/**
 *
 */
public class MFTileSqlDao extends MFSqlDao<MFTile> implements MFITileDao, Immutable
{
  // QUERIES
  private static final String CREATE    = "INSERT INTO tiles (map_id, ground_id, room_id, " +
          "object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) " +
          "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
  private static final String READ      = "SELECT id, map_id, ground_id, room_id, object_id, " +
          "x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor " +
          "FROM tiles WHERE id=?;";
  private static final String READ_ALL  = "SELECT id, map_id, ground_id, room_id, object_id, " +
          "x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor FROM tiles;";
  private static final String UPDATE    = "UPDATE tiles SET map_id=?, ground_id=?, room_id=?, " +
          "object_id=?, x=?, y=?, z=?, underground=?, dug_out=?, wall_n=?, wall_e=?, " +
          "wall_s=?, wall_w=?, floor=? WHERE id=?;";
  private static final String DESTROY   = "DELETE FROM tiles WHERE id=?";
  private static final String READ_MAP_TILES = "SELECT id, map_id, ground_id, room_id, object_id, " +
          "x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor FROM tiles " +
          "WHERE map_id=?;";

  /**
   * Basic constructor. For preparing statements only!
   * @param _db
   */
  public MFTileSqlDao(MFSqlConnector _db)
  {
    this(_db, null, null, null);
  }

  /**
   * Constructor used for loading tiles. No data about a tile is held.
   * @param _db
   */
  public MFTileSqlDao(MFSqlConnector _db, Map<Integer, MFGround> _groundTypes)
  {
    this(_db, null, null, _groundTypes);
    if (_groundTypes == null || _groundTypes.isEmpty()) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                        "without ground types.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Constructor used for saving/deleting tiles of a map
   * @param _db
   * @param _tile
   * @param _mapId
   */
  @SuppressWarnings("unchecked")
  public MFTileSqlDao(MFSqlConnector _db, MFTile _tile, MFMap _map)
  {
    this(_db, _tile, _map, Collections.EMPTY_MAP);
    if (_tile == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a tile.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_map == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }

  private MFTileSqlDao(MFSqlConnector _db, MFTile _tile, MFMap _map,
                                            Map<Integer, MFGround> _groundTypes)
  {
    super(_db, _tile);
    if (_map != null) {
      this.mapId = _map.getId();
    } else {
      this.mapId = this.getUnsavedMarker();
    }
    this.groundTypes = _groundTypes;
  }

  @Override
  public List<MFTile> loadAllOfMap(final int _mapId) throws DataAccessException
  {
    List<MFTile> gotTiles = new ArrayList<MFTile>();
    
    final List<Object> parameters = new ArrayList<Object>(1);
    parameters.add(_mapId);

    for (MFTile tile : super.loadAll("READ_MAP_TILES", parameters)) {
      gotTiles.add(tile);
    }
    return gotTiles;
  }

  @Override
  public int getMapId()
  {
    return this.mapId;
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

    this.getDb().prepareQuery("READ_MAP_TILES", READ_MAP_TILES);

    return queries;
  }

  @Override
  protected MFTile readVectorizedData(final Map<String, Object> _data)
          throws DataAccessException
  {
    assert _data != null && !_data.isEmpty();

    int id = (Integer) _data.get("id");
    int unusedMapId = (Integer) _data.get("map_id");
    int groundTypeId = (Integer) _data.get("ground_id");
    int roomId = (Integer) _data.get("room_id");
    int objectId = (Integer) _data.get("object_id");
    int posX = (Integer) _data.get("x");
    int posY = (Integer) _data.get("y");
    int posZ = (Integer) _data.get("z");
    boolean isUnderground = this.toBoolean(_data.get("underground"));
    boolean isDugOut = this.toBoolean(_data.get("dug_out"));
    boolean wallN = this.toBoolean(_data.get("wall_n"));
    boolean wallE = this.toBoolean(_data.get("wall_e"));
    boolean wallS = this.toBoolean(_data.get("wall_s"));
    boolean wallW = this.toBoolean(_data.get("wall_w"));
    boolean floor = this.toBoolean(_data.get("floor"));

    MFGround ground = groundTypes.get(groundTypeId);
    MFTile gotTile = new MFTile(id, posX, posY, posZ, isDugOut,
                      wallN, wallE, wallS, wallW, floor, isUnderground, ground);

    if (roomId != this.getUnsavedMarker()) {
      //TODO add tile to room
    }
    if (objectId != this.getUnsavedMarker()) {
      // TODO add object to tile
    }

    return gotTile;
  }

  @Override
  protected List<Object> getVectorizedData()
  {
    final List<Object> vectorizedData = new ArrayList<Object>(14);
    vectorizedData.add(this.mapId);
    // TODO make room and object saveable
    //vectorizedData.add(tile.getGround().getId());
    //vectorizedData.add(tile.getRoom().getId());
    //vectorizedData.add(tile.getObject().getId());
    vectorizedData.add(this.getUnsavedMarker());
    vectorizedData.add(this.getUnsavedMarker());
    vectorizedData.add(this.getUnsavedMarker());
    vectorizedData.add(this.getPayload().getPosX());
    vectorizedData.add(this.getPayload().getPosY());
    vectorizedData.add(this.getPayload().getPosZ());
    vectorizedData.add(this.getPayload().isUnderground());
    vectorizedData.add(this.getPayload().isDugOut());
    vectorizedData.add(this.getPayload().hasWallNorth());
    vectorizedData.add(this.getPayload().hasWallEast());
    vectorizedData.add(this.getPayload().hasWallSouth());
    vectorizedData.add(this.getPayload().hasWallWest());
    vectorizedData.add(this.getPayload().hasFloor());
    vectorizedData.add(this.getPayload().getId());
    return vectorizedData;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Id of the map. Saved here because tiles don't have that information */
  private final int mapId;
  /** List of ground types */
  private final Map<Integer, MFGround> groundTypes;

  /**
   * Converts an integer saved in the database to a boolean
   * @param _o The integer as an object
   * @return The boolean value
   */
  private boolean toBoolean(Object _o)
  {
    return (Integer) _o != 0;
  }

}
