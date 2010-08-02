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

import magefortress.jobs.subtasks.*;
import magefortress.core.MFItem;
import magefortress.core.MFWorkshop;
import magefortress.map.MFMap;

/**
 * Produces a single item.
 */
public class MFProduceItemJob extends MFAssignableJob
{
  public MFProduceItemJob(final MFWorkshop _sender, final MFMap _map, final MFBlueprint _blueprint)
  {
    super(_sender);
    this.map = _map;
    this.blueprint = _blueprint;
  }

  @Override
  protected void initJob()
  {
    MFWorkshop workshop = (MFWorkshop) this.getSender();

    for (MFItem material : this.blueprint.getMaterials()) {
      MFSubtask locateMaterial = new MFLocateSimilarItemSubtask(this.getOwner(), material);
      MFSubtask gotoMaterial   = new MFGotoLocationSubtask(this.map, this.getOwner());
      MFSubtask pickupMaterial = new MFPickupItemSubtask(this.getOwner());
      MFSubtask gotoWorkshop   = new MFGotoLocationSubtask(this.map, this.getOwner(), workshop.getLocation());
      MFSubtask dropMaterial   = new MFPutDraggedItemSubtask(this.getOwner(), workshop);
      this.addSubtask(locateMaterial);
      this.addSubtask(gotoMaterial);
      this.addSubtask(pickupMaterial);
      this.addSubtask(gotoWorkshop);
      this.addSubtask(dropMaterial);
    }

    MFSubtask produceItem = new MFProduceItemSubtask(this.getOwner(), workshop, this.blueprint);
    this.addSubtask(produceItem);
  }

  @Override
  public void pauseJob()
  {
    //TODO check how many materials where brought to the workshop and put more
    // dragging jobs in the queue accordingly
  }

  @Override
  public void cancelJob()
  {
    //TODO cancel current action according to which subtask is active
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private final MFMap map;
  private final MFBlueprint blueprint;

}
