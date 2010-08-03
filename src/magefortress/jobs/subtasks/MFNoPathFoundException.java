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
package magefortress.jobs.subtasks;

import magefortress.core.MFLocation;

/**
 *
 */
public class MFNoPathFoundException extends MFSubtaskCanceledException
{

  public MFNoPathFoundException(String _msg, MFLocation _start, MFLocation _goal)
  {
    super(_msg);
    this.start = _start;
    this.goal = _goal;
  }

  public MFNoPathFoundException(String _msg, MFLocation _start, MFLocation _goal, Throwable _t)
  {
    super(_msg, _t);
    this.start = _start;
    this.goal = _goal;
  }

  public MFLocation getStart()
  {
    return this.start;
  }

  public MFLocation getGoal()
  {
    return this.goal;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFLocation start;
  private final MFLocation goal;
}
