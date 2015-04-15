package com.tv.xeeng.gui.fragments;

import android.content.*;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.BaseLayoutXeengActivity;
import com.tv.xeeng.gui.customview.EventRewardDialog;
import com.tv.xeeng.gui.itemdata.InventoryItemData;
import com.tv.xeeng.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class UserInfoInventoryFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int MAX_PAGE = 3;
	private static final int ITEM_PER_PAGE = UserInfoInventorySinglePageFragment.MAX_ITEM;

	// ===========================================================
	// Fields
	// ===========================================================
	private ViewPager vpInventory;
	private CirclePageIndicator indicator;

	private ArrayList<InventoryItemData> inventoryData;

	private int currentPage;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_INVENTORY)) {
				if (status) {
					inventoryData = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					vpInventory.setAdapter(new InventoryPagerAdapter(
							getChildFragmentManager(), inventoryData));

					if (currentPage > 0
							&& currentPage < vpInventory.getAdapter()
									.getCount()) {
						vpInventory.setCurrentItem(currentPage, false);
					} else if (currentPage > 0) {
						vpInventory.setCurrentItem(currentPage - 1, false);
					}
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_USE_INVENTORY_ITEM)) {
				final String message = intent
						.getStringExtra(NetworkUtils.MESSAGE_INFO) != null ? intent
						.getStringExtra(NetworkUtils.MESSAGE_INFO) : "";

				((BaseLayoutXeengActivity) getActivity()).hideLoading();

				if (status) {
					EventRewardDialog dialog = new EventRewardDialog(
							getActivity(), message);

					BusinessRequester.getInstance().getInventory();

					dialog.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							BusinessRequester.getInstance().getUserInfo(
									GameData.shareData().getMyself().id);
						}
					});

					dialog.show();
				} else {
					((BaseLayoutXeengActivity) getActivity()).alert(message);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_info_inventory,
				container, false);

		inventoryData = new ArrayList<InventoryItemData>(0);

		vpInventory = (ViewPager) view
				.findViewById(R.id.vp_user_info_inventory);
		vpInventory.setAdapter(new InventoryPagerAdapter(
				getChildFragmentManager(), inventoryData));

		indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
		indicator.setViewPager(vpInventory);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentPage = position;
				Log.d("UserInfoInventory", "Current page: " + currentPage);
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class InventoryPagerAdapter extends
			FragmentStatePagerAdapter {
		private ArrayList<InventoryItemData> data;

		public InventoryPagerAdapter(FragmentManager fm,
				ArrayList<InventoryItemData> data) {
			super(fm);
			this.data = data != null ? data
					: new ArrayList<InventoryItemData>();
		}

		@Override
		public Fragment getItem(int position) {
			int start = position * ITEM_PER_PAGE;
			int end = start + ITEM_PER_PAGE >= data.size() ? data.size()
					: start + ITEM_PER_PAGE;
			if (start >= data.size()) {
				start = 0;
				end = 0;
			}
			ArrayList<InventoryItemData> subList = new ArrayList<InventoryItemData>(
					data.subList(start, end));
			Fragment fragment = UserInfoInventorySinglePageFragment
					.newInstance(subList);

			return fragment;
		}

		@Override
		public int getCount() {
			return (data.size() - 1) / ITEM_PER_PAGE + 1;
		}

	}
}
