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
package magefortress.jobs;

import java.awt.Graphics2D;
import java.util.logging.Logger;
import magefortress.channel.MFIChannelSender;
import magefortress.core.MFLocation;

/**
 * Base class for all construction sites. These are placed everywhere a dwarf
 * is needed to fullfill a task without a concrete object to attach the
 * task to. Examples:
 * <ul>
 * <li>Digging out a field</li>
 * <li>Cut a tree (Make this more abstract like demanding a number of trees?)</li>
 * <li>Place furniture or siege weapon (Should the building materials be
 * attached to the site or the placeable item?)</li>
 * </ul>
 */
public abstract class MFConstructionSite implements MFIChannelSender
{

  /**
   * Constructor taking the tile the site is attached to, its width and height.
   * If the construction site is bigger than one tile, the tile argument
   * defines the top-left tile of the whole site.
   * @param _topLeftLocation The top-left tile of the construction site
   * @param _width The width of the construction site
   * @param _height The height of the construction site
   * @param _jobFactory Factory to create job emitting objects
   */
  public MFConstructionSite(MFLocation _topLeftLocation, int _width, int _height, MFJobFactory _jobFactory)
  {
    if (_topLeftLocation == null) {
      String msg = "ConstructionSite: Top left location must not be null. Cannot " +
              "instantiate construction site.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_width <= 0 || _height <= 0) {
      String msg = "ConstructionSite: Width and height must not be 0. Cannot " +
              "instantiate construction site. Got " + _width + "x" + _height;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.location = _topLeftLocation;
    this.width = _width;
    this.height = _height;
    this.jobFactory = _jobFactory;
  }

  @Override
  public MFLocation getLocation()
  {
    return this.location;
  }

  public int getWidth()
  {
    return this.width;
  }

  public int getHeight()
  {
    return this.height;
  }

  public abstract void update();
  public abstract void paint(Graphics2D _g, int _x_translation, int _y_translation, MFLocation _location);

  protected MFJobFactory getJobFactory()
  {
    return this.jobFactory;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFJobFactory jobFactory;
  private final MFLocation location;
  private final int width;
  private final int height;
  final Logger logger = Logger.getLogger(MFConstructionSite.class.getName());

}
