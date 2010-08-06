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
package magefortress.channel;

import magefortress.core.MFLocation;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class MFCommunicationChannelTest {

  private MFCommunicationChannel testChannel;
  private MFIChannelSubscriber mockSubscriber;
  private MFIChannelSender mockSender;
  private MFChannelMessage mockMessage;

  @Before
  public void setUp()
  {
    testChannel = new MFCommunicationChannel("Test Channel");

    mockSubscriber = mock(MFIChannelSubscriber.class);

    mockSender = mock(MFIChannelSender.class);
    when(mockSender.getLocation()).thenReturn(new MFLocation(1,2,3));

    mockMessage = mock(MFChannelMessage.class);
    when(mockMessage.getSender()).thenReturn(mockSender);

  }

  @Test
  public void shouldNotifySubscribers()
  {
    testChannel.subscribe(mockSubscriber);
    testChannel.enqueueMessage(mockMessage);
    testChannel.update();

    verify(mockSubscriber).update(mockMessage);
  }

  @Test
  public void shouldNotNotifyUnsubscribedSubscribers()
  {
    testChannel.subscribe(mockSubscriber);
    testChannel.unsubscribe(mockSubscriber);
    testChannel.enqueueMessage(mockMessage);

    verify(mockSubscriber, never()).update(mockMessage);
  }

  @Test
  public void shouldNotNotifySubscribersOfUnsubscribedSenders()
  {
    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.enqueueMessage(mockMessage);
    testChannel.unsubscribeSender(mockSender);
    testChannel.update();

    verify(mockSubscriber, never()).update(mockMessage);
  }

  @Test
  public void shouldNotifySendersOfNewSubscribers()
  {
    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.update();

    verify(mockSender).newSubscriber(mockSubscriber);
  }

  @Test
  public void shouldNotNotifyUnsubscribedSendersOfNewSubscribers()
  {
    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.unsubscribeSender(mockSender);
    testChannel.update();

    verify(mockSender, never()).newSubscriber(mockSubscriber);
  }

  @Test
  public void shouldNotNotifySendersOfUnsubscribedSubscribers()
  {
    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.unsubscribe(mockSubscriber);
    testChannel.update();

    verify(mockSender, never()).newSubscriber(mockSubscriber);

  }

  @Test
  public void shouldRemoveMessagesFromQueue()
  {
    testChannel.subscribe(mockSubscriber);
    testChannel.enqueueMessage(mockMessage);

    testChannel.update();
    verify(mockSubscriber).update(mockMessage);

    testChannel.update();
    verifyNoMoreInteractions(mockSubscriber);
  }

  @Test
  public void shouldRemoveNewSubscribersFromQueue()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    mockMessage = mock(MFChannelMessage.class);
    mockSender = mock(MFIChannelSender.class);

    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);

    testChannel.update();
    verify(mockSender).newSubscriber(mockSubscriber);

    testChannel.update();
    verifyNoMoreInteractions(mockSender);
  }
}