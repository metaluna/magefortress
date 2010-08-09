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
package magefortress.jobs.digging;

import java.util.Set;
import magefortress.core.MFRoom;
import magefortress.map.MFTile;

/**
 * Room for mining ore
 */
public class MFQuarry extends MFRoom
{
  public MFQuarry(String _name, Set<MFTile> _tiles)
  {
    super(_name, _tiles);
  }

  public int getMiningEfficiency()
  {
    return this.miningEfficiency;
  }

  //---vvv---       ROOM METHODS       ---vvv---
  @Override
  protected void tileAdded(MFTile _tile)
  {
    this.addProduct(_tile.getGround().getBlueprint());
  }

  @Override
  protected void tileRemoved(MFTile _tile)
  {
    this.removeProduct(_tile.getGround().getBlueprint());
  }

  @Override
  protected void tileUpdated()
  {
    //noop - unless ground transformation is possible
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private int miningEfficiency;
}
