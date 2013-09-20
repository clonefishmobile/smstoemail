package com.clonefish.smstoemail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public abstract class NetworkConnection {
	private BroadcastReceiver connectivityReceiver;
	private boolean network;
	private int bandwidth;

	private final Activity game;

	public NetworkConnection(Activity game) {
		this.game = game;

		network = isDataConnected();
		bandwidth = isHighBandwidth();

		connectivityReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				network = isDataConnected();
				bandwidth = isHighBandwidth();

					Toast.makeText( context, "Connected: " + network + " Bandwidth: " + bandwidth, Toast.LENGTH_SHORT ).show();

				// При изменении состояния сети ставим необходимость перепинговаться
				onNetworkStateChanged();
			}
		};
	}

	public abstract void onNetworkStateChanged();

	private boolean isDataConnected() {
		try {
			ConnectivityManager cm = (ConnectivityManager) game.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}
	}

	private int isHighBandwidth() {
		ConnectivityManager cm = (ConnectivityManager) game.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				WifiManager wm = (WifiManager) game.getSystemService(Context.WIFI_SERVICE);
				return wm.getConnectionInfo().getLinkSpeed();
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager tm = (TelephonyManager) game.getSystemService(Context.TELEPHONY_SERVICE);
				return tm.getNetworkType();
			}
		}
		return 0;
	}

	public boolean isConnected() {
		return network;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public void pause() {
		game.unregisterReceiver(connectivityReceiver);
	}

	public void resume() {
		game.registerReceiver(connectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
	}
}
