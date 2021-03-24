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
import com.elfocrash.l2acp.requests.Request;
import com.elfocrash.l2acp.requests.RegisterRequest;
import com.elfocrash.l2acp.requests.TelegramChatRequest;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author netvirus
 *
 */
public enum Requests
{
	API_REGISTER_USER(1, RegisterRequest::new),
	API_ANNOUNCE(2, AnnounceRequest::new),
	API_CHAT_MESSAGE(3, TelegramChatRequest::new);

	int id;
	Supplier<Request> ctor;

	Requests(int id, Supplier<Request> ctor) {
		this.id = id;
		this.ctor = ctor;
	}

	public static Request getById(int id) {
		Requests wrapper = Arrays.stream(Requests.values()).filter(r -> r.id == id).findAny().orElse(null);
		return wrapper != null ? wrapper.ctor.get() : null;
	}
}
