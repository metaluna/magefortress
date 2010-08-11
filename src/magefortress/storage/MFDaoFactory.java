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
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import magefortress.creatures.MFRace;
import magefortress.items.MFBlueprint;
import magefortress.items.MFBlueprintSqlDao;
import magefortress.items.MFIBlueprintDao;
import magefortress.map.ground.MFGround;
import magefortress.map.MFMap;
import magefortress.map.MFTile;

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
    return this.getRaceSavingDao(null);
  }

  public MFIRaceDao getRaceSavingDao(MFRace _payload)
  {
    MFIRaceDao raceDao;
    switch (this.storage) {
      case SQL: raceDao = new MFRaceSqlDao(this.db, _payload); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return raceDao;
  }

  //---vvv---            BLUEPRINT DAOS                 ---vvv---
  public MFIBlueprintDao getBlueprintLoadingDao()
  {
    return this.getBlueprintSavingDao(null);
  }

  public MFIBlueprintDao getBlueprintSavingDao(MFBlueprint _payload)
  {
    MFIBlueprintDao blueprintDaoDao;
    switch (this.storage) {
      case SQL: blueprintDaoDao = new MFBlueprintSqlDao(this.db, _payload); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return blueprintDaoDao;
  }

  //---vvv---            MAP DAOS                 ---vvv---
  public MFIMapDao getMapLoadingDao(Map<Integer, MFGround> _groundTypes)
  {
    MFIMapDao mapDao;
    switch (this.storage) {
      case SQL: mapDao = new MFMapSqlDao(this.db, this, _groundTypes); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return mapDao;
  }

  public MFIMapDao getMapSavingDao(MFMap _payload)
  {
    MFIMapDao mapDao;
    switch (this.storage) {
      case SQL: mapDao = new MFMapSqlDao(this.db, _payload, this); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return mapDao;
  }

  //---vvv---            TILE DAOS                 ---vvv---
  /**
   * Factory method for constructing tile loading DAOs.
   * @param _groundTypes A list of all {@link MFGround} types in the game
   * @return a DAO containing all data needed to load one or all tiles.
   */
  public MFITileDao getTileLoadingDao(Map<Integer, MFGround> _groundTypes)
  {
    MFITileDao tileDao;
    switch (this.storage) {
      case SQL: tileDao = new MFTileSqlDao(this.db, _groundTypes);
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return tileDao;
  }

  /**
   * Factory method for constructing save/delete DAOs. Just call
   * {@link MFITileDao#save()} or {@link MFITileDao#delete()} to use it.
   * @param _payload The tile which shall be saved/deleted
   * @param _mapId The ID of the map which contains the tile
   * @return a DAO containing a reference to the tile
   */
  public MFITileDao getTileSavingDao(MFTile _payload, int _mapId)
  {
    MFITileDao tileDao;
    switch (this.storage) {
      case SQL: tileDao = new MFTileSqlDao(this.db, _payload, _mapId);
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return tileDao;
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
    new MFMapSqlDao(this.db, this, Collections.EMPTY_MAP).prepareStatements();
    new MFTileSqlDao(this.db, Collections.EMPTY_MAP).prepareStatements();
  }

}
