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
package magefortress.items.placeable;

/**
 * Used for items like tables, chairs, and beds which can be freely placed by
 * the player unlike {@link MFIEquipable}s or {@link MFIEatable}s.
 */
public interface MFIPlaceable
{
  /**
   * Gets wether the item can be placed by the player.
   * @return <code>true</code> when the item is placeable.
   */
  public boolean isPlaceable();
  /**
   * Places the item at its current position. If it cannot be placed because the
   * tile is already occupied or the item is not a Placeable it returns
   * <code>false</code>.
   */
  public boolean setPlaced(boolean _placed);
  /**
   * Gets wether the item was placed and is ready to be used. If the item is not
   * placeable it returns <code>false</code>.
   * @return <code>true</code> if the item has been placed.
   */
  public boolean isPlaced();
  /**
   * Gets the item's living value. If the item is not placeable it returns
   * <code>0</code>.
   * @return The item's living value.
   */
  public int getLivingValue();
}
