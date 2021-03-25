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

import l2r.gameserver.l2acp.models.AnalyticsPlayerData;
import l2r.gameserver.l2acp.responses.GetAnalyticsPlayersResponse;
import l2r.gameserver.l2acp.responses.Response;
import l2r.gameserver.l2acp.util.Helpers;
import com.google.gson.JsonObject;

public class GetAnalyticsPlayersRequest extends Request {

	@Override
	public Response getResponse() {
		ArrayList<AnalyticsPlayerData> data = Helpers.getTopAnalyticsPlayersData(100);
		
		return new GetAnalyticsPlayersResponse(200,"Success", data.toArray(new AnalyticsPlayerData[data.size()]));
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
	}
}
