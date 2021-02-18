package l2r.gameserver.model.quest.startcondition.impl;

import l2r.gameserver.model.Player;
import l2r.gameserver.model.quest.startcondition.ConditionList;
import l2r.gameserver.model.quest.startcondition.ICheckStartCondition;

public final class QuestCompletedCondition implements ICheckStartCondition {
    private final String _questSimpleName;

    public QuestCompletedCondition(final String questSimpleName) {
        _questSimpleName = questSimpleName;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        if (player.isQuestCompleted(_questSimpleName))
            return ConditionList.NONE;
        return ConditionList.QUEST;
    }
}
