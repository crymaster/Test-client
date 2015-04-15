package com.tv.xeeng.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gui.itemdata.EventHistoryItemData;
import com.tv.xeeng.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventDetailsHistoryFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TAG_EVENT_ID = "event_id";

	// ===========================================================
	// Fields
	// ===========================================================
	private ListView lvEventHistory;

	private ArrayList<EventHistoryItemData> eventHistoryData;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static EventDetailsHistoryFragment newInstance(long eventId) {
		EventDetailsHistoryFragment fragment = new EventDetailsHistoryFragment();
		Bundle args = new Bundle();

		args.putLong(TAG_EVENT_ID, eventId);

		fragment.setArguments(args);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_details_history,
				container, false);

		lvEventHistory = (ListView) view
				.findViewById(R.id.lv_event_details_history);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						boolean status = intent.getBooleanExtra(
								NetworkUtils.MESSAGE_STATUS, false);

						if (status) {
							eventHistoryData = intent.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
							lvEventHistory.setAdapter(new EventHistoryAdapter(
									eventHistoryData));
						}
					}
				}, new IntentFilter(MessageService.INTENT_GET_EVENT_HISTORY));

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		BusinessRequester.getInstance().getEventHistory(
				getArguments().getLong(TAG_EVENT_ID));
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static class EventHistoryAdapter extends BaseAdapter {
		private static final SimpleDateFormat formatter = new SimpleDateFormat(
				"HH:mm - dd/MM/yyyy");

		private ArrayList<EventHistoryItemData> data;

		public EventHistoryAdapter(ArrayList<EventHistoryItemData> data) {
			this.data = data != null ? data
					: new ArrayList<EventHistoryItemData>();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		static class EventHistoryViewHolder {
			TextView tvGoldReceived;
			TextView tvDateReceived;
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.fragment_event_details_history_item, parent,
						false);

				EventHistoryViewHolder holder = new EventHistoryViewHolder();
				holder.tvGoldReceived = (TextView) convertView
						.findViewById(R.id.tv_gold_received);
				holder.tvDateReceived = (TextView) convertView
						.findViewById(R.id.tv_date_received);

				convertView.setTag(holder);
			}

			EventHistoryItemData itemData = data.get(position);

			if (itemData != null) {
				EventHistoryViewHolder holder = (EventHistoryViewHolder) convertView
						.getTag();

				SpannableString ss = new SpannableString(itemData.getMessage());
				// ss.setSpan(new ForegroundColorSpan(Color.rgb(255, 217, 0)),
				// 12,
				// ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				holder.tvGoldReceived.setText(ss);

				holder.tvDateReceived.setText(formatter.format(new Date(
						itemData.getDate())));
			}
			return convertView;
		}
	}
}
