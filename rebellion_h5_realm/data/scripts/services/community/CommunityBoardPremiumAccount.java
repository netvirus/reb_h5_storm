package services.community;

import l2r.gameserver.Config;
import l2r.gameserver.data.htm.HtmCache;
import l2r.gameserver.data.xml.parser.PremiumSystemOptionsData;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.actor.instances.player.PremiumBonus;

import l2r.gameserver.network.serverpackets.ShowBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CommunityBoardPremiumAccount
{
	public static final Logger _log = LoggerFactory.getLogger(CommunityBoardPremiumAccount.class);

	private CommunityBoardPremiumAccount() {
		// visibility
	}

	public String getAction(Player player, String bypass)
	{
		String finalHtml = "";
		if(bypass.startsWith("list"))
		{
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/index.htm", player);
			StringBuilder builder = new StringBuilder();
			Map<Integer, PremiumBonus> _premiumBonuses = PremiumSystemOptionsData.getInstance().getPremiumBonusList();
			_premiumBonuses.forEach((k, v) -> {
				builder.append("<tr>");
				builder.append("<td width=\"32\"><img src=\"").append(v.getBonusIconName()).append("\" width=\"32\" height=\"32\"></td>");
				builder.append("<td><font color=FF6600 name=\"CreditTextNormal\">").append(v.getBonusName()).append("</font></td>");
				builder.append("<td><button value=\"Подробнее\" action=\"bypass -h premium_show_").append(v.getBonusId()).append("\" width=65 height=22 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				builder.append("</tr>");
			});
			finalHtml = html.replace("{list}", builder.toString());
		}
		else if(bypass.startsWith("show_"))
		{
			String command = bypass.substring(5).trim();
			String bonusId = command.split("\\s+")[0];
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/premium/detail.htm", player);
			PremiumBonus premium = PremiumSystemOptionsData.getInstance().findById(Integer.parseInt(bonusId));
			if (premium != null) {
				html = html.replace("{bonus_name}", premium.getBonusName());
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

				ShowBoard.separateAndSend(html, player);
			}
		}
		else if (bypass.equalsIgnoreCase("buy_"))
		{
			System.out.println("IT WORKS! - bbspremiumbuy");
		}
		return finalHtml;
	}

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

	public static CommunityBoardPremiumAccount getInstance() {
		return CommunityBoardPremiumAccount.SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		protected static final CommunityBoardPremiumAccount INSTANCE = new CommunityBoardPremiumAccount();
	}
}