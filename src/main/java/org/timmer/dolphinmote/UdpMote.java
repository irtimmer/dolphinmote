/*
 *  Copyright (C) 2014 Iwan Timmer
 *
 *  This file is part of DolphinMote.
 *
 *  DolphinMote is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  DolphinMote is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.timmer.dolphinmote;

import java.net.InetAddress;

public class UdpMote {

	private InetAddress host;
	private int port;
	private int index;
	private String name;

	public UdpMote(InetAddress host, int port, int index, String name) {
		this.host = host;
		this.port = port;
		this.index = index;
		this.name = name;
	}

	public UdpMote(String name) {
		this.name = name;
	}

	public InetAddress getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String toString() {
		if (host!=null)
			return name + " " + index + " (" + host.getHostAddress() + ":" + port + ")";
		else
			return name;
	}
}
