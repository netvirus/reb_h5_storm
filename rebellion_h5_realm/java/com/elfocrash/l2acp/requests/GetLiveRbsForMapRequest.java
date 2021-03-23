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
package com.elfocrash.l2acp.requests;

import com.elfocrash.l2acp.models.MapMob;
import com.elfocrash.l2acp.responses.GetLiveRbsForMapResponse;
import com.elfocrash.l2acp.responses.Response;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class GetLiveRbsForMapRequest extends Request {

	@Override
	public Response getResponse() {
		ArrayList<MapMob> mapMobs = new ArrayList<>();
		
//		for(L2RaidBossInstance boss : RaidBossSpawnManager.getInstance().getBosses().values()){
//			MapMob mob = new MapMob(boss.getName(),boss.getMaxHp(),(int)boss.getCurrentHp(),boss.getLevel(),boss.getX(),boss.getY());
//			mapMobs.add(mob);
//		}
		
		return new GetLiveRbsForMapResponse(200,"Success", mapMobs.toArray(new MapMob[mapMobs.size()]));
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
	}
}
