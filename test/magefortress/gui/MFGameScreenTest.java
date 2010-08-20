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
package magefortress.gui;

import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.input.MFGameInputFactory;
import magefortress.input.MFIInputTool;
import magefortress.input.MFInputAction;
import magefortress.input.MFInputManager;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGameScreenTest
{
  private MFGameScreen gameScreen;
  private MFScreensManager mockScreensManager;
  private MFGame game;

  public MFGameScreenTest()
  {

  }

  @Before
  public void setUp()
  {
    this.mockScreensManager = mock(MFScreensManager.class);
    this.game = mock(MFGame.class);
    MFMap map = mock(MFMap.class);
    MFGameInputFactory inputFactory = mock(MFGameInputFactory.class);
    when(this.game.getMap()).thenReturn(map);
    this.gameScreen = new MFGameScreen(mock(MFInputManager.class), mockScreensManager, 
                                                            game, inputFactory);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutAGame()
  {
    new MFGameScreen(mock(MFInputManager.class), this.mockScreensManager, null,
                                                      mock(MFGameInputFactory.class));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutAGameInputFactory()
  {
    new MFGameScreen(mock(MFInputManager.class), this.mockScreensManager,
                                                       mock(MFGame.class), null);
  }

  @Test
  public void shouldExecuteInputActions()
  {
    MFInputAction mockAction1 = mock(MFInputAction.class);
    MFInputAction mockAction2 = mock(MFInputAction.class);
    MFInputAction mockAction3 = mock(MFInputAction.class);
    InOrder inOrder = inOrder(mockAction1, mockAction2, mockAction3);

    gameScreen.enqueueInputAction(mockAction1);
    gameScreen.enqueueInputAction(mockAction2);
    gameScreen.enqueueInputAction(mockAction3);
    verifyZeroInteractions(mockAction1, mockAction2, mockAction3);

    gameScreen.update();
    inOrder.verify(mockAction1).execute();
    inOrder.verify(mockAction2).execute();
    inOrder.verify(mockAction3).execute();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotEnqueueInputActions()
  {
    gameScreen.enqueueInputAction(null);
  }

  @Test
  public void shouldRemoveExecutedActionsFromQueue()
  {
    MFInputAction mockAction = mock(MFInputAction.class);
    gameScreen.enqueueInputAction(mockAction);

    gameScreen.update();
    verify(mockAction).execute();
    gameScreen.update();
    verifyNoMoreInteractions(mockAction);
  }

  @Test
  public void shouldCloseTheScreen()
  {
    when(mockScreensManager.peek()).thenReturn(gameScreen);

    gameScreen.close();
    verify(mockScreensManager).pop();
  }

  @Test(expected=IllegalAccessError.class)
  public void shouldNotCloseTheScreen()
  {
    when(mockScreensManager.peek()).thenReturn(null);
    
    gameScreen.close();
    verify(mockScreensManager, never()).pop();
  }

  @Test
  public void shouldQueryInputTool()
  {
    // given a game screen
    MFMap map = mock(MFMap.class);
    when(this.game.getMap()).thenReturn(map);

    // when i activate an input tool
    MFIInputTool inputTool = mock(MFIInputTool.class);
    this.gameScreen.setActiveInputTool(inputTool);

    // and i hover over a tile
    final int x = MFTile.TILESIZE-1;
    final int y = MFTile.TILESIZE-1;
    this.gameScreen.mouseMoved(x, y);

    // then the active input tool should be queried
    verify(inputTool).isValid(new MFLocation(0,0,0));
  }

  @Test
  public void shouldNotQueryInputToolWhenNoneIsSelected()
  {
    // given a game screen

    // when i activate an input tool
    MFIInputTool inputTool = mock(MFIInputTool.class);
    this.gameScreen.setActiveInputTool(inputTool);

    // and i deactivate the input tool
    this.gameScreen.setActiveInputTool(null);

    // then the input tool should not be queried
    verify(inputTool, never()).isValid(any(MFLocation.class));
  }

  @Test
  public void shouldSendClickedLocationToActiveInputTool()
  {
    // given a game screen with an activated input tool
    MFMap map = mock(MFMap.class);
    when(this.game.getMap()).thenReturn(map);

    MFIInputTool inputTool = mock(MFIInputTool.class);
    this.gameScreen.setActiveInputTool(inputTool);

    // when i click on a tile
    final int x = MFTile.TILESIZE-1;
    final int y = MFTile.TILESIZE-1;
    this.gameScreen.mouseClicked(x, y);

    MFLocation tileLocation = MFMap.convertToTilespace(x, y, 0, 0, 0);

    // then the screen should send a click to the input tool
    verify(inputTool).click(tileLocation);
  }

  @Test
  public void shouldNotQueryInputToolWhenToolFinishedWasCalled()
  {
    // given a game screen with an activated input tool
    MFMap map = mock(MFMap.class);
    when(this.game.getMap()).thenReturn(map);

    MFIInputTool inputTool = mock(MFIInputTool.class);
    when(inputTool.isValid(any(MFLocation.class))).thenReturn(false);
    MFInputAction action = mock(MFInputAction.class);
    when(inputTool.buildAction()).thenReturn(action);
    this.gameScreen.setActiveInputTool(inputTool);

    // when the tool has finished
    this.gameScreen.toolFinished();

    // then it should no longer be queried
    this.gameScreen.mouseMoved(0, 0);
    verify(inputTool, never()).isValid(any(MFLocation.class));

    // and the screen should not send a click to the input tool
    this.gameScreen.mouseClicked(0, 0);
    verify(inputTool, never()).click(any(MFLocation.class));
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldThrowExceptionWhenToolFinishedWasCalledWithoutActiveTool()
  {
    // given a game screen without an active input tool
    this.gameScreen.setActiveInputTool(null);

    // when the tool is reported finished
    this.gameScreen.toolFinished();

    // then an exception should be thrown
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldThrowExceptionWhenToolPhaseChangedWasCalledWithoutActiveTool()
  {
    // given a game screen without an active input tool
    this.gameScreen.setActiveInputTool(null);

    // when the tool is reported finished
    this.gameScreen.toolPhaseChanged();

    // then an exception should be thrown
  }

}