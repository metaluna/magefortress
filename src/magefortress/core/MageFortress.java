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

import magefortress.creatures.behavior.instrumentable.MFTool;
import java.util.EnumMap;
import magefortress.creatures.MFRace;
import magefortress.graphics.MFImageLibrary;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.movable.MFWalksOnTwoLegs;
import magefortress.creatures.behavior.holdable.MFNullHoldable;
import magefortress.input.MFInputManager;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import magefortress.channel.MFChannelFactory;
import magefortress.creatures.behavior.instrumentable.MFEJob;
import magefortress.creatures.behavior.instrumentable.MFEToolLevel;
import magefortress.creatures.behavior.instrumentable.MFUnlimitedToolbelt;
import magefortress.gui.MFGameScreen;
import magefortress.gui.MFScreen;
import magefortress.gui.MFScreensManager;
import magefortress.jobs.MFJobFactory;
import magefortress.storage.MFDaoFactory;

/**
 * Starts everything
 * 
 */
public class MageFortress extends JFrame implements Runnable
{
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
  @Override
  public void run()
  {
    // visible screen
    MFScreen currentScreen;
    // use double buffering
    final BufferStrategy buffer = canvas.getBufferStrategy();

    final int interval = 1000/FPS;
    long nextUpdate = System.currentTimeMillis() + interval;

    // try fullscreen mode
    final GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final GraphicsDevice graphicsDevice = environment.getDefaultScreenDevice();
    if (graphicsDevice.isFullScreenSupported()) {
      try {
        graphicsDevice.setFullScreenWindow(this);

        logger.fine("Starting game loop...");
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
          final long diff = nextUpdate - System.currentTimeMillis();
          if (diff > 0)
          {
            try {
              Thread.sleep(diff/3);
            } catch (InterruptedException ex) {
              Logger.getLogger(MageFortress.class.getName()).log(Level.SEVERE, "Thread got woken up", ex);
            }
          }
          else
            logger.finest("Too slow (" + diff + "ms)");
          nextUpdate = System.currentTimeMillis() + interval;
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
//    MFMap demoMap = null;
//    try {
//      demoMap = daoFactory.getMapDao().load(DEMOMAP_ID);
//    } catch (DataAccessException ex) {
//      Logger.getLogger(MageFortress.class.getName()).log(Level.SEVERE, null, ex);
//    }
    MFImageLibrary imgLib = MFImageLibrary.getInstance();
    MFGame game = MFGame.loadGame(DEMOMAP_ID, imgLib, daoFactory);
    summonSticky(imgLib, game, new MFLocation(0,0,0), "Sticky 000");
//    summonSticky(imgLib, game, new MFLocation(0,1,0), "Sticky 010");
//    summonSticky(imgLib, game, new MFLocation(1,0,0), "Sticky 100");

    MFScreen gameScreen = new MFGameScreen(MFInputManager.getInstance(), this.screenStack, game);
    game.setScreen(gameScreen);
    screenStack.push(gameScreen);
  }

  private void summonSticky(MFImageLibrary _imgLib, MFGame _game, MFLocation _location, String _name)
  {
    // Testing creatures
    MFRace stickies = new MFRace(-1, "Sticky", MFWalksOnTwoLegs.class, MFNullHoldable.class);
    MFCreature sticky = new MFGameObjectFactory(_imgLib, new MFJobFactory(_game), _game.getMap(), _game).createCreature(stickies);
    sticky.setName(_name);
    sticky.setLocation(_location);
    EnumMap<MFEJob, Integer> toolSkills = new EnumMap<MFEJob, Integer>(MFEJob.class);
    sticky.setToolUsingBehavior(new MFUnlimitedToolbelt(sticky, toolSkills));
    MFTool tool = new MFTool("Pick", MFEJob.DIGGING, MFChannelFactory.getInstance().getChannel(MFEJob.DIGGING), MFEToolLevel.APPRENTICE, 50, 100);
    sticky.addTool(tool);
    _game.addCreature(sticky);
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
    Handler handler = new ConsoleHandler();
    handler.setLevel(Level.INFO);
    Logger appLogger = Logger.getLogger("magefortress");
    appLogger.addHandler(handler);
    appLogger.setLevel(Level.INFO);
    appLogger.setUseParentHandlers(false);
    appLogger.config("Setting log level to " + Logger.getLogger("").getLevel());

    MageFortress game = new MageFortress();
    Thread loop = new Thread(game);
    loop.start();
  }

  private static final Logger logger = Logger.getLogger(MageFortress.class.getName());
  private static final long serialVersionUID = 927348798723947L;
  private static final String VERSION = "0.1";
  private static final int FPS = 50;
  private static final MFDaoFactory.Storage STORAGE = MFDaoFactory.Storage.SQL;
  private static final String DATABASE              = "magefortress.db";
  private static final int DEMOMAP_ID               = 1;

  private MFScreensManager screenStack;
  private Canvas canvas;

}
