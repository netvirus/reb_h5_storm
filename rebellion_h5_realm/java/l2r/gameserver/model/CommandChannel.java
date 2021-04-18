package l2r.gameserver.model;

import l2r.commons.collections.JoinedIterator;
import l2r.gameserver.model.entity.Reflection;
import l2r.gameserver.model.instances.NpcFriendInstance;
import l2r.gameserver.model.matching.MatchingRoom;
import l2r.gameserver.network.serverpackets.ExMPCCClose;
import l2r.gameserver.network.serverpackets.ExMPCCOpen;
import l2r.gameserver.network.serverpackets.ExMPCCPartyInfoUpdate;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import l2r.gameserver.network.serverpackets.SystemMessage2;
import l2r.gameserver.network.serverpackets.components.CustomMessage;
import l2r.gameserver.network.serverpackets.components.IStaticPacket;
import l2r.gameserver.network.serverpackets.components.SystemMsg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CommandChannel implements PlayerGroup
{
	public static final int STRATEGY_GUIDE_ID = 8871;
	public static final int CLAN_IMPERIUM_ID = 391;

	private final List<Party> _commandChannelParties = new CopyOnWriteArrayList<Party>();
	private Player _commandChannelLeader;
	private int _commandChannelLvl;	
	private Reflection _reflection;

	private MatchingRoom _matchingRoom;

	/**
	 * Creates a New Command Channel and Add the Leaders party to the CC
	 * @param leader
	 */
	public CommandChannel(Player leader)
	{
		_commandChannelLeader = leader;
		_commandChannelParties.add(leader.getParty());
		_commandChannelLvl = leader.getParty().getLevel();
		leader.getParty().setCommandChannel(this);
		sendPacket(ExMPCCOpen.STATIC);
	}

	/**
	 * Adds a Party to the Command Channel
	 * @param party
	 */
	public void addParty(Party party)
	{
		sendPacket(new ExMPCCPartyInfoUpdate(party, 1));
		_commandChannelParties.add(party);
		refreshLevel();
		party.setCommandChannel(this);

		for(Player member : party)
		{
			member.sendPacket(ExMPCCOpen.STATIC);
			if(_matchingRoom != null)
				_matchingRoom.broadcastPlayerUpdate(member);
		}
	}

	/**
	 * Removes a Party from the Command Channel
	 * @param party
	 */
	public void removeParty(Party party)
	{
		_commandChannelParties.remove(party);
		refreshLevel();
		party.setCommandChannel(null);
		party.sendPacket(ExMPCCClose.STATIC);
		Reflection reflection = getReflection();
		if(reflection != null)
			for(Player player : party)
				player.teleToLocation(reflection.getReturnLoc(), 0);

		if(_commandChannelParties.size() < 2)
			disbandChannel();
		else
		{
			for(Player $member : party)
			{
				$member.sendPacket(new ExMPCCPartyInfoUpdate(party, 0));
				if(_matchingRoom != null)
					_matchingRoom.broadcastPlayerUpdate($member);
			}
		}
	}

	/**
	 * Распускает Command Channel
	 */
	public void disbandChannel()
	{
		sendPacket(new SystemMessage2(SystemMsg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED));
		for(Party party : _commandChannelParties)
		{
			party.setCommandChannel(null);
			party.sendPacket(ExMPCCClose.STATIC);
			if(isInReflection())
				party.sendPacket(new SystemMessage2(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(1));
		}
		Reflection reflection = getReflection();
		if(reflection != null)
		{
			reflection.startCollapseTimer(60000L);
			setReflection(null);
		}

		if(_matchingRoom != null)
			_matchingRoom.disband();
		_commandChannelParties.clear();
		_commandChannelLeader = null;
	}

	/**
	 * Broadcast packet to every channel member
	 * @param gsp
	 */
	@Override
	public void sendPacket(IStaticPacket... gsp)
	{
		for(Party party : _commandChannelParties)
			party.sendPacket(gsp);
	}
	
	@Override
	public void sendMessage(String message)
	{
		for(Party party : _commandChannelParties)
			party.sendMessage(message);
	}

	@Override
	public void sendChatMessage(int objectId, int messageType, String charName, String text)
	{
		for(Party party : _commandChannelParties)
			party.sendChatMessage(objectId, messageType, charName, text);
	}

	/**
	 * Broadcast packet to every party leader of command channel
	 */
	public void broadcastToChannelPartyLeaders(L2GameServerPacket gsp)
	{
		for(Party party : _commandChannelParties)
		{
			Player leader = party.getLeader();
			if(leader != null)
				leader.sendPacket(gsp);
		}
	}

	/**
	 * @return list of Parties in Command Channel
	 */
	public List<Party> getParties()
	{
		return _commandChannelParties;
	}

	@Override
	public Iterator<Player> iterator()
	{
		List<Iterator<Player>> iterators = new ArrayList<Iterator<Player>>(_commandChannelParties.size());
		for(Party p : getParties())
			iterators.add(p.getMembers().iterator());
		return new JoinedIterator<Player>(iterators);
	}
	
	@Override
	public int size()
	{
		int size = 0;
		for (Party party : _commandChannelParties)
			size += party.size();
			
		return size;
	}

	@Override
	public Player getLeader()
	{
		return _commandChannelLeader;
	}

	/**
	 * @return All party members of the command channel
	 */
	@Override
	public List<Player> getMembers(Player ... excluded)
	{
		List<Player> members = new ArrayList<Player>(_commandChannelParties.size());
		for(Party party : getParties())
			members.addAll(party.getMembers(excluded));
		return members;
	}

	@Override
	public boolean containsMember(Player player)
	{
		for (Party party : _commandChannelParties)
		{
			if (party.containsMember(player))
				return true;
		}
		
		return false;
	}

	/**
	 * @return Maximum level of all members in the command channel
	 */
	@Override
	public int getLevel()
	{
		return _commandChannelLvl;
	}

	/**
	 * @param newLeader the leader of the Command Channel
	 */
	public void setChannelLeader(Player newLeader)
	{
		_commandChannelLeader = newLeader;
		sendPacket(new SystemMessage2(SystemMsg.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1).addString(newLeader.getName()));
	}

	/**
	 * Queen Ant, Core, Orfen, Zaken: MemberCount > 36<br>
	 * Baium: MemberCount > 56<br>
	 * Antharas: MemberCount > 225<br>
	 * Valakas: MemberCount > 99<br>
	 * normal RaidBoss: MemberCount > 18
	 * 
	 * @param npc
	 * @return true if proper condition for RaidWar
	 */
	public boolean meetRaidWarCondition(NpcFriendInstance npc)
	{
		if(!npc.isRaid())
			return false;
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			case 29001: // Queen Ant
			case 29006: // Core
			case 29014: // Orfen
			case 29022: // Zaken
				return size() > 36;
			case 29020: // Baium
				return size() > 56;
			case 29019: // Antharas
				return size() > 225;
			case 29028: // Valakas
				return size() > 99;
			default: // normal Raidboss
				return size() > 18;
		}
	}

	private void refreshLevel()
	{
		_commandChannelLvl = 0;
		for(Party pty : _commandChannelParties)
			if(pty.getLevel() > _commandChannelLvl)
				_commandChannelLvl = pty.getLevel();
	}

	public boolean isInReflection()
	{
		return _reflection != null;
	}

	public void setReflection(Reflection reflection)
	{
		_reflection = reflection;
	}

	public Reflection getReflection()
	{
		return _reflection;
	}

	/**
	 * Проверяет возможность создания командного канала
	 */
	public static boolean checkAuthority(final Player creator) {
		// CC могут создавать только лидеры партий, состоящие в клане ранком не ниже барона
		// if (creator.getClan() == null || !creator.isInParty() || !creator.getParty().isLeader(creator) || creator.getPledgeClass() >= 5) {
		if (creator.getClan() == null || creator.getClan().getLevel() < 5)
		{
			creator.sendPacket(SystemMsg.COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN);
			return false;
		}

		if (!creator.isInParty() || !creator.getParty().isLeader(creator)) {
			creator.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
			return false;
		}
		return true;
	}

	public MatchingRoom getMatchingRoom()
	{
		return _matchingRoom;
	}

	public void setMatchingRoom(MatchingRoom matchingRoom)
	{
		_matchingRoom = matchingRoom;
	}

	@Override
	public void sendPacket(Player exclude, IStaticPacket... packets)
	{
		for (Party party : _commandChannelParties)
			party.sendPacket(packets);
	}

	@Override
	public void sendPacketInRange(GameObject obj, int range, IStaticPacket... packets)
	{
		for (Player member : getMembers())
		{
			if (member != null && member.isInRange(obj, range))
				member.sendPacket(packets);
		}
	}

	@Override
	public void sendMessage(CustomMessage string)
	{
		for (Party party : _commandChannelParties)
			party.sendMessage(string);
	}

	@Override
	public boolean isLeader(Player player)
	{
		if (getLeader() == player)
			return true;
		
		return false;
	}

	@Override
	public List<Player> getMembersInRange(GameObject obj, int range)
	{
		List<Player> result = new ArrayList<Player>();
		for(Player member : getMembers())
		{
			if (member != null && obj.isPlayer() && member.isInRange(obj, range))
				result.add(member);
		}
		
		return result;
	}

	@Override
	public int getMemberCountInRange(GameObject obj, int range)
	{
		int count = 0;
		
		for (Player plr : getMembers())
		{
			if (plr != null && plr.isInRangeZ(obj, range))
				count++;
		}
		
		return count;
	}
	
	@Override
	public List<Integer> getMembersObjIds(Player... excluded)
	{
		List<Integer> result = new ArrayList<Integer>();
		for (Player member : getMembers(excluded))
		{
			if (member != null)
				result.add(member.getObjectId());
		}
		
		return result;
		
	}
	
	@Override
	public List<Playable> getMembersWithPets(Player ... excluded)
	{
		List<Playable> result = new ArrayList<Playable>();
		for(Player member : getMembers(excluded))
		{
			if (member == null)
				continue;
			
			result.add(member);
			
			if(member.getPet() != null)
				result.add(member.getPet());
		}
		
		return result;
	}
	
	@Override
	public Player getPlayerByName(String name)
	{
		if (name == null)
			return null;
		
		for (Player member : getMembers())
		{
			if (name.equalsIgnoreCase(member.getName()))
				return member;
		}
		
		return null;
	}
	
	@Override
	public Player getPlayer(int objId)
	{
		if (getLeader() != null && getLeader().getObjectId() == objId)
			return getLeader();
		
		for (Player member : getMembers())
		{
			if (member != null && member.getObjectId() == objId)
				return member;
		}
		
		return null;
	}
}