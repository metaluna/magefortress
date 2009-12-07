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

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class MFCommunicationChannelTest {

  private MFCommunicationChannel testChannel;
  private MFIChannelSubscriber mockSubscriber;
  private MFIChannelSender mockSender;
  private MFChannelMessage testMessage;

  @Before
  public void setUp()
  {
  }

  @Test
  public void shouldNotifySubscribers()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    testChannel.subscribe(mockSubscriber);

    testMessage = mock(MFChannelMessage.class);

    testChannel.enqueueMessage(testMessage);
    testChannel.update();

    verify(mockSubscriber).update(testMessage);
  }

  @Test
  public void shouldNotNotifyUnsubscribedSubscribers()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    testChannel.subscribe(mockSubscriber);
    testChannel.unsubscribe(mockSubscriber);
    testChannel.enqueueMessage(testMessage);

    verify(mockSubscriber, never()).update(testMessage);
  }

  @Test
  public void shouldNotNotifySubscribersOfUnsubscribedSenders()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    mockSender = mock(MFIChannelSender.class);
    testMessage = new MFChannelMessage(mockSender);

    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.enqueueMessage(testMessage);
    testChannel.unsubscribeSender(mockSender);
    testChannel.update();

    verify(mockSubscriber, never()).update(testMessage);
  }

  @Test
  public void shouldNotifySendersOfNewSubscribers()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    mockSender = mock(MFIChannelSender.class);

    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.update();

    verify(mockSender).newSubscriber(mockSubscriber);
  }

  @Test
  public void shouldNotNotifyUnsubscribedSendersOfNewSubscribers()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    mockSender = mock(MFIChannelSender.class);

    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.unsubscribeSender(mockSender);
    testChannel.update();

    verify(mockSender, never()).newSubscriber(mockSubscriber);
  }

  @Test
  public void shouldNotNotifySendersOfUnsubscribedSubscribers()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    mockSender = mock(MFIChannelSender.class);

    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);
    testChannel.unsubscribe(mockSubscriber);
    testChannel.update();

    verify(mockSender, never()).newSubscriber(mockSubscriber);

  }

  @Test
  public void shouldRemoveMessagesFromQueue()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    testMessage = mock(MFChannelMessage.class);

    testChannel.subscribe(mockSubscriber);
    testChannel.enqueueMessage(testMessage);

    testChannel.update();
    verify(mockSubscriber).update(testMessage);

    testChannel.update();
    verifyNoMoreInteractions(mockSubscriber);
  }

  @Test
  public void shouldRemoveNewSubscribersFromQueue()
  {
    testChannel = new MFCommunicationChannel("Test Channel");
    mockSubscriber = mock(MFIChannelSubscriber.class);
    testMessage = mock(MFChannelMessage.class);
    mockSender = mock(MFIChannelSender.class);

    testChannel.subscribeSender(mockSender);
    testChannel.subscribe(mockSubscriber);

    testChannel.update();
    verify(mockSender).newSubscriber(mockSubscriber);

    testChannel.update();
    verifyNoMoreInteractions(mockSender);
  }
}