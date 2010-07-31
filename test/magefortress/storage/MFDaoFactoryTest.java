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

import magefortress.core.MFRace;
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
    this.factory = new MFDaoFactory(MFDaoFactory.Storage.SQL);
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
    MFIMapDao mapDao = factory.getMapDao();
    assertEquals(MFMapSqlDao.class, mapDao.getClass());
  }

  @Test
  public void shouldGetTileDaoWithTile()
  {
    MFTile mockTile = mock(MFTile.class);
    MFITileDao tileDao = factory.getTileDao(mockTile);
    assertEquals(mockTile, tileDao.getPayload());
  }

  @Test
  public void shouldGetTileSqlDao()
  {
    MFITileDao tileDao = factory.getTileDao();
    assertEquals(MFTileSqlDao.class, tileDao.getClass());
  }

   @Test
   public void shouldGetTileSqlDaoWithMap()
   {
     MFITileDao tileDao = factory.getTileDao(1);
     assertNull(tileDao.getPayload());
   }

}