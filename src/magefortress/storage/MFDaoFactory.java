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
  public MFDaoFactory(Storage _storage)
  {
    if (_storage == null) {
      String msg = "Cannot create DAOFactory without a storage mechanism.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    this.switchStorage(_storage);
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
    return this.getTileDao(null);
  }

  public MFITileDao getTileDao(MFTile _payload)
  {
    MFITileDao tileDao;
    switch (this.storage) {
      case SQL: tileDao = new MFTileSqlDao(this.db, _payload); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }
    return tileDao;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFDaoFactory.class.getName());
  private Storage storage;
  private MFSqlConnector db;

  private void switchStorage(Storage _storage)
  {
    switch (_storage) {
      case SQL: connectDb(); break;
      default: throw new AssertionError("Unexpected statement: storage mechanism " +
              storage + " unknown.");
    }

    this.storage = _storage;
  }

  private void connectDb()
  {
    // connect
    this.db = MFSqlConnector.getInstance();
    
    this.prepareStatements();
  }

  private void prepareStatements()
  {
    new MFRaceSqlDao(this.db).prepareStatements();
    new MFMapSqlDao(this.db, this).prepareStatements();
  }

}
