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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFScreensManagerTest
{
  MFScreensManager screensManager;

  @Before
  public void setUp()
  {
    screensManager = MFScreensManager.getInstance();
    // clear stack
    while (null != screensManager.peek()) {
      screensManager.pop();
    }
  }

  @Test
  public void shouldPushScreenOnStack()
  {
    MFScreen expScreen = mock(MFScreen.class);
    screensManager.push(expScreen);
    
    MFScreen screen = screensManager.peek();
    assertEquals(expScreen, screen);
    verify(expScreen).initialize();
  }

  @Test
  public void shouldPopScreenOffStack()
  {
    MFScreen expScreen = mock(MFScreen.class);
    screensManager.push(expScreen);

    MFScreen screen = screensManager.pop();
    assertEquals(expScreen, screen);
    screen = screensManager.peek();
    assertEquals(null, screen);

    verify(expScreen).deinitialize();
  }

  @Test(expected=NullPointerException.class)
  public void shouldNotPushScreenOnStack()
  {
    screensManager.push(null);
  }

}