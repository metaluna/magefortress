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
import magefortress.input.MFInputAction;
import magefortress.input.MFInputManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGameScreenTest
{
  private MFGameScreen gameScreen;
  private MFScreensManager mockScreensManager;

  public MFGameScreenTest()
  {

  }

  @Before
  public void setUp()
  {
    mockScreensManager = mock(MFScreensManager.class);
    this.gameScreen = new MFGameScreen(mock(MFInputManager.class), mockScreensManager, mock(MFGame.class));
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

    gameScreen.update(System.currentTimeMillis());
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

    gameScreen.update(System.currentTimeMillis());
    verify(mockAction).execute();
    gameScreen.update(System.currentTimeMillis());
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

}