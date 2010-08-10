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
package magefortress.jobs;

import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.creatures.MFCreature;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFJobSlotTest
{
  private MFJobSlot slot;
  private MFLocation location;

  @Before
  public void setUp()
  {
    this.location = new MFLocation(41, 42, 43);
    this.slot = new MFJobSlot(location);
  }

  //---vvv---        CONSTRUCTOR TESTS          ---vvv---
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutLocation()
  {
    new MFJobSlot(null);
  }

  //---vvv---          METHOD TESTS             ---vvv---
  @Test
  public void shouldGetLocation()
  {
    MFLocation expLocation = this.location;
    MFLocation gotLocation = this.slot.getLocation();
    assertEquals(expLocation, gotLocation);
  }

  @Test
  public void shouldNotBeOccupied()
  {
    assertTrue(this.slot.isAvailable());

  }

  @Test
  public void shouldBeOccupiedWhenACreatureIsSet()
  {
    MFCreature creature = mock(MFCreature.class);
    this.slot.occupy(creature);
    assertFalse(this.slot.isAvailable());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotBeOccupiedByNullCreature()
  {
    this.slot.occupy(null);
  }

  @Test
  public void shouldNoLongerBeOccupiedWhenTheOccupyingCreatureLeaves()
  {
    MFCreature creature = mock(MFCreature.class);
    this.slot.occupy(creature);
    this.slot.free(creature);
    assertTrue(this.slot.isAvailable());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotBeLeftByNullCreature()
  {
    this.slot.free(null);
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldThrowAnExceptionWhenAnotherCreatureWantsToOccupyTheSlot()
  {
    MFCreature creature1 = mock(MFCreature.class);
    when(creature1.getName()).thenReturn("Mock 1");
    MFCreature creature2 = mock(MFCreature.class);
    when(creature2.getName()).thenReturn("Mock 2");
    this.slot.occupy(creature1);
    this.slot.occupy(creature2);
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotBeLeftByOtherCreatureThanTheOccupier()
  {
    MFCreature creature1 = mock(MFCreature.class);
    when(creature1.getName()).thenReturn("Mock 1");
    this.slot.occupy(creature1);
    
    MFCreature creature2 = mock(MFCreature.class);
    when(creature2.getName()).thenReturn("Mock 2");
    this.slot.free(creature2);
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotBeLeftIfNotOccupied()
  {
    MFCreature creature1 = mock(MFCreature.class);
    when(creature1.getName()).thenReturn("Mock 1");
    this.slot.free(creature1);
  }

}