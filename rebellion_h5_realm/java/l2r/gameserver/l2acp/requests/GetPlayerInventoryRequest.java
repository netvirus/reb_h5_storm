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
package l2r.gameserver.l2acp.requests;

import l2r.gameserver.l2acp.models.InventoryInfo;
import l2r.gameserver.l2acp.responses.GetPlayerInventoryResponse;
import l2r.gameserver.l2acp.responses.Response;
import l2r.gameserver.l2acp.util.Helpers;
import com.google.gson.JsonObject;
import l2r.gameserver.model.GameObjectsStorage;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.items.ItemInstance;

import java.util.ArrayList;

public class GetPlayerInventoryRequest extends Request {

	private String Username;
	
	@Override
	public Response getResponse() {
		Player player = GameObjectsStorage.getPlayer(Username);
		if(player == null){
			player = Player.restore(Helpers.getPlayerIdByName(Username));
		}
		ArrayList<InventoryInfo> invInfo = new ArrayList<>();
		for(ItemInstance item : player.getInventory().getItems()){
			invInfo.add(new InventoryInfo(item.getObjectId(), item.getItemId(), item.getCount(), item.isEquipped(), item.getEnchantLevel()));
		}
		return new GetPlayerInventoryResponse(200,"Success", invInfo.toArray(new InventoryInfo[invInfo.size()]));
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		Username = content.get("Username").getAsString();
	}
}
