package quests;

import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.instances.NpcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.scripts.ScriptFile;

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

	int Shubain = 32194;
	int Gwain = 32197;

	int FelimLizardmanWarrior = 20014;
	int VenomousSpider = 20038;
	int TumranBugbear = 20062;

	int FelimHead = 9749;
	int VenomousSpiderLeg = 9750;
	int TumranBugbearHeart = 9751;
	int ShubainsRecommendation = 9752;
	int GwainsRecommendation = 9753;

	public _062_PathOfTheTrooper()
	{
		super(false);

		addStartNpc(Gwain);
		addTalkId(Gwain);
		addTalkId(Shubain);
		addKillId(FelimLizardmanWarrior);
		addKillId(VenomousSpider);
		addKillId(TumranBugbear);
		addQuestItem(FelimHead);
		addQuestItem(VenomousSpiderLeg);
		addQuestItem(ShubainsRecommendation);
		addQuestItem(TumranBugbearHeart);
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
		String htmltext = "noquest";
		int GetMemoState = st.getCond();
		int talker_level = st.getPlayer().getLevel();
		boolean GetOneTimeQuestFlag = st.getPlayer().getVarB("profession_145");
		int npcId = npc.getNpcId();
		int id = st.getState();
		switch (id) {
			case CREATED:
				if (npcId == Gwain) {
					switch (isAvailableFor(st.getPlayer())) {
						case LEVEL:
							htmltext = "master_tbwain_q0062_02.htm";
							st.exitCurrentQuest(true);
							break;
						case CLASS_ID:
							htmltext = "master_tbwain_q0062_03.htm";
							st.exitCurrentQuest(true);
							break;
						case RACE:
							htmltext = "master_tbwain_q0062_04.htm";
							st.exitCurrentQuest(true);
							break;
						default:
							htmltext = "master_tbwain_q0062_01.htm";
							break;
					}
				}
				break;
			case STARTED:
				if (npcId == Gwain){
					if (GetMemoState == 4) {
						st.takeItems(ShubainsRecommendation, -1);
						st.setCond(5);
						htmltext = "master_tbwain_q0062_08.htm";
					} else if (GetMemoState == 5 && st.getQuestItemsCount(TumranBugbearHeart) >= 1) {
						st.takeItems(TumranBugbearHeart, -1);
						if (st.getPlayer().getClassId().getLevel() == 1) {
							st.giveItems(GwainsRecommendation, 1);
							if (!st.getPlayer().getVarB("profession_145")) {
								st.getPlayer().setVar("prof1profession_145", "1", -1);
								if (talker_level >= 20)
									st.addExpAndSp(320534, 20848);
								else if (talker_level == 19)
									st.addExpAndSp(456128, 27546);
								else
									st.addExpAndSp(591724, 34244);
								st.giveItems(ADENA_ID, 163800);
//						st.addExpAndSp(160267, 11023);
//						st.giveItems(ADENA_ID, 81900);
							}
						}
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(true);
						htmltext = "master_tbwain_q0062_10.htm";
					}
				}
				if(npcId == Shubain) {
					if (GetMemoState == 1)
						htmltext = "master_shubain_q0062_01.htm";
					else if (GetMemoState == 2 && st.getQuestItemsCount(FelimHead) < 5) {
						htmltext = "master_shubain_q0062_03.htm";
					} else if (GetMemoState == 2 && st.getQuestItemsCount(FelimHead) >= 5) {
						st.takeItems(FelimHead, -1);
						st.setCond(3);
						htmltext = "master_shubain_q0062_04.htm";
					} else if (GetMemoState == 3 && st.getQuestItemsCount(VenomousSpiderLeg) < 10) {
						htmltext = "master_shubain_q0062_05.htm";
					} else if (GetMemoState == 3 && st.getQuestItemsCount(VenomousSpiderLeg) >= 10) {
						st.takeItems(VenomousSpiderLeg, -1);
						st.giveItems(ShubainsRecommendation, 1);
						st.setCond(4);
						htmltext = "master_shubain_q0062_06.htm";
					}
				}
				break;
			case COMPLETED:
				if (npcId == Gwain)
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
		if(id == FelimLizardmanWarrior && cond == 2)
		{
			long count = st.getQuestItemsCount(FelimHead);
			if(count < 5)
			{
				st.giveItems(FelimHead, 1);
				if(count == 4)
					st.playSound(SOUND_MIDDLE);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		if(id == VenomousSpider && cond == 3)
		{
			long count = st.getQuestItemsCount(VenomousSpiderLeg);
			if(count < 10)
			{
				st.giveItems(VenomousSpiderLeg, 1);
				if(count == 9)
					st.playSound(SOUND_MIDDLE);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		if(id == TumranBugbear && cond == 5)
			if(st.getQuestItemsCount(TumranBugbearHeart) == 0)
			{
				st.giveItems(TumranBugbearHeart, 1);
				st.playSound(SOUND_MIDDLE);
			}
		return null;
	}
}