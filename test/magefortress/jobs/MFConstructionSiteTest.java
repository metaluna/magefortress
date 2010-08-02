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
import magefortress.channel.MFCommunicationChannel;
import magefortress.channel.MFIChannelSubscriber;
import magefortress.core.MFLocation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFConstructionSiteTest
{
  private MFConstructionSite constructionSite;
  private MFLocation location;
  private final int WIDTH   = 1;
  private final int HEIGHT  = 1;

  @Before
  public void setUp()
  {
    this.location = mock(MFLocation.class);
    this.constructionSite = new MFConstructionSiteMock(this.location, WIDTH, HEIGHT);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotInstantiateWithoutLocation()
  {
    new MFConstructionSiteMock(null, 1, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotInstantiateWithZeroWidth()
  {
    new MFConstructionSiteMock(mock(MFLocation.class), 0, 1);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotInstantiateWithZeroHeight()
  {
    new MFConstructionSiteMock(mock(MFLocation.class), 1, 0);
  }

  @Test
  public void shouldGetLocation()
  {
    MFLocation expLocation = this.location;
    MFLocation gotLocation = this.constructionSite.getLocation();
    assertEquals(expLocation, gotLocation);
  }

  @Test
  public void shouldGetDimensions()
  {
    int expWidth = WIDTH;
    int gotWidth = this.constructionSite.getWidth();
    assertEquals(expWidth, gotWidth);

    int expHeight = HEIGHT;
    int gotHeight = this.constructionSite.getHeight();
    assertEquals(expHeight, gotHeight);
  }

  public class MFConstructionSiteMock extends MFConstructionSite
  {

    public MFConstructionSiteMock(MFLocation _location, int _width, int _height)
    {
      super(_location, _width, _height, mock(MFJobFactory.class),
              mock(MFCommunicationChannel.class));
    }

    @Override
    public void update(long _currentTime)
    {
    }

    @Override
    public boolean isJobAvailable()
    {
      return false;
    }

    @Override
    public MFAssignableJob getJob()
    {
      return null;
    }

    @Override
    public void newSubscriber(MFIChannelSubscriber subscriber)
    {
    }

    @Override
    public void paint(Graphics2D _g, int _x_translation, int _y_translation)
    {
    }
  }

}