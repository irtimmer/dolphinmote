package org.timmer.dolphinmote;

/**
 * Created by iwan on 1/10/14.
 */
public class UdpConstants {

	/* Buttons*/
	public final static int  BUTTON_1 = (1<<0);
	public final static int  BUTTON_2 = (1<<1);
	public final static int  BUTTON_A = (1<<2);
	public final static int  BUTTON_B = (1<<3);
	public final static int  BUTTON_P = (1<<4);
	public final static int  BUTTON_H = (1<<6);
	public final static int  BUTTON_U = (1<<7);
	public final static int  BUTTON_D = (1<<8);
	public final static int  BUTTON_L = (1<<9);
	public final static int  BUTTON_R = (1<<10);

	/* Message types */
	public final static int PACKET_ACCEL = (1<<0);
	public final static int PACKET_BUTTONS = (1<<1);
	public final static int PACKET_IR = (1<<2);
	public final static int PACKET_NUN = (1<<3);
	public final static int PACKET_NUNACCEL = (1<<4);


}
