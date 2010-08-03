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
package magefortress.channel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MFChannelFactory
{
  public static MFChannelFactory getInstance()
  {
    return MFChannelFactory.instance;
  }

  /**
   * Makes sure that there's always only one channel for a given channel type.
   * @param _channelType The channel type
   * @return The communication channel
   */
  public MFCommunicationChannel getChannel(MFEChannel _channelType)
  {
    MFCommunicationChannel channel = this.channels.get(_channelType);
    return channel;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final MFChannelFactory instance = new MFChannelFactory();
  private final Map<MFEChannel, MFCommunicationChannel> channels;

  private MFChannelFactory()
  {
    this.channels = new HashMap<MFEChannel, MFCommunicationChannel>();
    for (MFEChannel channelType : MFEChannel.values()) {
      this.channels.put(channelType, new MFCommunicationChannel(channelType.toString()));
    }
  }

}
