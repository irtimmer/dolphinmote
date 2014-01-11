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
