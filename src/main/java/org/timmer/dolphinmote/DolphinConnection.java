package org.timmer.dolphinmote;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DolphinConnection {

	private final DatagramSocket socket;
	private InetAddress host;

	private ByteBuffer buffer;
	private DatagramPacket packet;

	private int buttonMask;
	private int x, y, z;

	public DolphinConnection(String host, int port) throws SocketException, UnknownHostException {
		socket = new DatagramSocket();
		this.host = InetAddress.getByName(host);

		buffer = ByteBuffer.allocate(64);
		buffer.order(ByteOrder.BIG_ENDIAN);
		packet = new DatagramPacket(buffer.array(), buffer.limit());
		packet.setPort(port);
		packet.setAddress(this.host);
	}

	public void sendAccelerator(final float x, final float y, final float z) {
		this.x = (int)(x * 1024f * 1024f / 9.8f);
		this.y = (int)(y * 1024f * 1024f / 9.8f);
		this.z = (int)(z * 1024f * 1024f / 9.8f);

		sendPacket();
	}

	private void sendPacket() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (socket) {
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

}
