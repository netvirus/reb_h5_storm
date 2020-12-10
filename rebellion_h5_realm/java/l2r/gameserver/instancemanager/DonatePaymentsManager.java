package l2r.gameserver.instancemanager;

import l2r.gameserver.Config;
import l2r.gameserver.dao.DonationPaymentsDAO;
import l2r.gameserver.dao.MailDAO;
import l2r.gameserver.model.GameObjectsStorage;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.items.ItemInstance;
import l2r.gameserver.model.mail.Mail;
import l2r.gameserver.model.payment.Payment;
import l2r.gameserver.network.serverpackets.ExNoticePostArrived;
import l2r.gameserver.network.serverpackets.components.SystemMsg;
import l2r.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author netvirus
 */

public class DonatePaymentsManager implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DonatePaymentsManager.class);
    private static DonatePaymentsManager _instance;
    DonationPaymentsDAO _connect = DonationPaymentsDAO.getInstance();
    private boolean writeToLog = Config.DONATION_WRITE_TO_LOG;

    public DonatePaymentsManager() {
        if (writeToLog) {
            LOG.info("DonatePaymentsManager: Initializing");
        }
    }

    @Override
    public void run() {
        ArrayList<Payment> payments = _connect.getPayments();
        if (!payments.isEmpty()) {
            if (writeToLog) {
                LOG.info("DonatePaymentsManager: found ---> " + payments.size() + " payments");
            }

            for (Payment payment : payments) {
                Player player = GameObjectsStorage.getPlayer(payment.getRecipient());
                if (player != null) {
                    sendMail(player, Config.DONATION_REWARD_ITEM_ID, payment.getAmount(), payment);
                    payment.setStatus(1);
                    _connect.changeStatusToReceived(payment);
                    if (writeToLog) {
                        LOG.info("DonatePaymentsManager: The reward was mailed to the recipient ---> " + payment.getRecipient() + " in the amount of " + payment.getAmount() + " " + Config.DONATION_ITEM_NAME);
                    }
                }
            }
        }
        payments.clear();
    }

    public void sendMail(Player player, int itemId, int amount, Payment list) {
        Mail _mail = new Mail();
        _mail.setSenderId(1);
        _mail.setSenderName("Donate " + Config.DONATION_PROJECT_NAME);
        _mail.setReceiverId(player.getObjectId());
        _mail.setReceiverName(player.getName());
        _mail.setTopic("Your order №" + list.getId() + " and date " + list.getPayDate());
        _mail.setBody("There was a donat to " + list.getRecipient() + " in the amount of " + list.getAmount() + " " + Config.DONATION_ITEM_NAME);
        _mail.setType(Mail.SenderType.NORMAL);
        _mail.setUnread(true);
        _mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
        if (amount > 0) {
            ItemInstance item = ItemFunctions.createItem(itemId);
            item.setLocation(ItemInstance.ItemLocation.MAIL);
            item.setCount(amount);
            item.save();
            _mail.addAttachment(item);
        }
        MailDAO.getInstance().save(_mail);
        player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
        player.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
    }

    public static DonatePaymentsManager getInstance() {
        if (_instance == null)
            _instance = new DonatePaymentsManager();
        return _instance;
    }

    public static void reload() {
        _instance = new DonatePaymentsManager();
    }
}