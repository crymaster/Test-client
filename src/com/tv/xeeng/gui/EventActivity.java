package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.GiftCodeDialog;
import com.tv.xeeng.gui.fragments.EventDetailsFragment;
import com.tv.xeeng.gui.itemdata.MonthlyEventItemData;
import com.tv.xeeng.R;

import java.util.ArrayList;

public class EventActivity extends BaseLayoutXeengActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	private ListView lvEvents;
	private EventItemAdapter eventListAdapter;

	private Button btnCharging;
	private Button btnGift;
	private Button btnShopping;

	private ArrayList<MonthlyEventItemData> eventData;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			hideLoading();

			if (status) {
				eventData = intent
						.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);

				eventListAdapter = new EventItemAdapter(eventData);
				eventListAdapter.setSelected(0);

				lvEvents.setAdapter(eventListAdapter);

				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.replace(R.id.fl_event_details,
						EventDetailsFragment.newInstance(eventData.get(0)));
				ft.commitAllowingStateLoss();
			} else {
				alert(intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
			}
		}
	};

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_event);

		txtTitle = "Sự kiện tháng";

		initTopBar();
		initBottomBar();

		btnCharging = (Button) findViewById(R.id.btn_charging);
		btnCharging.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EventActivity.this,
						ChargingActivity.class);
				startActivity(intent);
			}
		});

		btnGift = (Button) findViewById(R.id.btn_gift);
		btnGift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final BasicDialog dialog = new GiftCodeDialog(
						EventActivity.this);
				dialog.show();
			}
		});

		btnShopping = (Button) findViewById(R.id.btn_shopping);
		btnShopping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EventActivity.this,
						StoreActivity.class);
				startActivity(intent);
			}
		});

		lvEvents = (ListView) findViewById(R.id.lv_event);
		lvEvents.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (eventListAdapter != null) {
					MonthlyEventItemData item = eventListAdapter
							.getItem(position);

					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					ft.replace(R.id.fl_event_details,
							EventDetailsFragment.newInstance(item));
					ft.commit();

					eventListAdapter.setSelected(position);
					eventListAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalBroadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_GET_MONTHLY_EVENT_LIST));
		BusinessRequester.getInstance().getMonthlyEventList();
		showLoading();
	}

	@Override
	protected void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class EventItemAdapter extends BaseAdapter {
		private ArrayList<MonthlyEventItemData> dataList;
		private int selected;

		public void setSelected(int selected) {
			this.selected = selected;
		}

		public int getSelected() {
			return selected;
		}

		public EventItemAdapter(ArrayList<MonthlyEventItemData> items) {
			dataList = items;
			selected = 0;
		}

		class EventItemCellViewHolder {
			// private TextView txtIndex;
			private TextView txtDescription;

			public void fillData(MonthlyEventItemData data) {
				// txtIndex.setText(Integer.toString(data.index));
				txtDescription.setText(data.getName());
			}
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public MonthlyEventItemData getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			View view;

			EventItemCellViewHolder viewHolder;

			if (convertView == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.layout_event_item, parent, false);
				viewHolder = new EventItemCellViewHolder();
				viewHolder.txtDescription = (TextView) view
						.findViewById(R.id.text_event_title);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (EventItemCellViewHolder) view.getTag();
			}

			MonthlyEventItemData data = getItem(pos);
			viewHolder.fillData(data);

			if (pos == selected) {
				setTextViewSelected(viewHolder.txtDescription, true);
			} else {
				setTextViewSelected(viewHolder.txtDescription, false);
			}

			return view;
		}

		private void setTextViewSelected(TextView text, boolean isSelect) {
			if (isSelect) {
				text.setBackgroundResource(R.drawable.shape_info_button_selected);
				text.setTextColor(getResources()
						.getColor(android.R.color.white));
			} else {
				text.setBackgroundResource(R.drawable.shape_info_button);
				text.setTextColor(getResources().getColor(
						R.color.color_info_text_normal));
			}
		}
	}
}
