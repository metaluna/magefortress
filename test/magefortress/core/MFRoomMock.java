package magefortress.core;

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
}
