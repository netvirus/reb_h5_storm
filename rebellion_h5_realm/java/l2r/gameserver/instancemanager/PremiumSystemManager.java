package l2r.gameserver.instancemanager;

import l2r.gameserver.dao.PremiumSystemDAO;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.actor.instances.player.PremiumBonus;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.ExBR_PremiumState;
import l2r.gameserver.network.serverpackets.components.NpcString;
import l2r.gameserver.skills.AbnormalEffect;
import l2r.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2r.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Premium system manager
 * @author netvirus
 */

public class PremiumSystemManager {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumSystemManager.class);
    private final Map<Integer, ScheduledFuture<?>> expiretasks = new HashMap<>();

    public void load(Player activeChar)
    {
        Map<Boolean, PremiumBonus> premiums = PremiumSystemDAO.getInstance().load(activeChar.getObjectId());
        if (!premiums.isEmpty()) {
            PremiumBonus premium = null;
            switch (premiums.size()) {
                // Have one premium
                case 1: {
                    premium = premiums.get(premiums.keySet().stream().findFirst().get());
                    activeChar.setTwoPremium(false);
                    break;
                }
                // Have two premium
                case 2: {
                    premium = premiums.get(false);
                    activeChar.setTwoPremium(true);
                    break;
                }
            }
            // Enable premium status
            enablePremiumStatus(activeChar, premium);
        } else {
            // Disable premium status
            disablePremiumStatus(activeChar);
        }
        premiums.clear();
    };

//    private final Consumer<OnPlayerLogout> playerLogoutEvent = (event) ->
//    {
//        //TODO Implement update time for not main premium
//        //stopExpireTask(player);
//    };

    private void startExpireTask(Player player)
    {
        final ScheduledFuture<?> task = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(player);
        expiretasks.put(player.getObjectId(), task);
    }

    public void stopExpireTask(Player player)
    {
        ScheduledFuture<?> task = expiretasks.remove(player.getObjectId());
        if (task != null)
        {
            task.cancel(false);
            task = null;
        }
        if (player.hasTwoPremium())
            load(player);
    }

    public void enablePremiumStatusFromComminityBoardPremiumAccount(Player player, PremiumBonus premium, boolean showVisualEffect) {
        load(player);
        player.broadcastPacket(new MagicSkillUse(player, player, 6463, 1, 0, 0));
    }

    private void enablePremiumStatus(Player player, PremiumBonus premium) {
        long timer = premium.getBonusDuration();
        setPremiumStatus(player, premium, true);
        startExpireTask(player);
        String premiumMsg = "Your premium subscription will expire in: " + TimeUtils.formatTime((int) (timer - (System.currentTimeMillis() / 1000)));
        player.sendPacket(new ExShowScreenMessage(NpcString.NONE, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, premiumMsg));
    }

    private void disablePremiumStatus(Player player) {
        setPremiumStatus(player, new PremiumBonus(), false);
        player.setTwoPremium(false);
    }

    /*
     * @param player - active char
     * @param premium is the object PremiumBonus
     * @param boolean state of premium
     */
    private void setPremiumStatus(Player player, PremiumBonus premium, boolean premiumState) {
        player.setPremiumStatus(premiumState);
        player.setPremiumBonus(premium);
        player.sendPacket(new ExBR_PremiumState(player.getObjectId(), premiumState));
        if (premium.isBonusAuraEnabled()) {
            player.startPremiumBonusAbnormalEffect(AbnormalEffect.S_AIR_STUN);
        } else {
            player.stopPremiumBonusAbnormalEffect(player.getPremiumBonusAbnormalEffect());
        }
    }

    public Map<Integer, ScheduledFuture<?>> getExpireTasks() {
        return expiretasks;
    }

    public static PremiumSystemManager getInstance() {
        return PremiumSystemManager.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final PremiumSystemManager INSTANCE = new PremiumSystemManager();
    }
}
