/*
 * Copyright (C) 2017  Nick Chapsas
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * L2ACP is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.l2acp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import l2r.gameserver.l2acp.models.AdminDonateListViewmodel;
import l2r.gameserver.l2acp.models.AnalyticsPlayerData;
import l2r.gameserver.l2acp.models.BuyListItem;
import l2r.gameserver.l2acp.models.DonateService;
import l2r.gameserver.l2acp.models.LuckyWheelItem;
import l2r.gameserver.model.GameObjectsStorage;
import l2r.gameserver.model.Player;
import l2r.loginserver.database.L2DatabaseFactory;


public class Helpers {
	public static int getPlayerIdByName(String name){
		String query = "SELECT obj_Id FROM characters WHERE char_name=?";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setString(1, name);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					int objId = rset.getInt("obj_Id");
					return objId;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void removeDonatePoints(String accountName, int price){
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("update accounts set donatepoints=(donatepoints - ?) WHERE login=?"))
		{
			ps.setInt(1, price);
			ps.setString(2, accountName);
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void addDonatePoints(String accountName, int price){
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("update accounts set donatepoints=(donatepoints + ?) WHERE login=?"))
		{
			ps.setInt(1, price);
			ps.setString(2, accountName);
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void deleteAllDonateItems(){
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("delete from l2acp_donateitems"))
		{
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void addDonateItems(AdminDonateListViewmodel[] items){
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement("insert into l2acp_donateitems (itemId,itemCount,enchant,price) values (?,?,?,?)");
			for(AdminDonateListViewmodel item : items){
				ps.setInt(1, item.itemid);
				ps.setInt(2, item.itemcount);
				ps.setInt(3, item.itemenchant);
				ps.setInt(4, item.itemprice);
				ps.addBatch();
			}
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void recordOnlinePlayersCount(){
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement ps = con.prepareStatement("insert into l2acp_onlineanalytics (playercount,recordedtime) values (?,?)");
			int count = GameObjectsStorage.getAllPlayers().size();
			ps.setInt(1, count);
			ps.setLong(2, System.currentTimeMillis());
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void spawn(int npcId,int x, int y, int z, int respawnTime, boolean permanent)
	{		
//		NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
//
//
//		try
//		{
//			L2Spawn spawn = new L2Spawn(template);
//			spawn.setLoc(x, y, z, 0);
//			spawn.setRespawnDelay(respawnTime);
//
//			if (RaidBossSpawnManager.getInstance().getValidTemplate(spawn.getNpcId()) != null)
//			{
//				if (RaidBossSpawnManager.getInstance().isDefined(spawn.getNpcId()))
//				{
//					//activeChar.sendMessage("You cannot spawn another instance of " + template.getName() + ".");
//					return;
//				}
//
//				spawn.setRespawnMinDelay(43200);
//				spawn.setRespawnMaxDelay(129600);
//				RaidBossSpawnManager.getInstance().addNewSpawn(spawn, 0, 0, 0, permanent);
//			}
//			else
//			{
//				SpawnTable.getInstance().addNewSpawn(spawn, permanent);
//				spawn.doSpawn(false);
//				if (permanent)
//					spawn.setRespawnState(true);
//			}
//
//			if (!permanent)
//				spawn.setRespawnState(false);
//
//
//		}
//		catch (Exception e)
//		{
//		}
	}
	
	public static int getDonatePoints(String accountName){
		String query = "SELECT donatepoints FROM accounts WHERE login=?";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setString(1, accountName);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					int donatepoints = rset.getInt("donatepoints");
					return donatepoints;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static String getAccountName(String charName){
		String query = "SELECT account_name FROM characters WHERE char_name=?";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setString(1, charName);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					String accName = rset.getString("account_name");
					return accName;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return charName;
	}
	
	public static ArrayList<BuyListItem> getDonateItemList(){
		
		ArrayList<BuyListItem> invInfo = new ArrayList<BuyListItem>(); 
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT itemId,itemCount,enchant,price FROM l2acp_donateitems");
			ResultSet itemList = statement.executeQuery();
			
			while (itemList.next())// fills the package
			{
				int itemId = itemList.getInt("itemId");
				int itemCount = itemList.getInt("itemCount");
				int enchant = itemList.getInt("enchant");
				int price = itemList.getInt("price");
				invInfo.add(new BuyListItem(itemId,itemCount,enchant,price));
			}
			
			itemList.close();
			statement.close();			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invInfo;
	}
	
	public static ArrayList<AnalyticsPlayerData> getTopAnalyticsPlayersData(int limit){
		
		ArrayList<AnalyticsPlayerData> invInfo = new ArrayList<AnalyticsPlayerData>(); 
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT playercount,recordedtime FROM l2acp_onlineanalytics order by recordedtime desc limit ?");
			statement.setInt(1, limit);
			
			ResultSet itemList = statement.executeQuery();
			
			while (itemList.next())// fills the package
			{
				int count = itemList.getInt("playercount");
				long time = itemList.getLong("recordedtime");
				invInfo.add(new AnalyticsPlayerData(count,time));
			}
			
			itemList.close();
			statement.close();			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invInfo;
	}
	
	public static ArrayList<LuckyWheelItem> getLuckyWheelList(){
		
		ArrayList<LuckyWheelItem> invInfo = new ArrayList<LuckyWheelItem>(); 
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT itemId,itemCount,enchant,chance FROM l2acp_luckywheelitems");
			ResultSet itemList = statement.executeQuery();
			
			while (itemList.next())// fills the package
			{
				int itemId = itemList.getInt("itemid");
				int itemCount = itemList.getInt("itemcount");
				int enchant = itemList.getInt("enchant");
				double chance = itemList.getDouble("chance");
				invInfo.add(new LuckyWheelItem(itemId,itemCount,enchant,chance));
			}
			
			itemList.close();
			statement.close();			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invInfo;
	}
	
	public static ArrayList<String> getAllPlayerNames(){
		
		ArrayList<String> names = new ArrayList<String>(); 
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters");
			ResultSet nameList = statement.executeQuery();
			
			while (nameList.next())// fills the package
			{
				String name = nameList.getString("char_name");
				names.add(name);
			}
			
			nameList.close();
			statement.close();			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return names;
	}
	
	public static void banChatOfflinePlayer(String name, int delay, boolean ban)
	{
		int level = 0;
		long value = 0;
		
		if (ban)
		{
//			level = Player.PunishLevel.CHAT.value();
			value = (delay > 0 ? delay * 60000L : 60000);
		}
		else
		{
//			level = Player.PunishLevel.NONE.value();
			value = 0;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, level);
			statement.setLong(2, value);
			statement.setString(3, name);
			
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
		}
		catch (SQLException se)
		{
		}
	}
	
	public static void jailOfflinePlayer(String name, int delay)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, -114356);
			statement.setInt(2, -249645);
			statement.setInt(3, -2984);
		//	statement.setInt(4, Player.PunishLevel.JAIL.value());
			statement.setLong(5, (delay > 0 ? delay * 60000L : 0));
			statement.setString(6, name);
			
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
			
		}
		catch (SQLException se)
		{
		}
	}
	
	public static void unjailOfflinePlayer(String name)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, 17836);
			statement.setInt(2, 170178);
			statement.setInt(3, -3507);
			statement.setInt(4, 0);
			statement.setLong(5, 0);
			statement.setString(6, name);
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
		}
		catch (SQLException se)
		{
		}
	}
	
	public static boolean changeCharAccessLevel(Player targetPlayer, String player, int lvl)
	{
		if (targetPlayer != null)
		{
			targetPlayer.setAccessLevel(lvl);
			targetPlayer.logout();
		}
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?");
				statement.setInt(1, lvl);
				statement.setString(2, player);
				statement.execute();
				int count = statement.getUpdateCount();
				statement.close();
				
				if (count == 0)
				{
					return false;
				}
			}
			catch (SQLException se)
			{				
				return false;
			}
		}
		return true;
	}
	
	public static ArrayList<DonateService> getDonateServices(){
			
			ArrayList<DonateService> invInfo = new ArrayList<DonateService>(); 
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("SELECT serviceid,servicename,price FROM l2acp_donateservices");
				ResultSet itemList = statement.executeQuery();
				
				while (itemList.next())// fills the package
				{
					int serviceId = itemList.getInt("serviceid");
					String serviceName = itemList.getString("servicename");
					int price = itemList.getInt("price");
					invInfo.add(new DonateService(serviceId,serviceName,price));
				}
				
				itemList.close();
				statement.close();			
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return invInfo;
		}
	}
