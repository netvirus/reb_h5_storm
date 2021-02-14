package l2r.gameserver.model.quest.startcondition.impl;

import l2r.gameserver.model.Player;
import l2r.gameserver.model.base.Race;
import l2r.gameserver.model.quest.startcondition.ConditionList;
import l2r.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.apache.commons.lang3.ArrayUtils;

public final class RaceCondition implements ICheckStartCondition {
    private final Race[] race;

    public RaceCondition(final Race... race) {
        this.race = race;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        if (ArrayUtils.contains(race, player.getRace()))
            return ConditionList.NONE;
        return ConditionList.RACE;
    }
}
