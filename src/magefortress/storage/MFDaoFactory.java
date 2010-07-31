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

import java.util.Properties;
import java.util.logging.Logger;
import magefortress.core.MFRace;
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
   * Constructor accepting the desired storage mechanism.
   * @param _storage
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

  public MFIRaceDao getRaceDao()
  {
    return this.getRaceDao(null);
  }

  public MFIRaceDao getRaceDao(MFRace _payload)
  {
    MFIRaceDao raceDao;
    switch (this.storage) {
      case SQL: raceDao = new MFRaceSqlDao(this.db, _payload); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return raceDao;
  }

  public MFIMapDao getMapDao()
  {
    return this.getMapDao(null);
  }

  public MFIMapDao getMapDao(MFMap _payload)
  {
    MFIMapDao mapDao;
    switch (this.storage) {
      case SQL: mapDao = new MFMapSqlDao(this.db, _payload, this); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return mapDao;
  }

  public MFITileDao getTileDao()
  {
    MFITileDao tileDao;
    switch (this.storage) {
      case SQL: tileDao = new MFTileSqlDao(this.db);
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return tileDao;
  }

  public MFITileDao getTileDao(int _mapId)
  {
    return this.getTileDao(null, _mapId);
  }

  public MFITileDao getTileDao(MFTile _payload)
  {
    return this.getTileDao(_payload, 0);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFDaoFactory.class.getName());
  private Storage storage;
  private MFSqlConnector db;

  private MFITileDao getTileDao(MFTile _payload, int _mapId)
  {
    MFITileDao tileDao;
    switch (this.storage) {
      case SQL: if (_payload != null) {
                  tileDao = new MFTileSqlDao(this.db, _payload);
                } else {
                  tileDao = new MFTileSqlDao(this.db, _mapId);
                }
                break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return tileDao;
  }

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

  private void prepareStatements()
  {
    new MFRaceSqlDao(this.db).prepareStatements();
    new MFMapSqlDao(this.db, this).prepareStatements();
    new MFTileSqlDao(this.db).prepareStatements();
  }

}
