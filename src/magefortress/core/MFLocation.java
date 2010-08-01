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
package magefortress.core;

import java.util.logging.Logger;

/**
 * A 3-dimensional position
 */
public class MFLocation
{
  public final int x, y, z;

  public static final MFLocation NOWHERE = new MFLocation(-1, -1, -1);

  public MFLocation(int _x, int _y, int _z)
  {
    this.x = _x;
    this.y = _y;
    this.z = _z;
  }

  public MFLocation(MFLocation _other)
  {
    this(_other.x, _other.y, _other.z);
  }

  public boolean isNeighborOf(MFLocation _other)
  {
    return !this.equals(NOWHERE) && !_other.equals(NOWHERE) && distanceTo(_other) == 1;
  }

  public int distanceTo(MFLocation _other)
  {
    int x_distance = Math.abs(x - _other.x);
    int y_distance = Math.abs(y - _other.y);
    int z_distance = Math.abs(z - _other.z);
    int result = Math.max(x_distance, y_distance);
    result = Math.max(result, z_distance);
    return result;
  }

  /**
   * Calculates the direction of one location to another one a plain.
   * @param _other the location to get the direction to
   * @return which way to go to reach the other location. Returns <code>null</code>
   * if the other location is the same as this location.
   */
  public MFEDirection directionOf(MFLocation _other)
  {
    if (_other == null) {
      String msg = "Location: Cannot find out direction of null location.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }

    if (this.equals(_other)) {
      return null;
    }

    if (this.equals(NOWHERE) || _other.equals(NOWHERE)) {
      return null;
    }

    MFEDirection result = null;
    
    if (_other.x == this.x && _other.y < this.y) {
      result = MFEDirection.N;
    } else if (_other.x > this.x && _other.y < this.y) {
      result = MFEDirection.NE;
    } else if (_other.x > this.x && _other.y == this.y) {
      result = MFEDirection.E;
    } else if (_other.x > this.x && _other.y > this.y) {
      result = MFEDirection.SE;
    } else if (_other.x == this.x && _other.y > this.y) {
      result = MFEDirection.S;
    } else if (_other.x < this.x && _other.y > this.y) {
      result = MFEDirection.SW;
    } else if (_other.x < this.x && _other.y == this.y) {
      result = MFEDirection.W;
    } else if (_other.x < this.x && _other.y < this.y) {
      result = MFEDirection.NW;
    }

    return result;
  }

  @SuppressWarnings("fallthrough")
  public MFLocation locationOf(MFEDirection _dir)
  {
    int xDiff=0, yDiff=0, zDiff=0;
    
    switch (_dir) {
      case NE: xDiff= 1;
      case N : yDiff=-1; break;
      case SE: yDiff= 1;
      case E : xDiff= 1; break;
      case SW: xDiff=-1;
      case S : yDiff= 1; break;
      case NW: yDiff=-1;
      case W : xDiff=-1; break;
    }

    MFLocation result = new MFLocation(x+xDiff, y+yDiff, z+zDiff);

    return result;
  }

  @Override
  public String toString()
  {
    return "" + x + "/" + y + "/" + z;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    
    final MFLocation other = (MFLocation) obj;
    if (this.x != other.x || y != other.y || this.z != other.z) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 41 * hash + this.x;
    hash = 41 * hash + this.y;
    hash = 41 * hash + this.z;
    return hash;
  }
  //---vvv---      PRIVATE METHODS      ---vvv---
  private final static Logger logger = Logger.getLogger(MFLocation.class.getName());
}
