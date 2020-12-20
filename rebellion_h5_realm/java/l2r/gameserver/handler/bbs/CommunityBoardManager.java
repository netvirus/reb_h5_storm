package l2r.gameserver.handler.bbs;

import l2r.gameserver.Config;
import l2r.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommunityBoardManager
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoardManager.class);
	private static final CommunityBoardManager _instance = new CommunityBoardManager();

	private final Map<String, ICommunityBoardHandler>_handlers = new HashMap<String, ICommunityBoardHandler>();
	private final StatsSet _properties = new StatsSet();

	public static CommunityBoardManager getInstance()
	{
		return _instance;
	}

	private CommunityBoardManager()
	{
		//
	}

	public void registerHandler(ICommunityBoardHandler commHandler)
	{
		for(String bypass : commHandler.getBypassCommands())
		{
			if(_handlers.containsKey(bypass))
				_log.warn("CommunityBoard: dublicate bypass registered! First handler: " + _handlers.get(bypass).getClass().getSimpleName() + " second: " + commHandler.getClass().getSimpleName());

			_handlers.put(bypass, commHandler);
		}
	}

	public void removeHandler(ICommunityBoardHandler handler)
	{
		for(String bypass : handler.getBypassCommands())
			_handlers.remove(bypass);
		_log.info("CommunityBoard: " + handler.getClass().getSimpleName() + " unloaded.");
	}

	public ICommunityBoardHandler getCommunityHandler(String bypass)
	{
		if(!Config.COMMUNITYBOARD_ENABLED || _handlers.isEmpty())
			return null;

		return _handlers.get(bypass);
	}
}
