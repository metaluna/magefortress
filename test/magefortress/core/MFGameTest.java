/*
 *  Copyright (c) 2009 Simon Hardijanto
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
package magefortress.core;

import magefortress.creatures.MFCreature;
import magefortress.graphics.MFImageLibrary;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import magefortress.gui.MFGameScreen;
import magefortress.gui.MFScreensManager;
import magefortress.input.MFGameInputFactory;
import magefortress.input.MFInputManager;
import magefortress.jobs.MFConstructionSite;
import magefortress.storage.MFDaoFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGameTest
{
  private MFGame game;
  private MFMap mockMap;
  private MFScreensManager mockScreensManager;

  @Before
  public void setUp()
  {
    mockMap = mock(MFMap.class);
    game = new MFGame(mockMap, MFImageLibrary.getInstance(), mock(MFDaoFactory.class));
  }

  @Test
  public void shouldGetTilesize()
  {
    int expSize = MFTile.TILESIZE;
    int gotSize = game.getTileSize();
    
    assertEquals(expSize, gotSize);
  }

  @Test
  public void shouldQuit()
  {
    // mock the screens manager because we can't verify if close() was called on
    // the screen any other way because the method is final
    mockScreensManager = mock(MFScreensManager.class);
    MFGameScreen gameScreen = new MFGameScreen(mock(MFInputManager.class), 
                      mockScreensManager, game, mock(MFGameInputFactory.class));
    game.setScreen(gameScreen);
    when(mockScreensManager.peek()).thenReturn(gameScreen);

    game.quit();
    verify(mockScreensManager).pop();
  }

  @Test(expected=NullPointerException.class)
  public void shouldNotQuit()
  {
    game.quit();
    verify(mockScreensManager, never()).pop();
  }

  @Test
  public void shouldUpdateCreatures()
  {
    MFCreature creature = mock(MFCreature.class);
    game.addCreature(creature);
    game.update();

    verify(creature).update();
  }

  @Test
  public void shouldAddConstructionSites()
  {
    MFConstructionSite newSite = mock(MFConstructionSite.class);
    game.addConstructionSite(newSite);

    boolean found = false;
    for (MFConstructionSite site : game.getConstructionSites()) {
      if (site == newSite) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void shouldRemoveConstructionSitesWithDelay()
  {
    MFConstructionSite newSite = mock(MFConstructionSite.class);
    MFLocation loc = new MFLocation(2,3,4);
    when(newSite.getLocation()).thenReturn(loc);

    game.addConstructionSite(newSite);
    game.removeConstructionSite(loc);

    boolean found = false;
    for (MFConstructionSite site : game.getConstructionSites()) {
      if (site == newSite) {
        found = true;
        break;
      }
    }
    assertTrue(found);

    game.update();
    
    found = false;
    for (MFConstructionSite site : game.getConstructionSites()) {
      if (site == newSite) {
        found = true;
        break;
      }
    }
    assertFalse(found);
  }

  @Test
  public void shouldUpdateConstructionSites()
  {
    MFConstructionSite newSite = mock(MFConstructionSite.class);
    game.addConstructionSite(newSite);
    game.update();

    verify(newSite).update();
  }

  //---vvv---     CONSTRUCTION SITE LISTENER INTERFACE      ---vvv---
  @Test
  public void shouldRemoveSiteAndUpdateMapDataWhenConstructionFinished()
  {
    // given a game with a construction site
    MFConstructionSite newSite = mock(MFConstructionSite.class);
    MFLocation loc = new MFLocation(2,3,4);
    when(newSite.getLocation()).thenReturn(loc);

    game.addConstructionSite(newSite);

    // when the construction is finished
    game.constructionSiteFinished(newSite);
    game.update();

    // then the site should be removed
    boolean found = false;
    for (MFConstructionSite site : game.getConstructionSites()) {
      if (site == newSite) {
        found = true;
        break;
      }
    }
    assertFalse(found);

    // and the navigation map should have been updated
    // can't test - game object factory needs to be injected
  }
}