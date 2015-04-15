package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.itemdata.UserLevelItemData;
import com.tv.xeeng.R;

public abstract class BaseLayoutXeengActivity extends BaseGeneralXeengActivity {
	protected boolean isVisibleBtnBack = true, isVisibleBtnHelp,
			isVisibleBtnSettings;
	protected boolean isVisibleTvTitle = true, isVisibleTvPromotion,
			isVisibleTvVersion;

	protected CharSequence txtTitle;

	// Top bar
	protected Button btnBack, btnHelp, btnSettings;
	protected TextView tvTitle, tvPromotion, tvVersion;

	// Bottom bar
	protected TextView tvProfileUsername, tvProfileLevel;
	protected TextView tvProfileGold, tvProfileXeeng;
	protected ImageView imvProfileAvatar;
	protected TextView tvProfileNotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_layout_xeeng);

		initTopBar();
		initBottomBar();
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusinessRequester.getInstance().getUserInfo(
				GameData.shareData().getMyself().id);
		updateBottomBar();
	}

	@Override
	protected void registerReceiveNotification() {
		super.registerReceiveNotification();
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_USER_INFO));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_ALL_USER_LEVELS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_ADVERTISEMENT));
	}

	@Override
	protected void unregisterReceiveNotification() {
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
		super.unregisterReceiveNotification();
	}

	@Override
	protected void needReconnection() {
		try {
			BusinessRequester.getInstance().reconnect(1, false);
		} catch (Exception e) {
			showFatalError();
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (MessageService.INTENT_GET_USER_INFO.equalsIgnoreCase(intent
					.getAction())) {

				if (status) {
					Player player = intent
							.getParcelableExtra(NetworkUtils.MESSAGE_INFO);

					if (GameData.shareData().getMyself().id == player.id) {
						GameData.shareData().setMyself(player);
						updateBottomBar();
					} else {

					}
				} else {

					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					alert(msg);
				}

			} else if (MessageService.INTENT_GET_ALL_USER_LEVELS
					.equalsIgnoreCase(intent.getAction())) {

				if (status) {
					GameData.shareData().allUserLevels = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					updateBottomBar();
				} else {
					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					alert(msg);
				}
			} else if (MessageService.INTENT_ADVERTISEMENT
					.equalsIgnoreCase(intent.getAction())) {
				if (status) {
					StringBuffer ad = new StringBuffer();
					String content = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					String[] values = NetworkUtils.stringSplit(content,
							NetworkUtils.ARRAY_SEPERATOR);

					for (String value : values) {
						String[] infoValues = NetworkUtils.stringSplit(value,
								NetworkUtils.ELEMENT_SEPERATOR);

						int type = Integer.parseInt(infoValues[1]);
						if (type == 2) {
							ad.append(infoValues[0] + "\n");
						}
					}

					if (!TextUtils.isEmpty(ad)) {
						BasicDialog dialog = alert(ad);
						((TextView) dialog.findViewById(R.id.txt_message)).setGravity(Gravity.LEFT);
					}
				}
			}
		}
	};

	protected void initTopBar() {
		btnBack = (Button) findViewById(R.id.btn_back);
		btnHelp = (Button) findViewById(R.id.btn_help);
		btnSettings = (Button) findViewById(R.id.btn_setting);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvPromotion = (TextView) findViewById(R.id.tv_promotion);
		tvVersion = (TextView) findViewById(R.id.tv_version);

		updateTopBar();
	}

	protected void initBottomBar() {
		tvProfileUsername = (TextView) findViewById(R.id.tv_profile_username);
		tvProfileLevel = (TextView) findViewById(R.id.tv_profile_level);
		tvProfileGold = (TextView) findViewById(R.id.tv_profile_gold);
//		tvProfileXeeng = (TextView) findViewById(R.id.tv_profile_xeeng);
		imvProfileAvatar = (ImageView) findViewById(R.id.imv_profile_avatar);
		tvProfileNotification = (TextView) findViewById(R.id.tv_profile_notification);
	}

	private void updateTopBar() {
		int visibility;
		if (btnBack != null) {
			visibility = isVisibleBtnBack ? View.VISIBLE : View.INVISIBLE;
			btnBack.setVisibility(visibility);
			btnBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}

		if (btnHelp != null) {
			visibility = isVisibleBtnHelp ? View.VISIBLE : View.INVISIBLE;
			btnHelp.setVisibility(visibility);
		}

		if (btnSettings != null) {
			visibility = isVisibleBtnSettings ? View.VISIBLE : View.INVISIBLE;
			btnSettings.setVisibility(visibility);
			btnSettings.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
//					Intent i = new Intent(BaseLayoutXeengActivity.this,
//							UserSettingsActivity.class);
//					startActivity(i);
				}
			});
		}

		if (tvTitle != null) {
			visibility = isVisibleTvTitle ? View.VISIBLE : View.INVISIBLE;
			tvTitle.setVisibility(visibility);
			tvTitle.setText(txtTitle);
		}

		if (tvPromotion != null) {
			visibility = isVisibleTvPromotion ? View.VISIBLE : View.INVISIBLE;
			tvPromotion.setVisibility(visibility);
			if (visibility == View.VISIBLE) {
				tvPromotion.setSelected(true);
			}
		}

		if (tvVersion != null) {
			visibility = isVisibleTvVersion ? View.VISIBLE : View.INVISIBLE;
			tvVersion.setVisibility(visibility);
			tvVersion.setText("Version: " + CommonUtils.getAppVersion());
		}
	}

	private void updateBottomBar() {
		GameData gameData = GameData.shareData();
		Player myself = gameData.getMyself();

		if (tvProfileUsername != null) {
			tvProfileUsername.setText(myself.character);
		}

		setPlayerLevel();

		if (tvProfileGold != null) {
			Log.d("BaseLayoutXeengActivity", "Update player gold: "
					+ myself.cash);
			StringBuilder goldValue = new StringBuilder();
			goldValue.append(CommonUtils.formatCash(myself.cash < 0 ? 0
					: myself.cash));
			tvProfileGold.setText(goldValue.toString());
		}

		if (tvProfileXeeng != null) {
			// StringBuilder xeengValue = new StringBuilder();
			// xeengValue.append(CommonUtils.formatCash(4000));
//			tvProfileXeeng.setText(CommonUtils.formatCash(myself.xeeng) + "");
		}

		if (imvProfileAvatar != null) {
			imvProfileAvatar.setImageResource((int) myself.avatarId);
			imvProfileAvatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
//					Intent intent = new Intent(BaseLayoutXeengActivity.this,
//							UserInfoActivity.class);
//					startActivity(intent);
				}
			});
		}
		setAvatar();
	}

	private void setAvatar() {
		if (imvProfileAvatar != null) {
			imvProfileAvatar.setImageResource(R.drawable.avatar_default);
			Bitmap bitmap = CommonUtils.getBitmapFromMemCache(GameData
					.shareData().getMyself().id);
			if (bitmap != null) {
				imvProfileAvatar.setImageBitmap(bitmap);
			} else {
				// Try get it from server
				BackgroundThreadManager.post(
						new Runnable() {

							@Override
							public void run() {
								if (BusinessRequester.getInstance()
										.getUserAvatar(
												GameData.shareData()
														.getMyself().id)) {
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											setAvatar();
										}
									});
								}
							}
						});
			}
		}
	}

	public void setPlayerLevel() {
		Log.d("BaseLayout", "Setting player level");
		if (GameData.shareData().allUserLevels == null) {
			// Delay this request so server wont think we're spamming...
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					BusinessRequester.getInstance().getAllUserLevels();
				}
			}, 500);
			return;
		}
		Player myself = GameData.shareData().getMyself();
		for (UserLevelItemData userLevel : GameData.shareData().allUserLevels) {
			if (myself.cash >= userLevel.getMinGold()) {
				myself.level = userLevel;
			}
		}

		if (tvProfileLevel != null) {
			tvProfileLevel.setText(myself.level.getName());
		}
	}

	protected void showTutorialView(int resId) {
		final ImageView imvTutorial = new ImageView(this);

		imvTutorial.setImageResource(resId);
		imvTutorial.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		imvTutorial.setBackgroundColor(Color.BLACK);

		final ViewGroup rootView = ((ViewGroup) getWindow().getDecorView()
				.getRootView());
		rootView.addView(imvTutorial);

		imvTutorial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				rootView.removeView(imvTutorial);
			}
		});
	}

	// private class GetAvatarTask extends AsyncTask<Void, Void, Boolean> {
	//
	// @Override
	// protected Boolean doInBackground(Void... params) {
	// return BusinessRequester.getInstance().getUserAvatar(
	// GameData.shareData().getMyself().id);
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// super.onPostExecute(result);
	// if (result) {
	// setAvatar();
	// }
	// }
	// }

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}