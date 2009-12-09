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

/**
 *
 * 
 */
public class MFTile implements MFIPaintable
{
  /** Saves the type of corner */
  public enum Corner {NONE,VERTICAL,HORIZONTAL,BOTH};
  /** Size of one tile. */
  public final static int TILESIZE = 48;

  /**
   * The type of corner is calculated according to the presence of the
   * surrounding walls.
   * @Transient
   */
  private Corner cornerNW, cornerNE, cornerSE, cornerSW;

  /**
   * Empty constructor
   */
  public MFTile() {}

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
    setWalls(_wallN, _wallE, _wallS, _wallW);
    this.floor = _floor;
    this.isUnderground = _isUnderground;
    this.cornerNE = Corner.NONE;
    this.cornerNW = Corner.NONE;
    this.cornerSE = Corner.NONE;
    this.cornerSW = Corner.NONE;
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
    boolean isFirstRow = (this.posY == 0);
    boolean isFirstColumn = (this.posX == 0);
    boolean isLastRow = (this.posY+1 == tiles[0].length);
    boolean isLastColumn = (this.posX+1 == tiles.length);

    boolean _wallN = isFirstRow || this.hasWallNorth();
    boolean _wallE = isLastColumn || this.hasWallEast();
    boolean _wallS = isLastRow || this.hasWallSouth();
    boolean _wallW = isFirstColumn || this.hasWallEast();
    boolean wallAboveW = isFirstRow || isFirstColumn || tiles[posX][posY-1].hasWallWest();
    boolean wallAboveE = isFirstRow || tiles[posX][posY-1].hasWallEast();
    boolean wallRightN = isFirstRow || isLastColumn || tiles[posX+1][posY].hasWallNorth();
    boolean wallRightS = isLastColumn || tiles[posX+1][posY].hasWallSouth();
    boolean wallBelowE = isLastRow || tiles[posX][posY+1].hasWallEast();
    boolean wallBelowW = isFirstColumn || isLastRow || tiles[posX][posY+1].hasWallWest();
    boolean wallLeftS = isFirstColumn || tiles[posX-1][posY].hasWallSouth();
    boolean wallLeftN = isFirstRow || isFirstColumn || tiles[posX-1][posY].hasWallNorth();

    // NW
    if (_wallN && _wallW)
    {
      setCornerNW(Corner.NONE);
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
      setCornerNE(Corner.NONE);
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
      setCornerSE(Corner.NONE);
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
      setCornerSW(Corner.NONE);
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

  public void update()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void paint(Graphics2D _g, int _x_translation, int _y_translation)
  {
    if (this.isUnderground()) {
      _g.setColor(Color.GREEN);
    } else if (this.isDugOut()){
      _g.setColor(Color.LIGHT_GRAY);
    } else {
      _g.setColor(Color.BLACK);
    }

    _g.fillRect(posX*TILESIZE + _x_translation,
                posY*TILESIZE + _y_translation,
                TILESIZE, TILESIZE);

    //TODO paint objects placed on the tile
  }
  
  //---vvv---      PRIVATE METHODS      ---vvv---
  private int posX, posY, posZ;
  private boolean isUnderground;
  private boolean isDugOut;
  private boolean wallN, wallE, wallS, wallW;
  private boolean floor;

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

}
