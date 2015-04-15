package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public abstract class BaseGeneralXeengActivity extends BaseXeengActivity {

	public static final String FROM_ACTIVITY = "from_activity";

	protected LocalBroadcastManager mLocalBroadcastManager;
	protected String mFromActivity;

	protected Button btnBack;

	private static BaseGeneralXeengActivity instance = null;

	public static BaseGeneralXeengActivity getInstance() {
		return instance;
	}

	// TungHX
	// protected Object mutexObj = new Object();
	//
	// public void waitFor() {
	// synchronized (mutexObj) {
	// try {
	// Log.d("PhomGame", "wait for mutex");
	// mutexObj.wait(3000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// public void doNotifyAll() {
	// synchronized (mutexObj) {
	// Log.d("PhomGame", "Notify OOOOOOOthers");
	// mutexObj.notifyAll(); // TungHX
	// }
	// }

	protected static MediaPlayer backgroundMusicPlayer;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		instance = this;
		// Change app states
		CustomApplication.shareApplication().currentActivity = this;
		CustomApplication.shareApplication().wasInBackground = false;
		CustomApplication.shareApplication().stopAppInBackgroundTimer();
		if (CustomApplication.shareApplication().wasCloseSocket) {
			CustomApplication.shareApplication().wasCloseSocket = false;
			GameSocket.shareSocket().reconnect();

			// Start a new thread to wait until GameSocket is ready to send
			// reconnect request
			new Thread() {
				public void run() {
					try {
						while (!GameSocket.shareSocket().isConnected()) {
							Thread.sleep(500);
						}
						BusinessRequester.getInstance().reconnect(1, false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				;
			}.start();
		}

		if (GameData.shareData() == null) {
			showFatalError();
		}
		try {
			playBackgroundMusic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	;

	protected void playBackgroundMusic() {
		if (UserPreference.sharePreference().isSoundBgMusicOn()) {
			try {
				if (backgroundMusicPlayer == null) {
					backgroundMusicPlayer = MediaPlayer.create(
							BaseGeneralXeengActivity.this,
							R.raw.a_bright_future);
					backgroundMusicPlayer.setLooping(true); // Set looping
					backgroundMusicPlayer.setVolume(100, 100);
				}

				if (!backgroundMusicPlayer.isPlaying()) {
					backgroundMusicPlayer.start();
				}
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected void pauseBackgroundMusic() {
		if (backgroundMusicPlayer != null)
			backgroundMusicPlayer.pause();
	}

	@Override
	protected void onPause() {
		// Change app states
		super.onPause();
		CustomApplication.shareApplication().wasInBackground = true;
		CustomApplication.shareApplication().startAppInBackgroundTimer();

		pauseBackgroundMusic();
	}

	;

	/**
	 * Show join or create table dialog.
	 * 
	 * @param msg
	 */
	protected void showTableMessages(String msg) {
		if (msg.contains("không đủ tiền")) {
			final BasicDialog dialog = new BasicDialog(
					BaseGeneralXeengActivity.this, "Thông báo", msg, "Đóng",
					"Nạp tiền");
			dialog.setPositiveOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent intent = new Intent(BaseGeneralXeengActivity.this,
							StoreActivity.class);
					startActivity(intent);
				}
			});
			dialog.show();
		} else {
			alert(msg);
		}
	}

	@Override
	protected void registerReceiveNotification() {
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_LOGOUT));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_INVITE_PLAY_NEW_GAME));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_ENTER_ZONE));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_JOIN_MATCH));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_USER_INFO));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_SEND_PRIVATE_MESSAGE));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_RECONNECTION));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_MULTIPLE_LOGIN));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_RECEIVE_FREE_GOLD));
	}

	@Override
	protected void unregisterReceiveNotification() {
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MessageService.INTENT_LOGOUT.equalsIgnoreCase(intent
					.getAction())) {

				hideLoading();
				Intent i = new Intent(BaseGeneralXeengActivity.this,
						LoginActivity.class);
				ComponentName cn = i.getComponent();
				Intent loginIntent = IntentCompat.makeRestartActivityTask(cn);
				startActivity(loginIntent);
				// getWindow().setWindowAnimations(0);

			} else if (MessageService.INTENT_ENTER_ZONE.equalsIgnoreCase(intent
					.getAction())) {

				hideLoading();
				Intent nextActivity = new Intent(BaseGeneralXeengActivity.this,
						TableListActivity.class);
				BaseGeneralXeengActivity.this.startActivity(nextActivity);

			} else if (MessageService.INTENT_INVITE_PLAY_NEW_GAME
					.equalsIgnoreCase(intent.getAction())) {

				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (status) {
					String string = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);

					String[] values = NetworkUtils.stringSplit(string,
							NetworkUtils.ELEMENT_SEPERATOR);

					long invitingPlayerId = Long.parseLong(values[0]);
					String invitingPlayerName = values[1];
					long minCash = Long.parseLong(values[3]);

					int gameId = Integer.parseInt(values[2]);
					long requestId = Long.parseLong(values[4]);

					int matchId = Integer.parseInt(values[5]);

					String inviteString = new StringBuffer(invitingPlayerName)
							.append(" mời bạn chơi game ")
							.append(CommonUtils.getGameName(gameId)).toString();

					showInviteRequester(inviteString, requestId,
							invitingPlayerId, gameId, matchId);
				}
			} else if (MessageService.INTENT_SEND_PRIVATE_MESSAGE
			.equalsIgnoreCase(intent.getAction())) {
				// Toast.makeText(BaseGeneralXeengActivity.this,
				// intent.getStringExtra(NetworkUtils.MESSAGE_INFO),
				// Toast.LENGTH_SHORT).show();
			} else if (MessageService.INTENT_JOIN_MATCH.equalsIgnoreCase(intent
					.getAction())) {
				Log.v("BaseGeneralXeengActivity", " ---> INTENT_JOIN_MATCH ");
				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (status) {

					Intent gameActivity = new Intent(
							BaseGeneralXeengActivity.this, GameData.shareData()
									.getGame().activityClass);
					if (gameActivity != null) {
						startActivity(gameActivity);
					}
				} else {

					final String msg = intent.getExtras().getString(
							NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						showTableMessages(msg);
					} else {
						alert("Có lỗi xảy ra");
					}

				}
			} else if (MessageService.INTENT_RECONNECTION
					.equalsIgnoreCase(intent.getAction())) {
				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (status) {
					String message = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);

					if (message == null || message.length() < 1) {
						showNetworkError();
					}
				}
			} else if (MessageService.INTENT_MULTIPLE_LOGIN
					.equalsIgnoreCase(intent.getAction())) {
				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (!status) {
					String message = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					BasicDialog dialog = new BasicDialog(
							BaseGeneralXeengActivity.this, "Thông báo",
							message, "", "Về login");
					dialog.setPositiveOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									BaseGeneralXeengActivity.this,
									LoginActivity.class);

							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});

					dialog.setCancelable(false);
					dialog.show();
				}
			} else if (MessageService.INTENT_RECEIVE_FREE_GOLD
					.equalsIgnoreCase(intent.getAction())) {
				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				Toast.makeText(BaseGeneralXeengActivity.this,
						intent.getStringExtra(NetworkUtils.MESSAGE_INFO),
						Toast.LENGTH_SHORT).show();
				if (status) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							BusinessRequester.getInstance().getUserInfo(
									GameData.shareData().getMyself().id);
						}
					}, 150);
				}
			}

		}
	};

	/**
	 * Go back to LoginActivity
	 */
	protected void logout() {
		showLoading("Đăng đăng xuất...");
		BusinessRequester.getInstance().logout();
		GameData.shareData().clean();

		// TODO: go back to login activity when receive message from server
		// Clear all activities and go back to LoginActivity
		hideLoading();
		Intent i = new Intent(this, LoginActivity.class);
		ComponentName cn = i.getComponent();
		Intent loginIntent = IntentCompat.makeRestartActivityTask(cn);
		startActivity(loginIntent);
		// getWindow().setWindowAnimations(0);

		if (backgroundMusicPlayer != null) {
			backgroundMusicPlayer.stop();
			backgroundMusicPlayer.release();
			backgroundMusicPlayer = null;
		}
	}

	private long lastInviteId = 0;

	protected void showInviteRequester(String string, final long requestID,
			final long invitePlayer, final int gameID, final int matchId) {
		if (!UserPreference.sharePreference().isAutoDenyInvitation()) {
			if (lastInviteId == invitePlayer) {
				return;
			}

			lastInviteId = invitePlayer;

			final BasicDialog dialog = confirm(string);
			dialog.setPositiveOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					MessageService.initDataGame(gameID);
					BusinessRequester.getInstance().acceptedInvite(requestID,
							invitePlayer, 1, matchId);
				}
			});
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					lastInviteId = 0;
				}
			});
		}
	}

}
