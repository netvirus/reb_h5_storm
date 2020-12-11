package l2r.gameserver.data.xml.parser;

import l2r.gameserver.model.L2PremiumBonus;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.utils.IXmlReader;
import l2r.gameserver.utils.IXmlStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class contains a set of different premium subscription options
 * @author netvirus
 */

public class PremiumSystemOptionsData implements IXmlReader, IXmlStreamReader {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumSystemOptionsData.class);

    private final Map<Integer, L2PremiumBonus> _premiumBonusList = new LinkedHashMap<>();

    public PremiumSystemOptionsData() { load(); }

    @Override
    public void load() {
        _premiumBonusList.clear();
        parseDatapackFile("config/premium/premiumSystemOptionsData.xml");
        LOG.info("Loaded {} Premium system profiles.", _premiumBonusList.size());
    }

    @Override
    public void parseDocument(Document doc) {
        nodeListStreamFilteredByName(doc.getFirstChild().getChildNodes(), "premium").forEach(n -> {
            final StatsSet set = new StatsSet();
            attributeStream(n.getAttributes()).forEach(a -> set.set(a.getNodeName(), a.getNodeValue()));
            Map<String, String> columnParam = new LinkedHashMap<>();
            columnParam.put("rates", "rates");
            columnParam.put("time", "time");
            columnParam.put("price", "price");
            nodeListStreamFilteredByMap(n.getChildNodes(), columnParam)
                    .forEach(p -> attributeStream(p.getAttributes()).forEach(k -> set.set(k.getNodeName(), k.getNodeValue())));
            _premiumBonusList.put(set.getInt("id"), new L2PremiumBonus(set));
        });
    }

    public static PremiumSystemOptionsData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public L2PremiumBonus findById(int bonus_id) {
        return _premiumBonusList.get(bonus_id);
    }

    private static class SingletonHolder {
        protected static final PremiumSystemOptionsData INSTANCE = new PremiumSystemOptionsData();
    }
}
