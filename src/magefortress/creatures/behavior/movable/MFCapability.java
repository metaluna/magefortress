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
package magefortress.creatures.behavior.movable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import magefortress.core.Immutable;

/**
 * The capability of creature consists of one or more {@link MFEMovementType}s.
 * It is immutable.
 */
public class MFCapability implements Immutable, Iterable<MFEMovementType>
{
  public static final MFCapability WALK = new MFCapability(MFEMovementType.WALK);
  public static final MFCapability FLY = new MFCapability(MFEMovementType.FLY);
  public static final MFCapability WALK_FLY = new MFCapability(MFEMovementType.WALK, MFEMovementType.FLY);
  public static final MFCapability NONE = new MFCapability();


  public MFCapability(MFEMovementType... _capabilities)
  {
    if (_capabilities.length > 0) {
      this.capabilities = EnumSet.copyOf(Arrays.asList(_capabilities));
    } else {
      this.capabilities = EnumSet.noneOf(MFEMovementType.class);
    }
  }

  public boolean containsAll(MFCapability _other)
  {
    return this.capabilities.containsAll(_other.capabilities);
  }

  @Override
  public Iterator<MFEMovementType> iterator()
  {
    return this.capabilities.iterator();
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final EnumSet<MFEMovementType> capabilities;

}
