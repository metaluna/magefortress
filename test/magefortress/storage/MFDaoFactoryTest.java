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
import magefortress.map.MFTileSqlDao;
import magefortress.map.MFMapSqlDao;
import magefortress.map.MFIMapDao;
import magefortress.creatures.MFRaceSqlDao;
import magefortress.creatures.MFIRaceDao;
import java.util.Collections;
import java.util.Properties;
import magefortress.creatures.MFRace;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDaoFactoryTest
{
  private MFDaoFactory factory;

  @Before
  public void setUp()
  {
    MFSqlConnector realDb = MFSqlConnector.getInstance();
    realDb.connect("magefortress.test.db");
    realDb.loadFromFile("magefortress.sql");
    realDb.loadFromFile("test_fixtures.sql");

    Properties props = new Properties();
    props.setProperty("STORAGE", MFDaoFactory.Storage.SQL.toString());
    props.setProperty("DATABASE", "magefortress.test.db");

    this.factory = new MFDaoFactory(props);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutStorageMechanism()
  {
    new MFDaoFactory(null);
  }

  @Test
  public void shouldGetRaceDaoWithoutRace()
  {
    MFIRaceDao raceDao = factory.getRaceDao();
    assertNotNull(raceDao);
    assertNull(raceDao.getPayload());
  }

  @Test
  public void shouldGetRaceDaoWithRace()
  {
    MFRace mockRace = mock(MFRace.class);
    MFIRaceDao raceDao = factory.getRaceDao(mockRace);
    assertEquals(mockRace, raceDao.getPayload());
  }

  @Test
  public void shouldGetRaceSqlDao()
  {
    MFIRaceDao raceDao = factory.getRaceDao();
    assertEquals(MFRaceSqlDao.class, raceDao.getClass());
  }

  @Test
  public void shouldGetMapDaoWithMap()
  {
    MFMap mockMap = mock(MFMap.class);
    MFIMapDao mapDao = factory.getMapDao(mockMap);
    assertEquals(mockMap, mapDao.getPayload());
  }

  @Test
  public void shouldGetMapSqlDao()
  {
    @SuppressWarnings("unchecked")
    MFIMapDao mapDao = factory.getMapDao(Collections.EMPTY_MAP);
    assertEquals(MFMapSqlDao.class, mapDao.getClass());
  }

  @Test
  public void shouldGetTileDaoWithTileAndMap()
  {
    MFTile mockTile = mock(MFTile.class);
    MFITileDao tileDao = factory.getTileSavingDao(mockTile, 1);
    assertEquals(mockTile, tileDao.getPayload());
    assertEquals(1, tileDao.getMapId());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldGetTileSqlDao()
  {
    MFITileDao tileDao = factory.getTileLoadingDao(Collections.EMPTY_MAP);
    assertEquals(MFTileSqlDao.class, tileDao.getClass());
  }

}