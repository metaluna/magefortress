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

import magefortress.map.MFITileDao;
import magefortress.map.MFIMapDao;
import magefortress.map.MFMapSqlDao;
import magefortress.map.MFTileSqlDao;
import magefortress.creatures.MFRaceSqlDao;
import magefortress.creatures.MFIRaceDao;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import magefortress.creatures.MFRace;
import magefortress.graphics.MFImageLibrary;
import magefortress.items.MFBlueprint;
import magefortress.items.MFBlueprintSqlDao;
import magefortress.items.MFIBlueprintDao;
import magefortress.map.ground.MFGround;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import magefortress.map.ground.MFGroundSqlDao;
import magefortress.map.ground.MFIGroundDao;

/**
 * Factory which produces Data Access Objects which are used to save&load
 * game objects.
 *
 * Before usage it has to be configured to the specific storage mechanism used.
 * Only then can it create DAOs which have access to this storage.
 */
public class MFDaoFactory
{
  /** The possible storage mechanisms */
  public enum Storage {SQL}

  /**
   * Constructor accepting the desired storage mechanism configured by
   * a properties object.
   * @param _props
   */
  public MFDaoFactory(Properties _props)
  {
    if (_props == null) {
      String msg = "Cannot create DAOFactory without properties.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.switchStorage(_props);
  }

  //---vvv---            RACE DAOS                 ---vvv---
  public MFIRaceDao getRaceLoadingDao()
  {
    return this.getRaceDao(true, null);
  }

  public MFIRaceDao getRaceSavingDao(MFRace _payload)
  {
    if (_payload == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                              "without a race.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    return this.getRaceDao(false, _payload);
  }

  private MFIRaceDao getRaceDao(boolean isForLoading, MFRace _payload)
  {
    MFIRaceDao resultDao;
    switch (this.storage) {
      case SQL: if (isForLoading) {
                  resultDao = new MFRaceSqlDao(this.db);
                } else {
                  resultDao = new MFRaceSqlDao(this.db, _payload);
                }
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return resultDao;
  }

  //---vvv---            BLUEPRINT DAOS                 ---vvv---
  public MFIBlueprintDao getBlueprintLoadingDao()
  {
    return this.getBlueprintDao(true, null);
  }

  public MFIBlueprintDao getBlueprintSavingDao(MFBlueprint _payload)
  {
    if (_payload == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                        "without a blueprint.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    return this.getBlueprintDao(false, _payload);
  }

  private MFIBlueprintDao getBlueprintDao(boolean isForLoading, MFBlueprint _payload)
  {
    MFIBlueprintDao resultDao;
    switch (this.storage) {
      case SQL: if (isForLoading) {
                  resultDao = new MFBlueprintSqlDao(this.db);
                } else {
                  resultDao = new MFBlueprintSqlDao(this.db, _payload);
                }
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return resultDao;
  }

  //---vvv---            MAP DAOS                 ---vvv---
  public MFIGroundDao getGroundLoadingDao(Map<Integer, MFBlueprint> _blueprints, MFImageLibrary _imgLib)
  {
    return this.getGroundDao(true, null, _blueprints, _imgLib);
  }

  public MFIGroundDao getGroundSavingDao(MFGround _payload)
  {
    return this.getGroundDao(false, _payload, null, null);
  }

  private MFIGroundDao getGroundDao(boolean isForLoading, MFGround _payload, Map<Integer, MFBlueprint> _blueprints, MFImageLibrary _imgLib)
  {
    MFIGroundDao resultDao;
    switch (this.storage) {
      case SQL: if (isForLoading) {
                  resultDao = new MFGroundSqlDao(this.db, _blueprints, _imgLib);
                } else {
                  resultDao = new MFGroundSqlDao(this.db, _payload);
                }
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return resultDao;
  }

  //---vvv---            MAP DAOS                 ---vvv---
  public MFIMapDao getMapLoadingDao(Map<Integer, MFGround> _groundTypes)
  {
    return this.getMapDao(true, null, _groundTypes);
  }

  public MFIMapDao getMapSavingDao(MFMap _payload)
  {
    return this.getMapDao(false, _payload, null);
  }

  private MFIMapDao getMapDao(boolean isForLoading, MFMap _payload,
                                            Map<Integer, MFGround> _groundTypes)
  {
    MFIMapDao resultDao;
    switch (this.storage) {
      case SQL: if (isForLoading) {
                  resultDao = new MFMapSqlDao(db, this, _groundTypes);
                } else {
                  resultDao = new MFMapSqlDao(this.db, _payload, this);
                }
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return resultDao;
  }


  //---vvv---            TILE DAOS                 ---vvv---
  /**
   * Factory method for constructing tile loading DAOs.
   * @param _groundTypes A list of all {@link MFGround} types in the game
   * @return a DAO containing all data needed to load one or all tiles.
   */
  public MFITileDao getTileLoadingDao(Map<Integer, MFGround> _groundTypes)
  {
    return this.getTileDao(true, null, null, _groundTypes);
  }

  /**
   * Factory method for constructing save/delete DAOs. Just call
   * {@link MFITileDao#save()} or {@link MFITileDao#delete()} to use it.
   * @param _payload The tile which shall be saved/deleted
   * @param _map The map which the tile is a part of
   * @return a DAO containing a reference to the tile
   */
  public MFITileDao getTileSavingDao(MFTile _payload, MFMap _map)
  {
    return this.getTileDao(false, _payload, _map, null);
  }

  private MFITileDao getTileDao(boolean isForLoading, MFTile _payload,
                                MFMap _map, Map<Integer, MFGround> _groundTypes)
  {
    MFITileDao resultDao;
    switch (this.storage) {
      case SQL: if (isForLoading) {
                  resultDao = new MFTileSqlDao(this.db, _groundTypes);
                } else {
                  resultDao = new MFTileSqlDao(this.db, _payload, _map);
                }
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return resultDao;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFDaoFactory.class.getName());
  private Storage storage;
  private MFSqlConnector db;

  private void switchStorage(Properties _props)
  {
    this.storage = Storage.valueOf(_props.getProperty("STORAGE"));

    switch (this.storage) {
      case SQL: connectDb(_props); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }

  }

  private void connectDb(Properties _props)
  {
    // connect
    this.db = MFSqlConnector.getInstance();
    this.db.connect(_props.getProperty("DATABASE"));
    this.prepareStatements();
  }

  @SuppressWarnings("unchecked")
  private void prepareStatements()
  {
    new MFRaceSqlDao(this.db).prepareStatements();
    new MFBlueprintSqlDao(this.db).prepareStatements();
    new MFGroundSqlDao(this.db).prepareStatements();
    new MFMapSqlDao(this.db).prepareStatements();
    new MFTileSqlDao(this.db).prepareStatements();
  }

}
