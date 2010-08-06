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

/**
 *
 */
public enum MFEToolLevel
{
  NONE(0,0),
  APPRENTICE(1,99),
  JOURNEYMAN(100,299),
  MASTER(300,599),
  GRANDMASTER(600,999),
  LEVEL5(1000,1499),
  LEVEL6(1500,2100);

  //---vvv---       PUBLIC METHODS        ---vvv---
  public final int minXp;
  public final int maxXp;

  public static MFEToolLevel levelOf(int _xp)
  {
    MFEToolLevel result = NONE;
    
    for (MFEToolLevel level : values()) {
      if (_xp <= level.maxXp) {
        result = level;
        break;
      }
    }

    return result;
  }

  //---vvv---       PRIVATE METHODS       ---vvv---
  
  private MFEToolLevel(int _minXp, int _maxXp)
  {
    this.minXp = _minXp;
    this.maxXp = _maxXp;
  }

}
