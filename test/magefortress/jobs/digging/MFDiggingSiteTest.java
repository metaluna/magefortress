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
package magefortress.jobs.digging;

import magefortress.channel.*;
import magefortress.core.MFLocation;
import magefortress.core.MFPrerequisitesNotMetException;
import magefortress.jobs.MFAssignableJob;
import magefortress.jobs.MFIConstructionSiteListener;
import magefortress.jobs.MFJobFactory;
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
  private MFMap map;
  private MFMap unreachableMockMap;
  private MFJobFactory mockJobFactory;
  private MFLocation location;
  private MFCommunicationChannel mockChannel;
  private MFCommunicationChannel unreachableMockChannel;
  private MFIConstructionSiteListener mockSiteListener;

  @Before
  public void setUp() throws Exception
  {

    this.mockChannel = mock(MFCommunicationChannel.class);
    this.location = new MFLocation(0, 0, 0);
    MFMap realMap = new MFMap(-1, 5, 5, 1);
    realMap.digOut(location);
    realMap.digOut(new MFLocation(1,0,0));
    realMap.digOut(new MFLocation(0,1,0));
    realMap.digOut(new MFLocation(1,1,0));
    this.map = spy(realMap);

    this.mockJobFactory = mock(MFJobFactory.class);
    MFAssignableJob job = mock(MFAssignableJob.class);
    when(this.mockJobFactory.createDiggingJob(any(MFDiggingSite.class))).thenReturn(job);

    this.mockSiteListener = mock(MFIConstructionSiteListener.class);

    this.diggingSite = new MFDiggingSite(this.location, this.map,
          this.mockJobFactory, this.mockChannel, this.mockSiteListener);

    // unreachable digging site
    unreachableMockChannel = mock(MFCommunicationChannel.class);
    unreachableMockMap = mock(MFMap.class);
    when(unreachableMockMap.getWidth()).thenReturn(2);
    when(unreachableMockMap.getHeight()).thenReturn(2);
    when(unreachableMockMap.getDepth()).thenReturn(2);
    MFTile undergroundTile = new MFTile(-1,0,0,0);
    when(unreachableMockMap.getTile(any(MFLocation.class))).thenReturn(undergroundTile);

    unreachableDiggingSite = new MFDiggingSite(location, unreachableMockMap, 
         mockJobFactory, unreachableMockChannel, this.mockSiteListener);

  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutLocation()
  {
    new MFDiggingSite(null, map, mockJobFactory, mockChannel, mockSiteListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationWidth()
  {
    MFLocation invalidLocation = new MFLocation(5, 0, 0);
    
    new MFDiggingSite(invalidLocation, map, mockJobFactory, mockChannel, mockSiteListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationHeight()
  {
    MFLocation invalidLocation = new MFLocation(0, 5, 0);

    new MFDiggingSite(invalidLocation, map, mockJobFactory, mockChannel, mockSiteListener);
  }


  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithIllegalLocationDepth()
  {
    MFLocation invalidLocation = new MFLocation(0, 0, 5);

    new MFDiggingSite(invalidLocation, map, mockJobFactory, mockChannel, mockSiteListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutMap()
  {
    new MFDiggingSite(mock(MFLocation.class), null, mockJobFactory, mockChannel, mockSiteListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutJobFactory()
  {
    new MFDiggingSite(mock(MFLocation.class), map, null, mockChannel, mockSiteListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutCommunicationChannel()
  {
    new MFDiggingSite(mock(MFLocation.class), map, mockJobFactory, null, mockSiteListener);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotCreateWithoutConstructionSiteListener()
  {
    new MFDiggingSite(mock(MFLocation.class), map, mockJobFactory, mockChannel, null);
  }

  @Test
  public void shouldCheckReachabilityDuringConstruction()
  {
    verify(this.map).getTile(any(MFLocation.class));
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

  @Test(expected=MFPrerequisitesNotMetException.class)
  public void shouldNotGiveOutJobIfAlreadyAssigned()
  {
    // given a job was assigned
    MFAssignableJob job1 = diggingSite.getJob();

    // when someone else tries to get the job
    MFAssignableJob job2 = diggingSite.getJob();

    // then an exception should be thrown
  }

  @Test
  public void shoulSubscribeToChannelIfReachable()
  {
    verify(mockChannel).subscribeSender(diggingSite);
  }

  @Test
  public void shouldNotifyConstructionSiteListenerWhenDone()
  {
    // given a job was assigned
    MFAssignableJob job = this.diggingSite.getJob();

    // when the job finishes
    this.diggingSite.jobDone(job);

    // then notify the construction site listener
    verify(mockSiteListener).constructionSiteFinished(diggingSite);

  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAcceptFinishedJobWhenNull()
  {
    // given a job was assigned
    MFAssignableJob job = this.diggingSite.getJob();

    // when a null job is reported as finished
    this.diggingSite.jobDone(null);

    // then an exception should be thrown
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldNotAcceptFinishedJobIfItsDifferentFromAssignedJob()
  {
    // given a job was assigned
    MFAssignableJob job = this.diggingSite.getJob();

    // when a job different from the job assigned is reported finished
    this.diggingSite.jobDone(mock(MFAssignableJob.class));

    // then an exception should be thrown
  }

  @Ignore
  @Test
  public void shouldSubscribeToChannelIfBecomesReachable()
  {
    
  }

}