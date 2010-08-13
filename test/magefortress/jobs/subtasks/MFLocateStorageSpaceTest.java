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
package magefortress.jobs.subtasks;

import magefortress.core.MFIStorageLocator;
import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;
import magefortress.items.MFBlueprint;
import magefortress.items.MFItem;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFLocateStorageSpaceTest
{
  private MFLocateStorageSpace task;
  private MFCreature owner;
  private MFIStorageLocator locator;
  private MFBlueprint blueprint;

  @Before
  public void setUp()
  {
    MFItem item = mock(MFItem.class);
    this.blueprint = mock(MFBlueprint.class);
    when(item.getBlueprint()).thenReturn(this.blueprint);
    when(this.blueprint.getName()).thenReturn("Quasarifibrilator");
    this.owner    = mock(MFCreature.class);
    when(this.owner.getItem()).thenReturn(item);
    when(this.owner.getName()).thenReturn("Sticky");
    
    this.locator  = mock(MFIStorageLocator.class);
    this.task     = new MFLocateStorageSpace(this.owner, this.locator);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutOwner()
  {
    new MFLocateStorageSpace(null, this.locator);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutStorageLocator()
  {
    new MFLocateStorageSpace(this.owner, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutCarriedItem()
  {
    when(this.owner.getItem()).thenReturn(null);
    new MFLocateStorageSpace(this.owner, this.locator);
  }

  @Test
  public void shouldFinishOnUpdate() throws MFSubtaskCanceledException
  {
    boolean done = this.task.update();
    assertTrue(done);
  }

  @Test
  public void shouldSetOwnersCurrentHeadingWhenALocationWasFound() throws MFSubtaskCanceledException
  {
    MFLocation stockpile = new MFLocation(42, 42, 42);
    when(this.locator.findStorage(blueprint)).thenReturn(stockpile);

    boolean done = this.task.update();
    assertTrue(done);

    verify(this.owner).setCurrentHeading(stockpile);
  }

  @Test(expected=MFSubtaskCanceledException.class)
  public void shouldThrowExceptionWhenNoLocationWasFound() throws MFSubtaskCanceledException
  {
    when(this.locator.findStorage(blueprint)).thenReturn(MFLocation.NOWHERE);

    this.task.update();
  }

}