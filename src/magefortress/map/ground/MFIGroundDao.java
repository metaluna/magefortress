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
package magefortress.map.ground;

import magefortress.storage.MFIDao;

public interface MFIGroundDao extends MFIDao<MFGround>
{
  // filename parts separator
  static final String SEPARATOR = "_";
  // sprite name suffixes - basename is the name of the blueprint
  static final String SPRITE_SOLID = "solid";
  static final String SPRITE_FLOOR = "floor";
  static final String SPRITE_WALL_N = "wall_n";
  static final String SPRITE_WALL_E = "wall_e";
  static final String SPRITE_WALL_S = "wall_s";
  static final String SPRITE_WALL_W = "wall_w";
  static final String SPRITE_CORNER_N_HORIZ = "corner_n_horiz";
  static final String SPRITE_CORNER_S_HORIZ = "corner_s_horiz";
  static final String SPRITE_CORNER_E_VERT  = "corner_e_vert";
  static final String SPRITE_CORNER_W_VERT  = "corner_w_vert";
  static final String SPRITE_CORNER_NE_IN   = "corner_ne_in";
  static final String SPRITE_CORNER_NE_OUT  = "corner_ne_out";
  static final String SPRITE_CORNER_SE_IN   = "corner_se_in";
  static final String SPRITE_CORNER_SE_OUT  = "corner_se_out";
  static final String SPRITE_CORNER_SW_IN   = "corner_sw_in";
  static final String SPRITE_CORNER_SW_OUT  = "corner_sw_out";
  static final String SPRITE_CORNER_NW_IN   = "corner_nw_in";
  static final String SPRITE_CORNER_NW_OUT  = "corner_nw_out";
}
