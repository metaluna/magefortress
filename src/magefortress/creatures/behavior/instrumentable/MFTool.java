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
package magefortress.creatures.behavior.instrumentable;

import java.util.logging.Logger;
import magefortress.channel.MFCommunicationChannel;
import magefortress.core.Immutable;

/**
 * Representation of a tool. Only holds data.
 */
public final class MFTool implements Immutable
{
  public MFTool(String _name, MFEJob _job, MFCommunicationChannel _channel,
                  MFEToolLevel _level, int _baseEfficiency, int _maxEfficiency)
  {
    validateConstructorParams(_name, _job, _channel, _level, _baseEfficiency, _maxEfficiency);

    this.name = _name;
    this.job = _job;
    this.channel = _channel;
    this.level = _level;
    this.baseEfficiency = _baseEfficiency;
    this.maxEfficiency = _maxEfficiency;
  }

  public String getName()
  {
    return this.name;
  }

  public MFEJob getJob()
  {
    return this.job;
  }

  public MFCommunicationChannel getChannel()
  {
    return this.channel;
  }

  public MFEToolLevel getToolLevel()
  {
    return this.level;
  }

  public int getBaseEfficiency()
  {
    return this.baseEfficiency;
  }

  public int getMaxEfficiency()
  {
    return this.maxEfficiency;
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFTool.class.getName());

  private final String name;
  private final MFEJob job;
  private final int maxEfficiency;
  private final int baseEfficiency;
  private final MFEToolLevel level;
  private final MFCommunicationChannel channel;

  private void validateConstructorParams(String _name, MFEJob _job,
                          MFCommunicationChannel _channel, MFEToolLevel _level,
                          int _baseEfficiency, int _maxEfficiency)
  {
    if (_name == null || _name.equals("")) {
      String msg = this.getClass().getSimpleName() + ": Cannot create tool "
                                                          + "without a name.";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_job == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
                                                  _name + "' without job type";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_channel == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
                                                  _name + "' without channel";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (!_channel.getName().equals(_job.toString())) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
                                _name + "' if job channel does not match job." +
                                _channel.getName() + " vs " + _job.toString();
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_level == null) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
                                                  _name + "' without tool level";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_level == MFEToolLevel.NONE) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
                                                  _name + "' with tool level 'NONE'";
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_baseEfficiency < 0) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
              _name + "' with negative base efficiency. Is " + _baseEfficiency;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_maxEfficiency < 0) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" +
                _name + "' with negative max efficiency. Is " + _maxEfficiency;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_maxEfficiency < _baseEfficiency) {
      String msg = this.getClass().getSimpleName() + ": Cannot create a '" + 
                _name + "' with max efficiency smaller than base efficiency. " +
                _maxEfficiency + "<" + _baseEfficiency;
      logger.severe(msg);
      throw new IllegalArgumentException(msg);
    }
  }


}
