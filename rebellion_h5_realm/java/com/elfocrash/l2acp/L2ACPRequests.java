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
package com.elfocrash.l2acp;

import com.elfocrash.l2acp.requests.AnnounceRequest;
import com.elfocrash.l2acp.requests.L2ACPRequest;
import com.elfocrash.l2acp.requests.RegisterRequest;

/**
 * @author Elfocrash, netvirus
 *
 */
public enum L2ACPRequests
{
	REGISTER(1, new RegisterRequest()),
	ANNOUNCE(2, new AnnounceRequest());

	private int _requestId;
	private L2ACPRequest _clazz;

	L2ACPRequests(int requestId, L2ACPRequest clazz){
		_requestId = requestId;
		_clazz = clazz != null ? clazz : null;
	}

	public final int getRequestId()
	{
		return _requestId;
	}

	public final L2ACPRequest getRequestClazz()
	{
		return _clazz;
	}

	public static L2ACPRequest getClazzByRequestId(int clazzId)
	{
		for (L2ACPRequests clazz : L2ACPRequests.values())
			if (clazz.getRequestId() == clazzId)
				return clazz.getRequestClazz();
	}
}
