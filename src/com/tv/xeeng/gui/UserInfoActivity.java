package com.tv.xeeng.gui;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.fragments.UserInfoFriendsFragment;
import com.tv.xeeng.gui.fragments.UserInfoInventoryFragment;
import com.tv.xeeng.gui.fragments.UserInfoMailBoxFragment;
import com.tv.xeeng.gui.fragments.UserInfoProfileFragment;
import com.tv.xeeng.gui.itemdata.MailItemData;
import com.tv.xeeng.R;

public class UserInfoActivity extends BaseLayoutXeengActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private RadioGroup headerTabs;
	private FragmentManager fragmentManager;

	private TextView title;
	private RadioButton btnTabFriends;
	private RadioButton btnTabMail;

	private ArrayList<Player> friendData;
	private ArrayList<MailItemData> mailData;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_FRIENDS_LIST_BLOG)) {

				if (status) {
					friendData = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					friendData = friendData != null ? friendData
							: new ArrayList<Player>();

					int online = 0;
					for (Player player : friendData) {
						if (player.isOnline) {
							online++;
						}
					}
					final int totalOnline = online;

					Fragment fragment = fragmentManager
							.findFragmentByTag(UserInfoFriendsFragment.class
									.getName());
					if (fragment != null && fragment.isVisible()) {
						((UserInfoFriendsFragment) fragment).update(friendData);
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							btnTabFriends.setText("BẠN BÈ (" + totalOnline
									+ "/" + friendData.size() + ")");
						}
					});
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_REMOVE_SOCIAL_FRIEND)) {
				if (status) {
					BusinessRequester.getInstance().getFriendList();
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_ALL_PRIVATE_MESSAGES)) {
				mailData = intent
						.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
				mailData = mailData != null ? mailData
						: new ArrayList<MailItemData>();

				Fragment fragment = fragmentManager
						.findFragmentByTag(UserInfoMailBoxFragment.class
								.getName());
				if (fragment != null && fragment.isVisible()) {
					((UserInfoMailBoxFragment) fragment).update(mailData);
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (mailData.size() > 0) {
							btnTabMail.setText("HÒM THƯ (" + mailData.size()
									+ ")");
						} else {
							btnTabMail.setText("HÒM THƯ");
						}
					}
				});
			}
		}
	};

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
	protected void needReconnection() {
	}

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_user_info);

		btnTabFriends = (RadioButton) findViewById(R.id.btn_tab_friends);
		btnTabMail = (RadioButton) findViewById(R.id.btn_tab_vip);

		title = (TextView) findViewById(R.id.tv_title);
		title.setText(getString(R.string.activity_user_info_title));

		fragmentManager = this.getSupportFragmentManager();

		friendData = new ArrayList<Player>();
		mailData = new ArrayList<MailItemData>();

		headerTabs = (RadioGroup) findViewById(R.id.layout_header);
		headerTabs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				FragmentTransaction ft = fragmentManager.beginTransaction();
				Fragment fragment = null;
				switch (checkedId) {
				case R.id.btn_tab_profile:
					fragment = UserInfoProfileFragment.newInstance();
					break;
				case R.id.btn_tab_friends:
					BusinessRequester.getInstance().getFriendList();
					fragment = UserInfoFriendsFragment.newInstance(friendData);
					break;
				case R.id.btn_tab_inventory:
					fragment = new UserInfoInventoryFragment();
					break;
				case R.id.btn_tab_vip:
					// BusinessRequester.getInstance().getAllPrivateMessages();
					// fragment = new UserInfoVIPFragment();
					Toast.makeText(UserInfoActivity.this,
							getString(R.string.text_developing),
							Toast.LENGTH_SHORT).show();
					group.setOnCheckedChangeListener(null);
					group.check(group.getCheckedRadioButtonId());
					group.setOnCheckedChangeListener(this);
					return;
				default:
					return;
				}
				ft.replace(R.id.fl_user_info, fragment, fragment.getClass()
						.getName());
				ft.commit();
			}
		});
		headerTabs.clearCheck();
		headerTabs.check(R.id.btn_tab_profile);

		txtTitle = getString(R.string.activity_info_title);
		isVisibleBtnSettings = true;

		initTopBar();
		initBottomBar();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				BusinessRequester.getInstance().getFriendList();
			}
		}, 250);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_FRIENDS_LIST_BLOG));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_ALL_PRIVATE_MESSAGES));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_REMOVE_SOCIAL_FRIEND));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
