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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFAreaInputActionTest
{
  private MFAreaInputAction action;
  private MFGame game;
  private Collection<MFLocation> locations;

  @Before
  public void setUp()
  {
    this.game = mock(MFGame.class);
    this.locations = new ArrayList<MFLocation>();
    this.locations.add(new MFLocation(42, 42, 42));
    this.action = new MFAreaInputActionMock(game, locations);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutGame()
  {
    new MFAreaInputActionMock(null, this.locations);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutLocations()
  {
    new MFAreaInputActionMock(this.game, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithZeroLocations()
  {
    new MFAreaInputActionMock(this.game, new ArrayList<MFLocation>());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateIfLocationsContainsNullElements()
  {
    this.locations = new ArrayList<MFLocation>();
    this.locations.add(null);
    new MFAreaInputActionMock(this.game, this.locations);
  }

  //---vvv---            METHOD TEST               ---vvv---
  @Test
  public void shouldGetArea()
  {
    
  }

}