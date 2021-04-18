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

import java.util.ArrayList;

import l2r.gameserver.l2acp.models.MapPlayer;
import l2r.gameserver.l2acp.responses.GetAllOnlinePlayersForMapResponse;
import l2r.gameserver.l2acp.responses.Response;
import com.google.gson.JsonObject;

import l2r.gameserver.model.GameObjectsStorage;
import l2r.gameserver.model.Player;

public class GetAllOnlinePlayersForMapRequest extends Request {

	@Override
	public Response getResponse() {
		ArrayList<MapPlayer> mapPlayers = new ArrayList<>();
		
		for(Player player : GameObjectsStorage.getAllPlayers()){
			mapPlayers.add(new MapPlayer(player.getName(), player.getTitle(), player.getLevel(), player.getX(), player.getY()));
		}
		
		return new GetAllOnlinePlayersForMapResponse(200,"Success", mapPlayers.toArray(new MapPlayer[mapPlayers.size()]));
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
	}
}
