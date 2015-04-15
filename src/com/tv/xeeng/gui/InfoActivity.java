package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gui.itemdata.EventItemData;
import com.tv.xeeng.R;

import java.util.ArrayList;

public class InfoActivity extends BaseLayoutXeengActivity {

	public static final String KEY_SELECTED_TAB = "type";
	public static final String KEY_SELECTED_CELL = "game";

	public static final int TAB_NEWS = 1;
	public static final int TAB_EVENTS = 2;
	public static final int TAB_HELPS = 3;

	public static final int GAME_SLOT_MACHINE = 1;
	public static final int GAME_PHOM = 2;
	public static final int GAME_TLMN = 3;
	public static final int GAME_SAM = 4;
	public static final int GAME_BACAY = 5;
	public static final int GAME_ALTP = 6;
	public static final int GAME_PIKACHU = 7;
	public static final int GAME_BAUCUA = 8;
	public static final int GAME_MAU_BINH = 9;

	private ListView listView;

	private WebView webView;
	private RadioGroup radioGroup;

	private String path = "file:///android_asset/game_guides/";

	private String[] listGameHelps;
	private String[] listFileName = { path + "policy_small.html",
			path + "vong_quay.html", path + "phom_small.html",
			path + "tlmn_small.html", path + "sam_small.html",
			path + "bacay_small.html", path + "altp_small.html",
			path + "pikachu_small.html", path + "baucua_small.html",
			path + "maubinh_small.html" };

	private String tag = getClass().getName();

	private int radioGroupSelect;

	@Override
	protected void needReconnection() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.activity_info);
		String[] listGame = { getString(R.string.info_help_policy),
				"Vòng quay may mắn", getString(R.string.info_help_phom),
				getString(R.string.info_help_tlmn), "Luật chơi Sâm",
				getString(R.string.info_help_3cay),
				getString(R.string.info_help_altp),
				getString(R.string.info_help_pikachu),
				getString(R.string.info_help_baucua), "Luật chơi Mậu Binh" };
		listGameHelps = listGame;

		init();
	}

	private void init() {
		listView = (ListView) findViewById(R.id.lv_info);
		radioGroup = (RadioGroup) findViewById(R.id.layout_info_table_headers);
		Button btnBack = (Button) findViewById(R.id.btn_back);
		webView = (WebView) findViewById(R.id.text_event_detail);
		webView.setBackgroundColor(0);

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		radioGroupSelect = getIntent().getExtras().getInt(KEY_SELECTED_TAB, -1);
		radioGroup.check(-1);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				Log.d(tag, "setOnCheckedChangeListener " + checkedId);
				switch (checkedId) {
				case R.id.btn_tab_event:
					BusinessRequester.getInstance().getEventsList();
					break;
				case R.id.btn_tab_news:
					BusinessRequester.getInstance().getMessageId(
							MessagesID.GET_ALL_NEWS);
					break;
				case R.id.btn_tab_help:
					getHelpList();
					break;

				default:
					break;
				}
			}
		});

		if (radioGroupSelect == TAB_EVENTS) {
			radioGroup.check(R.id.btn_tab_event);
		} else if (radioGroupSelect == TAB_NEWS) {
			radioGroup.check(R.id.btn_tab_news);
		} else if (radioGroupSelect == TAB_HELPS) {
			radioGroup.check(R.id.btn_tab_help);
		}

		txtTitle = getString(R.string.activity_info_title);
		initTopBar();
		initBottomBar();
	}

	private void getHelpList() {
		ArrayList<EventItemData> listData = new ArrayList<EventItemData>();
		for (int i = 0; i < listGameHelps.length; i++)
			listData.add(new EventItemData(i, listGameHelps[i], i));

		EventItemAdapter adapter = new EventItemAdapter(listData);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				webView.loadUrl(listFileName[position]);
				selectCell(position);
			}
		});

		int position = getIntent().getExtras().getInt(KEY_SELECTED_CELL, 0);
		listView.performItemClick(
				listView.getAdapter().getView(position, null, listView),
				position, listView.getItemIdAtPosition(position));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_EVENTS_LIST));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_EVENT_DETAILS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_ALL_NEWS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_NEWS_DETAIL));
		// if (radioGroupSelect == TAB_EVENTS) {
		// BusinessRequester.getInstance().getEventsList();
		// } else if (radioGroupSelect == TAB_NEWS) {
		// BusinessRequester.getInstance().getMessageId(
		// MessagesID.GET_ALL_NEWS);
		// }
		super.onResume();
		if (getIntent().getExtras().getInt(KEY_SELECTED_CELL, 0) == GAME_SLOT_MACHINE) {
			pauseBackgroundMusic();
		}
	}

	private void selectCell(int position) {
		EventItemAdapter adapter = (EventItemAdapter) listView.getAdapter();
		adapter.setSelected(position);
		adapter.notifyDataSetChanged();
		// listView.setSelection(position);
	}

	private void setTextViewSelected(TextView text, boolean isSelect) {
		if (isSelect) {
			text.setBackgroundResource(R.drawable.shape_info_button_selected);
			text.setTextColor(getResources().getColor(android.R.color.white));
		} else {
			text.setBackgroundResource(R.drawable.shape_info_button);
			text.setTextColor(getResources().getColor(
					R.color.color_info_text_normal));
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d(tag, "BroadcastReceiver " + intent.getAction());

			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_EVENTS_LIST)) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);

				if (status) {
					final ArrayList<EventItemData> data = intent.getExtras()
							.getParcelableArrayList(NetworkUtils.MESSAGE_INFO);

					EventItemAdapter adapter = new EventItemAdapter(data);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							BusinessRequester.getInstance().getEventDetail(
									data.get(position).eventId);
							selectCell(position);
						}
					});

					int position = ((EventItemAdapter) listView.getAdapter())
							.getSelected();
					if (position >= 0) {
						EventItemData eventData = data.get(position);
						BusinessRequester.getInstance().getEventDetail(
								eventData.eventId);
					}

				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_EVENT_DETAILS)) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {

					String _eventDetails = "<html><body bgcolor=\"#000000\"> <font color=\"white\" size=\"4\">"
							+ intent.getExtras().getString(
									NetworkUtils.MESSAGE_INFO)
							+ "</font></body></html>";

					webView.loadDataWithBaseURL(null, _eventDetails,
							"text/html", "utf-8", null);
					// textView.setText(_eventDetails);
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_ALL_NEWS)) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);

				if (status) {
					final ArrayList<EventItemData> data = intent.getExtras()
							.getParcelableArrayList(NetworkUtils.MESSAGE_INFO);

					EventItemAdapter adapter = new EventItemAdapter(data);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							BusinessRequester.getInstance().getNewsDetail(
									data.get(position).eventId);
							selectCell(position);
						}
					});

					int position = ((EventItemAdapter) listView.getAdapter())
							.getSelected();
					if (position >= 0) {
						EventItemData eventData = data.get(position);
						BusinessRequester.getInstance().getNewsDetail(
								eventData.eventId);
					}

				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_NEWS_DETAIL)) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {

					String _eventDetails = "<html><body bgcolor=\"#000000\"> <font color=\"white\" size=\"4\">"
							+ intent.getExtras().getString(
									NetworkUtils.MESSAGE_INFO)
							+ "</font></body></html>";

					webView.loadDataWithBaseURL(null, _eventDetails,
							"text/html", "utf-8", null);
					// textView.setText(_eventDetails);
				}
			}
		}
	};

	class EventItemAdapter extends BaseAdapter {
		private ArrayList<EventItemData> dataList;
		private int selected;

		public void setSelected(int selected) {
			this.selected = selected;
		}

		public int getSelected() {
			return selected;
		}

		public EventItemAdapter(ArrayList<EventItemData> items) {
			dataList = items;
			selected = 0;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public EventItemData getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.layout_event_item, parent, false);
				EventItemCellViewHolder viewHolder = new EventItemCellViewHolder();
				viewHolder.txtDescription = (TextView) convertView
						.findViewById(R.id.text_event_title);
				convertView.setTag(viewHolder);
			}
			EventItemCellViewHolder viewHolder = (EventItemCellViewHolder) convertView
					.getTag();

			EventItemData data = getItem(pos);
			viewHolder.fillData(data);

			setTextViewSelected(viewHolder.txtDescription, pos == selected);

			return convertView;
		}
	}

	class EventItemCellViewHolder {
		// private TextView txtIndex;
		private TextView txtDescription;

		public void fillData(EventItemData data) {
			// txtIndex.setText(Integer.toString(data.index));
			txtDescription.setText(data.eventName);
		}
	}

}
