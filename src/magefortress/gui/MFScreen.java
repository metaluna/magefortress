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

import magefortress.input.MFInputManager;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to display various screens of the game.
 * 
 */
public abstract class MFScreen
{
  private MFInputManager inputManager;
  private MFScreensManager screensManager;
  final static Logger logger = Logger.getLogger(MFScreen.class.getName());

  public MFScreen(MFInputManager _inputManager, MFScreensManager _screensManager)
  {
    if (_inputManager == null) {
      String msg = "Screen: InputManager must not be null. Can't instantiate screen.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    if (_screensManager == null) {
      String msg = "Screen: ScreensManager must not be null. Can't instantiate screen.";
      logger.log(Level.SEVERE, msg);
      throw new IllegalArgumentException(msg);
    }
    this.inputManager = _inputManager;
    this.screensManager = _screensManager;
  }

  final MFInputManager getInputManager()
  {
    return this.inputManager;
  }

  /**
   * Closes the screen by popping it of the screen manager's stack. If it's the
   * last screen, the application exits.
   */
  final public void close()
  {
    if (this.screensManager.peek() != this) {
      String msg = "Screen: Tried to close screen but different screen is on top.";
      logger.log(Level.WARNING, msg);
      throw new IllegalAccessError(msg);
    }

    this.screensManager.pop();
  }

  /**
   * Called when the screen becomes the top of the screens stack. This happens
   * when the screen is first pushed on the stack and when the screen was
   * covered by other screens and the covering screens were all closed.
   */
  public abstract void initialize();
  /**
   * Called when the screen is covered by another screen and when the screen
   * is closed.
   */
  public abstract void deinitialize();
  /**
   * Called when the game tells the screen to update its contents.
   */
  public abstract void update(long _currentTime);
  /**
   * Called when the game tells the screen to display its contents.
   * @param _g The canvas
   * @param _width The horizontal size of the canvas
   * @param _height The vertical size of the canvas
   */
  public abstract void paint(Graphics2D _g, int _width, int _height);
}
