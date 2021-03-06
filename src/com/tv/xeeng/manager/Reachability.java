package com.tv.xeeng.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Reachability {

	public boolean isReachable = false;
	public boolean isReceiving = false;

	private BroadcastReceiver receiver = null;

	public static Reachability reachability = null;

	static {
		reachability = new Reachability();
	}

	public static boolean registerReachability(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (reachability.isReceiving) {
			context.unregisterReceiver(reachability.receiver);
			reachability.isReceiving = false;
		}

		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		reachability.isReachable = info != null && info.isAvailable()
				&& info.isConnected();

		reachability.receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				reachability.isReachable = info != null && info.isAvailable()
						&& info.isConnected();
				if (info != null)
					Logger.getInstance().error(
							this,
							"REACHABILITY: connect is change : "
									+ info.getState().name());
			}

		};
		context.registerReceiver(reachability.receiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		reachability.isReceiving = true;

		return reachability.isReachable;
	}

}
