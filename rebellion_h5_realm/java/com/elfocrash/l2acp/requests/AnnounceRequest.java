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

import com.elfocrash.l2acp.responses.Response;
import com.google.gson.JsonObject;
import l2r.gameserver.Announcements;


public class AnnounceRequest extends Request {

	private String Text;

	@Override
	public Response getResponse() {
		
		Announcements.getInstance().announceToAll(Text);
		
		return new Response(200,"Successfully announced!");
	}

	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		Text = content.get("Message").getAsString();
	}
}
