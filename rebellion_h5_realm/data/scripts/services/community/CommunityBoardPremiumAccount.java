package services.community;

import l2r.gameserver.Config;
import l2r.gameserver.dao.PremiumAccountsTable;
import l2r.gameserver.dao.PremiumAccountsTable.PremiumTemplate;
import l2r.gameserver.data.htm.HtmCache;
import l2r.gameserver.data.xml.parser.PremiumSystemOptionsData;
import l2r.gameserver.handler.bbs.CommunityBoardManager;
import l2r.gameserver.handler.bbs.ICommunityBoardHandler;
import l2r.gameserver.listener.actor.player.OnAnswerListener;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.actor.instances.player.PremiumBonus;
import l2r.gameserver.network.serverpackets.ConfirmDlg;
import l2r.gameserver.network.serverpackets.ExBR_PremiumState;
import l2r.gameserver.network.serverpackets.ShowBoard;
import l2r.gameserver.network.serverpackets.components.ChatType;
import l2r.gameserver.network.serverpackets.components.CustomMessage;
import l2r.gameserver.network.serverpackets.components.SystemMsg;
import l2r.gameserver.scripts.ScriptFile;
import l2r.gameserver.utils.Log;
import l2r.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringTokenizer;

public class CommunityBoardPremiumAccount implements ScriptFile, ICommunityBoardHandler
{
	public static final Logger _log = LoggerFactory.getLogger(CommunityBoardPremiumAccount.class);

	@Override
	public String[] getBypassCommands() {
		return new String[]
				{
						"_bbs_premium_list",
						"_bbs_premium_show",
						"_bbs_premium_buy",
				};
	}

	@Override
	public void onBypassCommand(Player player, String bypass) {
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/RaidStatus.htm", player);

		if (!checkCondition(player))
			return;

		if ("bbs_premium_list".equals(cmd))
		{
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/index.htm", player);
			Map<Integer, PremiumBonus> _premiumBonuses = PremiumSystemOptionsData.getInstance().getPremiumBonusList();
			for (Map.Entry<Integer, PremiumBonus> premiumBonusEntry : _premiumBonuses.entrySet())
			{
				html = html + "<tr>";
				html = html + "<td width=\"32\"><img src=\"" + premiumBonusEntry.getValue().getBonusIconName() + "\" width=\"32\" height=\"32\"></td>";
				html = html + "<td><font color=FF6600 name=\"CreditTextNormal\">" + premiumBonusEntry.getValue().getBonusName() + "</font></td>";
				html = html + "<td><button value=\"\" action=\"bypass -h _bbslink_listprem:" + premiumBonusEntry.getValue().getBonusId() + "\" back=\"l2ui_ct1.Minimap.MiniMap_DF_PlusBtn_Red_Down\" fore=\"l2ui_ct1.Minimap.MiniMap_DF_PlusBtn_Red\" width=\"30\" height=\"30\" /></td>";
				html = html + "</tr>";
			}
			html = html.replace("{list}", html);
			_premiumBonuses.clear();
			ShowBoard.separateAndSend(html, player);
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {

	}

	@Override
	public void onLoad() {
		if (Config.ENABLE_PREMIUM_SYSTEM)
		{
			_log.info("CommunityBoard: Premium Account loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload() {
		if (Config.ENABLE_PREMIUM_SYSTEM)
			CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown() {	}


//	public void showPremiumBonusList()
//	{
////		_premiumBonusList.forEach((k, v) -> {
////			html = html + "<tr>";
////			html = html + "<td width=\"32\"><img src=\"" + v.getBonusIconName() + "\" width=\"32\" height=\"32\"></td>";
////			html = html + "<td><font color=FF6600 name=\"CreditTextNormal\">" + v.getBonusName() + "</font></td>";
////			html = html + "<td><button value=\"\" action=\"bypass -h scripts_services.PremiumAccountManagment:showPremiumBonusInfoById " + v.getBonusId() + "\" back=\"l2ui_ct1.Minimap.MiniMap_DF_PlusBtn_Red_Down\" fore=\"l2ui_ct1.Minimap.MiniMap_DF_PlusBtn_Red\" width=\"30\" height=\"30\" /></td>";
////			html = html + "</tr>";
////		});
////		_premiumBonusList.clear();
////		return html;
//	}
//
//	public void showPremiumBonusInfoById(String bonusId)
//	{
////		html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/detail.htm", player);
////		html = html + "<tr>";
////		html = html + "<td>TEXT FOR PREMIUM ID: " + bonusId + "</td>";
////		html = html + "</tr>";
////		ShowBoard.separateAndSend(html, player);
//		System.out.println(bonusId);
//	}
//
//	public void buy(String[] param)
//	{
//		String actualCommand = param[0]; // Get actual command
//
//		Player activeChar = getSelf();
//		if (activeChar == null)
//			return;
//
//		if (actualCommand.equalsIgnoreCase("bbsgetpremium"))
//		{
//			int premiumId = Integer.parseInt(param[1]);
//			String premiumTime = param[2];
//
//			PremiumTemplate currenttemplate = PremiumAccountsTable.getPremiumAccount(activeChar).getTemplate();
//
//			if (currenttemplate != PremiumAccountsTable.DEFAULT_PREMIUM_TEMPLATE)
//				askHim(activeChar, premiumId, premiumTime, currenttemplate);
//			else
//			{
//				try
//				{
//					long premiumDuration = 0;
//					int premiumCost = 0;
//
//					PremiumTemplate template = PremiumAccountsTable.getPremiumTemplate(premiumId);
//
//					if (template == PremiumAccountsTable.DEFAULT_PREMIUM_TEMPLATE)
//					{
//						activeChar.sendMessage("There is a error with premium system please contact the server administrator.");
//						_log.error("There was an error with premium templates: id " + premiumId + " , does not exsists...");
//						return;
//					}
//
//					switch (premiumTime)
//					{
//						case "week":
//							premiumDuration = 604800000L; // 7 days
//							premiumCost = template.costWeek;
//							break;
//						case "month":
//							premiumDuration = 2592000000L; // 30 days
//							premiumCost = template.costMonth;
//					}
//
//					if (template.cost <= 0 || template.costWeek <= 0  || template.costMonth <= 0 || premiumDuration <= 0 || premiumId <= 0)
//					{
//						activeChar.sendMessage(new CustomMessage("scripts.services.premiumaccount.err_code1", activeChar));
//						return;
//					}
//
//					if (activeChar.getInventory().destroyItemByItemId(template.cost, premiumCost))
//					{
//						long endtime = System.currentTimeMillis() + premiumDuration;
//						PremiumAccountsTable.savePremium(activeChar.getAccountName(), premiumId, endtime);
//						activeChar.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(),"Premium" ,"You have purchased " + template.name + " premium status for 1 " + premiumTime + ".");
//						activeChar.sendPacket(new ExBR_PremiumState(activeChar.getObjectId(), true));
//						Log.addDonation("Character " + activeChar + " has buyed premium: " + template.name  + " ID(" + premiumId + ") ", "premiumsystem");
//					}
//					else
//						activeChar.sendMessage(new CustomMessage("scripts.services.premiumaccount.cost", activeChar, premiumCost));
//				}
//				catch (Exception e)
//				{
//					activeChar.sendMessage(new CustomMessage("scripts.services.premiumaccount.err_code2", activeChar));
//				}
//			}
//		}
//	}
//
//	private static void askHim(final Player activeChar, final int premiumId, final String premiumTime, final PremiumTemplate currentTemplate)
//	{
//		if (activeChar != null)
//		{
//			final PremiumTemplate template = PremiumAccountsTable.getPremiumTemplate(premiumId);
//
//			if (template == PremiumAccountsTable.DEFAULT_PREMIUM_TEMPLATE)
//			{
//				activeChar.sendMessage("There is a error with premium system please contact the server administrator.");
//				_log.error("There was an error with premium templates: id " + premiumId + " , does not exsists...");
//				return;
//			}
//
//			activeChar.ask(new ConfirmDlg(SystemMsg.S1, 10000).addString("You have a " + currentTemplate.name + " premium account, do you want to continue?"), new OnAnswerListener()
//			{
//				@Override
//				public void sayYes()
//				{
//					try
//					{
//						long premiumDuration = 0;
//						int premiumCost = 0;
//
//						switch (premiumTime)
//						{
//							case "week":
//								premiumDuration = 604800000L; // 7 days
//								premiumCost = template.costWeek;
//								break;
//							case "month":
//								premiumDuration = 2592000000L; // 30 days
//								premiumCost = template.costMonth;
//						}
//
//						if (template.cost <= 0 || template.costWeek <= 0  || template.costMonth <= 0 || premiumDuration <= 0 || premiumId <= 0)
//						{
//							activeChar.sendMessage(new CustomMessage("scripts.services.premiumaccount.err_code1", activeChar));
//							return;
//						}
//
//						if (Util.getPay(activeChar, template.cost, premiumCost, true))
//						{
//							long endtime = System.currentTimeMillis() + premiumDuration;
//							PremiumAccountsTable.savePremium(activeChar.getAccountName(), premiumId, endtime);
//							activeChar.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(),"Premium" ,"You have purchased " + template.name + " premium status for 1 " + premiumTime + ".");
//							activeChar.sendPacket(new ExBR_PremiumState(activeChar.getObjectId(), true));
//							Log.addDonation("Character " + activeChar + " has buyed premium: " + template.name  + " ID(" + premiumId + ") ", "premiumsystem");
//						}
//						else
//							activeChar.sendMessage(new CustomMessage("scripts.services.premiumaccount.cost", activeChar, premiumCost));
//					}
//					catch (Exception e)
//					{
//						activeChar.sendMessage(new CustomMessage("scripts.services.premiumaccount.err_code2", activeChar));
//					}
//				}
//
//				@Override
//				public void sayNo()
//				{
//					//
//				}
//			});
//		}
//	}

	private static boolean checkCondition(Player player)
	{
		if (!Config.ENABLE_PREMIUM_SYSTEM || player == null)
			return false;
		return true;
	}
}