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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFEToolLevelTest
{

  @Before
  public void setUp()
  {
  }

  @Test
  public void shouldConvertFromIntegers()
  {
    assertEquals(MFEToolLevel.NONE, MFEToolLevel.levelOf(0));
    assertEquals(MFEToolLevel.APPRENTICE, MFEToolLevel.levelOf(1));
    assertEquals(MFEToolLevel.APPRENTICE, MFEToolLevel.levelOf(99));
    assertEquals(MFEToolLevel.JOURNEYMAN, MFEToolLevel.levelOf(100));
    assertEquals(MFEToolLevel.JOURNEYMAN, MFEToolLevel.levelOf(299));
    assertEquals(MFEToolLevel.MASTER, MFEToolLevel.levelOf(300));
    assertEquals(MFEToolLevel.MASTER, MFEToolLevel.levelOf(599));
    assertEquals(MFEToolLevel.GRANDMASTER, MFEToolLevel.levelOf(600));
    assertEquals(MFEToolLevel.GRANDMASTER, MFEToolLevel.levelOf(999));
    assertEquals(MFEToolLevel.LEVEL5, MFEToolLevel.levelOf(1000));
    assertEquals(MFEToolLevel.LEVEL5, MFEToolLevel.levelOf(1499));
    assertEquals(MFEToolLevel.LEVEL6, MFEToolLevel.levelOf(1500));
    assertEquals(MFEToolLevel.LEVEL6, MFEToolLevel.levelOf(2100));

  }

}