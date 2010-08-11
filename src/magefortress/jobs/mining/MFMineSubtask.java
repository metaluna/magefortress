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
import magefortress.creatures.MFCreature;
import magefortress.creatures.behavior.instrumentable.MFEJob;
import magefortress.items.MFItem;
import magefortress.jobs.subtasks.MFSubtask;
import magefortress.jobs.subtasks.MFSubtaskCanceledException;
import magefortress.map.MFMap;
import magefortress.map.ground.MFGround;

/**
 * Mines for ore/stones/gems at the present location of the owner.
 */
public class MFMineSubtask extends MFSubtask
{
  public MFMineSubtask(MFCreature _owner, MFMap _map, MFGameObjectFactory _gameObjectFactory)
  {
    super(_owner);
    validateConstructorParams(_map, _owner, _gameObjectFactory);
    this.map = _map;
    this.ground = this.map.getTile(this.getOwner().getLocation()).getGround();
    this.gameObjectFactory = _gameObjectFactory;
  }

  //---vvv---     SUBTASK METHODS       ---vvv---
  @Override
  public boolean update() throws MFSubtaskCanceledException
  {
    boolean result = false;

    if (this.ticksLeft == 0) {
      this.ticksLeft = this.calculateMiningTime();
    } else if (this.ticksLeft == 1) {
      getStone();
      gainExperience();
      result = true;
    } else {
      --ticksLeft;
    }
    
    return result;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final MFEJob JOB_SKILL_USED = MFEJob.DIGGING;
  private static final int MIN_DIGGING_TIME  = 10;

  private final MFMap map;
  private final MFGround ground;
  private final MFGameObjectFactory gameObjectFactory;

  private int ticksLeft;

  /**
   * Calculates the time it will take the creature to dig out the tile. Takes
   * the stone's hardness and the miner's skill into account.
   * @return The time left in ticks until the tile is dug out
   */
  private int calculateMiningTime()
  {
    final int hardness = this.ground.getHardness();
    final int jobSkill = this.getOwner().getJobSkill(JOB_SKILL_USED);

    int result = MIN_DIGGING_TIME;
    result += Math.max(0, hardness-jobSkill);

    return result;
  }

  /**
   * Creates an item according to the blueprint received from the ground, puts
   * it on the floor, and lets the creature pick it up. Afterwards the creature
   * receives experience.
   */
  private void getStone()
  {
    MFItem stone = this.gameObjectFactory.createItem(this.ground.getBlueprint());
    this.map.getTile(this.getOwner().getLocation()).setObject(stone);
    this.getOwner().pickup();
  }

  /**
   * Awards the mining creature with experience. The harder the stone,
   * the higher the reward.
   */
  private void gainExperience()
  {
    final int xp = (int) Math.ceil(this.ground.getHardness() / 100);
    this.getOwner().gainJobExperience(JOB_SKILL_USED, xp);
  }

  private void validateConstructorParams(MFMap _map, MFCreature _owner,
                                         MFGameObjectFactory _gameObjectFactory)
  {
    if (_map == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                                               "without a map.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (!_owner.canHold()) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                       "with a creature unable to carry items.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (!_owner.canUseTools()) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                         "with a creature unable to use tools.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_owner.getJobSkill(JOB_SKILL_USED) < 1) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                              "with a creature unable to mine.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_gameObjectFactory == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create " +
                                               "without a game object factory.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }

}
