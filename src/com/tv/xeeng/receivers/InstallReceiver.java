package com.tv.xeeng.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tv.xeeng.manager.UserPreference;

public class InstallReceiver extends BroadcastReceiver {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("InstallReferrer", "Install referrer received");
		Bundle extras = intent.getExtras();
		String referrer = extras.getString("referrer");
		
		Log.d("InstallReferrer", "Install referrer: " + referrer != null ? referrer : "null");

		UserPreference.sharePreference().storeInstallRefferer(referrer);
		Log.d("InstallReferrer", "Install referrer stored");
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
