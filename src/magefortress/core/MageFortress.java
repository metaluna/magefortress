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

import magefortress.map.MFMap;
import magefortress.input.MFInputManager;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import magefortress.gui.MFGameScreen;
import magefortress.gui.MFScreen;
import magefortress.gui.MFScreensManager;
import magefortress.storage.DataAccessException;
import magefortress.storage.MFDaoFactory;

/**
 * Starts everything
 * 
 */
public class MageFortress extends JFrame implements Runnable
{
  private static final long serialVersionUID = 927348798723947L;
  private static final String VERSION = "0.1";
  private static final int FPS = 50;

  private MFScreensManager screenStack;
  private Canvas canvas;

  /**
   * Constructor
   */
  public MageFortress()
  {
    screenStack = MFScreensManager.getInstance();
    initializeWindow();
    loadGame();
  }

  /**
   * The main game loop
   */
  public void run()
  {
    // visible screen
    MFScreen currentScreen;
    // use double buffering
    BufferStrategy buffer = canvas.getBufferStrategy();

    long nextUpdate = System.currentTimeMillis() + 1000/FPS;

    // try fullscreen mode
    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice graphicsDevice = environment.getDefaultScreenDevice();
    if (graphicsDevice.isFullScreenSupported()) {
      try {
        graphicsDevice.setFullScreenWindow(this);

        // Macht, dass Becci super is!
        while(null != (currentScreen = screenStack.peek())) {

          Graphics2D g = null;

          // process data
          currentScreen.update();

          // render
          try {
            g = (Graphics2D) buffer.getDrawGraphics();
            currentScreen.paint(g, canvas.getWidth(), canvas.getHeight());
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            g.dispose();
          }

          // does this really help?
          Toolkit.getDefaultToolkit().sync();
          // flip buffer
          buffer.show();

          // sync
          long now = System.currentTimeMillis();
          if (now < nextUpdate)
          {
            long delay = nextUpdate - now;
            try {
              Thread.sleep(delay);
            } catch (InterruptedException ex) {
              Logger.getLogger(MageFortress.class.getName()).log(Level.SEVERE, "Thread got woken up", ex);
            }
          }
          else
            System.out.println("Too slow");
          nextUpdate = System.currentTimeMillis() + 1000/FPS;
        }

      } finally {
        graphicsDevice.setFullScreenWindow(null);
      }
    }

    System.exit(0);
  }

  /**
   * Creates the game screen and pushes it on the stack.
   */
  private void loadGame()
  {
    // configure storage (sqlite)
    Properties props = new Properties();
    props.setProperty("STORAGE", STORAGE.toString());
    props.setProperty("DATABASE", DATABASE);
    MFDaoFactory daoFactory = new MFDaoFactory(props);

    //MFMap demomap = MFMap.createRandomMap(30, 30, 1);
    // load demo map from database
    MFMap demoMap = null;
    try {
      demoMap = daoFactory.getMapDao().load(DEMOMAP_ID);
    } catch (DataAccessException ex) {
      Logger.getLogger(MageFortress.class.getName()).log(Level.SEVERE, null, ex);
    }
    MFGame game = new MFGame(demoMap, daoFactory);
    
    MFScreen gameScreen = new MFGameScreen(MFInputManager.getInstance(), this.screenStack, game);
    game.setScreen(gameScreen);
    screenStack.push(gameScreen);
  }

  /**
   * Frame initialization
   */
  private void initializeWindow()
  {
    this.setTitle("Mage Fortress v" + VERSION);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setUndecorated(true);
    canvas = new Canvas();
    canvas.setSize(800, 600);
    canvas.setIgnoreRepaint(false);
    MFInputManager.getInstance().setMainContainer(canvas);
    this.getContentPane().add(canvas);
    this.pack();

    this.setVisible(true);
    canvas.createBufferStrategy(2);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    MageFortress game = new MageFortress();
    Thread loop = new Thread(game);
    loop.start();
  }
  
  private static final MFDaoFactory.Storage STORAGE = MFDaoFactory.Storage.SQL;
  private static final String DATABASE              = "magefortress.db";
  private static final int DEMOMAP_ID               = 1;


}
