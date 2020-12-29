package services.community;

import l2r.gameserver.Config;
import l2r.gameserver.dao.ItemsDAO;
import l2r.gameserver.dao.PremiumSystemDAO;
import l2r.gameserver.data.htm.HtmCache;
import l2r.gameserver.data.xml.parser.PremiumSystemOptionsData;
import l2r.gameserver.instancemanager.PremiumSystemManager;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.actor.instances.player.PremiumBonus;

import l2r.gameserver.model.items.ItemInstance;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.HideBoard;
import l2r.gameserver.network.serverpackets.PlaySound;
import l2r.gameserver.network.serverpackets.ShowBoard;
import l2r.gameserver.network.serverpackets.components.NpcString;
import l2r.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class CommunityBoardPremiumAccount {
    public static final Logger _log = LoggerFactory.getLogger(CommunityBoardPremiumAccount.class);

    private CommunityBoardPremiumAccount() {
        // visibility
    }

    public String getAction(Player player, String bypass) {
        String html = "";
        if (bypass.startsWith("list")) {
            html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/index.htm", player);
            StringBuilder builder = new StringBuilder();
            Map<Integer, PremiumBonus> _premiumBonuses = PremiumSystemOptionsData.getInstance().getPremiumBonusList();
            _premiumBonuses.forEach((k, v) -> {
                builder.append("<tr>");
                builder.append("<td width=\"32\"><img src=\"").append(v.getBonusIconName()).append("\" width=\"32\" height=\"32\"></td>");
                builder.append("<td><font color=FF6600 name=\"CreditTextNormal\">").append(v.getBonusName()).append("</font></td>");
                builder.append("<td><button value=\"Подробнее\" action=\"bypass -h premium_show_").append(v.getBonusId()).append("\" width=75 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                builder.append("</tr>");
            });
            html = html.replace("{list}", builder.toString());
        } else if (bypass.startsWith("show_")) {
            String bonusId = bypass.substring(5).trim();
            PremiumBonus premium = PremiumSystemOptionsData.getInstance().findById(Integer.parseInt(bonusId));
            if (premium != null) {
                if (!player.getDoublePremiumState()) {
                    if ((player.getPremiumMainTypeState() && !premium.isBonusMain()) || (player.getPremiumSecondTypeState() && premium.isBonusMain())) {
                        String itemName = ItemsDAO.getInstance().getItemsByItemId(premium.getBonusItemId()).stream().findFirst().get().getName();
                        html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/detail.htm", player);
                        String price = premium.getBonusItemAmount() + " " + itemName;
                        html = html.replace("{bonus_title}", premium.getBonusName());
                        html = html.replace("{bonus_price}", price);
                        // Not have
                        html = html.replace("{xp_n}", String.valueOf(player.getRateExp()));
                        html = html.replace("{sp_n}", String.valueOf(player.getRateSp()));
                        html = html.replace("{drop_n}", String.valueOf(player.getRateItems()));
                        html = html.replace("{drop_chance_n}", "x");
                        html = html.replace("{drop_amount_n}", "x");
                        html = html.replace("{spoil_n}", String.valueOf(player.getRateSpoil()));
                        html = html.replace("{spoil_chance_n}", "x");
                        html = html.replace("{spoil_amount_n}", "x");
                        html = html.replace("{adena_n}", String.valueOf(player.getRateAdena()));
                        html = html.replace("{weight_n}", String.valueOf(player.getWeightPercents()));
                        html = html.replace("{craft_n}", "x");
                        html = html.replace("{m_craft_n}", "x");
                        html = html.replace("{extract_n}", "x");
                        html = html.replace("{manor_n}", String.valueOf(Config.RATE_MANOR));
                        html = html.replace("{quest_n}", String.valueOf(Config.RATE_QUESTS_DROP));
                        html = html.replace("{quest_reward_n}", String.valueOf(Config.RATE_QUESTS_REWARD));
                        html = html.replace("{pet_xp_n}", "x");
                        html = html.replace("{raid_drop_chance_n}", String.valueOf(Config.RATE_DROP_RAIDBOSS));
                        html = html.replace("{raid_drop_amount_n}", "x");
                        html = html.replace("{herb_drop_chance_n}", String.valueOf(Config.RATE_DROP_HERBS));
                        html = html.replace("{herb_drop_amount_n}", "x");
                        // Have
                        html = html.replace("{xp_h}", String.valueOf(player.getRateExp() + premium.getBonusExpRate()));
                        html = html.replace("{sp_h}", String.valueOf(player.getRateSp() + premium.getBonusSpRate()));
                        html = html.replace("{drop_h}", String.valueOf(player.getRateItems() + premium.getBonusDropRate()));
                        html = html.replace("{drop_chance_h}", String.valueOf(premium.getBonusDropChance()));
                        html = html.replace("{drop_amount_h}", String.valueOf(premium.getBonusDropAmount()));
                        html = html.replace("{spoil_h}", String.valueOf(premium.getBonusSpoilRate()));
                        html = html.replace("{spoil_chance_h}", String.valueOf(premium.getBonusSpoilChance()));
                        html = html.replace("{spoil_amount_h}", String.valueOf(premium.getBonusSpoilAmount()));
                        html = html.replace("{adena_h}", String.valueOf(premium.getBonusAdenaDropRate()));
                        html = html.replace("{weight_h}", String.valueOf(premium.getBonusWeightLimitRate()));
                        html = html.replace("{craft_h}", String.valueOf(premium.getBonusCraftChance()));
                        html = html.replace("{m_craft_h}", String.valueOf(premium.getBonusMasterCraftChance()));
                        html = html.replace("{extract_h}", String.valueOf(premium.getBonusExtractableRate()));
                        html = html.replace("{manor_h}", String.valueOf(premium.getBonusManorDropRate()));
                        html = html.replace("{quest_h}", String.valueOf(premium.getBonusQuestDropRate()));
                        html = html.replace("{quest_reward_h}", String.valueOf(premium.getBonusQuestRewardRate()));
                        html = html.replace("{pet_xp_h}", String.valueOf(premium.getBonusPetExpRate()));
                        html = html.replace("{raid_drop_chance_h}", String.valueOf(premium.getBonusRaidDropChance()));
                        html = html.replace("{raid_drop_amount_h}", String.valueOf(premium.getBonusRaidDropAmount()));
                        html = html.replace("{herb_drop_chance_h}", String.valueOf(premium.getBonusHerbDropChance()));
                        html = html.replace("{herb_drop_amount_h}", String.valueOf(premium.getBonusHerbDropAmount()));
                        String button = "";
                        button += "<button value=\"Подключить\" action=\"bypass -h premium_buy_";
                        button += premium.getBonusId();
                        button += "\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"250\" height=\"30\" />";
                        html = html.replace("{buy}", button);
                    } else {
                        html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/thesame.htm", player);
                    }
                } else {
                    html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/alreadyhaveall.htm", player);
                }
                html = html.replace("{bonus_title}", premium.getBonusName());
                // Not have
                html = html.replace("{xp_n}", String.valueOf(player.getRateExp()));
                html = html.replace("{sp_n}", String.valueOf(player.getRateSp()));
                html = html.replace("{drop_n}", String.valueOf(player.getRateItems()));
                html = html.replace("{drop_chance_n}", "x");
                html = html.replace("{drop_amount_n}", "x");
                html = html.replace("{spoil_n}", String.valueOf(player.getRateSpoil()));
                html = html.replace("{spoil_chance_n}", "x");
                html = html.replace("{spoil_amount_n}", "x");
                html = html.replace("{adena_n}", String.valueOf(player.getRateAdena()));
                html = html.replace("{weight_n}", String.valueOf(player.getWeightPercents()));
                html = html.replace("{craft_n}", "x");
                html = html.replace("{m_craft_n}", "x");
                html = html.replace("{extract_n}", "x");
                html = html.replace("{manor_n}", String.valueOf(Config.RATE_MANOR));
                html = html.replace("{quest_n}", String.valueOf(Config.RATE_QUESTS_DROP));
                html = html.replace("{quest_reward_n}", String.valueOf(Config.RATE_QUESTS_REWARD));
                html = html.replace("{pet_xp_n}", "x");
                html = html.replace("{raid_drop_chance_n}", String.valueOf(Config.RATE_DROP_RAIDBOSS));
                html = html.replace("{raid_drop_amount_n}", "x");
                html = html.replace("{herb_drop_chance_n}", String.valueOf(Config.RATE_DROP_HERBS));
                html = html.replace("{herb_drop_amount_n}", "x");
                html = html.replace("{time}", TimeUtils.formatTime((int) premium.getBonusDuration()));
        }
        ShowBoard.separateAndSend(html, player);
    } else if(bypass.startsWith("buy_")) {
        if (!player.getDoublePremiumState()) {
            player.sendPacket(new HideBoard());
            String bonusId = bypass.substring(4).trim();
            PremiumBonus premium = PremiumSystemOptionsData.getInstance().findById(Integer.parseInt(bonusId));
            if (premium != null) {
                ItemInstance item = player.getInventory().getItemByItemId(premium.getBonusItemId());
                int amountItems = premium.getBonusItemAmount();
                if (item.getCount() >= amountItems) {
                    long bonusPeriod = (System.currentTimeMillis() / 1000) + premium.getBonusDurationFromProfile();
                    PremiumSystemDAO.getInstance().insert(player.getObjectId(), Integer.parseInt(bonusId), bonusPeriod);
                    PremiumSystemManager.getInstance().enablePremiumStatusFromCommunityBoardPremiumAccount(player);
                    _log.info("Player: " + player.getName() + " has activated " + premium.getBonusName());
                    player.getInventory().destroyItem(item, amountItems);
                } else {
                    player.sendPacket(new ExShowScreenMessage(NpcString.NONE, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, "Нет нужного кол-ва предметов для покупки!"));
                    player.sendPacket(PlaySound.BROKEN_KEY);
                }
            }
        }
    }
        return html;
}

    public static CommunityBoardPremiumAccount getInstance() {
        return CommunityBoardPremiumAccount.SingletonHolder.INSTANCE;
    }

private static class SingletonHolder {
    protected static final CommunityBoardPremiumAccount INSTANCE = new CommunityBoardPremiumAccount();
}
}