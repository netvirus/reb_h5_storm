package l2r.gameserver.network.clientpackets;

import l2r.gameserver.Config;
import l2r.gameserver.achievements.Achievements;
import l2r.gameserver.handler.bbs.CommunityBoardManager;
import l2r.gameserver.handler.bbs.ICommunityBoardHandler;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.network.serverpackets.SystemMessage2;
import l2r.gameserver.network.serverpackets.components.SystemMsg;
import l2r.gameserver.scripts.Scripts;

import java.util.Map;

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
		if (player == null)
			return;

		// Synerge - Support for handling bbs events on tutorial windows
		else if (_bypass.startsWith("_bbs"))
		{
			if (!Config.COMMUNITYBOARD_ENABLED)
				player.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
			else
			{
				String[] cm = _bypass.split(" ");
				final ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(cm[0]);
				if (handler != null)
					handler.onBypassCommand(player, _bypass);
			}
		}
		// Synerge - Support for handling scripts events on tutorial windows
		else if (_bypass.startsWith("scripts_"))
		{
			String command = _bypass.substring(8).trim();
			String[] word = command.split("\\s+");
			String[] args = command.substring(word[0].length()).trim().split("\\s+");
			String[] path = word[0].split(":");
			if (path.length != 2)
				return;

			Map<String, Object> variables = null;

			if (word.length == 1)
				Scripts.getInstance().callScripts(player, path[0], path[1], variables);
			else
				Scripts.getInstance().callScripts(player, path[0], path[1], new Object[] { args }, variables);
		}
		else if (Config.ENABLE_ACHIEVEMENTS && _bypass.startsWith("_bbs_achievements"))
		{
			String[] cm = _bypass.split(" ");
			if (_bypass.startsWith("_bbs_achievements_cat"))
			{
				int page = 0;
				if (cm.length < 1)
					page = 1;
				else
					page = Integer.parseInt(cm[2]);

				Achievements.getInstance().generatePage(player, Integer.parseInt(cm[1]), page);
			}
			else
				Achievements.getInstance().onBypass(player, _bypass, cm);
		}
		else
		{
			Quest tutorial = QuestManager.getQuest(255);

			if (tutorial != null)
				player.processQuestEvent(tutorial.getName(), _bypass, null);
		}

	}
}