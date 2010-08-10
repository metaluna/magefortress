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
package magefortress.creatures.behavior.holdable;

import magefortress.items.MFItem;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class MFNullHoldableTest
{
  private MFIHoldable holdable;

  @Before
  public void setUp()
  {
    this.holdable = new MFNullHoldable();
  }

  @Test
  public void shouldNotHold()
  {
    assertFalse(this.holdable.canHold());
  }

  @Test
  public void shouldNeverPickup()
  {
    assertFalse(this.holdable.pickup());
  }

  @Test
  public void shouldNotAddAndPutItem()
  {
    this.holdable.addItem(mock(MFItem.class));
    assertFalse(this.holdable.putItem(mock(MFTile.class)));
  }

  @Test
  public void shouldNotPutNoItem()
  {
    assertFalse(this.holdable.putItem(mock(MFTile.class)));
  }

}