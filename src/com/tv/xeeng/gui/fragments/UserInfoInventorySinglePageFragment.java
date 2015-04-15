package com.tv.xeeng.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import com.tv.xeeng.gui.BaseLayoutXeengActivity;
import com.tv.xeeng.gui.customview.InventoryItem;
import com.tv.xeeng.gui.customview.InventoryItemInfoDialog;
import com.tv.xeeng.gui.customview.InventoryItemInfoDialog.InventoryItemInfoDialogListener;
import com.tv.xeeng.gui.itemdata.InventoryItemData;
import com.tv.xeeng.R;

import java.util.ArrayList;

public class UserInfoInventorySinglePageFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TAG_INVENTORY_PAGE_DATA = "inventory_data";
	public static final int MAX_ITEM = 8;

	// ===========================================================
	// Fields
	// ===========================================================
	private GridView gvInventoryItems;
	private ArrayList<InventoryItemData> data;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static UserInfoInventorySinglePageFragment newInstance(
			ArrayList<InventoryItemData> data) {
		UserInfoInventorySinglePageFragment fragment = new UserInfoInventorySinglePageFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(TAG_INVENTORY_PAGE_DATA, data);

		fragment.setArguments(bundle);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_user_info_inventory_single_page, container,
				false);

		Bundle bundle = getArguments();
		data = bundle.getParcelableArrayList(TAG_INVENTORY_PAGE_DATA);

		gvInventoryItems = (GridView) view
				.findViewById(R.id.gv_inventory_items);
		gvInventoryItems.setAdapter(new InventoryItemsGridAdapter(data));
		gvInventoryItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				InventoryItemData itemData = (InventoryItemData) adapterView
						.getItemAtPosition(position);
				if (itemData == null) {
					return;
				}
				InventoryItemInfoDialog dialog = new InventoryItemInfoDialog(
						getActivity(), itemData,
						new InventoryItemInfoDialogListener() {

							@Override
							public void btnUseItemOnClick(View v) {
								((BaseLayoutXeengActivity) getActivity())
										.showLoading();
							}

							@Override
							public void btnSendGiftOnClick(View v) {
							}
						});
				dialog.show();
			}
		});

		return view;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class InventoryItemsGridAdapter extends BaseAdapter {
		private ArrayList<InventoryItemData> data;

		public InventoryItemsGridAdapter(ArrayList<InventoryItemData> data) {
			super();
			this.data = new ArrayList<InventoryItemData>();
			if (data != null) {
				this.data.addAll(data);
			}
		}

		@Override
		public int getCount() {
			return 8;
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
				convertView = LayoutInflater.from(container.getContext())
						.inflate(R.layout.fragment_user_info_inventory_item,
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

	}
}
