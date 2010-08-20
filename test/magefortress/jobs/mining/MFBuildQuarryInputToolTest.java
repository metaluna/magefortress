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
import java.util.Collection;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.input.MFGameInputFactory;
import magefortress.input.MFIInputToolListener;
import magefortress.input.MFInputAction;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFBuildQuarryInputToolTest
{
  private MFBuildQuarryInputTool tool;
  private MFMap map;
  private MFGameInputFactory inputFactory;
  private MFIInputToolListener toolListener;

  private MFTile validTile;
  private MFLocation validLocation;
  private MFBuildQuarryInputAction action;
  @Before
  public void setUp()
  {
    // tool
    this.map = mock(MFMap.class);
    this.toolListener = mock(MFIInputToolListener.class);
    this.inputFactory = mock(MFGameInputFactory.class);
    this.toolListener = mock(MFIInputToolListener.class);
    this.tool = new MFBuildQuarryInputTool(this.map, this.inputFactory, this.toolListener);

    // valid tile
    this.validTile = mock(MFTile.class);
    when(validTile.isUnderground()).thenReturn(true);
    when(validTile.isDugOut()).thenReturn(true);
    this.validLocation = new MFLocation(42, 42, 42);
    when(this.map.getTile(validLocation)).thenReturn(validTile);
    when(this.map.isInsideMap(validLocation)).thenReturn(true);

    // input action factory
    final Collection<MFLocation> locations = new ArrayList<MFLocation>(1);
    locations.add(this.validLocation);
    this.action = mock(MFBuildQuarryInputAction.class);
    when(this.inputFactory.createQuarryAction(locations)).thenReturn(action);
    when(action.getJobSlotCount()).thenReturn(1);
    when(action.isValidJobSlotLocation(this.validLocation)).thenReturn(true);
  }

  //---vvv---   CONSTRUCTOR TESTS     ---vvv---

  //---vvv---    ROOM SELECTION PHASE     ---vvv---
  @Test
  public void shouldBeValidIfDugOutAndUnderground()
  {
    boolean valid = this.tool.isValid(this.validLocation);

    assertTrue(valid);
  }

  @Test
  public void shouldNotBeValidIfDugOutAndNotUnderground()
  {
    MFLocation invalidLocation = new MFLocation(23, 23, 23);
    when(this.map.isInsideMap(invalidLocation)).thenReturn(true);

    MFTile tile = mock(MFTile.class);
    when(tile.isDugOut()).thenReturn(true);
    when(tile.isUnderground()).thenReturn(false);
    when(this.map.getTile(invalidLocation)).thenReturn(tile);

    boolean valid = this.tool.isValid(invalidLocation);

    assertFalse(valid);
  }

  @Test
  public void shouldNotBeValidIfNotInsideMap()
  {
    MFLocation invalidLocation = new MFLocation(23, 23, 23);
    when(this.map.isInsideMap(invalidLocation)).thenReturn(false);

    boolean valid = this.tool.isValid(invalidLocation);

    assertFalse(valid);
  }

  @Test
  public void shouldChangePhaseWhenValidTileWasClicked()
  {
    this.tool.click(validLocation);

    final Collection<MFLocation> locations = new ArrayList<MFLocation>(1);
    locations.add(this.validLocation);
    verify(this.inputFactory).createQuarryAction(locations);

    verify(this.toolListener).toolPhaseChanged();
  }

  @Test
  public void shouldNotChangePhaseWhenInvalidTileWasClicked()
  {
    MFLocation invalidLocation = new MFLocation(23, 23, 23);
    when(this.map.isInsideMap(invalidLocation)).thenReturn(false);

    this.tool.click(invalidLocation);

    final Collection<MFLocation> locations = new ArrayList<MFLocation>(1);
    locations.add(this.validLocation);
    verify(this.inputFactory, never()).createQuarryAction(locations);

    verify(this.toolListener, never()).toolPhaseChanged();
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotBuildActionInRoomSelectionPhase()
  {
    this.tool.buildAction();
  }

  //---vvv---        JOB SLOT PLACEMENT PHASE       ---vvv---
  @Test
  public void shouldBeValidIfJobSlotIsPlacedInsideRoom()
  {
    // given an input tool in its job slot placement phase
    this.tool.click(validLocation);

    // when a tile inside the room is clicked
    boolean valid = this.tool.isValid(this.validLocation);

    // then the tile should be valid
    assertTrue(valid);
  }

  @Test
  public void shouldNotBeValidIfJobSlotIsPlacedOutsideRoom()
  {
    // given an input tool in its job slot placement phase
    this.tool.click(validLocation);

    // when a tile outside the room is clicked
    MFLocation outsideRoom = validLocation.locationOf(MFEDirection.N);

    boolean valid = this.tool.isValid(outsideRoom);

    // then the tile should not be valid
    assertFalse(valid);
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotBuildActionInJobSlotPlacementPhase()
  {
    this.tool.click(this.validLocation);

    this.tool.buildAction();
  }

  //---vvv---           ALL DONE PHASE     ---vvv---
  @Test
  public void shouldBeFinishedWhenAllJobSlotsWerePlaced()
  {
    this.tool.click(validLocation);

    this.tool.click(validLocation);

    verify(this.toolListener).toolFinished();
    MFInputAction gotAction = this.tool.buildAction();
    assertEquals(this.action, gotAction);
    assertTrue(gotAction instanceof MFBuildQuarryInputAction);
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldThrowExceptionIfTryingToCheckValidityWhenDone()
  {
    this.tool.click(validLocation);

    this.tool.click(validLocation);
    
    this.tool.isValid(validLocation);
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldThrowExceptionIfTryingToClickWhenDone()
  {
    this.tool.click(validLocation);

    this.tool.click(validLocation);

    this.tool.click(validLocation);
  }

}