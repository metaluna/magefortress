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

/**
 * Encapsulates a message. At the moment only notifications about available jobs
 * are sent over the communication channel.
 * @see MFCommunicationChannel
 * @see MFIChannelSender
 * @see MFIChannelSubscriber
 */
public class MFChannelMessage
{
  /** The object which sent the message */
  private MFIChannelSender sender;

  /**
   * Constructor
   * @param _sender The object which sent the message.
   */
  public MFChannelMessage(MFIChannelSender _sender)
  {
    this.sender = _sender;
  }

  /**
   * The sender of the message
   * @return The sender of the message
   */
  public MFIChannelSender getSender()
  {
    return this.sender;
  }
}
