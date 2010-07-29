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
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import magefortress.core.Singleton;

/**
 * Central class where every object that directly interacts with the player
 * can register listeners. Notifications about different events will be
 * generated.
 * It is a singleton.
 * 
 */
public class MFInputManager extends MouseAdapter implements KeyListener, Singleton
{
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
   * for event listening. May be set to <code>null</code> to de-activate all
   * input listening
   * @param _mainContainer The container of the game
   */
  public void setMainContainer(Component _mainContainer)
  {
    boolean hasMouseListeners = this.mouseListeners.size() > 0;
    boolean hasKeyListeners   = this.keyListeners.size() > 0;
    
    // unregister event listeners from previous container
    if (this.mainContainer != null) {
      if (hasMouseListeners) {
        this.deactivateMouseListening();
      }
      if (hasKeyListeners) {
        this.deactivateKeyListening();
      }
    }

    // set container
    this.mainContainer = _mainContainer;

    // (re-)register event listeners with the new container
    if (this.mainContainer != null) {
      if (hasMouseListeners) {
        this.activateMouseListening();
      }
      if (hasKeyListeners) {
        this.activateKeyListening();
      }
    }
  }

  /**
   * Adds a component wishing to be notified of mouse events.
   * @param _mouseListener The new mouse listener
   */
  public void addMouseListener(MFIMouseListener _mouseListener)
  {
    // activate mouse listening at the parent component if this is the first listener
    if (this.mouseListeners.size() == 0) {
      this.activateMouseListening();
    }
    this.mouseListeners.add(_mouseListener);
  }

  /**
   * Removes a component which was listening to mouse events.
   * @param _mouseListener The component wishing to be removed
   */
  public void removeMouseListener(MFIMouseListener _mouseListener)
  {
    this.mouseListeners.remove(_mouseListener);
    // de-activate mouse listening if this was the last listener
    if (this.mouseListeners.size() == 0) {
      this.deactivateMouseListening();
    }
  }

  /**
   * Adds a component wishing to be notified of keyboard events.
   * @param _keyListener The new keyboard listener
   */
  public void addKeyListener(MFIKeyListener _keyListener)
  {
    // activate listening in the parent component if this is the first listener
    if (this.keyListeners.size() == 0) {
      this.activateKeyListening();
    }
    this.keyListeners.add(_keyListener);
  }

  /**
   * Removes a component which was listening to keyboard events.
   * @param _keyListener The component wishing to be removed
   */
  public void removeKeyListener(MFIKeyListener _keyListener)
  {
    this.keyListeners.remove(_keyListener);
    // de-activate listening in the parent component if this was the last listener
    if (this.keyListeners.size() == 0) {
      this.deactivateKeyListening();
    }
  }

  @Override
  public void mouseClicked(MouseEvent e)
  {
    for (MFIMouseListener listener : this.mouseListeners) {
      listener.mouseClicked(e.getX(), e.getY());
    }
  }

  @Override
  public void mouseDragged(MouseEvent e)
  {
  }

  @Override
  public void mouseMoved(MouseEvent e)
  {
    for (MFIMouseListener listener : this.mouseListeners) {
      listener.mouseMoved(e.getX(), e.getY());
    }
  }

  public void keyTyped(KeyEvent e)
  {
    // no action
  }

  public void keyPressed(KeyEvent e)
  {
    for (MFIKeyListener listener : this.keyListeners) {
      listener.keyPressed(e.getKeyCode());
    }
  }

  public void keyReleased(KeyEvent e)
  {
    // no action
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Singleton instance */
  private static MFInputManager instance;
  /** Parent container used for registering for receiving input events*/
  private Component mainContainer;
  /** List of components wishing to be notified of mouse events*/
  private LinkedList<MFIMouseListener> mouseListeners;
  /** List of components wishing to be notified of keyboard events*/
  private LinkedList<MFIKeyListener> keyListeners;
  /** The logger */
  private static final Logger logger = Logger.getLogger(MFInputManager.class.getName());

  /**
   * Hidden constructor.
   * cf. Singleton pattern
   */
  private MFInputManager()
  {
    this.mouseListeners = new LinkedList<MFIMouseListener>();
    this.keyListeners   = new LinkedList<MFIKeyListener>();
  }

  private void activateMouseListening()
  {
    if (this.mainContainer == null) {
      String message = "InputManager: Can't activate mouse listening when " +
              "parent container wasn't set previously.";
      logger.log(Level.SEVERE, message);
    }
    this.mainContainer.addMouseListener(this);
    this.mainContainer.addMouseMotionListener(this);
  }

  private void deactivateMouseListening()
  {
    if (this.mainContainer == null) {
      String message = "InputManager: Can't de-activate mouse listening when " +
              "parent container wasn't set previously.";
      logger.log(Level.SEVERE, message);
    }
    this.mainContainer.removeMouseListener(this);
    this.mainContainer.removeMouseMotionListener(this);
  }
  private void activateKeyListening()
  {
    if (this.mainContainer == null) {
      String message = "InputManager: Can't activate keyboard listening when " +
              "parent container wasn't set previously.";
      logger.log(Level.SEVERE, message);
    }
    this.mainContainer.addKeyListener(this);
  }

  private void deactivateKeyListening()
  {
    if (this.mainContainer == null) {
      String message = "InputManager: Can't de-activate keyboard listening when " +
              "parent container wasn't set previously.";
      logger.log(Level.SEVERE, message);
    }
    this.mainContainer.removeKeyListener(this);
  }

}
