package com.tv.xeeng;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.manager.Logger;
import com.tv.xeeng.manager.Reachability;
import com.tv.xeeng.R;

public class CustomApplication extends Application {

	static CustomApplication instance;
	private Timer mBackgroundTimer;
	private TimerTask mBackgroundTask;
	private Tracker tracker;
	
	public boolean wasCloseSocket;
	public boolean wasInBackground;
	public boolean wasInGame = false;
	public boolean finishGameInBackground = false;
	public boolean shouldShowHomeTutorial = true;
	public boolean shouldShowTableTutorial = true;

	private final long MAX_SOCKET_ALIVE = 60 * 1000;

	public Activity currentActivity;

	public final static String INTENT_APP_WAS_IN_BACKGROUND = "com.vmgames.app.in.bg";

	public static CustomApplication shareApplication() {

		return instance;
	}

	public synchronized Tracker getTracker() {

		if (tracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			analytics.getLogger().setLogLevel(LogLevel.VERBOSE);
			tracker = analytics.newTracker(R.xml.analytics);
		}
		return tracker;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		instance = this;
		GameSocket.initialize();
		Reachability.registerReachability(this);
		
//		backgroundThread.start();
	}
	
	public void startAppInBackgroundTimer() {

		Logger.getInstance().warn(this, "Start app in background timer");
		mBackgroundTimer = new Timer();
		if (!wasInGame) {

			mBackgroundTask = new TimerTask() {

				@Override
				public void run() {

					Logger.getInstance().warn(this, "App will close socket");
					CustomApplication.shareApplication().wasCloseSocket = true;
					GameSocket.shareSocket().close();
				}
			};
			mBackgroundTimer.schedule(mBackgroundTask, MAX_SOCKET_ALIVE);
		}
	}

	public void stopAppInBackgroundTimer() {
		Logger.getInstance().warn(this, "STOP appinbackground timer");
		
		if (mBackgroundTask != null) {

			mBackgroundTask.cancel();
			mBackgroundTask = null;
		}

		if (mBackgroundTimer != null) {

			mBackgroundTimer.cancel();
			mBackgroundTimer = null;
		}
	}
}
