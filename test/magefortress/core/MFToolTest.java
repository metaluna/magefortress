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
package magefortress.core;

import magefortress.channel.MFCommunicationChannel;
import magefortress.creatures.behavior.MFEJob;
import magefortress.creatures.behavior.MFEToolLevel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFToolTest
{
  private MFTool tool;
  private String name;
  private MFEJob job;
  private MFCommunicationChannel channel;
  private MFEToolLevel toolLevel;
  private int baseEfficiency;
  private int maxEfficiency;

  @Before
  public void setUp()
  {
    this.name = "Test Tool";
    this.job = MFEJob.DIGGING;
    this.channel = new MFCommunicationChannel(job.toString());
    this.toolLevel = MFEToolLevel.JOURNEYMAN;
    this.baseEfficiency = 150;
    this.maxEfficiency = 200;
    this.tool = new MFTool(name, job, channel, toolLevel, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutName()
  {
    new MFTool(null, job, channel, toolLevel, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithEmptyName()
  {
    new MFTool("", job, channel, toolLevel, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutJob()
  {
    new MFTool(name, null, channel, toolLevel, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutChannel()
  {
    new MFTool(name, job, null, toolLevel, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateIfJobChannelDoesNotMatchJob()
  {
    new MFTool(name, MFEJob.WOODCUTTING, channel, toolLevel, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutToolLevel()
  {
    new MFTool(name, job, channel, null, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithToolLevelOfNone()
  {
    new MFTool(name, job, channel, MFEToolLevel.NONE, baseEfficiency, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithNegativeBaseEfficiency()
  {
    new MFTool(name, job, channel, toolLevel, -1, maxEfficiency);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithNegativeMaxEfficiency()
  {
    new MFTool(name, job, channel, toolLevel, baseEfficiency, -1);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithBaseBiggerMaxEfficiency()
  {
    new MFTool(name, job, channel, toolLevel, maxEfficiency, baseEfficiency);
  }

  @Test
  public void shouldGetName()
  {
    assertEquals(this.name, this.tool.getName());
  }

  @Test
  public void shouldGetJob()
  {
    assertEquals(this.job, this.tool.getJob());
  }

  @Test
  public void shouldGetChannel()
  {
    MFCommunicationChannel gotChannel = this.tool.getChannel();
    assertEquals(channel, gotChannel);
    assertEquals(gotChannel.getName(), this.tool.getJob().toString());
  }

  @Test
  public void shouldGetJobLevel()
  {
    assertEquals(this.toolLevel, this.tool.getToolLevel());
  }

  @Test
  public void shouldGetBaseEfficiency()
  {
    assertEquals(this.baseEfficiency, this.tool.getBaseEfficiency());
  }

  @Test
  public void shouldGetMaxEfficiency()
  {
    assertEquals(this.maxEfficiency, this.tool.getMaxEfficiency());
  }

}