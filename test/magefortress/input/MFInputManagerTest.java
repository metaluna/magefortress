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
package magefortress.input;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFInputManagerTest
{
  private MFInputManager inputManager = MFInputManager.getInstance();
  private Component mockContainer;

  public MFInputManagerTest()
  {
    mockContainer = mock(Component.class);
    inputManager.setMainContainer(mockContainer);
  }

  @Test
  public void shouldSendMouseClickedEvents()
  {
    MFIMouseListener mockMouseListener = mock(MFIMouseListener.class);
    final int X = 1;
    final int Y = 2;
    MouseEvent mockEvent = mock(MouseEvent.class);
    when(mockEvent.getX()).thenReturn(X);
    when(mockEvent.getY()).thenReturn(Y);

    inputManager.addMouseListener(mockMouseListener);

    inputManager.mouseClicked(mockEvent);
    verify(mockEvent).getX();
    verify(mockEvent).getY();
    verify(mockMouseListener).mouseClicked(X, Y);

    // clean-up singleton
    inputManager.removeMouseListener(mockMouseListener);
  }

  @Test
  public void shouldNoLongerSendMouseClickedEvents()
  {
    MFIMouseListener mockUnsubscribedListener = mock(MFIMouseListener.class);
    MFIMouseListener mockSubscribedListener = mock(MFIMouseListener.class);
    final int X = 1;
    final int Y = 2;
    MouseEvent mockEvent = mock(MouseEvent.class);
    when(mockEvent.getX()).thenReturn(X);
    when(mockEvent.getY()).thenReturn(Y);

    inputManager.addMouseListener(mockUnsubscribedListener);
    // add another one so that receiving clicks isn't de-activated
    inputManager.addMouseListener(mockSubscribedListener);
    inputManager.removeMouseListener(mockUnsubscribedListener);

    inputManager.mouseClicked(mockEvent);
    verify(mockEvent).getX();
    verify(mockEvent).getY();
    verify(mockUnsubscribedListener, never()).mouseClicked(X, Y);
    verify(mockSubscribedListener).mouseClicked(X, Y);

    // clean-up singleton
    inputManager.removeMouseListener(mockUnsubscribedListener);
    inputManager.removeMouseListener(mockSubscribedListener);
  }

  @Test
  public void shouldSendMouseMovedEvents()
  {
    MFIMouseListener mockMouseListener = mock(MFIMouseListener.class);
    final int X = 1;
    final int Y = 2;
    MouseEvent mockEvent = mock(MouseEvent.class);
    when(mockEvent.getX()).thenReturn(X);
    when(mockEvent.getY()).thenReturn(Y);

    inputManager.addMouseListener(mockMouseListener);

    inputManager.mouseMoved(mockEvent);
    verify(mockEvent).getX();
    verify(mockEvent).getY();
    verify(mockMouseListener).mouseMoved(X, Y);

    // clean-up singleton
    inputManager.removeMouseListener(mockMouseListener);
  }

  @Test
  public void shouldNoLongerSendMouseMoveEvents()
  {
    MFIMouseListener mockUnsubscribedListener = mock(MFIMouseListener.class);
    MFIMouseListener mockSubscribedListener = mock(MFIMouseListener.class);
    final int X = 1;
    final int Y = 2;
    MouseEvent mockEvent = mock(MouseEvent.class);
    when(mockEvent.getX()).thenReturn(X);
    when(mockEvent.getY()).thenReturn(Y);

    inputManager.addMouseListener(mockUnsubscribedListener);
    inputManager.addMouseListener(mockSubscribedListener);
    inputManager.removeMouseListener(mockUnsubscribedListener);

    inputManager.mouseMoved(mockEvent);
    verify(mockEvent).getX();
    verify(mockEvent).getY();
    verify(mockUnsubscribedListener, never()).mouseMoved(X, Y);
    verify(mockSubscribedListener).mouseMoved(X, Y);

    // clean-up singleton
    inputManager.removeMouseListener(mockUnsubscribedListener);
    inputManager.removeMouseListener(mockSubscribedListener);
  }

  @Test
  public void shouldSendKeyPressedEvents()
  {
    MFIKeyListener mockKeyListener = mock(MFIKeyListener.class);
    final int KEY = KeyEvent.VK_ESCAPE;
    KeyEvent mockEvent = mock(KeyEvent.class);
    when(mockEvent.getKeyCode()).thenReturn(KEY);

    inputManager.addKeyListener(mockKeyListener);

    inputManager.keyPressed(mockEvent);
    verify(mockEvent).getKeyCode();
    verify(mockKeyListener).keyPressed(KEY);

    // clean-up singleton
    inputManager.removeKeyListener(mockKeyListener);
  }

  @Test
  public void shouldNoLongerSendKeyPressedEvents()
  {
    MFIKeyListener mockUnsubscribedListener = mock(MFIKeyListener.class);
    MFIKeyListener mockSubscribedListener = mock(MFIKeyListener.class);
    final int KEY = KeyEvent.VK_ESCAPE;
    KeyEvent mockEvent = mock(KeyEvent.class);
    when(mockEvent.getKeyCode()).thenReturn(KEY);

    inputManager.addKeyListener(mockUnsubscribedListener);
    inputManager.addKeyListener(mockSubscribedListener);
    inputManager.removeKeyListener(mockUnsubscribedListener);

    inputManager.keyPressed(mockEvent);
    verify(mockEvent).getKeyCode();
    verify(mockUnsubscribedListener, never()).keyPressed(KEY);
    verify(mockSubscribedListener).keyPressed(KEY);

    // clean-up singleton
    inputManager.removeKeyListener(mockUnsubscribedListener);
    inputManager.removeKeyListener(mockSubscribedListener);
  }

  @Test
  public void shouldRegisterMouseListeningAtContainer()
  {
    // needed to trigger registration
    MFIMouseListener mockListener = mock(MFIMouseListener.class);

    inputManager.addMouseListener(mockListener);
    verify(mockContainer).addMouseListener(inputManager);
    verify(mockContainer).addMouseMotionListener(inputManager);

    // clean-up singleton
    inputManager.removeMouseListener(mockListener);
  }

  @Test
  public void shouldUnregisterMouseListeningFromContainer()
  {
    // needed to trigger registration
    MFIMouseListener mockListener = mock(MFIMouseListener.class);

    inputManager.addMouseListener(mockListener);
    verify(mockContainer).addMouseListener(inputManager);
    verify(mockContainer).addMouseMotionListener(inputManager);

    inputManager.removeMouseListener(mockListener);
    verify(mockContainer).removeMouseListener(inputManager);
    verify(mockContainer).removeMouseMotionListener(inputManager);
  }

  @Test
  public void shouldRegisterKeyboardListeningAtContainer()
  {
    // needed to trigger registration
    MFIKeyListener mockListener = mock(MFIKeyListener.class);

    inputManager.addKeyListener(mockListener);
    verify(mockContainer).addKeyListener(inputManager);

    // clean-up singleton
    inputManager.removeKeyListener(mockListener);
  }

  @Test
  public void shouldUnregisterKeyboardListeningAtContainer()
  {
    // needed to trigger registration
    MFIKeyListener mockListener = mock(MFIKeyListener.class);

    inputManager.addKeyListener(mockListener);
    verify(mockContainer).addKeyListener(inputManager);

    inputManager.removeKeyListener(mockListener);
    verify(mockContainer).removeKeyListener(inputManager);
  }

  @Test
  public void shouldUnregisterFromOldAndRegisterAtNewContainerForMouseEvents()
  {
    // needed to trigger registration
    MFIMouseListener mockListener = mock(MFIMouseListener.class);
    // container to re-register event listening
    Component newContainer = mock(Component.class);

    inputManager.addMouseListener(mockListener);
    verify(mockContainer).addMouseListener(inputManager);
    verify(mockContainer).addMouseMotionListener(inputManager);

    inputManager.setMainContainer(newContainer);
    verify(mockContainer).removeMouseListener(inputManager);
    verify(mockContainer).removeMouseMotionListener(inputManager);
    verify(newContainer).addMouseListener(inputManager);
    verify(newContainer).addMouseMotionListener(inputManager);

    // clean-up singleton
    inputManager.removeMouseListener(mockListener);
    inputManager.setMainContainer(mockContainer);
  }

  @Test
  public void shouldUnregisterFromOldAndRegisterAtNewContainerForKeyEvents()
  {
    // needed to trigger registration
    MFIKeyListener mockListener = mock(MFIKeyListener.class);
    // container to re-register event listening
    Component newContainer = mock(Component.class);

    inputManager.addKeyListener(mockListener);
    verify(mockContainer).addKeyListener(inputManager);

    inputManager.setMainContainer(newContainer);
    verify(mockContainer).removeKeyListener(inputManager);
    verify(newContainer).addKeyListener(inputManager);

    // clean-up singleton
    inputManager.removeKeyListener(mockListener);
    inputManager.setMainContainer(mockContainer);
  }

}