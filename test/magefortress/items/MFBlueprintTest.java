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
package magefortress.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import magefortress.items.placeable.MFIPlaceable;
import magefortress.items.placeable.MFPlaceableMock;
import magefortress.items.placeable.MFUnplaceable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFBlueprintTest
{
  private MFBlueprint blueprint;

  @Before
  public void setUp()
  {
    this.blueprint = new MFBlueprint(42, "Test print");
  }

  //---vvv---      CONSTRUCTOR TESTS        ---vvv---
  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutName()
  {
    new MFBlueprint(-1, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithEmtpyName()
  {
    new MFBlueprint(-1, "");
  }

  //---vvv---          METHOD TESTS        ---vvv---
  @Test
  public void shouldGetId()
  {
    int expId = 42;
    int gotId = this.blueprint.getId();
    assertEquals(expId, gotId);
  }

  @Test
  public void shouldSetId()
  {
    int expId = 23;
    this.blueprint.setId(expId);

    int gotId = this.blueprint.getId();
    assertEquals(expId, gotId);
  }

  @Test
  public void shouldGetName()
  {
    String expName = "Test print";
    String gotName = this.blueprint.getName();
    assertEquals(expName, gotName);
  }

  @Test
  public void shouldAddMaterial()
  {
    MFBlueprint material = mock(MFBlueprint.class);
    this.blueprint.addMaterial(material);
    
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAddNullMaterial()
  {
    this.blueprint.addMaterial(null);
  }

  @Test
  public void shouldGetMaterials()
  {
    MFBlueprint material1 = mock(MFBlueprint.class);
    this.blueprint.addMaterial(material1);
    MFBlueprint material2 = mock(MFBlueprint.class);
    this.blueprint.addMaterial(material2);
    MFBlueprint material3 = mock(MFBlueprint.class);
    this.blueprint.addMaterial(material3);

    List<MFBlueprint> expMaterials = new ArrayList<MFBlueprint>(Arrays.asList(material1, material2, material3));
    List<MFBlueprint> gotMaterials = this.blueprint.getMaterials();
    assertEquals(expMaterials, gotMaterials);
  }

  @Test
  public void shouldSetPlacingBehavior()
  {
    Class<? extends MFIPlaceable> placeable = MFPlaceableMock.class;
    this.blueprint.setPlacingBehavior(placeable);

    assertEquals(placeable, this.blueprint.getPlacingBehavior());
  }

  @Test
  public void shouldSetPlacingBehaviorByString() throws ClassNotFoundException
  {
    Class<? extends MFIPlaceable> placeable = MFPlaceableMock.class;
    String className = placeable.getName();
    this.blueprint.setPlacingBehavior(className);

    assertEquals(placeable, this.blueprint.getPlacingBehavior());
  }

  @Test(expected=ClassNotFoundException.class)
  public void shouldNotSetPlacingBehaviorToFaultyString() throws ClassNotFoundException
  {
    this.blueprint.setPlacingBehavior("magefortress.nowhere.NonExistingPlaceable");
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotSetPlacingBehaviorToWrongInterface() throws ClassNotFoundException
  {
    String className = MFBlueprint.class.getName();
    this.blueprint.setPlacingBehavior(className);
  }

  @Test
  public void shouldHaveDefaultPlacingBehavior()
  {
    Class<? extends MFIPlaceable> expClass = MFUnplaceable.class;
    Class<? extends MFIPlaceable> gotClass = this.blueprint.getPlacingBehavior();
    assertEquals(expClass, gotClass);
  }

  @Test
  public void shouldCreateItem()
  {
    MFItem item = this.blueprint.createItem();
    assertEquals(this.blueprint.getName(), item.getName());
    assertEquals(this.blueprint, item.getBlueprint());
  }

  @Test
  public void shouldCreatePlaceableItem()
  {
    MFIPlaceable placeable = new MFPlaceableMock();
    this.blueprint.setPlacingBehavior(placeable.getClass());

    MFItem item = this.blueprint.createItem();
    assertTrue(item.isPlaceable());
  }

}