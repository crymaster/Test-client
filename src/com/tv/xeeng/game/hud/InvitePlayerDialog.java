package com.tv.xeeng.game.hud;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.R;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;

public class InvitePlayerDialog extends Dialog {
	private LocalBroadcastManager mLocalBroadcastManager;
	private ListView playerList;

	private ArrayList<Player> playerData;
	private PlayerListAdapter adapter;

	private RadioGroup tableTabs;

	private ArrayList<Long> processedUserIds;
	private Handler uiHandler;

	public InvitePlayerDialog(Context context) {
		super(context);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
	}

	@Override
	public void show() {
		super.show();
		registerNotification();
		BusinessRequester.getInstance().getFriendList();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHandler = new Handler();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		processedUserIds = new ArrayList<Long>();

		initLayout();
	}

	private void initLayout() {
		setContentView(R.layout.dialog_invite_player_in_game);

		playerList = (ListView) findViewById(R.id.lv_dialog_invite_list_friend);
		playerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PlayerListAdapter adapter = (PlayerListAdapter) parent
						.getAdapter();
				if (adapter == null) {
					return;
				}
				Player player = (Player) adapter.getItem(position);
				BusinessRequester.getInstance().invitePlayGame(
						GameData.shareData().gameId,
						GameData.shareData().getGame().getCurrentBetCash(),
						player.id, GameData.shareData().getGame().getMatchId());
				Toast toast = Toast.makeText(getContext(),
						"Bạn đã gửi lời mời tới " + player.character,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, (int) getContext()
						.getResources().getDimension(R.dimen.toast_offset_y));
				toast.show();
				InvitePlayerDialog.this.dismiss();
			}
		});

		tableTabs = (RadioGroup) findViewById(R.id.rg_dialog_invite_table_tab);
		tableTabs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				switch (id) {
				case R.id.dialog_invite_tab_free_player:
					BusinessRequester.getInstance().getFreePlayerList();
					break;
				case R.id.dialog_invite_tab_friends:
					BusinessRequester.getInstance().getFriendList();
					break;
				}
			}
		});
	}

	private void registerNotification() {
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_FRIENDS_LIST_BLOG));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_PLAYERS_FREE));
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_FRIENDS_LIST_BLOG)
					|| intent.getAction().equalsIgnoreCase(
							MessageService.INTENT_GET_PLAYERS_FREE)) {
				playerData = intent
						.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);

				playerData = playerData == null ? new ArrayList<Player>()
						: playerData;
				adapter = new PlayerListAdapter(playerData);
				playerList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}

	};

	// find free player in game
	private class PlayerListAdapter extends BaseAdapter {
		private ArrayList<Player> data;
		private ArrayList<Player> filteredData;

		public PlayerListAdapter(ArrayList<Player> data) {
			this.data = data;
			this.filteredData = new ArrayList<Player>();
			if (data != null) {
				for (Player player : data) {
					if (player.isOnline) {
						filteredData.add(player);
					}
				}
			}
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public int getCount() {
			if (data == null) {
				return 0;
			}
			return data.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UserItemCellViewHolder viewHolder;

			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_home_table, null);

				viewHolder = new UserItemCellViewHolder();
				viewHolder.txtIndex = (TextView) convertView
						.findViewById(R.id.txt_index);
				viewHolder.txtUsername = (TextView) convertView
						.findViewById(R.id.txt_player_name);
				viewHolder.txtCashValue = (TextView) convertView
						.findViewById(R.id.txt_player_gold);
				viewHolder.imvAvatar = (ImageView) convertView
						.findViewById(R.id.imv_profile_avatar);

				viewHolder.txtIndex.setVisibility(View.GONE);

				convertView.setTag(viewHolder);

			}
			viewHolder = (UserItemCellViewHolder) convertView.getTag();

			Player player = data.get(position);
			viewHolder.fillData(player, position);
			return convertView;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
	}

	private class UserItemCellViewHolder {
		TextView txtIndex;
		TextView txtUsername;
		TextView txtCashValue;
		ImageView imvAvatar;

		public void fillData(final Player player, int index) {
			txtIndex.setText(new StringBuffer(Integer.toString(index + 1))
					.append(""));
			txtUsername.setText(player.character);
			txtCashValue.setText(new StringBuffer(CommonUtils
					.formatCash(player.cash)).toString());
			Bitmap bitmap = CommonUtils.getBitmapFromMemCache(player.id);
			if (bitmap != null) {
				imvAvatar.setImageBitmap(bitmap);
			} else {
				imvAvatar.setImageResource(R.drawable.avatar_default);
				if (!processedUserIds.contains(player.id)) {
					processedUserIds.add(player.id);

					BackgroundThreadManager.post(new Runnable() {

						@Override
						public void run() {
							if (BusinessRequester.getInstance().getUserAvatar(
									player.id)) {
								uiHandler.post(new Runnable() {

									@Override
									public void run() {
										adapter.notifyDataSetChanged();
									}
								});
							}
						}
					});
				}
			}
		}
	}

	// private class GetAvatarTask extends AsyncTask<Long, Void, Boolean> {
	// @Override
	// protected Boolean doInBackground(Long... params) {
	// if (params[0] > 0) {
	// return BusinessRequester.getInstance().getUserAvatar(params[0]);
	// }
	// return false;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// super.onPostExecute(result);
	// if (result) {
	// adapter.notifyDataSetChanged();
	// }
	// }
	// }
}
