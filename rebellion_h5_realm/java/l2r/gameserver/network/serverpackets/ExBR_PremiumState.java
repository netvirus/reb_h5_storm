package l2r.gameserver.network.serverpackets;

import l2r.gameserver.model.Player;

public class ExBR_PremiumState extends L2GameServerPacket
{
	private int _objectId;
	private int _state;

	public ExBR_PremiumState(int objectId, boolean state)
	{
		_objectId = objectId;
		_state = state ? 1 : 0;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xD9);
		writeD(_objectId);
		writeC(_state);
	}
}
