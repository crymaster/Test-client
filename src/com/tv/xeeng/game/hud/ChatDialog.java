package com.tv.xeeng.game.hud;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.ChatItemData;
import com.tv.xeeng.R;

import java.util.ArrayList;

public class ChatDialog extends Dialog {

	private ListView historyListView;
	private GridView templateMsgGridView;
	private EditText msgUserEditText;

	private Button btnDialogChatSend;

	private ArrayList<ChatItemData> mhistoryList;
	private ArrayList<String> mtemplateList;

	private OnDismissChatDialogListener mdismissListener;

	private boolean isScrolling;

	public ChatDialog(Context context) {

		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        initLayout();
	}

	public OnDismissChatDialogListener getDismissListener() {
		return mdismissListener;
	}

	public void setDismissListener(OnDismissChatDialogListener dismissListener) {
		this.mdismissListener = dismissListener;
	}

	private void initLayout() {

		setContentView(R.layout.dialog_chat);

		historyListView = (ListView) findViewById(R.id.lv_dialog_chat_history);
		templateMsgGridView = (GridView) findViewById(R.id.gv_dialog_chat_template);
		msgUserEditText = (EditText) findViewById(R.id.et_dialog_chat_message);
		btnDialogChatSend = (Button) findViewById(R.id.btn_dialog_chat_send);

		historyListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View touchedView, MotionEvent motionEvent) {
				int action = motionEvent.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					return false;
				case MotionEvent.ACTION_UP:
					if (!isScrolling) {
						// Check to see if user just end scroll.
						ChatDialog.this.dismiss();
					}
					isScrolling = false;
					return false;
				default:
					return false;
				}
			}
		});
		historyListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_TOUCH_SCROLL
						|| scrollState == SCROLL_STATE_FLING) {
					isScrolling = true;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		msgUserEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView,
							int actionId, KeyEvent keyEvent) {
						if (actionId == EditorInfo.IME_ACTION_SEND) {
							if (msgUserEditText.getText() != null
									&& msgUserEditText.getText().length() > 0) {
								sendChatMessage(msgUserEditText.getText()
										.toString());
								ChatDialog.this.dismiss();
							}
							return true;
						} else {
							return false;
						}
					}
				});
		createTemplateAdapterIfNeed();
		templateMsgGridView.setAdapter(new TemplateAdapter());

		templateMsgGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int index, long id) {
				dismiss();
				TemplateAdapter tadapter = (TemplateAdapter) adapterView
						.getAdapter();
				sendChatMessage(tadapter.getItem(index));
			}
		});

		btnDialogChatSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(msgUserEditText.getText().toString())) {
					sendChatMessage(msgUserEditText.getText().toString());
					ChatDialog.this.dismiss();
				}
			}
		});
	}

	private void sendChatMessage(String message) {
		BusinessRequester.getInstance().chat(message);
		if (mdismissListener != null) {
			mdismissListener.onIChat(message);
		}
	}

	private void createTemplateAdapterIfNeed() {

		if (templateMsgGridView.getAdapter() == null) {

			mtemplateList = CommonUtils.getTemplateMessageList(GameData
					.shareData().gameId);
			if (mtemplateList == null) {
				return;
			}
			templateMsgGridView.setAdapter(new TemplateAdapter());
		}
	}

	@Override
	public void show() {

		historyListView.clearChoices();
		msgUserEditText.clearFocus();
		msgUserEditText.setText("");
		super.show();
		LocalBroadcastManager.getInstance(getContext()).registerReceiver(
				mReceiver, new IntentFilter(MessageService.INTENT_CHAT_GAME));

		mhistoryList = GameData.shareData().getGame().cloneHistoryChatList();
		historyListView.setAdapter(new HistoryChatAdapter());
	}

	@Override
	public void dismiss() {

		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
				mReceiver);
		super.dismiss();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);
			if (MessageService.INTENT_CHAT_GAME.equalsIgnoreCase(intent
					.getAction())) {

				if (status) {

					long playerId = intent.getLongExtra(
							NetworkUtils.MESSAGE_INFO, -1);
					if (playerId < 0)
						return;
					String name = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO_3);
					String content = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO_2);
					mhistoryList.add(new ChatItemData(playerId, name, content));
					((HistoryChatAdapter) historyListView.getAdapter())
							.notifyDataSetChanged();
				}
			}
		}
	};

	private class HistoryChatAdapter extends BaseAdapter {

		private HistoryChatAdapter() {
		}

		@Override
		public int getCount() {

			return mhistoryList.size();
		}

		@Override
		public ChatItemData getItem(int index) {

			return mhistoryList.get(index);
		}

		@Override
		public long getItemId(int arg0) {

			return 0;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			View view;
			HistoryViewHolder holder = null;
			if (convertView == null) {

				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.dialog_chat_history_item, null, false);
				holder = new HistoryViewHolder();
				holder.playerNameTV = (TextView) view
						.findViewById(R.id.tv_dialog_chat_history_item_player);
				holder.contentChatTV = (TextView) view
						.findViewById(R.id.tv_dialog_chat_history_item_content);
				view.setTag(holder);
			} else {

				view = convertView;
				holder = (HistoryViewHolder) view.getTag();
			}

			ChatItemData itemData = mhistoryList.get(index);
			holder.playerNameTV.setText(itemData.getPlayerName());
			holder.contentChatTV.setText(itemData.getMessage());

			return view;
		}

		@Override
		public boolean areAllItemsEnabled() {

			return false;
		}
	}

	private class HistoryViewHolder {

		private TextView playerNameTV;
		private TextView contentChatTV;
	}

	private class TemplateAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mtemplateList.size();
		}

		@Override
		public String getItem(int position) {

			return mtemplateList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			TemplateViewHolder holder;
			if (convertView == null) {

				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.dialog_chat_message_item, null, false);
				holder = new TemplateViewHolder();
				holder.contentTV = (TextView) view
						.findViewById(R.id.tv_dialog_chat_message_item_msg);
				view.setTag(holder);
			} else {

				view = convertView;
				holder = (TemplateViewHolder) view.getTag();
			}

			holder.contentTV.setText(mtemplateList.get(position));

			return view;
		}
	}

	private class TemplateViewHolder {

		private TextView contentTV;
	}

	public interface OnDismissChatDialogListener {

		public void onIChat(String msg);

		public void onIExposeEmotion(int pIndex);
	}
}
