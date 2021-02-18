package l2r.gameserver.model.quest.startcondition.impl;

import l2r.gameserver.model.Player;
import l2r.gameserver.model.items.ItemInstance;
import l2r.gameserver.model.quest.startcondition.ConditionList;
import l2r.gameserver.model.quest.startcondition.ICheckStartCondition;

public final class HasItemCondition implements ICheckStartCondition {
    int[] itemIds;

    public HasItemCondition(int... ids) {
        itemIds = ids;
    }

    @Override
    public ConditionList checkCondition(Player player) {
        for (int id : itemIds) {
            ItemInstance item = player.getInventory().getItemByItemId(id);
            if (item == null || item.getCount() < 1)
                return ConditionList.ITEM;
        }
        return ConditionList.NONE;
    }
}