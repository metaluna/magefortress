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
import magefortress.map.MFTile;

/**
 *
 */
class MFTileSqlDao extends MFSqlDao implements MFITileDao
{
  // QUERIES
  private static final String CREATE    = "INSERT INTO tiles (map_id, room_id, " +
          "object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) " +
          "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
  private static final String READ      = "SELECT id, map_id, room_id, object_id, " +
          "x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor " +
          "FROM tiles WHERE id=?;";
  private static final String READ_ALL  = "SELECT id, map_id, room_id, object_id, " +
          "x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor FROM tiles;";
  private static final String UPDATE    = "UPDATE tiles SET map_id=?, room_id=?, " +
          "object_id=?, x=?, y=?, z=?, underground=?, dug_out=?, wall_n=?, wall_e=?, " +
          "wall_s=?, wall_w=?, floor=? WHERE id=?;";
  private static final String DESTROY   = "DELETE FROM tiles WHERE id=?";
  private static final String READ_MAP_TILES = "SELECT id, map_id, room_id, object_id, " +
          "x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor FROM tiles " +
          "WHERE map_id=?;";

  /**
   * Most basic constructor
   * @param _db
   */
  public MFTileSqlDao(MFSqlConnector _db)
  {
    this(_db, null);
  }

  /**
   * Constructor used for saving tiles
   * @param _db
   */
  public MFTileSqlDao(MFSqlConnector _db, MFTile _tile)
  {
    super(_db);
    this.tile = _tile;
    this.mapId = -1;
  }

  /**
   * Constructor used for loading tiles of a map
   * @param _db
   * @param _mapId
   */
  public MFTileSqlDao(MFSqlConnector _db, int _mapId)
  {
    super(_db);
    this.tile = null;
    this.mapId = _mapId;
  }

  @Override
  public MFTile load(int _id) throws DataAccessException
  {
    MFTile gotTile = (MFTile) super.load(_id);

    return gotTile;
  }

  @Override
  public List<MFTile> loadAllOfMap(final int _mapId) throws DataAccessException
  {
    List<MFTile> gotTiles = new ArrayList<MFTile>();
    
    final List<Object> parameters = new ArrayList<Object>(1);
    parameters.add(_mapId);

    for (MFISaveable payload : super.loadAll("READ_MAP_TILES", parameters)) {
      gotTiles.add((MFTile) payload);
    }
    return gotTiles;
  }

  @Override
  public MFTile getPayload()
  {
    return this.tile;
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
    int id = (Integer) _data.get("id");
    int mapId = (Integer) _data.get("map_id");
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

    MFTile gotTile = new MFTile(id, posX, posY, posZ, isDugOut,
                              wallN, wallE, wallS, wallW, floor, isUnderground);

    if (roomId != this.getUnsavedMarker()) {
      //TODO add tile to room
    }
    if (objectId != this.getUnsavedMarker()) {
      // TODO add object to tile
    }

    if (mapId != this.mapId) {
      String msg = "Map id mismatch. Expected " + this.mapId + ", got " + mapId;
      logger.warning(msg);
    }

    return gotTile;
  }

  @Override
  protected List<Object> getVectorizedData()
  {
    final List<Object> vectorizedData = new ArrayList<Object>(14);
    vectorizedData.add(this.mapId);
    // TODO make room and object saveable
    //vectorizedData.add(tile.getRoom().getId());
    //vectorizedData.add(tile.getObject().getId());
    vectorizedData.add(this.getUnsavedMarker());
    vectorizedData.add(this.getUnsavedMarker());
    vectorizedData.add(tile.getPosX());
    vectorizedData.add(tile.getPosY());
    vectorizedData.add(tile.getPosZ());
    vectorizedData.add(tile.isUnderground());
    vectorizedData.add(tile.isDugOut());
    vectorizedData.add(tile.hasWallNorth());
    vectorizedData.add(tile.hasWallEast());
    vectorizedData.add(tile.hasWallSouth());
    vectorizedData.add(tile.hasWallWest());
    vectorizedData.add(tile.hasFloor());
    vectorizedData.add(tile.getId());
    return vectorizedData;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** The represented tile. May be null if this object is used to load data */
  private final MFTile tile;
  /** Id of the map. Saved here because tiles don't have that information */
  private final int mapId;

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
