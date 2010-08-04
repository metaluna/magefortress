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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFNullInstrumentableTest
{
  private MFIInstrumentable instrumentable;

  @Before
  public void setUp()
  {
    this.instrumentable = new MFNullInstrumentable();
  }

  @Test
  public void shouldNotUseTools()
  {
    assertFalse(this.instrumentable.canUseTools());
  }

  @Test
  public void shouldNotAddTool()
  {
    MFTool mockTool = mock(MFTool.class);
    assertFalse(this.instrumentable.addTool(mockTool));
  }

  @Test
  public void shouldNotRemoveTool()
  {
    for (MFEJob job : MFEJob.values()) {
      assertNull(this.instrumentable.removeTool(job));
    }
  }

  @Test
  public void shouldGetZeroToolSkill()
  {
    for (MFEJob job : MFEJob.values()) {
      assertEquals(0, this.instrumentable.getToolSkill(job));
    }
  }

  @Test
  public void shouldGetZeroJobSkill()
  {
    for (MFEJob job : MFEJob.values()) {
      assertEquals(0, this.instrumentable.getJobSkill(job));
    }
  }

  @Test
  public void shouldHaveToolSkillLevelNone()
  {
    assertEquals(MFEToolLevel.NONE, this.instrumentable.getToolSkillLevel(MFEJob.DIGGING));
  }

}