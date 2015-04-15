package com.tv.xeeng.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.BaseLayoutXeengActivity;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.EventRewardDialog;
import com.tv.xeeng.gui.customview.InventoryItem;
import com.tv.xeeng.gui.itemdata.InventoryItemData;
import com.tv.xeeng.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EventDetailsParticipateJoinItemsFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TAG_EVENT_ID = "event_id";
	public static final String TAG_EVENT_COMPONENTS = "event_components";
	public static final String TAG_EVENT_CONTENT = "event_content";

	// ===========================================================
	// Fields
	// ===========================================================
	private Button btnParticipateOnce;
	private Button btnParticipateAll;

	private ImageView imvReward;

	private TextView tvEventDescription;

	private ArrayList<InventoryItemData> eventItemData;
	private GridView gvEventItems;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_INVENTORY)) {
				if (status) {
					eventItemData = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					update();
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_PARTICIPATE_JOIN_ITEMS_EVENT)) {
				((BaseLayoutXeengActivity) getActivity()).hideLoading();

				String response = intent
						.getStringExtra(NetworkUtils.MESSAGE_INFO);

				final String data[] = NetworkUtils.stringSplit(response,
						NetworkUtils.ELEMENT_SEPERATOR);

				if (status && data.length > 1) {
					final BasicDialog dialog = ((BaseLayoutXeengActivity) getActivity())
							.confirm(data[0]);
					dialog.setPositiveText("Có");
					dialog.setNegativeText("Không");
					dialog.setPositiveOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							BusinessRequester.getInstance().useInventoryItem(
									data[1]);
							dialog.dismiss();
						}
					});
				} else {
					((BaseLayoutXeengActivity) getActivity()).alert(data[0]);
				}

				if (status) {
					BusinessRequester.getInstance().getInventory();
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_USE_INVENTORY_ITEM)) {
				String msg = intent.getStringExtra(NetworkUtils.MESSAGE_INFO);
				if (status) {
					EventRewardDialog dialog = new EventRewardDialog(
							getActivity(), msg);
					dialog.show();

					BusinessRequester.getInstance().getUserInfo(
							GameData.shareData().getMyself().id);
				} else {
					((BaseLayoutXeengActivity) getActivity()).alert(msg);
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
	public static EventDetailsParticipateJoinItemsFragment newInstance(
			long eventId, String[] componentsData, String content) {
		EventDetailsParticipateJoinItemsFragment fragment = new EventDetailsParticipateJoinItemsFragment();

		Bundle args = new Bundle();

		args.putLong(TAG_EVENT_ID, eventId);
		args.putStringArray(TAG_EVENT_COMPONENTS, componentsData);
		args.putString(TAG_EVENT_CONTENT, content);

		fragment.setArguments(args);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		String description = args.getString(TAG_EVENT_CONTENT);
		description = description == null ? "" : description;

		final long eventId = args.getLong(TAG_EVENT_ID);

		View view = inflater.inflate(
				R.layout.fragment_event_details_participate_join_items,
				container, false);

		btnParticipateOnce = (Button) view
				.findViewById(R.id.btn_participate_once);
		btnParticipateOnce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(getActivity(), R.string.text_developing,
				// Toast.LENGTH_SHORT).show();
				((BaseLayoutXeengActivity) getActivity()).showLoading();
				BusinessRequester.getInstance().participateEvent(
						MessagesID.PARTICIPATE_JOIN_ITEMS_EVENT, eventId);
			}
		});

		btnParticipateAll = (Button) view
				.findViewById(R.id.btn_participate_all);
		btnParticipateAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), R.string.text_developing,
						Toast.LENGTH_SHORT).show();
			}
		});

		gvEventItems = (GridView) view.findViewById(R.id.gv_event_items);

		tvEventDescription = (TextView) view
				.findViewById(R.id.tv_event_description);
		tvEventDescription.setText(description);
		imvReward = (ImageView) view.findViewById(R.id.imv_reward);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(receiver,
						new IntentFilter(MessageService.INTENT_GET_INVENTORY));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(
						MessageService.INTENT_PARTICIPATE_JOIN_ITEMS_EVENT));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(MessageService.INTENT_USE_INVENTORY_ITEM));

		BusinessRequester.getInstance().getInventory();
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				receiver);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void update() {
		String[] components = getArguments().getStringArray(
				TAG_EVENT_COMPONENTS);
		ArrayList<InventoryItemData> itemsToShow = new ArrayList<InventoryItemData>();

		// Last component is reward item.
		int rewardId = CustomApplication
				.shareApplication()
				.getResources()
				.getIdentifier(
						InventoryItemData.RES_PREFIX
								+ components[components.length - 1],
						"drawable",
						CustomApplication.shareApplication().getPackageName());
		if (rewardId > 0) {
			imvReward.setImageResource(rewardId);
		}

		// Let's try something interesting here.
		HashMap<String, Integer> itemOccurences = new HashMap<String, Integer>();
		HashMap<String, Integer> itemExtra = new HashMap<String, Integer>();

		for (String str : components) {
			if (itemOccurences.containsKey(str)) {
				itemOccurences.put(str, itemOccurences.get(str) + 1);
			} else {
				itemOccurences.put(str, 1);
			}
		}
		for (String str : components) {
			InventoryItemData invData = new InventoryItemData(str, "", "", 0,
					false);
			int count = 0;
			for (InventoryItemData inv : eventItemData) {
				if (inv.getItemCode().equalsIgnoreCase(str)) {
					count = inv.getCount() / itemOccurences.get(str);
					if (inv.getCount() % itemOccurences.get(str) > 0) {
						if (itemExtra.containsKey(str)) {
							itemExtra.put(str, itemExtra.get(str) - 1);
						} else {
							itemExtra.put(str,
									inv.getCount() % itemOccurences.get(str));
						}
						count += itemExtra.get(str) > 0 ? 1 : 0;
					}
					break;
				}
			}

			invData.setCount(count);
			itemsToShow.add(invData);
		}
		itemsToShow.remove(itemsToShow.size() - 1);

		gvEventItems.setNumColumns(itemsToShow.size());
		gvEventItems.setAdapter(new EventItemsGridAdapter(itemsToShow));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class EventItemsGridAdapter extends BaseAdapter {
		private ArrayList<InventoryItemData> data;

		public EventItemsGridAdapter(ArrayList<InventoryItemData> data) {
			super();
			this.data = new ArrayList<InventoryItemData>();
			if (data != null) {
				this.data.addAll(data);
			}
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			if (data.size() < 1 || arg0 >= data.size()) {
				return null;
			}
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = LayoutInflater
						.from(container.getContext())
						.inflate(
								R.layout.fragment_event_details_participate_join_items_item,
								container, false);
				InventoryItem inventoryItemView = (InventoryItem) convertView
						.findViewById(R.id.inventory_item);

				convertView.setTag(inventoryItemView);
			}

			InventoryItem inventoryItemView = (InventoryItem) convertView
					.getTag();

			if (position < data.size()) {
				InventoryItemData itemData = data.get(position);

				inventoryItemView.setName(itemData.getName());
				inventoryItemView.setCount(itemData.getCount());
				inventoryItemView.setResId(itemData.getResId());
				inventoryItemView.setEmpty(false);
			} else {
				inventoryItemView.setEmpty(true);
			}

			return convertView;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}
}
