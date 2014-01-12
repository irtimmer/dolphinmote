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

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;

public class DolphinConnection {

	private final static int ANNOUNCE_PORT = 4431;

	private DatagramSocket socket;

	private ByteBuffer buffer;
	private DatagramPacket packet;

	private int buttonMask;
	private int x, y, z;

	private boolean connected;

	public DolphinConnection() throws SocketException {
		socket = new DatagramSocket(ANNOUNCE_PORT);

		buffer = ByteBuffer.allocate(64);
		buffer.order(ByteOrder.BIG_ENDIAN);
		packet = new DatagramPacket(buffer.array(), buffer.limit());
	}

	public void setServer(InetAddress host, int port) throws SocketException {
		socket.close();
		socket = new DatagramSocket();

		packet.setAddress(host);
		packet.setPort(port);

		connected = true;
	}

	public void sendAccelerator(final float x, final float y, final float z) {
		this.x = (int)(x * 1024f * 1024f / 9.8f);
		this.y = (int)(y * 1024f * 1024f / 9.8f);
		this.z = (int)(z * 1024f * 1024f / 9.8f);

		sendPacket();
	}

	private void sendPacket() {
		if (!connected)
			return;

		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (DolphinConnection.this) {
					buffer.clear();
					buffer.put((byte) 0xde);
					buffer.put((byte) 0);

					buffer.put((byte) (UdpConstants.PACKET_BUTTONS | UdpConstants.PACKET_ACCEL));

					buffer.putInt(x);
					buffer.putInt(y);
					buffer.putInt(z);

					buffer.putInt(buttonMask);

					try {
						packet.setLength(buffer.position());
						socket.send(packet);
					} catch (IOException e) {
						Log.e("Packet", e.getMessage(), e);
					}
				}
			}
		}).start();
	}

	public void receiveDiscover(final DiscoverListener listener) {
		if (connected)
			return;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (DolphinConnection.this) {
						HashSet<Short> discovered = new HashSet<Short>();

						while (!connected) {
							buffer.clear();
							socket.receive(packet);
							buffer.limit(packet.getLength());

							if (buffer.get()!=(byte) 0xdf)
								continue;

							short id = buffer.getShort();

							if (discovered.contains(id))
								return;

							byte index = buffer.get();
							if (index<0 || index>3)
								continue;

							int port = buffer.getShort() & 0xffff;

							int length = buffer.get();
							if (length != buffer.remaining())
								continue;

							byte[] name = new byte[length];
							buffer.get(name, 0, length);

							discovered.add(id);
							listener.onDiscover(new UdpMote(packet.getAddress(), port, index, new String(name)));
						}
					}
				} catch (IOException e) {
					Log.e("Packet", e.getMessage(), e);
				}
			}
		}).start();
	}

	public void keyDown(int key) {
		System.out.println(key);
		buttonMask |= key;
		sendPacket();
	}

	public void keyUp(int key) {
		buttonMask &= ~key;
		sendPacket();
	}

	public void close() {
		socket.close();
	}

	public interface DiscoverListener {

		public void onDiscover(UdpMote mote);

	}

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

		public InetAddress getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public String toString() {
			return name + " " + index + " (" + host.getHostAddress() + ":" + port + ")";
		}
	}

}
