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
package magefortress.creatures;

import magefortress.creatures.MFRace;
import magefortress.creatures.MFCreature;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.MFMockHoldable;
import magefortress.creatures.behavior.MFMockMovable;
import magefortress.creatures.behavior.MFIHoldable;
import magefortress.creatures.behavior.MFNullMovable;
import magefortress.creatures.behavior.MFIMovable;
import magefortress.creatures.behavior.MFNullHoldable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFCreatureTest
{
  private static final String NAME = "Test Creature";
  private static final String RACE_NAME = "Test Race";
  private static final String MOVING_BEHAVIOR = "magefortress.creatures.behavior.MFMockMovable";
  private static final String HOLDING_BEHAVIOR = "magefortress.creatures.behavior.MFMockHoldable";
  private MFRace race;
  private MFCreature creature;

  @Before
  public void setUp() throws ClassNotFoundException
  {
    race = new MFRace(1, RACE_NAME, MOVING_BEHAVIOR, HOLDING_BEHAVIOR);
    creature = new MFCreature(NAME, race);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutName()
  {
    creature = new MFCreature(null, race);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutRace()
  {
    creature = new MFCreature(NAME, null);
  }

  @Test
  public void shouldGetName()
  {
    assertEquals(NAME, creature.getName());
  }

  @Test
  public void shouldSetName()
  {
    String expName = "New Name";
    creature.setName(expName);
    assertEquals(expName, creature.getName());
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotSetNameToNull()
  {
    creature.setName(null);
  }

  @Test
  public void shouldGetRace()
  {
    assertEquals(this.race, creature.getRace());
  }

  @Test
  public void shouldByDefaultBeNowhere()
  {
    assertEquals(MFLocation.NOWHERE, creature.getLocation());
  }

  @Test
  public void shouldSetMovingBehavior()
          throws InstantiationException, IllegalAccessException
  {
    MFIMovable expMovingBehavior = MFMockMovable.class.newInstance();
    creature.setMovingBehavior(expMovingBehavior);

    MFIMovable gotMovingBehavior = creature.getMovingBehavior();
    assertEquals(expMovingBehavior, gotMovingBehavior);
  }

  @Test
  public void shouldByDefaultHaveNullMovingBehavior()
  {
    Class<? extends MFIMovable> expMovingBehavior = MFNullMovable.class;
    Class<? extends MFIMovable> gotMovingBehavior = creature.getMovingBehavior().getClass();
    assertEquals(expMovingBehavior, gotMovingBehavior);
  }

  @Test
  public void shouldSetHoldingBehavior()
          throws InstantiationException, IllegalAccessException
  {
    MFIHoldable expHoldingBehavior = MFMockHoldable.class.newInstance();
    creature.setHoldingBehavior(expHoldingBehavior);

    MFIHoldable gotHoldingBehavior = creature.getHoldingBehavior();
    assertEquals(expHoldingBehavior, gotHoldingBehavior);
  }

  @Test
  public void shouldByDefaultHaveNullHoldingBehavior()
  {
    Class<? extends MFIHoldable> expHoldingBehavior = MFNullHoldable.class;
    Class<? extends MFIHoldable> gotHoldingBehavior = creature.getHoldingBehavior().getClass();
    assertEquals(expHoldingBehavior, gotHoldingBehavior);
  }

}