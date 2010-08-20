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
package magefortress.jobs.mining;

import java.util.ArrayList;
import java.util.List;
import magefortress.core.MFGame;
import magefortress.core.MFGameObjectFactory;
import magefortress.core.MFJobSlotSite;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.jobs.MFConstructionSite;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFBuildQuarryInputActionTest
{
  private MFBuildQuarryInputAction action;
  private MFGame game;
  private List<MFLocation> locations;

  @Before
  public void setUp()
  {
    this.game = mock(MFGame.class);
    this.locations = new ArrayList<MFLocation>();
    this.locations.add(new MFLocation(42, 42, 42));
    this.locations.add(new MFLocation(43, 42, 42));
    this.locations.add(new MFLocation(44, 42, 42));
    this.locations.add(new MFLocation(45, 42, 42));
    this.action = new MFBuildQuarryInputAction(this.game, locations);
  }

  @Test
  public void shouldHaveAtLeastOneJobSlot()
  {
    int gotJobSlotCount = this.action.getJobSlotCount();
    assertThat(gotJobSlotCount, greaterThan(0));
  }

  @Test
  public void shouldBeValidJobSlot()
  {
    for (MFLocation location : this.locations) {
      assertTrue(this.action.isValidJobSlotLocation(location));
    }
  }

  @Test
  public void shouldNotBeValidJobSlotIfOutsideTheRoom()
  {
    MFLocation outsideRoom = new MFLocation(23, 23, 23);
    assertFalse(this.action.isValidJobSlotLocation(outsideRoom));
  }

  @Test
  public void shouldPutJobSlot()
  {
    this.action.putJobSlot(this.locations.get(0));
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotPutMoreJobSlotsThanPossible()
  {
    for (int i=0; i<=this.action.getJobSlotCount(); ++i) {
      MFLocation location = this.locations.get(i);
      this.action.putJobSlot(location);
    }
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotPutJobSlotOutsideTheRoom()
  {
    MFLocation outsideRoom = new MFLocation(23, 23, 23);
    this.action.putJobSlot(outsideRoom);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotPutJobSlotIfNull()
  {
    this.action.putJobSlot(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotPutSameJobSlotTwice()
  {
    this.action.putJobSlot(this.locations.get(0));
    this.action.putJobSlot(this.locations.get(0));
  }

  @Test
  public void shouldCreateConstructionSiteOnExecute()
  {
    MFJobSlotSite jobSlotSite = mock(MFJobSlotSite.class);
    MFGameObjectFactory gameObjectFactory = mock(MFGameObjectFactory.class);
    when(gameObjectFactory.createJobSlotSite(any(MFLocation.class))).thenReturn(jobSlotSite);
    when(this.game.getGameObjectFactory()).thenReturn(gameObjectFactory);
    this.action.putJobSlot(this.locations.get(0));
    this.action.execute();
    verify(this.game).addConstructionSite(any(MFConstructionSite.class));
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotCreateConstructionSiteIfNotAllJobSlotsWerePlaced()
  {
    this.action.execute();
  }

}