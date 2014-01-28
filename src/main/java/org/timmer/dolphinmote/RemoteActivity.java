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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RemoteActivity extends Activity implements SensorEventListener, View.OnTouchListener {

	private DolphinConnection conn;

	private SensorManager sensors;
	private Sensor accelerometer;
	private AlertDialog discoverDialog, customDialog;

	private final static int[] BUTTONS = new int[]{ R.id.button_up, R.id.button_down, R.id.button_left, R.id.button_right, R.id.button_a, R.id.button_b, R.id.button_back, R.id.button_home, R.id.button_pause, R.id.button_1, R.id.button_2};

	public RemoteActivity() throws IOException {
		conn = new DolphinConnection();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_main);

		sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		Display display = getWindowManager().getDefaultDisplay();
		int minSize = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);

			minSize = size.y/7;
		} else
			minSize = display.getHeight()/7;

		for (int button:BUTTONS) {
			Button view = (Button) findViewById(button);
			view.setMinimumWidth(minSize);
			view.setTextSize(minSize/6);
			view.setOnTouchListener(this);
		}
	}

	@Override
	protected void onResume() {
		sensors.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		if (discoverDialog == null)
			showDiscoverDialog();

		super.onResume();
	}

	@Override
	protected void onPause() {
		sensors.unregisterListener(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		conn.close();
		super.onDestroy();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			conn.sendAccelerator(-event.values[0], -event.values[1], event.values[2]);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			View decorView = this.getWindow().getDecorView();
			decorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if( event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
			int key = 0;
			if (v.getId() == R.id.button_up)
				key = UdpConstants.BUTTON_U;
			else if (v.getId() == R.id.button_down)
				key = UdpConstants.BUTTON_D;
			else if (v.getId() == R.id.button_left)
				key = UdpConstants.BUTTON_L;
			else if (v.getId() == R.id.button_right)
				key = UdpConstants.BUTTON_R;
			else if (v.getId() == R.id.button_a)
				key = UdpConstants.BUTTON_A;
			else if (v.getId() == R.id.button_b)
				key = UdpConstants.BUTTON_B;
			else if (v.getId() == R.id.button_back)
				key = UdpConstants.BUTTON_BACK;
			else if (v.getId() == R.id.button_home)
				key = UdpConstants.BUTTON_HOME;
			else if (v.getId() == R.id.button_pause)
				key = UdpConstants.BUTTON_PAUSE;
			else if (v.getId() == R.id.button_1)
				key = UdpConstants.BUTTON_1;
			else if (v.getId() == R.id.button_2)
				key = UdpConstants.BUTTON_2;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				conn.keyDown(key);
				v.setPressed(true);
			} else {
				conn.keyUp(key);
				v.setPressed(false);
			}
		}

		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {}

	public void showDiscoverDialog() {
		final ArrayAdapter<UdpMote> list = new ArrayAdapter<UdpMote>(this, android.R.layout.select_dialog_singlechoice);
		list.add(new UdpMote(getText(R.string.custom).toString()));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.pick_mote);
		builder.setAdapter(list, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				UdpMote mote = list.getItem(which);

				if (mote.getHost()!=null) {
					try {
						conn.setServer(mote.getHost(), mote.getPort());
					} catch (SocketException e) {
						e.printStackTrace();
					}
				} else
					showCustomDialog();
			}
		});
		discoverDialog = builder.create();
		discoverDialog.show();

		discoverDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				if (customDialog!=null)
					customDialog.show();
				else if (!conn.isConnected())
					RemoteActivity.this.finish();
			}
		});

		conn.receiveDiscover(new DolphinConnection.DiscoverListener() {
			@Override
			public void onDiscover(final UdpMote mote) {
				RemoteActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						list.add(mote);
					}
				});
			}
		});
	}

	public void showCustomDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.custom);
		LayoutInflater inflater = this.getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_custom, null);

		builder.setView(view).setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				TextView hostname = (TextView) view.findViewById(R.id.server);
				TextView port = (TextView) view.findViewById(R.id.port);

				try {
					InetAddress host = InetAddress.getByName(hostname.getText().toString());
					conn.setServer(host, Integer.parseInt(port.getText().toString()));
					dialog.dismiss();
				} catch (UnknownHostException e) {
					Toast.makeText(RemoteActivity.this, getText(R.string.unknown_host), Toast.LENGTH_LONG);
				} catch (SocketException e) {
					Toast.makeText(RemoteActivity.this, getText(R.string.connection_error), Toast.LENGTH_LONG);
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				RemoteActivity.this.finish();
			}
		});
		customDialog = builder.create();
		customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				if (!conn.isConnected())
					RemoteActivity.this.finish();
			}
		});
	}

}
