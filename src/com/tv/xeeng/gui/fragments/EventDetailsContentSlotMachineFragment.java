package com.tv.xeeng.gui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gui.SlotMachineActivity;
import com.tv.xeeng.gui.itemdata.SlotMachineComboItemData;
import com.tv.xeeng.R;

public class EventDetailsContentSlotMachineFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private ListView lvSlotMachineCombo;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (status) {
				if (intent.getAction().equalsIgnoreCase(
						MessageService.INTENT_GET_ROULETTE_GUIDE)) {
					ArrayList<SlotMachineComboItemData> data = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					lvSlotMachineCombo.setAdapter(new SlotMachineComboAdapter(
							data));
					
					((Filterable) lvSlotMachineCombo.getAdapter()).getFilter()
							.filter(((SlotMachineActivity) getActivity())
									.getPrevReward());
				} else if (intent.getAction().equalsIgnoreCase(
						SlotMachineActivity.INTENT_SLOT_MACHINE_STOP_SPINNING)) {
					String reward = intent
							.getStringExtra(SlotMachineActivity.KEY_REWARD);

					((Filterable) lvSlotMachineCombo.getAdapter()).getFilter()
							.filter(reward);
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

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_event_details_content_slot_machine,
				container, false);

		lvSlotMachineCombo = (ListView) view
				.findViewById(R.id.lv_slot_machine_combo);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(MessageService.INTENT_GET_ROULETTE_GUIDE));

		BusinessRequester.getInstance().getRouletteGuide();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(MessageService.INTENT_GET_ROULETTE_GUIDE));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(
						SlotMachineActivity.INTENT_SLOT_MACHINE_STOP_SPINNING));
		
		ListAdapter adapter = lvSlotMachineCombo.getAdapter();
		if (adapter != null) {
			((Filterable) adapter).getFilter().filter(
					((SlotMachineActivity) getActivity()).getPrevReward());
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				receiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static class SlotMachineComboAdapter extends BaseAdapter implements
			Filterable {
		private ArrayList<SlotMachineComboItemData> data;
		private ArrayList<SlotMachineComboItemData> filteredData;

		private Filter filter;

		private static final Map<String, Integer> itemToResourceMap = new HashMap<String, Integer>();

		static {
			itemToResourceMap.put("c1", R.drawable.items_slot1);
			itemToResourceMap.put("c2", R.drawable.items_slot2);
			itemToResourceMap.put("c3", R.drawable.items_slot3);
			itemToResourceMap.put("r1", R.drawable.items_slot4);
			itemToResourceMap.put("r2", R.drawable.items_slot5);
			itemToResourceMap.put("r3", R.drawable.items_slot6);
			itemToResourceMap.put("x10", R.drawable.items_slot7);
			itemToResourceMap.put("x50", R.drawable.items_slot8);
		}

		public SlotMachineComboAdapter(ArrayList<SlotMachineComboItemData> data) {
			this.data = data;
			this.filteredData = new ArrayList<SlotMachineComboItemData>();

			for (SlotMachineComboItemData slotMachineComboItemData : data) {
				if (slotMachineComboItemData.getRound() == 1) {
					filteredData.add(slotMachineComboItemData);
				}
			}
		}

		@Override
		public int getCount() {
			return filteredData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return filteredData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = LayoutInflater
						.from(container.getContext())
						.inflate(
								R.layout.fragment_event_details_content_slot_machine_item,
								container, false);
			}

			SlotMachineComboItemData item = filteredData.get(position);
			if (item != null) {
				ImageView[] ivItems = new ImageView[3];
				ivItems[0] = (ImageView) convertView
						.findViewById(R.id.iv_slot_item_1);
				ivItems[1] = (ImageView) convertView
						.findViewById(R.id.iv_slot_item_2);
				ivItems[2] = (ImageView) convertView
						.findViewById(R.id.iv_slot_item_3);

				TextView tvReward = (TextView) convertView
						.findViewById(R.id.tv_slot_reward);
				tvReward.setText("x" + item.getPrice());

				int i = 0;

				// if (!item.getLastCombo().equalsIgnoreCase("none")) {
				// String[] lastCombos = item.getLastCombo().split(";");
				// for (i = 0; i < lastCombos.length; i++) {
				// ivItems[i].setImageResource(itemToResourceMap
				// .get(lastCombos[i]));
				// }
				// }
				ivItems[i++].setImageResource(itemToResourceMap.get(item
						.getNextCombo()));
				for (; i < 3; i++) {
					ivItems[i].setImageDrawable(null);
				}
			}

			return convertView;
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new SlotMachineComboFilter();
			}
			return filter;
		}

		private class SlotMachineComboFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence cs) {
				FilterResults result = new FilterResults();

				filteredData.clear();

				if (cs == null) {
					for (SlotMachineComboItemData slotMachineComboItemData : data) {
						if (slotMachineComboItemData.getRound() == 1) {
							filteredData.add(slotMachineComboItemData);
						}
					}
				} else {
					for (SlotMachineComboItemData itemData : data) {
						if (itemData.getLastCombo().equalsIgnoreCase(
								cs.toString())) {
							filteredData.add(itemData);
						}
					}
				}

				if (filteredData.size() == 0) {
					for (SlotMachineComboItemData slotMachineComboItemData : data) {
						if (slotMachineComboItemData.getRound() == 1) {
							filteredData.add(slotMachineComboItemData);
						}
					}
				}

				result.count = filteredData.size();
				result.values = filteredData;

				return result;
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
}
