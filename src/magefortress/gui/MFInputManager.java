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

import java.awt.Component;

/**
 * Central class where every object that directly interacts with the player
 * can register listeners. Notifications about different events will be
 * generated.
 * It is a singleton.
 * 
 */
public class MFInputManager
{
  private static MFInputManager instance;
  private Component mainContainer;

  /**
   * cf. Singleton pattern
   * @return The instance of the input manager
   */
  public static synchronized MFInputManager getInstance()
  {
    if (instance == null) {
      instance = new MFInputManager();
    }

    return instance;
  }

  /**
   * Sets the container where the input manager will register
   * for event listening.
   * @param _mainContainer The container of the game
   */
  public void setMainContainer(Component _mainContainer)
  {
    this.mainContainer = _mainContainer;
  }


//  public static addMouseListener()
//  {
//
//  }
    //---vvv---      PRIVATE METHODS      ---vvv---

  /**
   * Hidden constructor.
   * cf. Singleton pattern
   */
  private MFInputManager()
  {

  }

}
