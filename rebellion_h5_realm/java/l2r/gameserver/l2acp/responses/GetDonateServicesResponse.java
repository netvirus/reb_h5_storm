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
package l2r.gameserver.l2acp.responses;

import l2r.gameserver.l2acp.models.DonateService;

/**
 * @author Elfocrash
 *
 */
public class GetDonateServicesResponse extends Response
{
	private DonateService[] DonateServices; 
	
	public GetDonateServicesResponse(int code, String message, DonateService[] info)
	{
		super(code, message);
		DonateServices = info;		
	}
}
