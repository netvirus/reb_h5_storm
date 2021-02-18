/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * 
 * This file is part of Vote Rewarding System.
 * 
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.randoms.votingengine;

import l2r.gameserver.model.Player;

public enum UserScope
{
	ACCOUNT
	{
		@Override
		public String getData(Player player)
		{
			return player.getAccountName();
		}
	},
	IP
	{
		@Override
		public String getData(Player player)
		{
			return player.getIP();
		}
	};
	
	public abstract String getData(Player player);
	
	public boolean isSupported(Player player)
	{
		return true;
	}
	
	public static UserScope findByName(String name)
	{
		for (UserScope scope : values())
		{
			if (scope.name().equals(name))
			{
				return scope;
			}
		}
		return null;
	}
}