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
package magefortress.map.ground;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import magefortress.core.MFEDirection;
import magefortress.graphics.MFIPaintable;
import magefortress.graphics.MFImageLibrary;
import magefortress.graphics.MFStillPaintable;
import magefortress.items.MFBlueprint;
import magefortress.map.MFTile.Corner;
import magefortress.storage.DataAccessException;
import magefortress.storage.MFESqlOperations;
import magefortress.storage.MFSqlConnector;
import magefortress.storage.MFSqlDao;

/**
 *
 */
public class MFGroundSqlDao extends MFSqlDao<MFGround> implements MFIGroundDao
{
  // QUERIES
  private static final String CREATE    = "INSERT INTO grounds (blueprint_id, hardness) VALUES (?,?);";
  private static final String READ      = "SELECT id, blueprint_id, hardness FROM grounds WHERE id=?;";
  private static final String READ_ALL  = "SELECT id, blueprint_id, hardness FROM grounds ORDER BY blueprint_id ASC;";
  private static final String UPDATE    = "UPDATE grounds SET blueprint_id=?, hardness=? WHERE id=?;";
  private static final String DESTROY   = "DELETE FROM grounds WHERE id=?";

  /**
   * Basic constructor
   * @param _db The database to connect to
   */
  public MFGroundSqlDao(MFSqlConnector _db)
  {
    this(_db, null, null, null);
  }

  /**
   * Loading constructor
   * @param _db The database to connect to
   */
  public MFGroundSqlDao(MFSqlConnector _db, Map<Integer, MFBlueprint> _blueprints, MFImageLibrary _imgLib)
  {
    this(_db, null, _blueprints, _imgLib);
    if (_blueprints == null || _blueprints.isEmpty()) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                "without a list of blueprints.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_imgLib == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                   "without the image library.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Save/delete constructor
   * @param _db     The database to connect to
   * @param _ground The data to save/delete. Maybe <code>null</code> if
   *                this DAO is used for loading.
   */
  public MFGroundSqlDao(MFSqlConnector _db, MFGround _ground)
  {
    this(_db, _ground, null, null);
    if (_ground == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                             "without a ground to save/delete.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
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
  protected MFGround readVectorizedData(final Map<String, Object> _data)
          throws DataAccessException
  {
    assert _data != null && !_data.isEmpty();

    int id                  = (Integer) _data.get("id");
    int blueprint_id        = (Integer) _data.get("blueprint_id");
    int hardness            = (Integer) _data.get("hardness");

    MFBlueprint blueprint = this.blueprints.get(blueprint_id);

    String sprite_basename = blueprint.getName().toLowerCase();
    MFIPaintable solid = loadSolid(sprite_basename);
    MFIPaintable basic_floor = loadFloor(sprite_basename);
    EnumMap<MFEDirection, MFIPaintable> walls = loadWalls(sprite_basename);
    EnumMap<MFEDirection, EnumMap<Corner,MFIPaintable>> corners = loadCorners(sprite_basename);

    MFGround gotGround = new MFGround(id, blueprint, hardness,
                                      solid, basic_floor, walls, corners);

    return gotGround;
  }

  @Override
  protected List<Object> getVectorizedData()
  {
    final List<Object> vectorizedData = new ArrayList<Object>(4);
    vectorizedData.add(this.getPayload().getBlueprint().getId());
    vectorizedData.add(this.getPayload().getHardness());
    vectorizedData.add(this.getPayload().getId());
    return vectorizedData;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final Map<Integer, MFBlueprint> blueprints;
  private final MFImageLibrary imgLib;

  /**
   * Full constructor
   * @param _db The connection to the database
   * @param _ground The ground to save/delete
   * @param _blueprints The blueprints used when loading a ground
   */
  private MFGroundSqlDao(MFSqlConnector _db, MFGround _ground, Map<Integer, 
                              MFBlueprint> _blueprints, MFImageLibrary _imgLib)
  {
    super(_db, _ground);
    this.blueprints = _blueprints;
    this.imgLib = _imgLib;
  }

  private MFIPaintable loadSolid(final String _spriteBasename)
  {
    return loadFile(_spriteBasename, SPRITE_SOLID);
  }

  private MFIPaintable loadFloor(final String _spriteBasename)
  {
    return loadFile(_spriteBasename, SPRITE_FLOOR);
  }

  private EnumMap<MFEDirection, MFIPaintable> loadWalls(final String _spriteBasename)
  {
    EnumMap<MFEDirection, MFIPaintable> result =
                    new EnumMap<MFEDirection, MFIPaintable>(MFEDirection.class);
    result.put(MFEDirection.N, loadFile(_spriteBasename, SPRITE_WALL_N));
    result.put(MFEDirection.E, loadFile(_spriteBasename, SPRITE_WALL_E));
    result.put(MFEDirection.S, loadFile(_spriteBasename, SPRITE_WALL_S));
    result.put(MFEDirection.W, loadFile(_spriteBasename, SPRITE_WALL_W));
    return result;
  }

  private EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>> loadCorners(final String _spriteBasename)
  {
    EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>> result =
          new EnumMap<MFEDirection, EnumMap<Corner, MFIPaintable>>(MFEDirection.class);
    
    EnumMap<Corner, MFIPaintable> ne = new EnumMap<Corner, MFIPaintable>(Corner.class);
    result.put(MFEDirection.NE, ne);
    ne.put(Corner.HORIZONTAL, loadFile(_spriteBasename, SPRITE_CORNER_N_HORIZ));
    ne.put(Corner.VERTICAL,   loadFile(_spriteBasename, SPRITE_CORNER_E_VERT));
    ne.put(Corner.INWARD,     loadFile(_spriteBasename, SPRITE_CORNER_NE_IN));
    ne.put(Corner.BOTH,       loadFile(_spriteBasename, SPRITE_CORNER_NE_OUT));

    EnumMap<Corner, MFIPaintable> se = new EnumMap<Corner, MFIPaintable>(Corner.class);
    result.put(MFEDirection.SE, se);
    se.put(Corner.HORIZONTAL, loadFile(_spriteBasename, SPRITE_CORNER_S_HORIZ));
    se.put(Corner.VERTICAL,   loadFile(_spriteBasename, SPRITE_CORNER_E_VERT));
    se.put(Corner.INWARD,     loadFile(_spriteBasename, SPRITE_CORNER_SE_IN));
    se.put(Corner.BOTH,       loadFile(_spriteBasename, SPRITE_CORNER_SE_OUT));

    EnumMap<Corner, MFIPaintable> sw = new EnumMap<Corner, MFIPaintable>(Corner.class);
    result.put(MFEDirection.SW, sw);
    sw.put(Corner.HORIZONTAL, loadFile(_spriteBasename, SPRITE_CORNER_S_HORIZ));
    sw.put(Corner.VERTICAL,   loadFile(_spriteBasename, SPRITE_CORNER_W_VERT));
    sw.put(Corner.INWARD,     loadFile(_spriteBasename, SPRITE_CORNER_SW_IN));
    sw.put(Corner.BOTH,       loadFile(_spriteBasename, SPRITE_CORNER_SW_OUT));

    EnumMap<Corner, MFIPaintable> nw = new EnumMap<Corner, MFIPaintable>(Corner.class);
    result.put(MFEDirection.NW, nw);
    nw.put(Corner.HORIZONTAL, loadFile(_spriteBasename, SPRITE_CORNER_N_HORIZ));
    nw.put(Corner.VERTICAL,   loadFile(_spriteBasename, SPRITE_CORNER_W_VERT));
    nw.put(Corner.INWARD,     loadFile(_spriteBasename, SPRITE_CORNER_NW_IN));
    nw.put(Corner.BOTH,       loadFile(_spriteBasename, SPRITE_CORNER_NW_OUT));

    return result;
  }

  private MFIPaintable loadFile(final String prefix, final String suffix)
  {
    final String filename = prefix + SEPARATOR + suffix + ".png";
    final MFIPaintable result = new MFStillPaintable(this.imgLib.get(filename));
    return result;
  }
}
