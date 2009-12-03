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

import java.util.LinkedList;

/**
 * A Stack of screen objects. Screens are pushed on the stack one by one. Only
 * the top most is active at any one time. When a screen closes it's pushed off
 * the stack and the screen below will be displayed.
 * Like all managers, it's a Singleton.
 */
public class MFScreensManager
{
  private static MFScreensManager instance;
  private LinkedList<MFScreen> screens;

  /**
   * cf. Singleton pattern
   * @return The instance of the screens manager
   */
  public static synchronized MFScreensManager getInstance()
  {
      if (instance == null) {
          instance = new MFScreensManager();
      }

      return instance;
  }

  /**
   * Pushes a screen on the top of the stack and calls its initialize() method.
   * @param _screen The screen that will be shown
   */
  public void push(MFScreen _screen)
  {
      _screen.initialize();
      this.screens.push(_screen);
  }

  /**
   * Gets the currently top most screen on the stack.
   * @return The currently visible screen
   */
  public MFScreen peek()
  {
      return this.screens.peek();
  }

  /**
   * Removes the screen on the top of the stack and calls its deinitialize()
   * method
   * @return The currently visible screen
   */
  public MFScreen pop()
  {
      MFScreen screen = this.screens.pop();
      screen.deinitialize();
      return screen;
  }
  //---vvv---      PRIVATE METHODS      ---vvv---

  /**
   * Hidden constructor
   * cf. Singleton pattern
   */
  private MFScreensManager()
  {
      screens = new LinkedList<MFScreen>();
  }

}
