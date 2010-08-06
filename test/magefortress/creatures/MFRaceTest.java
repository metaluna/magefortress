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

import magefortress.creatures.behavior.holdable.MFMockHoldable;
import magefortress.creatures.behavior.movable.MFMockMovable;
import magefortress.creatures.behavior.holdable.MFIHoldable;
import magefortress.creatures.behavior.movable.MFIMovable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFRaceTest
{
  private MFRace race;
  private final static Class<? extends MFIMovable> MOVE_CLASS = MFMockMovable.class;
  private final static Class<? extends MFIHoldable> HOLD_CLASS = MFMockHoldable.class;
  private final static int ID = 42;
  private final static String NAME = "Test Race";

  @Before
  public void setUp()
  {
    race = new MFRace(ID, NAME, MOVE_CLASS, HOLD_CLASS);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateRaceWithoutName()
  {
    race = new MFRace(ID, null, MFMockMovable.class, MFMockHoldable.class);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateRaceWithoutMovingBehaviour()
  {
    race = new MFRace(ID, NAME, null, HOLD_CLASS);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateRaceWithoutHoldingBehavior()
  {
    race = new MFRace(ID, NAME, MOVE_CLASS, null);
  }

  @Test(expected=ClassNotFoundException.class)
  public void shouldNotCreateRaceWithNonExistingMovingBehavior()
          throws ClassNotFoundException
  {
    final String movingBehaviorClassName = "MFMockMovable";
    final String holdingBehaviorClassName = "magefortress.creatures.behavior.holdable.MFMockHoldable";
    race = new MFRace(ID, NAME, movingBehaviorClassName, holdingBehaviorClassName);
  }

  @Test(expected=ClassNotFoundException.class)
  public void shouldNotCreateRaceWithNonExistingHoldingBehavior()
          throws ClassNotFoundException
  {
    final String movingBehaviorClassName = "magefortress.creatures.behavior.movable.MFMockMovable";
    final String holdingBehaviorClassName = "MFMockHoldable";
    race = new MFRace(ID, NAME, movingBehaviorClassName, holdingBehaviorClassName);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateRaceWithWrongMovingClassName()
          throws ClassNotFoundException
  {
    final String wrongMovingBehaviorClassName = "magefortress.creatures.behavior.holdable.MFMockHoldable";
    final String holdingBehaviorClassName = "magefortress.creatures.behavior.holdable.MFMockHoldable";

    race = new MFRace(ID, NAME, wrongMovingBehaviorClassName, holdingBehaviorClassName);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateRaceWithWrongHoldingClassName()
          throws ClassNotFoundException
  {
    final String movingBehaviorClassName = "magefortress.creatures.behavior.movable.MFMockMovable";
    final String wrongHoldingBehaviorClassName = "magefortress.creatures.behavior.movable.MFMockMovable";

    race = new MFRace(ID, NAME, movingBehaviorClassName, wrongHoldingBehaviorClassName);
  }

  @Test
  public void shouldCreateRaceWithBehaviorsByName()
          throws ClassNotFoundException
  {
    final String movingBehaviorClassName = "magefortress.creatures.behavior.movable.MFMockMovable";
    final String holdingBehaviorClassName = "magefortress.creatures.behavior.holdable.MFMockHoldable";
    race = new MFRace(ID, NAME, movingBehaviorClassName, holdingBehaviorClassName);

    assertEquals(movingBehaviorClassName, race.getMovingBehaviorClass().getName());
    assertEquals(holdingBehaviorClassName, race.getHoldingBehaviorClass().getName());
  }

  @Test
  public void shouldGetId()
  {
    assertEquals(ID, race.getId());
  }

  @Test
  public void shouldGetName()
  {
    assertEquals(NAME, race.getName());
  }

  @Test
  public void shouldGetMovingBehaviorClass()
  {
    Class<? extends MFIMovable> expMovingBehavior = MOVE_CLASS;
    Class<? extends MFIMovable> gotMovingBehavior = race.getMovingBehaviorClass();

    assertEquals(expMovingBehavior, gotMovingBehavior);
  }

  @Test
  public void shouldGetHoldingBehaviorClass()
  {
    Class<? extends MFIHoldable> expHoldingBehavior = HOLD_CLASS;
    Class<? extends MFIHoldable> gotHoldingBehavior = race.getHoldingBehaviorClass();

    assertEquals(expHoldingBehavior, gotHoldingBehavior);
  }

  @Test
  public void shouldCreateCreature()
  {
    Class<? extends MFIMovable> movingBehaviorClass = MFMockMovable.class;
    Class<? extends MFIHoldable> holdingBehaviorClass = MFMockHoldable.class;

    MFRace testrace = new MFRace(ID, NAME, movingBehaviorClass, holdingBehaviorClass);

    MFCreature gotCreature = testrace.createCreature();

    assertEquals(NAME, gotCreature.getName());
    assertEquals(movingBehaviorClass, gotCreature.getMovingBehavior().getClass());
    assertEquals(holdingBehaviorClass, gotCreature.getHoldingBehavior().getClass());
  }

}