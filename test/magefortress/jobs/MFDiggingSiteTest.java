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
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.map.MFMap;
import magefortress.map.MFTile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MFDiggingSiteTest
{
  private MFDiggingSite diggingSite;
  private MFDiggingSite unreachableDiggingSite;
  private MFMap mockMap;
  private MFMap unreachableMockMap;
  private MFJobFactory mockJobFactory;
  private MFLocation location;
  private MFCommunicationChannel mockChannel;
  private MFCommunicationChannel unreachableMockChannel;

  @Before
  public void setUp() throws Exception
  {
    this.mockChannel = mock(MFCommunicationChannel.class);
    this.location = new MFLocation(0, 0, 0);
    this.mockMap = mock(MFMap.class);
    when(this.mockMap.getWidth()).thenReturn(2);
    when(this.mockMap.getHeight()).thenReturn(2);
    when(this.mockMap.getDepth()).thenReturn(2);
    MFTile tile = new MFTile(-1, 1, 0, 0, true, true, true, false, false, true, true);
    when(mockMap.getTile(new MFLocation(1,0,0))).thenReturn(tile);
    tile = new MFTile(-1, 0, 1, 0, true, false, false, true, true, true, true);
    when(mockMap.getTile(new MFLocation(0,1,0))).thenReturn(tile);
    tile = new MFTile(-1, 1, 1, 0, true, false, true, true, true, true, true);
    when(mockMap.getTile(new MFLocation(1,1,0))).thenReturn(tile);

    this.mockJobFactory = mock(MFJobFactory.class);
    MFAssignableJob job = mock(MFAssignableJob.class);
    when(this.mockJobFactory.createDiggingJob(any(MFDiggingSite.class))).thenReturn(job);

    this.diggingSite = new MFDiggingSite(this.location, this.mockMap, this.mockJobFactory, this.mockChannel);

    // unreachable digging site
    unreachableMockChannel = mock(MFCommunicationChannel.class);
    unreachableMockMap = mock(MFMap.class);
    when(unreachableMockMap.getWidth()).thenReturn(2);
    when(unreachableMockMap.getHeight()).thenReturn(2);
    when(unreachableMockMap.getDepth()).thenReturn(2);
    MFTile undergroundTile = new MFTile(-1,0,0,0);
    when(unreachableMockMap.getTile(any(MFLocation.class))).thenReturn(undergroundTile);

    unreachableDiggingSite = new MFDiggingSite(location, unreachableMockMap, mockJobFactory, unreachableMockChannel);

  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutLocation()
  {
    new MFDiggingSite(null, mockMap, mockJobFactory, mockChannel);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationWidth()
  {
    MFLocation invalidLocation = new MFLocation(5, 0, 0);
    
    new MFDiggingSite(invalidLocation, mockMap, mockJobFactory, mockChannel);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationHeight()
  {
    MFLocation invalidLocation = new MFLocation(0, 5, 0);

    new MFDiggingSite(invalidLocation, mockMap, mockJobFactory, mockChannel);
  }


  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationDepth()
  {
    MFLocation invalidLocation = new MFLocation(0, 0, 5);

    new MFDiggingSite(invalidLocation, mockMap, mockJobFactory, mockChannel);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFDiggingSite(mock(MFLocation.class), null, mockJobFactory, mockChannel);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutJobFactory()
  {
    new MFDiggingSite(mock(MFLocation.class), mockMap, null, mockChannel);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutCommunicationChannel()
  {
    new MFDiggingSite(mock(MFLocation.class), mockMap, mockJobFactory, null);
  }

  @Test
  public void shouldCheckReachabilityDuringConstruction()
  {
    verify(mockMap, times(3)).getTile(any(MFLocation.class));
  }

  @Test
  public void shouldGetJobIfAvailable()
  {
    assertTrue(this.diggingSite.isJobAvailable());

    MFAssignableJob gotJob = this.diggingSite.getJob();
    assertNotNull(gotJob);
  }

  @Test
  public void shouldNotGetJobIfNotAvailable()
  {
    assertTrue(this.diggingSite.isJobAvailable());

    MFAssignableJob gotJob = this.diggingSite.getJob();
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
    MFAssignableJob gotJob = this.diggingSite.getJob();
    assertNotNull(gotJob);
    assertFalse(this.diggingSite.isJobAvailable());

    MFIChannelSubscriber subscriber = mock(MFIChannelSubscriber.class);

    this.diggingSite.newSubscriber(subscriber);
    verify(subscriber, never()).update(any(MFChannelMessage.class));
  }

  @Test
  public void shouldSendInitialJobOfferToChannel()
  {
    verify(mockChannel).enqueueMessage(any(MFChannelMessage.class));
  }

  @Test
  public void shouldNotSendInitialJobOfferIfUnreachable()
  {
    verify(unreachableMockChannel, never()).enqueueMessage(any(MFChannelMessage.class));
  }

  @Test
  public void shouldNotBeAvailableIfUnreachable()
  {
    assertFalse(unreachableDiggingSite.isJobAvailable());
  }

  @Test
  public void shouldNotSendJobOfferIfUnreachable()
  {
    MFIChannelSubscriber mockApplier = mock(MFIChannelSubscriber.class);

    unreachableDiggingSite.newSubscriber(mockApplier);
    verify(mockApplier, never()).update(any(MFChannelMessage.class));
  }

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotGiveOutJobIfUnreachable()
  {
    unreachableDiggingSite.getJob();
  }

  @Ignore
  @Test
  public void shouldSubscribeToChannelIfBecomesReachable()
  {
    
  }

}