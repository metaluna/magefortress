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

import java.awt.Component;
import magefortress.gui.MFScreen;
import magefortress.gui.MFScreensManager;
import magefortress.input.MFInputAction;
import magefortress.input.MFInputManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGameTest
{
  private MFGame game;

  @BeforeClass
  public static void setUpClass()
  {
    MFInputManager.getInstance().setMainContainer(mock(Component.class));
  }

  @AfterClass
  public static void tearDownClass()
  {
    MFInputManager.getInstance().setMainContainer(null);
  }

  @Before
  public void setUp()
  {
    game = new MFGame();
  }

  @Test
  public void shouldGetTilesize()
  {
    int expSize = MFMap.TILESIZE;
    int gotSize = game.getTilesize();
    
    assertEquals(expSize, gotSize);
  }

  @Test
  public void shouldExecuteInputActions()
  {
    MFInputAction mockAction1 = mock(MFInputAction.class);
    MFInputAction mockAction2 = mock(MFInputAction.class);
    MFInputAction mockAction3 = mock(MFInputAction.class);
    InOrder inOrder = inOrder(mockAction1, mockAction2, mockAction3);
    game.enqueueInputAction(mockAction1);
    game.enqueueInputAction(mockAction2);
    game.enqueueInputAction(mockAction3);

    game.update();
    inOrder.verify(mockAction1).execute();
    inOrder.verify(mockAction2).execute();
    inOrder.verify(mockAction3).execute();
  }

  @Test
  public void shouldRemoveExecutedActionsFromQueue()
  {
    MFInputAction mockAction = mock(MFInputAction.class);
    game.enqueueInputAction(mockAction);

    game.update();
    verify(mockAction).execute();
    game.update();
    verifyNoMoreInteractions(mockAction);
  }

  @Test
  public void shouldQuit()
  {
    MFScreen mockScreen = mock(MFScreen.class);
    MFScreensManager.getInstance().push(mockScreen);

    game.quit();
    verify(mockScreen).deinitialize();
  }

}