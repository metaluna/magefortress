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

import magefortress.core.MFTool;

/**
 * This interface is implemented by all game objects able to use a
 * {@link MFTool}. It gives access to the skill levels of the tool user, the
 * job skills of the tools, assigning and unassigning of tools, and gaining
 * experience.
 */
public interface MFIInstrumentable
{
  public boolean canUseTools();
  public boolean addTool(MFTool _tool);
  public MFTool removeTool(MFEJob _job);
  public int getToolSkill(MFEJob _job);
  public int getJobSkill(MFEJob _job);
  public void gainJobExperience(MFEJob _job, int _xp);
  public MFEToolLevel getToolSkillLevel(MFEJob _job);
}
