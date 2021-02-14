package l2r.gameserver.model.quest.startcondition.impl;

import l2r.gameserver.model.Player;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.quest.startcondition.ConditionList;
import l2r.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.apache.commons.lang3.ArrayUtils;

public final class ClassIdCondition implements ICheckStartCondition {
    private ClassId[] classId;

    public ClassIdCondition(final ClassId... classId) {
        this.classId = classId;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        if (ArrayUtils.contains(classId, player.getBaseClassId()))
            return ConditionList.NONE;
        return ConditionList.CLASS_ID;
    }
}
