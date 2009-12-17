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

import magefortress.channel.*;
import magefortress.core.MFLocation;
import magefortress.map.MFMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDiggingSiteTest
{
  private MFDiggingSite diggingSite;
  private MFMap mockMap;
  private MFLocation location;

  @Before
  public void setUp() throws Exception
  {
    this.location = new MFLocation(0, 0, 0);
    this.mockMap = mock(MFMap.class);
    when(this.mockMap.getWidth()).thenReturn(1);
    when(this.mockMap.getHeight()).thenReturn(1);
    when(this.mockMap.getDepth()).thenReturn(1);
    this.diggingSite = new MFDiggingSite(this.location, this.mockMap);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutLocation()
  {
    new MFDiggingSite(null, mock(MFMap.class));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationWidth()
  {
    MFLocation invalidLocation = new MFLocation(5, 0, 0);
    mockMap = mock(MFMap.class);
    when(mockMap.getWidth()).thenReturn(1);
    when(mockMap.getHeight()).thenReturn(1);
    when(mockMap.getDepth()).thenReturn(1);
    
    new MFDiggingSite(invalidLocation, mockMap);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationHeight()
  {
    MFLocation invalidLocation = new MFLocation(0, 5, 0);
    mockMap = mock(MFMap.class);
    when(mockMap.getWidth()).thenReturn(1);
    when(mockMap.getHeight()).thenReturn(1);
    when(mockMap.getDepth()).thenReturn(1);

    new MFDiggingSite(invalidLocation, mockMap);
  }


  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationDepth()
  {
    MFLocation invalidLocation = new MFLocation(0, 0, 5);
    mockMap = mock(MFMap.class);
    when(mockMap.getWidth()).thenReturn(1);
    when(mockMap.getHeight()).thenReturn(1);
    when(mockMap.getDepth()).thenReturn(1);

    new MFDiggingSite(invalidLocation, mockMap);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFDiggingSite(mock(MFLocation.class), null);
  }

  @Test
  public void shouldGetJobIfAvailable()
  {
    assertTrue(this.diggingSite.isJobAvailable());

    MFJob gotJob = this.diggingSite.getJob();
    assertNotNull(gotJob);
    assertEquals(this.diggingSite, gotJob.getSender());
    assertFalse(this.diggingSite.isJobAvailable());
  }

  @Test
  public void shouldNotGetJobIfNotAvailable()
  {
    assertTrue(this.diggingSite.isJobAvailable());

    MFJob gotJob = this.diggingSite.getJob();
    assertNotNull(gotJob);
    assertFalse(this.diggingSite.isJobAvailable());

    gotJob = this.diggingSite.getJob();
    assertNull(gotJob);
  }

  @Test
  public void shouldSendJobOfferToNewSubscriber()
  {
    MFIChannelSubscriber subscriber = mock(MFIChannelSubscriber.class);

    this.diggingSite.newSubscriber(subscriber);
    ArgumentCaptor<MFChannelMessage> gotMessage = ArgumentCaptor.forClass(MFChannelMessage.class);
    verify(subscriber).update(gotMessage.capture());
    assertEquals(this.diggingSite, gotMessage.getValue().getSender());
  }

  @Test
  public void shouldNotSendJobOfferToNewSubscriber()
  {
    // grab the job before new interested creature subscribes
    MFJob gotJob = this.diggingSite.getJob();
    assertNotNull(gotJob);
    assertFalse(this.diggingSite.isJobAvailable());

    MFIChannelSubscriber subscriber = mock(MFIChannelSubscriber.class);

    this.diggingSite.newSubscriber(subscriber);
    verify(subscriber, never()).update(any(MFChannelMessage.class));
  }

}