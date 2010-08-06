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
package magefortress.creatures.behavior.instrumentable;

import java.util.EnumMap;
import magefortress.channel.MFCommunicationChannel;
import magefortress.creatures.MFCreature;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFUnlimitedToolbeltTest
{
  private MFUnlimitedToolbelt toolbelt;
  private MFTool tool;
  private MFCommunicationChannel channel;
  private MFEJob job;
  private MFEToolLevel toolLevel;
  private MFCreature creature;
  private final int toolSkillDigging = MFEToolLevel.NONE.minXp;
  private final int toolSkillWoodcutting = MFEToolLevel.MASTER.minXp;
  private final int jobSkillWoodBase = 250;
  private final int jobSkillWoodMax  = 300;

  @Before
  public void setUp()
  {
    this.creature = mock(MFCreature.class);
    EnumMap<MFEJob, Integer> toolSkills = new EnumMap<MFEJob, Integer>(MFEJob.class);
    toolSkills.put(MFEJob.DIGGING, toolSkillDigging);
    toolSkills.put(MFEJob.WOODCUTTING, toolSkillWoodcutting);

    this.toolbelt = new MFUnlimitedToolbelt(this.creature, toolSkills);

    this.toolLevel = MFEToolLevel.MASTER;
    this.job = MFEJob.WOODCUTTING;
    this.channel = mock(MFCommunicationChannel.class);
    when(this.channel.getName()).thenReturn(this.job.toString());
    this.tool = new MFTool("Test Tool", job, channel, toolLevel, jobSkillWoodBase, jobSkillWoodMax);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutAChannelSubscriber()
  {
    new MFUnlimitedToolbelt(null, new EnumMap<MFEJob, Integer>(MFEJob.class));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutAListOfToolSkills()
  {
    new MFUnlimitedToolbelt(creature, null);
  }

  @Test
  public void shouldUseTools()
  {
    assertTrue(this.toolbelt.canUseTools());
  }

  @Test
  public void shouldAddTool()
  {
    boolean success = this.toolbelt.addTool(this.tool);
    assertTrue(success);
  }

  @Test
  public void shouldNotAddTwoToolsForOneJob()
  {
    boolean success = this.toolbelt.addTool(this.tool);
    assertTrue(success);
    success = this.toolbelt.addTool(this.tool);
    assertFalse(success);
  }

  @Test
  public void shouldNotAddToolWithoutNecessaryQualification()
  {
    MFTool difficultToUse = new MFTool("Difficult Tool", job, channel,
                  MFEToolLevel.GRANDMASTER, jobSkillWoodBase, jobSkillWoodMax);
    boolean success = this.toolbelt.addTool(difficultToUse);
    assertFalse(success);
  }

  @Test
  public void shouldAddToolOfLowestToolLevelToUnexperiencedUser()
  {
    MFCommunicationChannel digChannel = mock(MFCommunicationChannel.class);
    when(digChannel.getName()).thenReturn(MFEJob.DIGGING.toString());

    MFTool easyToUse = new MFTool("Simple Tool", MFEJob.DIGGING, digChannel,
                    MFEToolLevel.APPRENTICE, jobSkillWoodBase, jobSkillWoodMax);
    boolean success = this.toolbelt.addTool(easyToUse);
    assertTrue(success);
  }

  @Test
  public void shouldRegisterWithJobChannel()
  {
    this.toolbelt.addTool(this.tool);
    verify(this.channel).subscribe(this.creature);
  }

  @Test
  public void shouldRemoveTool()
  {
    boolean success = this.toolbelt.addTool(this.tool);
    assertTrue(success);
    MFTool removedTool = this.toolbelt.removeTool(this.tool.getJob());
    assertEquals(this.tool, removedTool);
  }

  @Test
  public void shouldNotRemoveTool()
  {
    MFTool removedTool = this.toolbelt.removeTool(MFEJob.DIGGING);
    assertNull(removedTool);
  }

  @Test
  public void shouldUnsubscribeFromJobChannelOnRemoval()
  {
    boolean success = this.toolbelt.addTool(this.tool);
    assertTrue(success);
    MFTool removedTool = this.toolbelt.removeTool(this.tool.getJob());
    assertEquals(this.tool, removedTool);
    verify(this.channel).unsubscribe(this.creature);
  }

  @Test
  public void shouldGetToolSkill()
  {
    assertEquals(this.toolSkillDigging, this.toolbelt.getToolSkill(MFEJob.DIGGING));
    assertEquals(this.toolSkillWoodcutting, this.toolbelt.getToolSkill(MFEJob.WOODCUTTING));
  }

  @Test
  public void shouldGetJobSkill()
  {
    this.toolbelt.addTool(tool);

    int expJobSkill = this.jobSkillWoodBase;
    int gotJobSkill = this.toolbelt.getJobSkill(job);
    assertEquals(expJobSkill, gotJobSkill);
  }

  @Test
  public void shouldGainJobExperiencePlus50Percent()
  {
    this.toolbelt.addTool(tool);

    float toolSkillPlus50 = (MFEToolLevel.MASTER.maxXp+1 - MFEToolLevel.MASTER.minXp)/2;
    this.toolbelt.gainJobExperience(job, (int) toolSkillPlus50);

    float halfwayMark = (jobSkillWoodMax - jobSkillWoodBase)/2;
    int expJobSkill = (int)(jobSkillWoodBase + halfwayMark);
    int gotJobSkill = this.toolbelt.getJobSkill(job);
    assertEquals(expJobSkill, gotJobSkill);
  }

  @Test
  public void shouldGainJobExperiencePlus75Percent()
  {
    this.toolbelt.addTool(tool);

    float toolSkillPlus75 = (MFEToolLevel.MASTER.maxXp+1 - MFEToolLevel.MASTER.minXp)*3/4;
    this.toolbelt.gainJobExperience(job, (int) toolSkillPlus75);

    float threequarter = (jobSkillWoodMax - jobSkillWoodBase)*3/4;
    int expJobSkill = (int)(jobSkillWoodBase + threequarter);
    int gotJobSkill = this.toolbelt.getJobSkill(job);
    assertEquals(expJobSkill, gotJobSkill);
  }

  @Test
  public void shouldGainJobExperiencePlus100Percent()
  {
    this.toolbelt.addTool(tool);

    int dinggrats = MFEToolLevel.MASTER.maxXp+1 - MFEToolLevel.MASTER.minXp;
    this.toolbelt.gainJobExperience(job, dinggrats);

    int expJobSkill = this.jobSkillWoodMax;
    int gotJobSkill = this.toolbelt.getJobSkill(job);
    assertEquals(expJobSkill, gotJobSkill);
  }

  @Test
  public void shouldGetToolSkillLevel()
  {
    this.toolbelt.addTool(tool);

    MFEToolLevel expLevel = MFEToolLevel.levelOf(this.toolSkillDigging);
    MFEToolLevel gotLevel = this.toolbelt.getToolSkillLevel(MFEJob.DIGGING);
    assertEquals(expLevel, gotLevel);
    
    expLevel = MFEToolLevel.levelOf(this.toolSkillWoodcutting);
    gotLevel = this.toolbelt.getToolSkillLevel(MFEJob.WOODCUTTING);
    assertEquals(expLevel, gotLevel);
  }

}