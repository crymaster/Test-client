package com.tv.xeeng.gui.fragments;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.xeeng.R;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.itemdata.MailItemData;

public class UserInfoPrivateChatMessagesFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_DATA = "player_data";
	private static final int LIMIT = 10;

	// ===========================================================
	// Fields
	// ===========================================================
	private ListView messagesList;
	private int offset;
	private boolean isEnded;
	private boolean shouldScrollToBottom;
	private OnScrollListener messageListOnScrollListener = new OnScrollListener() {
		private boolean shouldRequest = false;
		private int lastTotalItemCount = 0;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			shouldRequest = true;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem == 0 && shouldRequest && !isEnded
					&& lastTotalItemCount != view.getCount()) {
				lastTotalItemCount = view.getCount();
				shouldScrollToBottom = false;
				shouldRequest = false;

				BusinessRequester.getInstance().getPrivateChatMessages(
						targetPlayer.id, offset, LIMIT);
			}
		}
	};

	private ChatMessageListAdapter messagesListAdapter;

	private Player targetPlayer;
	private PrivateChatMessagesFragmentListener listener;

	private ImageButton btnClose;
	private ImageButton btnSend;
	private EditText edtChatMessage;

	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);
			if (MessageService.INTENT_GET_PRIVATE_CHAT_MESSAGES
					.equalsIgnoreCase(intent.getAction())) {
				if (status) {
					if (targetPlayer != null) {
						final ArrayList<MailItemData> temp = intent
								.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);

						if (messagesListAdapter == null) {
							messagesListAdapter = new ChatMessageListAdapter(
									temp, targetPlayer);
							messagesList.setAdapter(messagesListAdapter);
						} else {
							if (shouldScrollToBottom) {
								messagesListAdapter.getData().addAll(temp);
							} else {
								messagesListAdapter.getData().addAll(0, temp);
							}
						}
						offset = messagesListAdapter.getData().size();
						isEnded = temp.size() == 0;

						messagesList.setOnScrollListener(null);
						messagesListAdapter.notifyDataSetChanged();
						if (shouldScrollToBottom) {
							messagesList
									.setSelection(messagesListAdapter.getData()
											.size() - 1 >= 0 ? messagesListAdapter
											.getData().size() - 1 : 0);
						} else {
							View v = messagesList.getChildAt(0);
							int index = messagesList.getFirstVisiblePosition() + temp.size();
							int top = (v == null) ? 0 : v.getTop();
							messagesList.setSelectionFromTop(index, top);
						}
						messagesList
								.setOnScrollListener(messageListOnScrollListener);
					}
				}
			} else if (MessageService.INTENT_SEND_PRIVATE_MESSAGE
					.equalsIgnoreCase(intent.getAction())) {
				if (status) {
					shouldScrollToBottom = true;
					BusinessRequester.getInstance().getPrivateChatMessages(
							targetPlayer.id, 0, 1);
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
	public static UserInfoPrivateChatMessagesFragment newInstance(
			Player player, PrivateChatMessagesFragmentListener listener) {
		UserInfoPrivateChatMessagesFragment fragment = new UserInfoPrivateChatMessagesFragment();

		Bundle args = new Bundle();
		args.putParcelable(KEY_DATA, player);

		fragment.setListener(listener);
		fragment.setArguments(args);

		return fragment;
	}

	public void setListener(PrivateChatMessagesFragmentListener listener) {
		this.listener = listener;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_private_chat_messages,
				container, false);
		targetPlayer = getArguments().getParcelable(KEY_DATA);

		messagesList = (ListView) view.findViewById(R.id.lv_chat_messages);
		messagesList.setOnScrollListener(messageListOnScrollListener);

		edtChatMessage = (EditText) view.findViewById(R.id.edt_chat_message);

		btnClose = (ImageButton) view.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onBtnCloseClicked();
				}
			}
		});

		btnSend = (ImageButton) view.findViewById(R.id.btn_send);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validateInputs()) {
					BusinessRequester.getInstance().sendPrivateMessage(
							targetPlayer.id, "",
							edtChatMessage.getText().toString());

					edtChatMessage.setText("");
				}
			}
		});

		isEnded = false;
		offset = 0;

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		shouldScrollToBottom = true;
		BusinessRequester.getInstance().getPrivateChatMessages(targetPlayer.id,
				offset, LIMIT);

		broadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_GET_PRIVATE_CHAT_MESSAGES));
		broadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_SEND_PRIVATE_MESSAGE));
	}

	@Override
	public void onPause() {
		super.onPause();
		broadcastManager.unregisterReceiver(receiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private boolean validateInputs() {
		String chatMes = edtChatMessage.getText().toString().trim();
		if (TextUtils.isEmpty(chatMes)) {
			return false;
		}
		if (chatMes.length() > 120) {
			Toast.makeText(getActivity(),
					"Độ dài tin nhắn không được vượt quá 120 kí tự",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static class ChatMessageListAdapter extends BaseAdapter {
		private ArrayList<MailItemData> data;
		private Player targetPlayer;

		public ChatMessageListAdapter(ArrayList<MailItemData> data,
				Player targetPlayer) {
			this.data = data;
			this.targetPlayer = targetPlayer;
		}

		public static class MailListItemViewHolder {
			TextView tvContent;
			TextView tvName;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return this.data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return data.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				int layoutId = R.layout.fragment_private_chat_messages_item;
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						layoutId, parent, false);

				MailListItemViewHolder holder = new MailListItemViewHolder();
				holder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_content);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);

				convertView.setTag(holder);
			}

			MailItemData mail = data.get(position);
			if (mail != null) {
				MailListItemViewHolder holder = (MailListItemViewHolder) convertView
						.getTag();
				String name = mail.getSenderId() == targetPlayer.id ? targetPlayer.character
						: GameData.shareData().getMyself().character;

				holder.tvContent.setText(mail.getContent());
				holder.tvName.setText(name + ":");
			}

			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		public ArrayList<MailItemData> getData() {
			return data;
		}
	}

	public interface PrivateChatMessagesFragmentListener {
		public void onBtnCloseClicked();
	}
}