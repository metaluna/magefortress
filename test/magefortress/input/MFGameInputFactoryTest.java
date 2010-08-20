/*
 *  Copyright (c) 2010 Simon Hardijanto
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

import java.util.ArrayList;
import java.util.Collection;
import magefortress.core.MFGame;
import magefortress.core.MFLocation;
import magefortress.jobs.digging.MFDigInputAction;
import magefortress.jobs.digging.MFDigInputTool;
import magefortress.jobs.mining.MFBuildQuarryInputAction;
import magefortress.jobs.mining.MFBuildQuarryInputTool;
import magefortress.map.MFMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFGameInputFactoryTest
{
  private MFGameInputFactory gameInputFactory;
  private MFGame game;

  @Before
  public void setUp()
  {
    this.game = mock(MFGame.class);
    MFMap map = mock(MFMap.class);
    when(this.game.getMap()).thenReturn(map);
    this.gameInputFactory = new MFGameInputFactory(this.game);
  }

  //---vvv---        CONSTURCTOR TESTS       ---vvv---
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutGame()
  {
    new MFGameInputFactory(null);
  }

  //---vvv---      FACTORY METHODS         ---vvv---
  @Test
  public void shouldCreateQuarryTool()
  {
    MFIInputToolListener toolListener = mock(MFIInputToolListener.class);
    MFIInputTool gotTool = this.gameInputFactory.createQuarryTool(toolListener);

    assertNotNull(gotTool);
    assertTrue(gotTool instanceof MFBuildQuarryInputTool);
  }

  @Test
  public void shouldCreateQuarryAction()
  {
    final Collection<MFLocation> locations = new ArrayList<MFLocation>(1);
    locations.add(new MFLocation(42, 42, 42));
    MFInputAction gotAction = this.gameInputFactory.createQuarryAction(locations);
    assertNotNull(gotAction);
    assertTrue(gotAction instanceof MFBuildQuarryInputAction);
  }

  @Test
  public void shouldCreateDigTool()
  {
    MFIInputToolListener toolListener = mock(MFIInputToolListener.class);
    MFIInputTool gotTool = this.gameInputFactory.createDigTool(toolListener);

    assertNotNull(gotTool);
    assertTrue(gotTool instanceof MFDigInputTool);
  }

  @Test
  public void shouldCreateDigAction()
  {
    Collection<MFLocation> locations = new ArrayList<MFLocation>(1);
    locations.add(new MFLocation(42, 42, 42));
    MFInputAction gotAction = this.gameInputFactory.createDigAction(locations);
    assertNotNull(gotAction);
    assertTrue(gotAction instanceof MFDigInputAction);
  }

}