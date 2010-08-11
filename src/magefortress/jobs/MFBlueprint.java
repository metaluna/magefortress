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
package magefortress.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import magefortress.items.MFItem;

/**
 * The class holds information about materials needed to produce an item.
 */
public class MFBlueprint
{
  /**
   * Constructor
   * @param _product The item which will be produced
   */
  public MFBlueprint(MFIProducible _product)
  {
    if (_product == null) {
      String msg = "Blueprint: Cannot create blueprint without product.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.product = _product;
    this.materials = new ArrayList<MFItem>();
  }

  /**
   * An ingredient needed to produce the item.
   * @param _material The ingredient
   */
  public void addMaterial(MFItem _material)
  {
    if (_material == null) {
      String msg = "Blueprint: Cannot add null material.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    this.materials.add(_material);
  }

  public List<MFItem> getMaterials()
  {
    return Collections.unmodifiableList(this.materials);
  }

  public MFIProducible getProduct()
  {
    return this.product;
  }

  public MFItem createItem()
  {
    MFItem result = new MFItem("TODO");

    return result;
  }


  //---vvv---      PRIVATE METHODS      ---vvv---
  /** Log */
  private static final Logger logger = Logger.getLogger(MFBlueprint.class.getName());
  /** The product */
  private final MFIProducible product;
  /** The ingredients */
  private final ArrayList<MFItem> materials;

}
