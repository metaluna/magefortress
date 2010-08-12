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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import magefortress.creatures.MFRace;
import magefortress.graphics.MFImageLibrary;
import magefortress.items.MFBlueprint;
import magefortress.items.MFBlueprintSqlDao;
import magefortress.items.MFIBlueprintDao;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import magefortress.map.ground.MFGround;
import magefortress.map.ground.MFGroundSqlDao;
import magefortress.map.ground.MFIGroundDao;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDaoFactoryTest
{
  private MFDaoFactory sqlFactory;

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

    this.sqlFactory = new MFDaoFactory(props);
  }


  //---vvv---        CONSTRUCTOR TESTS           ---vvv---
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutStorageMechanism()
  {
    new MFDaoFactory(null);
  }

  //---vvv---         RACE DAO TESTS               ---vvv---
  @Test
  public void shouldGetRaceLoadingDao()
  {
    MFIRaceDao raceDao = sqlFactory.getRaceLoadingDao();

    assertNotNull(raceDao);
    assertNull(raceDao.getPayload());
  }

  @Test
  public void shouldGetRaceSavingDao()
  {
    MFRace mockRace = mock(MFRace.class);

    MFIRaceDao raceDao = sqlFactory.getRaceSavingDao(mockRace);

    assertEquals(mockRace, raceDao.getPayload());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetRaceSavingDaoWithoutRace()
  {
    sqlFactory.getRaceSavingDao(null);
  }

  @Test
  public void shouldGetRaceSqlDao()
  {
    MFIRaceDao raceDao = sqlFactory.getRaceLoadingDao();
    assertEquals(MFRaceSqlDao.class, raceDao.getClass());
  }

  //---vvv---         BLUEPRINT DAO TESTS               ---vvv---
  @Test
  public void shouldGetBlueprintLoadingDao()
  {
    MFIBlueprintDao blueprintDao = sqlFactory.getBlueprintLoadingDao();
    assertNotNull(blueprintDao);
    assertNull(blueprintDao.getPayload());
  }

  @Test
  public void shouldGetBlueprintSavingDao()
  {
    MFBlueprint mockBlueprint = mock(MFBlueprint.class);
    MFIBlueprintDao blueprintDao = sqlFactory.getBlueprintSavingDao(mockBlueprint);
    assertEquals(mockBlueprint, blueprintDao.getPayload());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetBlueprintSavingDaoWithoutRace()
  {
    sqlFactory.getBlueprintSavingDao(null);
  }


  @Test
  public void shouldGetBlueprintSqlDao()
  {
    MFIBlueprintDao blueprintDao = sqlFactory.getBlueprintLoadingDao();
    assertEquals(MFBlueprintSqlDao.class, blueprintDao.getClass());
  }

  //---vvv---         GROUND DAO TESTS               ---vvv---
  @Test
  public void shouldGetGroundLoadingDao()
  {
    MFImageLibrary imgLib = MFImageLibrary.getInstance();
    Map<Integer, MFBlueprint> blueprints = new HashMap<Integer, MFBlueprint>();
    blueprints.put(1, mock(MFBlueprint.class));

    MFIGroundDao groundDao = sqlFactory.getGroundLoadingDao(blueprints, imgLib);

    assertNotNull(groundDao);
    assertNull(groundDao.getPayload());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetGroundLoadingDaoWithoutBlueprints()
  {
    sqlFactory.getGroundLoadingDao(null, MFImageLibrary.getInstance());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetGroundLoadingDaoWithEmtpyBlueprintsMap()
  {
    Map<Integer, MFBlueprint> blueprints = new HashMap<Integer, MFBlueprint>();

    sqlFactory.getGroundLoadingDao(blueprints, MFImageLibrary.getInstance());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetGroundLoadingDaoWithoutImageLibrary()
  {
    Map<Integer, MFBlueprint> blueprints = new HashMap<Integer, MFBlueprint>();
    blueprints.put(1, mock(MFBlueprint.class));

    sqlFactory.getGroundLoadingDao(blueprints, null);
  }

  @Test
  public void shouldGetGroundSavingDao()
  {
    MFGround mockGround = mock(MFGround.class);

    MFIGroundDao groundDao = sqlFactory.getGroundSavingDao(mockGround);

    assertEquals(mockGround, groundDao.getPayload());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetGroundSavingDaoWithoutGround()
  {
    sqlFactory.getGroundSavingDao(null);
  }

  @Test
  public void shouldGetGroundSqlDao()
  {
    MFImageLibrary imgLib = MFImageLibrary.getInstance();
    Map<Integer, MFBlueprint> blueprints = new HashMap<Integer, MFBlueprint>();
    blueprints.put(1, mock(MFBlueprint.class));

    MFIGroundDao groundDao = sqlFactory.getGroundLoadingDao(blueprints, imgLib);

    assertEquals(MFGroundSqlDao.class, groundDao.getClass());
  }

  //---vvv---         MAP DAO TESTS               ---vvv---
  @Test
  public void shouldGetMapLoadingDao()
  {
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();
    groundTypes.put(1, mock(MFGround.class));
    
    MFIMapDao mapDao = sqlFactory.getMapLoadingDao(groundTypes);

    assertEquals(MFMapSqlDao.class, mapDao.getClass());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetMapLoadingDaoWithoutGroundTypes()
  {
    sqlFactory.getMapLoadingDao(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetGroundLoadingDaoWithEmtpyGroundTypesMap()
  {
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();

    sqlFactory.getMapLoadingDao(groundTypes);
  }

  @Test
  public void shouldGetMapSavingDao()
  {
    MFMap mockMap = mock(MFMap.class);

    MFIMapDao mapDao = sqlFactory.getMapSavingDao(mockMap);

    assertEquals(mockMap, mapDao.getPayload());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetMapSavingDaoWithoutMap()
  {
    sqlFactory.getMapSavingDao(null);
  }

  //---vvv---         TILE DAO TESTS               ---vvv---
  @Test
  public void shouldGetTileLoadingDao()
  {
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();
    groundTypes.put(1, mock(MFGround.class));
    
    MFITileDao tileDao = sqlFactory.getTileLoadingDao(groundTypes);

    assertNotNull(tileDao);
    assertNull(tileDao.getPayload());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetTileLoadingDaoWithoutGroundTypesMap()
  {
    sqlFactory.getTileLoadingDao(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetTileLoadingDaoWithEmtpyGroundTypesMap()
  {
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();

    sqlFactory.getTileLoadingDao(groundTypes);
  }

  @Test
  public void shouldGetTileSavingDao()
  {
    MFTile mockTile = mock(MFTile.class);
    MFMap mockMap = mock(MFMap.class);
    int mapId = 42;
    when(mockMap.getId()).thenReturn(mapId);

    MFITileDao tileDao = sqlFactory.getTileSavingDao(mockTile, mockMap);

    assertEquals(mockTile, tileDao.getPayload());
    assertEquals(mapId, tileDao.getMapId());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetTileSavingDaoWithoutTile()
  {
    MFMap mockMap = mock(MFMap.class);
    when(mockMap.getId()).thenReturn(42);

    sqlFactory.getTileSavingDao(null, mockMap);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotGetTileSavingDaoWithoutMap()
  {
    MFTile mockTile = mock(MFTile.class);
    
    sqlFactory.getTileSavingDao(mockTile, null);
  }

  @Test
  public void shouldGetTileSqlDao()
  {
    Map<Integer, MFGround> groundTypes = new HashMap<Integer, MFGround>();
    groundTypes.put(1, mock(MFGround.class));

    MFITileDao tileDao = sqlFactory.getTileLoadingDao(groundTypes);

    assertEquals(MFTileSqlDao.class, tileDao.getClass());
  }

}