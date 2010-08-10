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
package magefortress.jobs.digging;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import magefortress.items.MFItem;
import magefortress.jobs.MFBlueprint;
import magefortress.jobs.MFIProducible;
import magefortress.map.MFTile;
import magefortress.map.ground.MFGround;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFQuarryTest
{
  private MFQuarry quarry;
  private MFBlueprint graniteBlueprint;
  private MFBlueprint bauxiteBlueprint;

  @Before
  public void setUp()
  {
    this.graniteBlueprint = mock(MFBlueprint.class);
    this.bauxiteBlueprint = mock(MFBlueprint.class);

    MFGround stone = mock(MFGround.class);
    when(stone.getBlueprint()).thenReturn(graniteBlueprint);

    MFTile granite = mock(MFTile.class);
    when(granite.getGround()).thenReturn(stone);

    final Set<MFTile> terrain = new HashSet<MFTile>();
    terrain.add(granite);

    this.quarry = new MFQuarry("Testing quarry", terrain);
  }

  @Test
  public void shouldGetMinableStonesAccordingToTerrainDuringCreation()
  {
    // given a quarry room on granite
    // when i query it
    List<MFBlueprint> products = this.quarry.getProducts();

    // then i should be able to produce only granite in this quarry
    assertEquals(1, products.size());

    final MFBlueprint expBlueprint = this.graniteBlueprint;
    final MFBlueprint gotBlueprint = products.get(0);
    assertEquals(expBlueprint, gotBlueprint);
  }

  @Test
  public void shouldGetMinableStonesOfAddedTerrain()
  {
    // given a quarry room on granite

    // when i add bauxite tiles
    final MFGround bauxite = mock(MFGround.class);
    when(bauxite.getBlueprint()).thenReturn(this.bauxiteBlueprint);

    final MFTile bauxiteTile = mock(MFTile.class);
    when(bauxiteTile.getGround()).thenReturn(bauxite);

    final Set<MFTile> tiles = new HashSet<MFTile>();
    tiles.add(bauxiteTile);

    this.quarry.addTiles(tiles);

    // then i should be able to produce bauxite
    List<MFBlueprint> products = this.quarry.getProducts();
    assertEquals(2, products.size());

    assertTrue(products.contains(this.bauxiteBlueprint));
    assertTrue(products.contains(this.graniteBlueprint));
  }

  @Test
  public void shouldNotGetMinableStoneOfRemoveTerrain()
  {
    // given a quarry room on granite and bauxite
    final MFGround bauxite = mock(MFGround.class);
    when(bauxite.getBlueprint()).thenReturn(this.bauxiteBlueprint);

    final MFTile bauxiteTile = mock(MFTile.class);
    when(bauxiteTile.getGround()).thenReturn(bauxite);

    final Set<MFTile> tiles = new HashSet<MFTile>();
    tiles.add(bauxiteTile);

    this.quarry.addTiles(tiles);

    // when i remove the bauxite tiles
    this.quarry.removeTiles(tiles);

    // then i should be able to produce only granite
    List<MFBlueprint> products = this.quarry.getProducts();
    assertEquals(1, products.size());

    assertTrue(products.contains(this.graniteBlueprint));
  }

}