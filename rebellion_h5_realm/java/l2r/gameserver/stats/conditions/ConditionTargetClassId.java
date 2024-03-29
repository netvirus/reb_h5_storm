package l2r.gameserver.stats.conditions;

import l2r.gameserver.model.Player;
import l2r.gameserver.stats.Env;

public class ConditionTargetClassId extends Condition
{
	private final int[] _classIds;

	public ConditionTargetClassId(String[] ids)
	{
		_classIds = new int[ids.length];
		for(int i = 0; i < ids.length; i++)
			_classIds[i] = Integer.parseInt(ids[i]);
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer() || !env.target.isPlayer())
			return false;

		int targetClassId = ((Player) env.target).getActiveClassId();
		for(int id : _classIds)
			if(targetClassId == id)
				return true;

		return false;
	}
}