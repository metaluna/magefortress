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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import magefortress.core.MFGame;
import magefortress.core.MFGameObjectFactory;
import magefortress.core.MFLocation;
import magefortress.jobs.MFConstructionSite;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDigInputActionTest
{
  private MFDigInputAction action;
  private MFGame game;
  private MFLocation[] locations;

  @Before
  public void setUp()
  {
    this.game = mock(MFGame.class);
    this.locations = new MFLocation[] {new MFLocation(42, 42, 42)};
    this.action = new MFDigInputAction(this.game, this.locations);
  }

  @Test
  public void shouldCreateConstructionSiteOnExecute()
  {
    Iterable<MFConstructionSite> iterable = new ArrayList<MFConstructionSite>();
    when(this.game.getConstructionSites()).thenReturn(iterable);
    MFGameObjectFactory gameObjectFactory = mock(MFGameObjectFactory.class);
    when(this.game.getGameObjectFactory()).thenReturn(gameObjectFactory);

    this.action.execute();

    verify(this.game).addConstructionSite(any(MFConstructionSite.class));
  }

  @Test
  public void shouldNotCreateConstructionSiteOnExecuteIfLocationIsOccupied()
  {
    MFConstructionSite site = mock(MFDiggingSite.class);
    when(site.getLocation()).thenReturn(this.locations[0]);
    List<MFConstructionSite> constructionSites = new ArrayList<MFConstructionSite>();
    constructionSites.add(site);
    when(this.game.getConstructionSites()).thenReturn(constructionSites);
    MFGameObjectFactory gameObjectFactory = mock(MFGameObjectFactory.class);
    when(this.game.getGameObjectFactory()).thenReturn(gameObjectFactory);

    this.action.execute();

    verify(this.game, never()).addConstructionSite(any(MFConstructionSite.class));
  }

}