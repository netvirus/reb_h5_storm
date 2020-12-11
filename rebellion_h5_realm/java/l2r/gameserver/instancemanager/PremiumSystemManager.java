package l2r.gameserver.instancemanager;

import l2r.gameserver.listener.PlayerListener;
import l2r.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2r.gameserver.model.L2PremiumBonus;
import l2r.gameserver.model.Player;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.ExBR_PremiumState;
import l2r.gameserver.network.serverpackets.components.NpcString;
import l2r.gameserver.utils.TimeUtils;
import org.napile.primitive.Containers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Premium system manager
 * @author netvirus
 */

public class PremiumSystemManager implements OnPlayerEnterListener {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumSystemManager.class);
    private final Map<Integer, ScheduledFuture<?>> expiretasks = new HashMap<>();

    @Override
    public void onPlayerEnter(Player player) {
        System.out.println("JH{OUG{IUG{IGUOGHOHIPHPIHOUGIUGIUGrgeohior0eet9u7");
    }

//    protected PremiumSystemManager()
//    {
//        listenerContainer.addListener(new ConsumerEventListener(listenerContainer, EventType.ON_PLAYER_LOGIN, playerLoginEvent, this));
//        listenerContainer.addListener(new ConsumerEventListener(listenerContainer, EventType.ON_PLAYER_LOGOUT, playerLogoutEvent, this));
//    }
//
//    private final Consumer<OnPlayerLogin> playerLoginEvent = (event) ->
//    {
//        Map<Boolean, L2PremiumBonus> premiums = DAOFactory.getInstance().getPremiumSystemDAO().load(event.getActiveChar().getObjectId());
//        Player player = event.getActiveChar();
//        if (!premiums.isEmpty()) {
//            L2PremiumBonus premium = null;
//            switch (premiums.size()) {
//                // Have one premium
//                case 1: {
//                    premium = premiums.get(premiums.keySet().stream().findFirst().get());
//                    player.setTwoPremium(false);
//                    break;
//                }
//                // Have two premium
//                case 2: {
//                    premium = premiums.get(false);
//                    player.setTwoPremium(true);
//                    break;
//                }
//            }
//            // Enable premium status
//            enablePremiumStatus(player, premium);
//        } else {
//            // Disable premium status
//            disablePremiumStatus(player);
//        }
//        premiums.clear();
//    };
//
//    private final Consumer<OnPlayerLogout> playerLogoutEvent = (event) ->
//    {
//        //TODO Implement update time for not main premium
//        //stopExpireTask(player);
//    };
//
//    private void startExpireTask(Player player, long delay)
//    {
//        //final ScheduledFuture<?> task = ThreadPoolManager.getInstance().scheduleEvent(new PremiumExpireTask(player), delay);
//        //expiretasks.put(player.getObjectId(), task);
//    }
//
//    private void stopExpireTask(Player player)
//    {
////        ScheduledFuture<?> task = expiretasks.remove(player.getAccountName());
////        if (task != null)
////        {
////            task.cancel(false);
////            task = null;
////        }
//    }
//
//    private void enablePremiumStatus(Player player, L2PremiumBonus premium, boolean showVisualEffect) {
//        enablePremiumStatus(player, premium);
//        player.broadcastPacket(new MagicSkillUse(player, player, 6463, 1, 0, 0));
//    }
//
//    private void enablePremiumStatus(Player player, L2PremiumBonus premium) {
//        long timer = premium.getBonusDuration();
//        setPremiumStatus(player, premium, true);
//        startExpireTask(player, ((timer * 1000L) - System.currentTimeMillis()));
//        String premiumMsg = "Your premium subscription will expire in: " + TimeUtils.formatTime((int) (timer - (System.currentTimeMillis() / 1000)));
//        player.sendPacket(new ExShowScreenMessage(NpcString.NONE, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, premiumMsg));
//    }
//
//    private void disablePremiumStatus(Player player) {
//        setPremiumStatus(player, new L2PremiumBonus(), false);
//        player.setTwoPremium(false);
//    }
//
//    /*
//     * @param player - active char
//     * @param premium is the object L2PremiumBonus
//     * @param boolean state of premium
//     */
//    private void setPremiumStatus(Player player, L2PremiumBonus premium, boolean premiumState) {
//        player.setPremiumStatus(premiumState);
//        player.setPremiumBonus(premium);
//        player.sendPacket(new ExBR_PremiumState(player.getObjectId(), premiumState));
//    }

    public static PremiumSystemManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final PremiumSystemManager INSTANCE = new PremiumSystemManager();
    }
}
