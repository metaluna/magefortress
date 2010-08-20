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

import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.input.MFIInputToolListener;
import magefortress.input.MFInputAction;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDigInputToolTest
{
  private MFDigInputTool digInputTool;
  private MFMap map;
  private MFGame game;
  private MFIInputToolListener toolListener;

  @Before
  public void setUp()
  {
    this.map = mock(MFMap.class);
    this.game = mock(MFGame.class);
    this.toolListener = mock(MFIInputToolListener.class);
    this.digInputTool = new MFDigInputTool(map, game, toolListener);
  }

  //---vvv---       CONSTRUCTOR TESTS      ---vvv---
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFDigInputTool(null, this.game, this.toolListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutGame()
  {
    new MFDigInputTool(this.map, null, this.toolListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutInputToolListener()
  {
    new MFDigInputTool(this.map, this.game, null);
  }

  //---vvv---      MEMBER METHOD TESTS      ---vvv---
  @Test
  public void shouldBeValidIfNotDugOut()
  {
    MFTile tile = mock(MFTile.class);
    when(tile.isDugOut()).thenReturn(false);
    
    MFLocation selectedLocation = new MFLocation(42, 42, 42);
    when(this.map.isInsideMap(selectedLocation)).thenReturn(true);
    when(this.map.getTile(selectedLocation)).thenReturn(tile);

    boolean valid = this.digInputTool.isValid(selectedLocation);

    assertTrue(valid);
  }

  @Test
  public void shouldNotBeValidIfNotInsideMap()
  {
    MFLocation selectedLocation = new MFLocation(42, 42, 42);
    when(this.map.isInsideMap(selectedLocation)).thenReturn(false);

    boolean valid = this.digInputTool.isValid(selectedLocation);

    assertFalse(valid);
  }

  @Test
  public void shouldNotBeValidIfDugOut()
  {
    MFTile tile = mock(MFTile.class);
    when(tile.isDugOut()).thenReturn(true);
    MFLocation selectedLocation = new MFLocation(42, 42, 42);
    when(this.map.getTile(selectedLocation)).thenReturn(tile);

    boolean valid = this.digInputTool.isValid(selectedLocation);

    assertFalse(valid);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowExceptionIfTestingValidityOfNullLocation()
  {
    this.digInputTool.isValid(null);
  }

  @Test
  public void shouldReturnDigInputAction()
  {
    MFTile tile = mock(MFTile.class);
    when(tile.isDugOut()).thenReturn(false);
    MFLocation selectedLocation = new MFLocation(42, 42, 42);
    when(this.map.isInsideMap(selectedLocation)).thenReturn(true);
    when(this.map.getTile(selectedLocation)).thenReturn(tile);

    this.digInputTool.click(selectedLocation);
    MFInputAction action = this.digInputTool.buildAction();

    assertNotNull(action);
    assertTrue(action instanceof MFDigInputAction);
  }

  @Test
  public void shouldBeFinishedAfterOneValidClick()
  {
    MFTile tile = mock(MFTile.class);
    when(tile.isDugOut()).thenReturn(false);
    MFLocation selectedLocation = new MFLocation(42, 42, 42);
    when(this.map.isInsideMap(selectedLocation)).thenReturn(true);
    when(this.map.getTile(selectedLocation)).thenReturn(tile);

    this.digInputTool.click(selectedLocation);

    verify(this.toolListener).toolFinished();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowExceptionIfSelectionIsNull()
  {
    this.digInputTool.click(null);
  }

}