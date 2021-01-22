package l2r.gameserver.network.clientpackets;

import l2r.gameserver.Config;
import l2r.gameserver.achievements.Achievements;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.randoms.PvPCharacterIntro;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	// format: cS

	String _bypass = null;

	@Override
	protected void readImpl()
	{
		_bypass = readS();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Quest tutorial = QuestManager.getQuest(255);

		if(tutorial != null)
			player.processQuestEvent(tutorial.getName(), _bypass, null);
		
		if (_bypass.startsWith("emailvalidation") && Config.ENABLE_EMAIL_VALIDATION)
		{
			String[] cm = _bypass.split(" ");
			
			if (cm.length < 3)
			{
				player.sendMessage("Please fill all required fields.");
				return;
			}
		}
		else if (_bypass.startsWith("_bbs_achievements") && Config.ENABLE_ACHIEVEMENTS)
		{
			String[] cm = _bypass.split(" ");
			
			Achievements.getInstance().usebypass(player, _bypass, cm);
		}
		else if(Config.ENABLE_ACHIEVEMENTS && _bypass.startsWith("_bbs_achievements_cat"))
		{
			String[] cm = _bypass.split(" ");

			int page = 0;
			if (cm.length < 1)
				page = 1;
			else
				page = Integer.parseInt(cm[2]);
				
			Achievements.getInstance().generatePage(player, Integer.parseInt(cm[1]), page);
			return;
		}
		else if(Config.ENABLE_ACHIEVEMENTS && _bypass.startsWith("_bbs_achievements_close"))
		{
			String[] cm = _bypass.split(" ");

			Achievements.getInstance().usebypass(player, _bypass, cm);
			return;
		}
		else if (_bypass.startsWith("_pvpcharintro"))
		{
			PvPCharacterIntro.getInstance().bypassIntro(player, _bypass);
		}
	}
}