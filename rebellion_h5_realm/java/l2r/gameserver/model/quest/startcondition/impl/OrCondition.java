package l2r.gameserver.model.quest.startcondition.impl;

import l2r.gameserver.model.Player;
import l2r.gameserver.model.quest.startcondition.ConditionList;
import l2r.gameserver.model.quest.startcondition.ICheckStartCondition;

public class OrCondition implements ICheckStartCondition {
    ICheckStartCondition[] conditions;

    public OrCondition(ICheckStartCondition... conds) {
        conditions = conds;
    }

    @Override
    public ConditionList checkCondition(Player player) {
        for (ICheckStartCondition cond : conditions) {
            if (cond.checkCondition(player) == ConditionList.NONE) {
                return ConditionList.NONE;
            }
        }
        return ConditionList.OR;
    }
}