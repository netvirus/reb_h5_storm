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

    private static final Logger _log = LoggerFactory.getLogger(PremiumSystemManager.class);
    private final Map<Integer, ScheduledFuture<?>> expiretasks = new HashMap<>();

    private PremiumSystemManager()
    {
        // Visibility
    }

    public void load(Player activeChar)
    {
        Map<Boolean, PremiumBonus> premiums = PremiumSystemDAO.getInstance().load(activeChar.getObjectId());
        if (!premiums.isEmpty()) {
            PremiumBonus premium = null;
            if (premiums.size() == 1)
            {
                premium = premiums.get(premiums.keySet().stream().findFirst().get());
                activeChar.setDoublePremiumState(false);
            }
            else if (premiums.size() == 2)
            {
                premium = premiums.get(false);
                activeChar.setDoublePremiumState(true);
            }
            enablePremiumState(activeChar, premium);
        }
        premiums.clear();
    };

    public void enablePremiumStatusFromCommunityBoardPremiumAccount(Player activeChar) {
        load(activeChar);
        activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 6463, 1, 0, 0));
    }

    private void enablePremiumState(Player activeChar, PremiumBonus premium) {
        long timer = premium.getBonusDuration();

        changePlayerPremiumBonusState(activeChar, premium);

        activeChar.setPremiumBonus(premium);
        activeChar.sendPacket(new ExBR_PremiumState(activeChar.getObjectId(), true));

        if (premium.isBonusAuraEnabled())
            activeChar.startPremiumBonusAbnormalEffect(AbnormalEffect.S_AIR_STUN);

        String premiumMsg = "Ваша премиум подписка закончится через: " + TimeUtils.formatTime((int) (timer - (System.currentTimeMillis() / 1000)));
        activeChar.sendPacket(new ExShowScreenMessage(NpcString.NONE, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, premiumMsg));

        startExpireTask(activeChar);
    }

    /**
     * Disable any premium state on player active premium bonus
     * @param activeChar
     */
    private void disablePremiumState(Player activeChar) {
        // Get player's active premium bonus
        PremiumBonus premiumBonus = activeChar.getPremiumBonus();
        // Change premium bonus type state to disabled (false)
        changePlayerPremiumBonusState(activeChar, premiumBonus);
        // Reset rates to default
        activeChar.setPremiumBonus(new PremiumBonus());
        // Disable premium frame
        activeChar.sendPacket(new ExBR_PremiumState(activeChar.getObjectId(), false));
        // Disable premium abnormal visual effect
        if (activeChar.getPremiumBonusAbnormalEffectState())
            activeChar.stopPremiumBonusAbnormalEffect(activeChar.getPremiumBonusAbnormalEffectType());
        // Load any active premium subscription
        load(activeChar);
        _log.info("Disabled premium for player: " + activeChar.getName());
    }

    /**
     * Checks the type of premium subscription and changes the values of the variables depending on the passed type
     * @param activeChar
     * @param premiumBonus
     */
    private void changePlayerPremiumBonusState(Player activeChar, PremiumBonus premiumBonus)
    {
        if (premiumBonus.isBonusMain())
        {
            activeChar.setPremiumMainTypeState(true);
            if (activeChar.getPremiumSecondTypeState() && !activeChar.getDoublePremiumState())
                activeChar.setDoublePremiumState(true);
        }
        else
        {
            activeChar.setPremiumSecondTypeState(true);
            if (activeChar.getPremiumMainTypeState() && !activeChar.getDoublePremiumState())
                activeChar.setDoublePremiumState(true);
        }
    }

    private void startExpireTask(Player activeChar)
    {
        final ScheduledFuture<?> task = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(activeChar);
        expiretasks.put(activeChar.getObjectId(), task);
    }

    public void stopExpireTask(Player activeChar)
    {
        ScheduledFuture<?> task = expiretasks.remove(activeChar.getObjectId());
        if (task != null)
        {
            task.cancel(false);
        }
            disablePremiumState(activeChar);
            load(activeChar);
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
