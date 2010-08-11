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
package magefortress.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFItemTest
{
  private MFItem item;
  private MFBlueprint blueprint;

  @Before
  public void setUp()
  {
    this.blueprint = mock(MFBlueprint.class);
    when(this.blueprint.getName()).thenReturn("Test Item");
    this.item = new MFItem(blueprint);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutBlueprint()
  {
    new MFItem(null);
  }

  @Test
  public void shouldGetNameOfTheBlueprint()
  {
    String name = "Blueprint Name";
    when(this.blueprint.getName()).thenReturn(name);
    this.item = new MFItem(blueprint);

    assertEquals(name, this.item.getName());
  }

  @Test
  public void shouldGetBlueprint()
  {
    assertEquals(this.blueprint, this.item.getBlueprint());
  }

  @Test
  public void shouldByDefaultBeNotPlaceable()
  {
    assertFalse(this.item.isPlaceable());
  }

  @Test
  public void shouldSetPlaceable()
  {
  }

}