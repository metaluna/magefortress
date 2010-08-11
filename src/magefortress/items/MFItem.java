/*
 *  Copyright (c) 2009 Simon Hardijanto
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
package magefortress.items;

import magefortress.items.placeable.MFIPlaceable;
import magefortress.jobs.MFBlueprint;
import magefortress.jobs.MFIProducible;

/**
 * Base class for all items
 */
public class MFItem implements MFIPlaceable, MFIProducible
{
  private String name;

  public MFItem(String _name)
  {
    this.name = _name;
  }
  
  public String getName()
  {
    return this.name;
  }

  //---vvv---     PLACEABLE INTERFACE METHODS     ---vvv---
  @Override
  public boolean isPlaceable()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean setPlaced(boolean _placed)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isPlaced()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getLivingValue()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //---vvv---     PRODUCIBLE INTERFACE METHODS     ---vvv---
  @Override
  public MFBlueprint getBlueprint()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //---vvv---      PRIVATE METHODS      ---vvv---

}
