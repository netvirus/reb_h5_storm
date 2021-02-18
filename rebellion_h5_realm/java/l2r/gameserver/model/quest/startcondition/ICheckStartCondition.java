package l2r.gameserver.model.quest.startcondition;

import l2r.gameserver.model.Player;

public interface ICheckStartCondition {
    ConditionList checkCondition(Player player);
}
