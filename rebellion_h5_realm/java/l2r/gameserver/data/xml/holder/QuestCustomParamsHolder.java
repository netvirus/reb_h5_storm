package l2r.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2r.commons.data.xml.AbstractHolder;
import l2r.gameserver.templates.QuestCustomParams;

import java.util.Optional;

/**
 * @author netvirus
 */

public class QuestCustomParamsHolder extends AbstractHolder {
    public static QuestCustomParamsHolder instance;
    private final TIntObjectHashMap<QuestCustomParams> holder = new TIntObjectHashMap<>();

    public static QuestCustomParamsHolder getInstance() {
        return instance == null ? instance = new QuestCustomParamsHolder() : instance;
    }

    public void add(QuestCustomParams params) {
        holder.put(params.getId(), params);
    }

    public Optional<QuestCustomParams> get(int questId) {
        return Optional.ofNullable(holder.get(questId));
    }

    public TIntObjectHashMap<QuestCustomParams> getHolder() {
        return holder;
    }

    public int size() {
        return holder.size();
    }

    @Override
    public void clear() {
        holder.clear();
    }
}
