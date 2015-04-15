package com.tv.xeeng.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.net.URLDecoder;
import java.util.HashMap;

//import com.google.analytics.tracking.android.CampaignTrackingReceiver;

public class InstallReferrerReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

		// new CampaignTrackingReceiver().onReceive(context, intent);

		HashMap<String, String> values = new HashMap<String, String>();

		Bundle extras = intent.getExtras();
		Log.d("LoginActivity",
				"onReceive INSTALL_REFERRER " + extras.getString("referrer"));

		// if (extras != null) {
		// for (String key : extras.keySet()) {
		// Object value = extras.get(key);
		// Log.d("LoginActivity", String.format("%s %s (%s)", key,
		// value.toString(), value.getClass().getName()));
		//
		// }
		// }

		if (intent.hasExtra("referrer")) {

			Log.d("LoginActivity", "intent has extra referrer");

			String referrers[] = intent.getStringExtra("referrer").split("&");
			for (String referrerValue : referrers) {
				String keyValue[] = referrerValue.split("=");
				values.put(URLDecoder.decode(keyValue[0]),
						URLDecoder.decode(keyValue[1]));

				if (URLDecoder.decode(keyValue[0]).equalsIgnoreCase(
						"utm_source")) {
					ConfigData.getInstance().setParterId(
							URLDecoder.decode(keyValue[1]));
				} else if (URLDecoder.decode(keyValue[0]).equalsIgnoreCase(
						"utm_medium")) {
					ConfigData.getInstance().setRefCode(
							URLDecoder.decode(keyValue[1]));
				}

				ConfigData.getInstance().saveConfigData(context);
			}
		}

		Log.d("LoginActivity", "referrer: " + values);
	}

}
