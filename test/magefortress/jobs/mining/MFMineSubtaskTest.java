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
package magefortress.jobs.mining;

import magefortress.core.MFGameObjectFactory;
import magefortress.core.MFLocation;
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.instrumentable.MFEJob;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import magefortress.map.ground.MFGround;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFMineSubtaskTest
{
  private MFMineSubtask task;
  private MFCreature miner;
  private MFLocation minerLoc;
  private MFMap map;
  private MFGround ground;
  private MFGameObjectFactory gameObjectFactory;

  @Before
  public void setUp()
  {
    this.miner      = mock(MFCreature.class);
    this.minerLoc   = new MFLocation(42, 42, 42);
    when(miner.getLocation()).thenReturn(minerLoc);
    when(miner.canHold()).thenReturn(true);
    when(miner.canUseTools()).thenReturn(true);
    when(miner.getJobSkill(MFEJob.DIGGING)).thenReturn(501);

    MFTile mockTile = mock(MFTile.class);
    this.ground     = mock(MFGround.class);
    when(mockTile.getGround()).thenReturn(ground);

    this.map        = mock(MFMap.class);
    when(map.getTile(minerLoc)).thenReturn(mockTile);

    this.gameObjectFactory = mock(MFGameObjectFactory.class);
    
    this.task       = new MFMineSubtask(this.miner, this.map, this.gameObjectFactory);
  }

  //---vvv---        CONSTRUCTOR TESTS          ---vvv---
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutOwner()
  {
    new MFMineSubtask(null, this.map, this.gameObjectFactory);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFMineSubtask(this.miner, null, this.gameObjectFactory);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutGameObjectFactory()
  {
    new MFMineSubtask(this.miner, this.map, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateIfUnableToHold()
  {
    when(this.miner.canHold()).thenReturn(false);
    new MFMineSubtask(this.miner, this.map, this.gameObjectFactory);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateIfUnableToUseTools()
  {
    when(this.miner.canUseTools()).thenReturn(false);
    new MFMineSubtask(this.miner, this.map, this.gameObjectFactory);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateIfHasNoDiggingSkillLevel()
  {
    when(this.miner.getJobSkill(MFEJob.DIGGING)).thenReturn(0);
    new MFMineSubtask(this.miner, this.map, this.gameObjectFactory);
  }

  //---vvv---        METHOD TESTS         ---vvv---
  @Test
  public void shouldStartMiningOnFirstUpdate() throws MFSubtaskCanceledException
  {
    boolean done = this.task.update();
    assertFalse(done);
  }

  @Test
  public void shouldBeDoneWhenMiningFinishes() throws MFSubtaskCanceledException
  {
    when(this.ground.getHardness()).thenReturn(150);

    boolean done = true;
    for (int i=0; i<10; ++i) {
      done = this.task.update();
      assertFalse(done);
    }

    done = this.task.update();
    assertTrue(done);
    verify(this.miner).gainJobExperience(eq(MFEJob.DIGGING), anyInt());
    verify(this.miner).pickup();
  }
}