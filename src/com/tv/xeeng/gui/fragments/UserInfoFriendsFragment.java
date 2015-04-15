package com.tv.xeeng.gui.fragments;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.R;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.SendMessageDialog;
import com.tv.xeeng.gui.fragments.UserInfoPrivateChatMessagesFragment.PrivateChatMessagesFragmentListener;

public class UserInfoFriendsFragment extends Fragment implements
		PrivateChatMessagesFragmentListener {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_PLAYER_DATA = "player_data";

	// ===========================================================
	// Fields
	// ===========================================================
	private Handler uiHandler;

	private ListView friendList;
	private EditText edtSearch;
	private ImageView avatar;

	private FriendListAdapter friendListAdapter;
	private ArrayList<Player> friendData;
	private Player selectedPlayer;

	private TextView username;
	private TextView gender;
	private TextView gold;
	private TextView stats;

	private Button btnSendMessage;
	private Button btnDeleteFriend;
	private SendMessageDialog sendMessageDialog;

	private BasicDialog confirmDialog;

	private ViewGroup layoutFriendDetails;

	private ArrayList<Long> idInProgress = new ArrayList<Long>();

	private FrameLayout flChatMessages;

	private LocalBroadcastManager localBroadcastManager;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);
			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_USER_INFO)) {
				if (status) {
					Player player = (Player) intent
							.getParcelableExtra(NetworkUtils.MESSAGE_INFO);

					if (selectedPlayer != null && player != null) {
						selectedPlayer.sex = player.sex;
						if (gender != null) {
							SpannableString ss = new SpannableString(
									"Giới tính: "
											+ (selectedPlayer.sex ? "Nam"
													: "Nữ"));
							ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
									11,
									SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
							gender.setText(ss);
						}
					}
				}
			}
		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static UserInfoFriendsFragment newInstance(ArrayList<Player> players) {
		UserInfoFriendsFragment fragment = new UserInfoFriendsFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(KEY_PLAYER_DATA, players);

		fragment.setArguments(bundle);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================\
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		localBroadcastManager = LocalBroadcastManager
				.getInstance(getActivity());

		uiHandler = new Handler();
	}

	@Override
	public void onResume() {
		super.onResume();

		localBroadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_GET_USER_INFO));
	}

	@Override
	public void onPause() {
		super.onPause();

		localBroadcastManager.unregisterReceiver(receiver);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_info_friends,
				container, false);

		friendData = new ArrayList<Player>();
		friendListAdapter = new FriendListAdapter(friendData);
		friendList = (ListView) view.findViewById(R.id.lv_friends);
		friendList.setAdapter(friendListAdapter);
		friendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectedPlayer = (Player) adapterView
						.getItemAtPosition(position);
				username.setText(selectedPlayer.character);

				SpannableString ss = new SpannableString("Gold: "
						+ CommonUtils.formatCash(selectedPlayer.cash));
				ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 5,
						SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
				gold.setText(ss);

				ss = new SpannableString("Giới tính: "
						+ (selectedPlayer.sex ? "Nam" : "Nữ"));
				ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 11,
						SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
				gender.setText(ss);

				layoutFriendDetails.setVisibility(View.VISIBLE);

				friendListAdapter.setSelected(position);
				friendListAdapter.notifyDataSetChanged();

				Bitmap bitmap = CommonUtils
						.getBitmapFromMemCache(selectedPlayer.id);
				if (bitmap != null) {
					avatar.setImageBitmap(bitmap);
				} else {
					avatar.setImageResource(R.drawable.avatar_default);
				}

				flChatMessages.setVisibility(View.GONE);
				BusinessRequester.getInstance().getUserInfo(selectedPlayer.id);
			}
		});

		edtSearch = (EditText) view.findViewById(R.id.edt_search);
		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				friendListAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		avatar = (ImageView) view.findViewById(R.id.imv_profile_avatar);

		username = (TextView) view.findViewById(R.id.tv_friend_username);
		gold = (TextView) view.findViewById(R.id.tv_gold);
		gender = (TextView) view.findViewById(R.id.tv_gender);
		stats = (TextView) view.findViewById(R.id.tv_stats);

		Bundle args = getArguments();
		if (args != null) {
			update(args.<Player> getParcelableArrayList(KEY_PLAYER_DATA));
		}

		layoutFriendDetails = (ViewGroup) view
				.findViewById(R.id.layout_friend_details);
		layoutFriendDetails.setVisibility(View.INVISIBLE);

		btnSendMessage = (Button) view.findViewById(R.id.btn_private_chat);
		btnSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (sendMessageDialog == null) {
				// sendMessageDialog = new SendMessageDialog(getActivity());
				// sendMessageDialog.setPositiveText("Gửi tin nhắn");
				// }
				// sendMessageDialog.setTitleText(username.getText());
				// sendMessageDialog
				// .setPositiveOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // sendMessageDialog.dismiss();
				// // sendMessage(selectedPlayer.id, GameData
				// // .shareData().getMyself().character,
				// // sendMessageDialog.getInputValue());
				// }
				// });
				// sendMessageDialog.show();
				//
				// BusinessRequester.getInstance().getPrivateChatMessages(
				// selectedPlayer.id, 0, 20);
				getChildFragmentManager()
						.beginTransaction()
						.replace(
								flChatMessages.getId(),
								UserInfoPrivateChatMessagesFragment
										.newInstance(selectedPlayer,
												UserInfoFriendsFragment.this))
						.commit();
				flChatMessages.setVisibility(View.VISIBLE);

				if (layoutFriendDetails != null) {
					layoutFriendDetails.setVisibility(View.INVISIBLE);
				}
			}
		});

		btnDeleteFriend = (Button) view.findViewById(R.id.btn_delete_friend);
		btnDeleteFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (confirmDialog == null) {
					confirmDialog = new BasicDialog(getActivity(), "Thông báo",
							"Bạn có chắc muốn xóa người này khỏi danh sách?",
							"Không", "Có");
					confirmDialog
							.setPositiveOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									confirmDialog.dismiss();
									BusinessRequester.getInstance()
											.removeSocialFriend(
													selectedPlayer.id);
									layoutFriendDetails
											.setVisibility(View.INVISIBLE);
									friendListAdapter.setSelected(-1);
									friendData.remove(selectedPlayer);
									update(friendData);
									selectedPlayer = null;
									Toast.makeText(getActivity(),
											"Xóa thành công",
											Toast.LENGTH_SHORT).show();
								}
							});
				}
				confirmDialog.show();
			}
		});

		flChatMessages = (FrameLayout) view
				.findViewById(R.id.fl_private_chat_messages);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void update(ArrayList<Player> friendData) {
		this.friendData = friendData;
		friendListAdapter.setData(friendData);
		friendListAdapter.getFilter().filter(edtSearch.getText().toString());
		friendListAdapter.notifyDataSetChanged();
	}

	private void sendMessage(long id, CharSequence title, CharSequence content) {
		BusinessRequester.getInstance().sendPrivateMessage(id, title, content);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class FriendListAdapter extends BaseAdapter implements Filterable {
		private ArrayList<Player> filteredData;
		private ArrayList<Player> data;
		private int selected;
		private Filter filter;

		public FriendListAdapter(ArrayList<Player> data) {
			this.filteredData = new ArrayList<Player>(data);
			this.data = data;
			selected = -1;
		}

		class FriendListViewHolders {
			ImageView avatar;
			ImageView status;
			TextView username;
		}

		public void setSelected(int selected) {
			this.selected = selected;
		}

		public void setData(ArrayList<Player> data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			return filteredData.size();
		}

		@Override
		public Object getItem(int position) {
			return filteredData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return filteredData.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.fragment_user_info_friends_item,
								parent, false);
				FriendListViewHolders holder = new FriendListViewHolders();
				holder.avatar = (ImageView) convertView
						.findViewById(R.id.imv_profile_avatar);
				holder.username = (TextView) convertView
						.findViewById(R.id.imv_friend_item_username);
				holder.status = (ImageView) convertView
						.findViewById(R.id.imv_friend_status);
				convertView.setTag(holder);
			}
			final Player player = filteredData.get(position);
			if (player != null) {
				FriendListViewHolders holder = (FriendListViewHolders) convertView
						.getTag();

				holder.username.setText(player.character);
				holder.status
						.setImageResource(player.isOnline ? R.drawable.icon_online
								: R.drawable.icon_offline);
				if (selected == data.indexOf(player)) {
					convertView
							.setBackgroundColor(parent
									.getContext()
									.getResources()
									.getColor(
											R.color.friend_list_item_background_selected));
				} else {
					convertView
							.setBackgroundColor(parent
									.getContext()
									.getResources()
									.getColor(
											R.color.friend_list_item_background_normal));
				}

				Bitmap bitmap = CommonUtils.getBitmapFromMemCache(player.id);
				if (bitmap != null) {
					holder.avatar.setImageBitmap(bitmap);
				} else {
					holder.avatar.setImageResource(R.drawable.avatar_default);
					if (!idInProgress.contains(Long.valueOf(player.id))) {
						idInProgress.add(player.id);
						BackgroundThreadManager.post(new Runnable() {
							@Override
							public void run() {
								if (BusinessRequester.getInstance()
										.getUserAvatar(player.id)) {
									uiHandler.post(new Runnable() {

										@Override
										public void run() {
											friendListAdapter
													.notifyDataSetChanged();
										}
									});
								}
							}
						});
					}
				}
			}
			return convertView;
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new FriendFilter();
			}
			return filter;
		}

		private class FriendFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				if (constraint != null && constraint.length() > 0) {
					filteredData = new ArrayList<Player>();
					for (Player player : data) {
						if (player.character.toLowerCase().contains(
								constraint.toString().toLowerCase())) {
							filteredData.add(player);
						}
					}
				} else {
					filteredData = new ArrayList<Player>(data);
				}

				results.values = filteredData;
				results.count = filteredData.size();
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}
	}

	@Override
	public void onBtnCloseClicked() {
		if (layoutFriendDetails != null) {
			layoutFriendDetails.setVisibility(View.VISIBLE);
		}
		flChatMessages.setVisibility(View.GONE);
	}

	// private class GetAvatarTask extends AsyncTask<Long, Void, Boolean> {
	//
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
	// friendListAdapter.notifyDataSetChanged();
	// }
	// }
	// }
}
