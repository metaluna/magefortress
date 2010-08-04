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
package magefortress.creatures.behavior;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;
import magefortress.channel.MFIChannelSubscriber;
import magefortress.core.MFTool;

/**
 * This toolbelt allows an arbitrary number of tools for <strong>different</strong>
 * jobs.
 */
public class MFUnlimitedToolbelt implements MFIInstrumentable
{
  public MFUnlimitedToolbelt(MFIChannelSubscriber _channelSubscriber,
          EnumMap<MFEJob, Integer> _toolSkills)
  {
    if (_channelSubscriber == null) {
      String msg = MFUnlimitedToolbelt.class.getSimpleName() +
              ": Cannot create without a channel subscriber.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_toolSkills == null) {
      String msg = MFUnlimitedToolbelt.class.getSimpleName() +
              ": Cannot create without a list of tool skills.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.channelSubscriber = _channelSubscriber;
    
    this.toolbelt  = new EnumMap<MFEJob, MFTool>(MFEJob.class);
    this.toolSkills = new EnumMap<MFEJob, Integer>(_toolSkills);
  }

  @Override
  public boolean canUseTools()
  {
    return true;
  }

  /**
   * Adds a tool only if there's not already one in the tool's job slot present
   * in this toolbelt. Otherwise it return false.
   * @param _tool The tool to add
   * @return <code>true</code> if there's not already a tool in the tool's job
   *         slot present.
   */
  @Override
  public boolean addTool(MFTool _tool)
  {
    boolean result = false;
    
    if (this.toolbelt.get(_tool.getJob()) == null) {

      this.toolbelt.put(_tool.getJob(), _tool);
      _tool.getChannel().subscribe(this.channelSubscriber);
      result = true;

    } else {

      String msg = this.getClass().getSimpleName() + ": Cannot add the '" +
              _tool.getName() + "'. There's already a '" +
              this.toolbelt.get(_tool.getJob()).getName() + "' in the slot '" +
              _tool.getJob() + "'.";
      logger.warning(msg);
      
    }

    return result;
  }

  @Override
  public MFTool removeTool(MFEJob _job)
  {
    final MFTool result = this.toolbelt.remove(_job);

    if (result != null) {
      result.getChannel().unsubscribe(this.channelSubscriber);
    } else {
      String msg = this.getClass().getSimpleName() + ": Trying to remove non-" +
              "existing tool for '" + _job + "'.";
      logger.warning(msg);
    }

    return result;
  }

  @Override
  public int getToolSkill(MFEJob _job)
  {
    Integer result = this.toolSkills.get(_job);
    
    if (result == null) {
      result = 0;
    }

    return result;
  }

  @Override
  public MFEToolLevel getToolSkillLevel(MFEJob _job)
  {
    return MFEToolLevel.levelOf(this.getToolSkill(_job));
  }

  @Override
  public int getJobSkill(MFEJob _job)
  {
    MFTool tool = this.toolbelt.get(_job);

    int result;
    if (tool == null) {
      result = 0;
    } else {
      result = effectiveJobSkill(tool, this.getToolSkill(_job));
    }

    return result;
  }

  @Override
  public void gainJobExperience(MFEJob _job, int _xp)
  {
    int newXp = this.getToolSkill(_job) + _xp;
    this.toolSkills.put(_job, newXp);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFUnlimitedToolbelt.class.getName());
  
  private final MFIChannelSubscriber channelSubscriber;
  private final Map<MFEJob, MFTool> toolbelt;
  private final Map<MFEJob, Integer> toolSkills;

  private int effectiveJobSkill(final MFTool _tool, final int _toolSkill)
  {
    float result = _tool.getBaseEfficiency();

    if (_toolSkill > 0) {
      final MFEToolLevel level = MFEToolLevel.levelOf(_toolSkill);
      if (level.ordinal() > _tool.getToolLevel().ordinal()) {
        result = _tool.getMaxEfficiency();
      } else {
        final float ratio = (float)(_toolSkill - level.minXp)/(float)((level.maxXp+1) - level.minXp);
        result += (_tool.getMaxEfficiency()-_tool.getBaseEfficiency())*ratio;
      }
    }
    return (int) result;
  }
}
