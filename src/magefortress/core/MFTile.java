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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class MFTile implements MFIPaintable
{
  /** Saves the type of corner */
  public enum Corner {NONE,VERTICAL,HORIZONTAL,BOTH,INWARD};
  /** Size of one tile. */
  public final static int TILESIZE = 48;

  /**
   * The type of corner is calculated according to the presence of the
   * surrounding walls.
   * @Transient
   */
  private Corner cornerNW, cornerNE, cornerSE, cornerSW;

  /**
   * Convenience constructor creating an underground tile with no walls.
   * @param _posX Position on the map
   * @param _posY Position on the map
   * @param _posZ Position on the map
   */
  public MFTile(int _posX, int _posY, int _posZ)
  {
    this(_posX, _posY, _posZ, false,false,false,false,false,true,true);
  }
  /**
   * Full constructor
   * @param _posX Position on the map
   * @param _posY Position on the map
   * @param _posZ Position on the map
   * @param _dugOut Was it dug out or is it still solid?
   * @param _wallN Wall status
   * @param _wallE Wall status
   * @param _wallS Wall status
   * @param _wallW Wall status
   * @param _floor Floor status
   * @param _isUnderground Is or was it underground?
   */
  public MFTile(int _posX, int _posY, int _posZ, boolean _isDugOut,
          boolean _wallN, boolean _wallE, boolean _wallS, boolean _wallW,
          boolean _floor, boolean _isUnderground)
  {
    this.posX = _posX;
    this.posY = _posY;
    this.posZ = _posZ;
    this.isDugOut = _isDugOut;
    this.isUnderground = _isUnderground;
    setWalls(_wallN, _wallE, _wallS, _wallW);
    this.floor = _floor;
    this.cornerNE = Corner.NONE;
    this.cornerNW = Corner.NONE;
    this.cornerSE = Corner.NONE;
    this.cornerSW = Corner.NONE;

    // init clearance values
    this.clearanceValues = new EnumMap<MFEMovementType,Integer>(MFEMovementType.class);
  }

  /**
   * Calculates the types of corner.
   * Only horizontal wall on the tile -> horizontal corner
   * Only vertical wall on the tile -> vertical corner
   * No adjacent walls on the tile
   * AND no adjacent walls on surrounding tiles -> no corner
   * Otherwise -> both
   */
  public void calculateCorners(MFTile[][] tiles)
  {
    if (!this.isDugOut) {
      logger.log(Level.WARNING, "Tile " + this.toString() + 
                                ": Trying to calculate corners on solid tile");
      return;
    }
    boolean isFirstRow = (this.posY == 0);
    boolean isFirstColumn = (this.posX == 0);
    boolean isLastRow = (this.posY == tiles[0].length-1);
    boolean isLastColumn = (this.posX == tiles.length-1);
    
    MFTile neighborN = null;
    if (!isFirstRow) {
      neighborN = tiles[this.getPosX()][this.getPosY()-1];
    }
    MFTile neighborE = null;
    if (!isLastColumn) {
      neighborE = tiles[this.getPosX()+1][this.getPosY()];
    }
    MFTile neighborS = null;
    if (!isLastRow) {
      neighborS = tiles[this.getPosX()][this.getPosY()+1];
    }
    MFTile neighborW = null;
    if (!isFirstColumn) {
      neighborW = tiles[this.getPosX()-1][this.getPosY()];
    }

    boolean _wallN = isFirstRow || this.hasWallNorth();
    boolean _wallE = isLastColumn || this.hasWallEast();
    boolean _wallS = isLastRow || this.hasWallSouth();
    boolean _wallW = isFirstColumn || this.hasWallWest();
    boolean wallAboveW = isFirstRow ||
                          (neighborN.isDugOut() && neighborN.hasWallWest());
    boolean wallAboveE = isFirstRow ||
                          (neighborN.isDugOut() && neighborN.hasWallEast());
    boolean wallRightN = isLastColumn ||
                          (neighborE.isDugOut() && neighborE.hasWallNorth());
    boolean wallRightS = isLastColumn ||
                          (neighborE.isDugOut() && neighborE.hasWallSouth());
    boolean wallBelowE = isLastRow ||
                          (neighborS.isDugOut() && neighborS.hasWallEast());
    boolean wallBelowW = isLastRow ||
                          (neighborS.isDugOut() && neighborS.hasWallWest());
    boolean wallLeftS  = isFirstColumn ||
                          (neighborW.isDugOut() && neighborW.hasWallSouth());
    boolean wallLeftN  = isFirstColumn ||
                          (neighborW.isDugOut() && neighborW.hasWallNorth());

    // NW
    if (_wallN && _wallW)
    {
      setCornerNW(Corner.INWARD);
    }
    else if (_wallN)
    {
      setCornerNW(Corner.HORIZONTAL);
    }
    else if (_wallW)
    {
      setCornerNW(Corner.VERTICAL);
    }
    else if (wallLeftN || wallAboveW)
    {
      setCornerNW(Corner.BOTH);
    }
    else
    {
      setCornerNW(Corner.NONE);
    }

    // NE
    if (_wallN && _wallE)
    {
      setCornerNE(Corner.INWARD);
    }
    else if (_wallN)
    {
      setCornerNE(Corner.HORIZONTAL);
    }
    else if (_wallE)
    {
      setCornerNE(Corner.VERTICAL);
    }
    else if (wallAboveE || wallRightN)
    {
      setCornerNE(Corner.BOTH);
    }
    else
    {
      setCornerNE(Corner.NONE);
    }
    
    // SE
    if (_wallS && _wallE)
    {
      setCornerSE(Corner.INWARD);
    }
    else if (_wallS)
    {
      setCornerSE(Corner.HORIZONTAL);
    }
    else if (_wallE)
    {
      setCornerSE(Corner.VERTICAL);
    }
    else if (wallRightS || wallBelowE)
    {
      setCornerSE(Corner.BOTH);
    }
    else
    {
      setCornerSE(Corner.NONE);
    }
    
    // SW
    if (_wallS && _wallW)
    {
      setCornerSW(Corner.INWARD);
    }
    else if (_wallS)
    {
      setCornerSW(Corner.HORIZONTAL);
    }
    else if (_wallW)
    {
      setCornerSW(Corner.VERTICAL);
    }
    else if (wallLeftS || wallBelowW)
    {
      setCornerSW(Corner.BOTH);
    }
    else
    {
      setCornerSW(Corner.NONE);
    }

  }

  public int getPosX()
  {
    return posX;
  }

  public int getPosY()
  {
    return posY;
  }

  public int getPosZ()
  {
    return posZ;
  }

  public boolean isDugOut()
  {
    return isDugOut;
  }

  public void setDugOut(boolean dugOut)
  {
    this.isDugOut = dugOut;
  }

  public boolean isUnderground()
  {
    return this.isUnderground;
  }

  public boolean hasWallNorth()
  {
    return wallN;
  }

  public void setWallNorth(boolean wallNorth)
  {
    setWalls(wallNorth, wallE, wallS, wallW);
  }

  public boolean hasWallEast()
  {
    return wallE;
  }

  public void setWallEast(boolean wallEast)
  {
    setWalls(wallN, wallEast, wallS, wallW);
  }

  public boolean hasWallSouth()
  {
    return wallS;
  }

  public void setWallSouth(boolean wallSouth)
  {
    setWalls(wallN, wallE, wallSouth, wallW);
  }

  public boolean hasWallWest()
  {
    return wallW;
  }

  public void setWallWest(boolean wallWest)
  {
    setWalls(wallN, wallE, wallS, wallWest);
  }

  public void setWalls(boolean n, boolean e, boolean s, boolean w)
  {
    this.wallN = n;
    this.wallE = e;
    this.wallS = s;
    this.wallW = w;
  }

  public boolean hasFloor()
  {
    return floor;
  }

  public void setFloor(boolean floor)
  {
    this.floor = floor;
  }

  public Corner getCornerNW()
  {
    return cornerNW;
  }

  public Corner getCornerNE()
  {
    return cornerNE;
  }

  public Corner getCornerSE()
  {
    return cornerSE;
  }

  public Corner getCornerSW()
  {
    return cornerSW;
  }

  public void setClearance(MFEMovementType _movementType, int _clearance)
  {
    this.clearanceValues.put(_movementType, _clearance);
  }

  public int getClearance(MFEMovementType _movementType)
  {
    Integer result = this.clearanceValues.get(_movementType);
    if (result == null) {
      String msg = "Tile " + this.toString() +
                    ": Must set clearance value for " + _movementType.toString() +
                    " before trying to get it.";
      logger.log(Level.WARNING, msg);
      throw new IllegalArgumentException(msg);
    }
    return result;
  }

  boolean isWalkable(MFEMovementType _movementType)
  {
    switch (_movementType) {
      case WALK : return isDugOut();
      case FLY  : return isDugOut();
      default   : return false;
    }
  }

  public void update()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    final int x = posX * TILESIZE + _x_translation;
    final int y = posY * TILESIZE + _y_translation;
    // paint floor
    if (this.isUnderground()) {
      if (!this.hasFloor()) {
        _g.setColor(Color.BLUE); 
      } else if (this.isDugOut()) {
        _g.setColor(Color.LIGHT_GRAY);
      } else {
        _g.setColor(Color.BLACK);
      }
    } else {
      _g.setColor(Color.GREEN);
    }

    _g.fillRect(x, y, TILESIZE, TILESIZE);

    if (this.isDugOut()) {
      // paint walls
      _g.setColor(Color.DARK_GRAY);
      final int wallLength = TILESIZE-WALL_WIDTH*2;

      if (this.hasWallNorth()) {
        _g.fillRect(x + WALL_WIDTH, y, wallLength, WALL_WIDTH);
      }
      if (this.hasWallEast()) {
        _g.fillRect(x + wallLength + WALL_WIDTH, y + WALL_WIDTH, WALL_WIDTH, wallLength);
      }
      if (this.hasWallSouth()) {
        _g.fillRect(x + WALL_WIDTH, y + wallLength + WALL_WIDTH, wallLength, WALL_WIDTH);
      }
      if (this.hasWallWest()) {
        _g.fillRect(x, y + WALL_WIDTH, WALL_WIDTH, wallLength);
      }

      // paint corners
//      if (this.cornerNE == Corner.HORIZONTAL) {
//        _g.fillRect(x + wallLength + WALL_WIDTH, y, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerNE == Corner.VERTICAL) {
//        _g.fillRect(x + wallLength + WALL_WIDTH, y, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerNE == Corner.NONE) {
//        _g.fillRect(x + wallLength + WALL_WIDTH, y, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerNE == Corner.BOTH) {
//        _g.fillArc(x + wallLength + WALL_WIDTH, y - WALL_WIDTH,
//                  WALL_WIDTH*2, WALL_WIDTH*2, 180, 90);
//      }
//
//      if (this.cornerSE == Corner.HORIZONTAL) {
//        _g.fillRect(x + wallLength + WALL_WIDTH, y + wallLength + WALL_WIDTH,
//                    WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerSE == Corner.VERTICAL) {
//        _g.fillRect(x + wallLength + WALL_WIDTH, y + wallLength + WALL_WIDTH,
//                    WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerSE == Corner.NONE) {
//        _g.fillRect(x + wallLength + WALL_WIDTH, y + wallLength + WALL_WIDTH,
//                    WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerSE == Corner.BOTH) {
//        _g.fillArc(x + wallLength + WALL_WIDTH, y + wallLength + WALL_WIDTH,
//                  WALL_WIDTH*2, WALL_WIDTH*2, 180, -90);
//      }
//
//      if (this.cornerSW == Corner.HORIZONTAL) {
//        _g.fillRect(x, y + wallLength + WALL_WIDTH, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerSW == Corner.VERTICAL) {
//        _g.fillRect(x, y + wallLength + WALL_WIDTH, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerSW == Corner.NONE) {
//        _g.fillRect(x, y + wallLength + WALL_WIDTH, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerSW == Corner.BOTH) {
//        _g.fillArc(x - WALL_WIDTH, y + wallLength + WALL_WIDTH,
//                  WALL_WIDTH*2, WALL_WIDTH*2, 0, 90);
//      }
//
//      if (this.cornerNW == Corner.HORIZONTAL) {
//        _g.fillRect(x, y, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerNW == Corner.VERTICAL) {
//        _g.fillRect(x, y, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerNW == Corner.NONE) {
//        _g.fillRect(x, y, WALL_WIDTH, WALL_WIDTH);
//      } else if (this.cornerNW == Corner.BOTH) {
//        _g.fillArc(x - WALL_WIDTH, y - WALL_WIDTH,
//                  WALL_WIDTH*2, WALL_WIDTH*2, 0, -90);
//      }


      this.paintCorner(_g, x + wallLength + WALL_WIDTH, y, this.cornerNE, MFEDirection.NE);
      this.paintCorner(_g, x + wallLength + WALL_WIDTH, y + wallLength + WALL_WIDTH, this.cornerSE, MFEDirection.SE);
      this.paintCorner(_g, x, y + wallLength + WALL_WIDTH, this.cornerSW, MFEDirection.SW);
      this.paintCorner(_g, x, y, this.cornerNW, MFEDirection.NW);

    }


    //TODO paint objects placed on the tile
  }

  @Override
  public String toString()
  {
    return "" + posX + "/" + posY + "/" + posZ;
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private final int WALL_WIDTH = 12;
  private int posX, posY, posZ;
  private boolean isUnderground;
  private boolean isDugOut;
  private boolean wallN, wallE, wallS, wallW;
  private boolean floor;
  private EnumMap<MFEMovementType, Integer> clearanceValues;
  private static Logger logger = Logger.getLogger(MFTile.class.getName());

  private void setPosX(int posX)
  {
    this.posX = posX;
  }

  private void setPosY(int posY)
  {
    this.posY = posY;
  }

  private void setPosZ(int posZ)
  {
    this.posZ = posZ;
  }

  private void setCornerNW(Corner cornerNW)
  {
    this.cornerNW = cornerNW;
  }

  private void setCornerNE(Corner cornerNE)
  {
    this.cornerNE = cornerNE;
  }

  private void setCornerSE(Corner cornerSE)
  {
    this.cornerSE = cornerSE;
  }

  private void setCornerSW(Corner cornerSW)
  {
    this.cornerSW = cornerSW;
  }

  private void paintCorner(Graphics2D _g, int _x, int _y, Corner _corner, MFEDirection _direction)
  {
      if (_corner == Corner.HORIZONTAL) {
        _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
      } else if (_corner == Corner.VERTICAL) {
        _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
      } else if (_corner == Corner.INWARD) {
        _g.fillRect(_x, _y, WALL_WIDTH, WALL_WIDTH);
      } else if (_corner == Corner.BOTH) {
        int startAngle = 0;
        if (_direction == MFEDirection.NE || _direction == MFEDirection.SE) {
          startAngle = 180;
        }

        int arc = 90;
        if (_direction == MFEDirection.NW || _direction == MFEDirection.SE) {
          arc *= -1;
        }

        if (_direction == MFEDirection.NW || _direction == MFEDirection.SW) {
          _x -=WALL_WIDTH;
        }
        if (_direction == MFEDirection.NW || _direction == MFEDirection.NE) {
          _y -=WALL_WIDTH;
        }

        _g.fillArc(_x, _y, WALL_WIDTH*2, WALL_WIDTH*2, startAngle, arc);
      }
  }
}
