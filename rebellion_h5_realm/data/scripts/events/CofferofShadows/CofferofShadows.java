package events.CofferofShadows;

import l2r.gameserver.Announcements;
import l2r.gameserver.Config;
import l2r.gameserver.cache.Msg;
import l2r.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.SimpleSpawner;
import l2r.gameserver.model.actor.listener.CharListenerList;
import l2r.gameserver.model.instances.NpcInstance;
import l2r.gameserver.network.serverpackets.components.CustomMessage;
import l2r.gameserver.scripts.Functions;
import l2r.gameserver.scripts.ScriptFile;
import l2r.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Эвент Coffer of Shadows
public class CofferofShadows extends Functions implements ScriptFile, OnPlayerEnterListener
{
	private static int COFFER_PRICE = 50000; // 50.000 adena at x1 servers
	private static int COFFER_ID = 8659;
	private static int EVENT_MANAGER_ID = 32091;
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	private static final Logger _log = LoggerFactory.getLogger(CofferofShadows.class);
	private static boolean _active = false;

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { -14823, 123567, -3143, 8192 }, // Gludio
				{ -83159, 150914, -3155, 49152 }, // Gludin
				{ 18600, 145971, -3095, 40960 }, // Dion
				{ 82158, 148609, -3493, 60 }, // Giran
				{ 110992, 218753, -3568, 0 }, // Hiene
				{ 116339, 75424, -2738, 0 }, // Hunter Village
				{ 81140, 55218, -1551, 32768 }, // Oren
				{ 147148, 27401, -2231, 2300 }, // Aden
				{ 43532, -46807, -823, 31471 }, // Rune
				{ 87765, -141947, -1367, 6500 }, // Schuttgart
				{ 147154, -55527, -2807, 61300 } // Goddard
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("CofferofShadows");
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();

		if(SetActive("CofferofShadows", true))
		{
			spawnEventManagers();
			System.out.println("Event: Coffer of Shadows started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStarted", null);
		}
		else
			player.sendMessage(new CustomMessage("scripts.events.cofferofshadows.eventstarted", player));

		_active = true;
		show("admin/events/events.htm", player);
	}

	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if(SetActive("CofferofShadows", false))
		{
			unSpawnEventManagers();
			System.out.println("Event: Coffer of Shadows stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStoped", null);
		}
		else
			player.sendMessage(new CustomMessage("scripts.events.cofferofshadows.stopevent", player));

		_active = false;
		show("admin/events/events.htm", player);
	}

	/**
	 * Продает 1 сундук игроку
	 */
	public void buycoffer(String[] var)
	{
		Player player = getSelf();

		if(!player.isQuestContinuationPossible(true))
			return;

		if(!NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		int coffer_count = 1;
		try
		{
			coffer_count = Integer.valueOf(var[0]);
		}
		catch(Exception E)
		{}

		long need_adena = (long) (COFFER_PRICE * Config.EVENT_COFFER_OF_SHADOWS_PRICE_RATE * coffer_count);
		if(player.getAdena() < need_adena)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(need_adena, true);
		Functions.addItem(player, COFFER_ID, coffer_count);
	}

	public String DialogAppend_32091(Integer val)
	{
		if(val != 0)
			return "";

		String price;
		String append = "";
		for(int cnt : Config.EVENT_BUY_COFFER_COUNTS)
		{
			price = Util.formatAdena((long) (COFFER_PRICE * Config.EVENT_COFFER_OF_SHADOWS_PRICE_RATE * cnt));
			append += "<a action=\"bypass -h scripts_events.CofferofShadows.CofferofShadows:buycoffer " + cnt + "\">";
			if(cnt == 1)
				append += new CustomMessage("scripts.events.CofferofShadows.buycoffer", getSelf()).addString(price);
			else
				append += new CustomMessage("scripts.events.CofferofShadows.buycoffers", getSelf()).addNumber(cnt).addString(price);
			append += "</a><br>";
		}

		return append;
	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Coffer of Shadows [state: activated]");
		}
		else
			_log.info("Loaded Event: Coffer of Shadows [state: deactivated]");
	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.CofferofShadows.AnnounceEventStarted", null);
	}
}
