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
package l2r.gameserver.l2acp.models;


public class InventoryInfo{
	private int ObjectId;
	private int ItemId;
	private long ItemCount;
	private boolean Equipped;
	private int Enchant;
	
	public InventoryInfo(int objectId, int itemId, long itemCount, boolean equipped, int enchant){
		ObjectId = objectId;
		ItemId = itemId;
		ItemCount = itemCount;
		Equipped = equipped;
		Enchant = enchant;
	}
}
