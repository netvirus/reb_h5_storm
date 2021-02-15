package quests;

import l2r.commons.util.Rnd;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.base.Race;
import l2r.gameserver.model.instances.NpcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.scripts.ScriptFile;

/**
 * Reworked 15.02.2021
 * @author netvirus
 */

public class _062_PathOfTheTrooper extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	// NPC
	int SHUBAIN = 32194;
	int GWAIN = 32197;
	// MOBS
	int FELIM_LIZARDMAN_WARRIOR = 20014;
	int VENOMUS_SPIDER = 20038;
	int TUMRAN_BUGBEAR = 20062;
	// ITEMS
	int FELIM_HEAD = 9749;
	int VENOMOUS_SPIDER_LEG = 9750;
	int TUMRAN_BUGBEAR_HEART = 9751;
	int SHUBAINS_RECOMMENDATION = 9752;
	int GWAINS_RECOMMENDATION = 9753;

	public _062_PathOfTheTrooper()
	{
		super(false);
		addStartNpc(GWAIN);
		addTalkId(GWAIN, SHUBAIN);
		addKillId(FELIM_LIZARDMAN_WARRIOR, VENOMUS_SPIDER, TUMRAN_BUGBEAR);
		addQuestItem(FELIM_HEAD, VENOMOUS_SPIDER_LEG, TUMRAN_BUGBEAR_HEART, SHUBAINS_RECOMMENDATION);
		addLevelCheck(18);
		addClassIdCheck(ClassId.maleSoldier);
		addRaceCheck(Race.kamael);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("master_tbwain_q0062_06.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("master_shubain_q0062_02.htm"))
			st.setCond(2);
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int GetMemoState = st.getCond();
		int talker_level = st.getPlayer().getLevel();
		int GetOneTimeQuestFlag = st.getPlayer().getVarInt("profession_145");
		int npcId = npc.getNpcId();
		int id = st.getState();
		switch (id) {
			case CREATED:
				if (npcId == GWAIN) {
					switch (isAvailableFor(st.getPlayer())) {
						case LEVEL:
							htmltext = "master_tbwain_q0062_03.htm";
							st.exitCurrentQuest(true);
							break;
						case CLASS_ID:
							htmltext = "master_tbwain_q0062_04.htm";
							st.exitCurrentQuest(true);
							break;
						case RACE:
							htmltext = "master_tbwain_q0062_02.htm";
							st.exitCurrentQuest(true);
							break;
						default:
							htmltext = "master_tbwain_q0062_01.htm";
							break;
					}
				}
				break;
			case STARTED:
				if (npcId == GWAIN){
					if (GetMemoState == 1 || GetMemoState == 2 || GetMemoState == 3)
						htmltext = "master_tbwain_q0062_07.htm";
					else if (GetMemoState == 4) {
						st.takeItems(SHUBAINS_RECOMMENDATION, -1);
						st.setCond(5);
						htmltext = "master_tbwain_q0062_08.htm";
					} else if (GetMemoState == 5 && st.getQuestItemsCount(TUMRAN_BUGBEAR_HEART) == 0)	{
						htmltext = "master_tbwain_q0062_09.htm";
					} else if (GetMemoState == 5 && st.getQuestItemsCount(TUMRAN_BUGBEAR_HEART) >= 1) {
						st.takeItems(TUMRAN_BUGBEAR_HEART, -1);
						if (st.getPlayer().getClassId().getLevel() == 1) {
							st.giveItems(GWAINS_RECOMMENDATION, 1);
							if (GetOneTimeQuestFlag == 0) {
								st.getPlayer().setVar("profession_145", String.valueOf(1), -1);
								if (talker_level >= 20)
									st.addExpAndSp(320534, 20848);
								else if (talker_level == 19)
									st.addExpAndSp(456128, 27546);
								else
									st.addExpAndSp(591724, 34244);
								st.giveItems(ADENA_ID, 163800);
							}
						}
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(true);
						st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
						htmltext = "master_tbwain_q0062_10.htm";
					}
				}
				if(npcId == SHUBAIN) {
					if (GetMemoState == 1)
						htmltext = "master_shubain_q0062_01.htm";
					else if (GetMemoState == 2 && st.getQuestItemsCount(FELIM_HEAD) < 5) {
						htmltext = "master_shubain_q0062_03.htm";
					} else if (GetMemoState == 2 && st.getQuestItemsCount(FELIM_HEAD) >= 5) {
						st.takeItems(FELIM_HEAD, -1);
						st.setCond(3);
						htmltext = "master_shubain_q0062_04.htm";
					} else if (GetMemoState == 3 && st.getQuestItemsCount(VENOMOUS_SPIDER_LEG) < 10) {
						htmltext = "master_shubain_q0062_05.htm";
					} else if (GetMemoState == 3 && st.getQuestItemsCount(VENOMOUS_SPIDER_LEG) >= 10) {
						st.takeItems(VENOMOUS_SPIDER_LEG, -1);
						st.giveItems(SHUBAINS_RECOMMENDATION, 1);
						st.setCond(4);
						htmltext = "master_shubain_q0062_06.htm";
					} else if (GetMemoState == 4)
						htmltext = "master_shubain_q0062_07.htm";
				}
				break;
			case COMPLETED:
				if (npcId == GWAIN)
					htmltext = "master_tbwain_q0062_05.htm";
				break;
		}
		return htmltext;
		}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int id = npc.getNpcId();
		int cond = st.getCond();
		int questItemCountWithPremiumBonus = (int) Math.ceil(1 * st.getPlayer().getPremiumBonus().getBonusQuestDropRate());

		if(id == FELIM_LIZARDMAN_WARRIOR)
		{
			long count = st.getQuestItemsCount(FELIM_HEAD);
			if(cond == 2 && count < 5)
			{
				st.giveItems(FELIM_HEAD, questItemCountWithPremiumBonus);
				if(count < 5)
					st.playSound(SOUND_MIDDLE);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		if(id == VENOMUS_SPIDER)
		{
			long count = st.getQuestItemsCount(VENOMOUS_SPIDER_LEG);
			if(cond == 3 && count < 10)
			{
				st.giveItems(VENOMOUS_SPIDER_LEG, questItemCountWithPremiumBonus);
				if(count < 10)
					st.playSound(SOUND_MIDDLE);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		if(id == TUMRAN_BUGBEAR && cond == 5) {
			int chance = Rnd.get(1000);
			if (chance < 500 && st.getQuestItemsCount(TUMRAN_BUGBEAR_HEART) < 1) {
				st.giveItems(TUMRAN_BUGBEAR_HEART, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}