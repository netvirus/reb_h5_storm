package l2r.gameserver;

import l2r.commons.lang.StatsUtils;
import l2r.commons.listener.Listener;
import l2r.commons.listener.ListenerList;
import l2r.commons.net.nio.impl.SelectorThread;
import l2r.commons.versioning.Version;
import l2r.gameserver.achievements.Achievements;
import l2r.gameserver.achievements.PlayerTops;
import l2r.gameserver.auction.AuctionManager;
import l2r.gameserver.cache.CrestCache;
import l2r.gameserver.cache.ImagesChache;
import l2r.gameserver.dao.*;
import l2r.gameserver.data.BoatHolder;
import l2r.gameserver.data.xml.Parsers;
import l2r.gameserver.data.xml.holder.EventHolder;
import l2r.gameserver.data.xml.holder.ResidenceHolder;
import l2r.gameserver.data.xml.holder.StaticObjectHolder;
import l2r.gameserver.data.xml.parser.PremiumSystemOptionsData;
import l2r.gameserver.database.DatabaseFactory;
import l2r.gameserver.geodata.GeoEngine;
import l2r.gameserver.handler.admincommands.AdminCommandHandler;
import l2r.gameserver.handler.items.ItemHandler;
import l2r.gameserver.handler.usercommands.UserCommandHandler;
import l2r.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.instancemanager.*;
import l2r.gameserver.instancemanager.games.*;
import l2r.gameserver.instancemanager.games.DonationBonusDay;
import l2r.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2r.gameserver.instancemanager.naia.NaiaCoreManager;
import l2r.gameserver.instancemanager.naia.NaiaTowerManager;
import l2r.gameserver.listener.GameListener;
import l2r.gameserver.listener.game.OnShutdownListener;
import l2r.gameserver.listener.game.OnStartListener;
import l2r.gameserver.model.AcademyList;
import l2r.gameserver.model.AcademyRewards;
import l2r.gameserver.model.World;
import l2r.gameserver.model.entity.Hero;
import l2r.gameserver.model.entity.MonsterRace;
import l2r.gameserver.model.entity.SevenSigns;
import l2r.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2r.gameserver.model.entity.olympiad.Olympiad;
import l2r.gameserver.network.GameClient;
import l2r.gameserver.network.GamePacketHandler;
import l2r.gameserver.network.loginservercon.AuthServerCommunication;
import l2r.gameserver.network.telnet.TelnetServer;
import l2r.gameserver.nexus_interface.NexusEvents;
import l2r.gameserver.randoms.CaptchaImage;
import l2r.gameserver.randoms.PlayerKill;
import l2r.gameserver.randoms.Visuals;
import l2r.gameserver.randoms.votingengine.VotingRewardAPI;
import l2r.gameserver.scripts.Scripts;
import l2r.gameserver.tables.AdminTable;
import l2r.gameserver.tables.AugmentationData;
import l2r.gameserver.tables.ClanTable;
import l2r.gameserver.tables.EnchantHPBonusTable;
import l2r.gameserver.tables.FakePcsTable;
import l2r.gameserver.tables.PetSkillsTable;
import l2r.gameserver.tables.SkillTreeTable;
import l2r.gameserver.taskmanager.ItemsAutoDestroy;
import l2r.gameserver.taskmanager.TaskManager;
import l2r.gameserver.taskmanager.tasks.RestoreOfflineBuffers;
import l2r.gameserver.taskmanager.tasks.RestoreOfflineTraders;
import l2r.gameserver.utils.RRDTools;
import l2r.gameserver.utils.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer
{
	public static final int AUTH_SERVER_PROTOCOL = 2;
	private static final Logger _log = LoggerFactory.getLogger(GameServer.class);
	private static final Map<String, String> _args = new FastMap<>();
	
	private static HttpURLConnection con  = null;
	private static BufferedReader in = null;
	
	private static boolean loaded = false;
	private static boolean retryConnection = false;
	private static int retriesCount = 0;
	
	public class GameServerListenerList extends ListenerList<GameServer>
	{
		public void onStart()
		{
			for (Listener<GameServer> listener : getListeners())
				if (OnStartListener.class.isInstance(listener))
					((OnStartListener) listener).onStart();
		}
		
		public void onShutdown()
		{
			for (Listener<GameServer> listener : getListeners())
				if (OnShutdownListener.class.isInstance(listener))
					((OnShutdownListener) listener).onShutdown();
		}
	}
	
	public static GameServer _instance;
	
	private final SelectorThread<GameClient> _selectorThreads[];
	private TelnetServer statusServer;
	private Version version;
	private final GameServerListenerList _listeners;
	private boolean _hasLoaded;
	
	private int _serverStarted;
	
	public SelectorThread<GameClient>[] getSelectorThreads()
	{
		return _selectorThreads;
	}
	
	public int time()
	{
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	public int uptime()
	{
		return time() - _serverStarted;
	}
	
	private void showVersion()
	{
		version = new Version(GameServer.class);
		
		_log.info("=================================================");
		_log.info("Copyright: ............... " + "L2relax.fun");
		_log.info("Chronicle: ............... " + "High Five Part 5");
		_log.info("Revision: ................ " + version.getRevisionNumber());
		_log.info("Build date: .............. " + version.getBuildDate());
		_log.info("=================================================");
	}

	@SuppressWarnings("unchecked")
	public GameServer() throws Exception
	{
		_hasLoaded = false;
		long startMs = System.currentTimeMillis();
		_instance = this;
		_serverStarted = time();
		_listeners = new GameServerListenerList();
		
		new File("./log/").mkdir();
		
		showVersion();
//		showLogo();
		
		// Initialize config
		printSection("Config");
		Config.load();
		
		// Check binding address
		checkFreePorts();
		// Initialize database
		Class.forName(Config.DATABASE_DRIVER).getDeclaredConstructor().newInstance();
		DatabaseFactory.getInstance().getConnection().close();
		
		printSection("IdFactory");
		IdFactory _idFactory = IdFactory.getInstance();
		if (!_idFactory.isInitialized())
		{
			_log.error("Could not read object IDs from DB. Please Check Your Data.");
			throw new Exception("Could not initialize the ID factory");
		}
		
		CacheManager.getInstance();
		ThreadPoolManager.getInstance();
		
		// Auth
		///if (!initCheck())
		//	scheduleTryReconnect();
		
		printSection("Scripts");
		Scripts.getInstance();
		printSection("Geodata");
		GeoEngine.load();
		printSection("");
		Strings.reload();
		GameTimeController.getInstance();
		printSection("World");
		World.init();
		printSection("Parsers");
		Parsers.parseAll();
		ItemsDAO.getInstance();
		CharacterDAO.getInstance();
		printSection("Crests");
		CrestCache.getInstance();
		printSection("Clans");
		ClanTable.getInstance();
		
		if (Config.ENABLE_COMMUNITY_ACADEMY)
		{
			AcademyList.restore();
			AcademyRewards.getInstance().load();
		}
		
		printSection("Nexus Engine");
		if (!Config.DONTLOADNEXUS)
			NexusEvents.start();
		else
			_log.info("Nexus Events Engine is Disabled!");
		
		printSection("Skills");
		SkillTreeTable.getInstance();
		printSection("Augments");
		AugmentationData.getInstance();
		printSection("");
		EnchantHPBonusTable.getInstance();
		PetSkillsTable.getInstance();
		ItemAuctionManager.getInstance();
		printSection("Scripts Initialization");
		Scripts.getInstance().init();
		printSection("Spawns");
		SpawnManager.getInstance().spawnAll();
		BoatHolder.getInstance().spawnAll();
		StaticObjectHolder.getInstance().spawnAll();
		printSection("");
		RaidBossSpawnManager.getInstance();
		DimensionalRiftManager.getInstance();
		Announcements.getInstance();
		LotteryManager.getInstance();
		PlayerMessageStack.getInstance();
		if (Config.AUTODESTROY_ITEM_AFTER > 0)
			ItemsAutoDestroy.getInstance();
		MonsterRace.getInstance();
		printSection("Seven Signs");
		SevenSigns.getInstance();
		SevenSignsFestival.getInstance();
		SevenSigns.getInstance().updateFestivalScore();
		AutoSpawnManager.getInstance();
		SevenSigns.getInstance().spawnSevenSignsNPC();
		printSection("");
		if(Config.DEADLOCKCHECK_INTERVAL > 0)
			new DeadlockDetector().start();
		
		if (Config.ENABLE_OLYMPIAD)
		{
			Olympiad.load();
			Hero.getInstance();
		}
		PetitionManager.getInstance();
		CursedWeaponsManager.getInstance();
		if (!Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
			_log.info("CoupleManager initialized");
		}
		ItemHandler.getInstance();
		AdminCommandHandler.getInstance().log();
		UserCommandHandler.getInstance().log();
		VoicedCommandHandler.getInstance().log();
		TaskManager.getInstance();
		AutoHuntingManager.getInstance();
		AdminTable.getInstance();
		printSection("Residence");
		ResidenceHolder.getInstance().callInit();
		EventHolder.getInstance().callInit();
		CastleManorManager.getInstance();
		printSection("");
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		_log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
		
		CoupleManager.getInstance();
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionShipManager.getInstance();
		
		printSection("Hellbound");
		HellboundManager.getInstance();
		NaiaTowerManager.getInstance();
		NaiaCoreManager.getInstance();
		
		printSection("Gracia");
		SoDManager.getInstance();
		SoIManager.getInstance();
		BloodAltarManager.getInstance();
		
		MiniGameScoreManager.getInstance();
		
		printSection("Custom Shits");
		ImagesChache.getInstance();
		
		L2TopManager.getInstance();
		
		//BotCheckerManager.load();
		
		//MMOTopManager.getInstance();
		
		//SMSWayToPay.getInstance();
		
		PlayerTops.getInstance();

		if (Config.ENABLE_PREMIUM_SYSTEM) {
			printSection("Premium system");
			PremiumSystemOptionsData.getInstance();
		}

		if (Config.ENABLE_ACHIEVEMENTS)
			Achievements.getInstance();
		
		if (Config.ENABLE_CUSTOM_AUCTION)
			AuctionManager.init();
		
		SchemeBufferManager.getInstance();
		
		VoteManager.getInstance();
		
		ChampionTemplateTable.getInstance();
		
		if (Config.ACTIVITY_REWARD_ENABLED)
			ActivityReward.getInstance();
		
		if (Config.ENABLE_VISUAL_SYSTEM)
			Visuals.getInstance().load();
		
		if (Config.ENABLE_EMOTIONS)
		{
			EmotionsTable.init();
			_log.info("Emotions Loaded....");
		}
		
		if (Config.ENABLE_PLAYER_KILL_SYSTEM)
			PlayerKill.getInstance().init();

		if (Config.ENABLE_FAKEPC)
			FakePcsTable.getInstance().init();

		if (Config.ENABLE_CAPTCHA)
		{
			new CaptchaImage();
			_log.info("Captcha system loaded.");
		}
		
		// If there is no such var in server var create such with default false.
		if(ServerVariables.getString("DonationBonusActive", "").isEmpty())
			ServerVariables.set("DonationBonusActive", false);

		if (ServerVariables.getBool("DonationBonusActive", true))
			DonationBonusDay.getInstance().continuePormotion();
		else
			DonationBonusDay.getInstance().stopPromotion();

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new DonatePaymentsManager(), Config.DONATION_CHECK_DELAY, Config.DONATION_CHECK_DELAY);
		
		BetaServer.getInstance();
		
		VotingRewardAPI.getInstance();

		if (Config.ENABLE_DONATION_READER) {
			DonationPaymentsDAO.getInstance();
			_log.info("Donation auto check system is enabled.");
		}

		Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, Shutdown.RESTART);
		_log.info("GameServer Started");
		_log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
		
		GamePacketHandler gph = new GamePacketHandler();
		
		// Multiple IPs and ports supported.
		_selectorThreads = new SelectorThread[Config.PORTS_GAME.length];
		for (int i = 0; i < Config.PORTS_GAME.length; i++)
		{
			InetAddress serverAddr = InetAddress.getByName(Config.EXTERNAL_HOSTNAME[i]);
			_selectorThreads[i] = new SelectorThread<GameClient>(Config.SELECTOR_CONFIG, gph, gph, gph, null);
			_selectorThreads[i].openServerSocket(serverAddr, Config.PORTS_GAME[i]);
			_selectorThreads[i].start();
		}
		
		if (Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART)
			ThreadPoolManager.getInstance().schedule(new RestoreOfflineTraders(), 30000L);
		
		if (Config.RESTORE_OFFLINE_BUFFERS_ON_RESTART)
			ThreadPoolManager.getInstance().schedule(new RestoreOfflineBuffers(), 30000L);
		
		if (!Config.DONTAUTOANNOUNCE)
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoAnnounce(), 60000, 60000);

		getListeners().onStart();
		
		if (Config.IS_TELNET_ENABLED)
			statusServer = new TelnetServer();
		else
			_log.info("Telnet server is currently disabled.");
		
		if(Config.RRD_ENABLED)
			RRDTools.init();

		printSection("Memory");
		String memUsage = new StringBuilder().append(StatsUtils.getMemUsage()).toString();
		for (String line : memUsage.split("\n"))
			_log.info(line);
		printSection("");
		_log.info("Server loaded for: " + (System.currentTimeMillis() - startMs) / 1000 + " seconds.");
		
		AuthServerCommunication.getInstance().start();
		_hasLoaded = true;
	}
	
	public GameServerListenerList getListeners()
	{
		return _listeners;
	}
	
	public static GameServer getInstance()
	{
		return _instance;
	}
	
	public <T extends GameListener> boolean addListener(T listener)
	{
		return _listeners.add(listener);
	}
	
	public <T extends GameListener> boolean removeListener(T listener)
	{
		return _listeners.remove(listener);
	}
	
	public static void checkFreePorts()
	{
		boolean binded = false;
		while (!binded)
			for (int i = 0; i < Config.PORTS_GAME.length; i++)
				try
				{
					ServerSocket ss;
					if (Config.EXTERNAL_HOSTNAME[i].equalsIgnoreCase("*"))
						ss = new ServerSocket(Config.PORTS_GAME[i]);
					else
						ss = new ServerSocket(Config.PORTS_GAME[i], 50, InetAddress.getByName(Config.EXTERNAL_HOSTNAME[i]));
					ss.close();
					binded = true;
				}
				catch (Exception e)
				{
					_log.warn("Port " + Config.PORTS_GAME[i] + " is already binded. Please free it and restart server.");
					binded = false;
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e2)
					{
					}
				}
	}
	
	public static void printSection(String s)
	{
		if (s.isEmpty())
			s = "------------------------------------------------------------------------------";
		else
		{
			s = "=[ " + s + " ]";
			while (s.length() < 78)
				s = "-" + s;
		}
		_log.info(s);
	}
	
	public static void main(String[] args) throws Exception
	{
		for (String arg : args)
		{
			int index = arg.indexOf(':');
			if (index == -1)
				System.out.println("WARNING: Invalid argument [" + arg + "] index of ':' expected, but none found. Skipping argument");
			else
			{
				_args.put(arg.substring(0, index), arg.substring(index+1));
				System.out.println("Loaded server argument: " + arg);
			}
		}
		
		new GameServer();
	}
	
	/**
	 * 
	 * @param argument : the program argument which is unput on launch options.
	 * @return argument value or null if argument not found.
	 */
	public static String getArgumentValue(String argument)
	{
		return _args.get(argument);
	}
	
	/**
	 * 
	 * @param argument : the program argument which is unput on launch options.
	 * @param def : default value if argument not found.
	 * @return argument value or default value if argument not found.
	 */
	public static String getArgumentValue(String argument, String def)
	{
		String ret = _args.get(argument);
		if (ret == null)
			return def;
		
		return ret;
	}
	
	public Version getVersion()
	{
		return version;
	}
	
	public String getRevisionNumber()
	{
		return version.getRevisionNumber();
	}
	
	public String getBuildDate()
	{
		return version.getBuildDate();
	}
	
	public TelnetServer getStatusServer()
	{
		return statusServer;
	}
	
	public boolean hasLoaded()
	{
		return _hasLoaded;
	}
	
	public boolean initCheck()
	{
		try
		{
			try
			{
				URL site = new URL("http://www.test.charlitaxi.com");
				con = (HttpURLConnection) site.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = con.getResponseCode();
				
				if (responseCode == 403)
				{
					printSection("Authentication");
					_log.error("Error code: 403 - Wrong license. Server will be terminated.");
					System.exit(0);
				}
				
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			}
			catch (UnknownHostException e)
			{
				_log.warn("Can't connect to the authentication server. Next try in 10 seconds.");
				retryConnection = true;
				return false;
			}
			catch (IOException e) 
			{
				_log.warn("Can't connect to the authentication server. Next try in 10 seconds.");
				retryConnection = true;
				return false;
			}
			catch (Exception e)
			{
				_log.warn("Can't connect to the authentication server. Next try in 10 seconds.");
				retryConnection = true;
				return false;
			}
			
			String inputLine;
			boolean allisok = false;
			
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.contains("Welcome"))
				{
					allisok = true;
					printSection("Authentication");
					_log.info("Authentication was successful. Server is starting...");
					break;
				}
				else if (inputLine.contains("Forbidden"))
				{
					allisok = false;
					retryConnection = false;
					printSection("Authentication");
					_log.error("Wrong license. Server will be terminated.");
					break;
				}
			}
			
			in.close();
			closeSockets();
			
			return allisok;
		}
		catch (Exception e)
		{
			_log.warn("Connection to the authentication server timed out. Next try in 10 seconds.");
			retryConnection = true;
			closeSockets();
			return false;
		}
	}
	
	private final void closeSockets()
	{
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			_log.warn("IOException on close - " + e);
		}
	}
	
	private final void scheduleTryReconnect()
	{
		if(!loaded && retryConnection)
		{
			ThreadPoolManager.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						tryReconnect();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}, 10000);
		}
		else
		{
			printSection("Authentication");
			_log.error("Authentication has failed... contact the administrator.");
			System.exit(0);
		}
	}
	
	private final void tryReconnect() throws Exception
	{
		if(loaded || !retryConnection)
			return;
		
		if (retriesCount++ > 3)
		{
			printSection("Authentication");
			_log.error("Authentication failed after 3 retries... server will be terminated. Please contact the administrator.");
			System.exit(0);
		}
		
		if (!initCheck())
			scheduleTryReconnect();
		_log.info("Reconnecting to the auth server...");
		
		
	}
}