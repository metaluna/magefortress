package magefortress.core;

import magefortress.channel.MFIChannelSubscriber;
import magefortress.jobs.MFAssignableJob;
import magefortress.map.MFTile;
import java.util.Set;

public class MFRoomMock extends MFRoom
{

  public MFRoomMock(Set<MFTile> _tiles)
  {
    super("Test Room", _tiles);
  }

  public void tileAdded(MFTile _tile)
  {
  }

  public void tileRemoved(MFTile _tile)
  {
  }

  public void tileUpdated()
  {
  }

  public boolean isJobAvailable()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MFAssignableJob getJob()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MFLocation getLocation()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void newSubscriber(MFIChannelSubscriber subscriber)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void jobDone(MFAssignableJob _finishedJob)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
