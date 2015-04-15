package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.devsmart.android.ui.HorizontalListView;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.CashShopItemInfoDialog;
import com.tv.xeeng.gui.itemdata.CashShopItemData;
import com.tv.xeeng.gui.itemdata.ShopItemData;
import com.tv.xeeng.R;

import java.util.ArrayList;

public class StoreActivity extends BaseLayoutXeengActivity {

	private HorizontalListView lvStoreItems;

	private Button btnCharge;
	private RadioGroup rgStoreCategoryTabs;
	private int checkedId = R.id.rb_store_gold;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_store);

		findViewById(R.id.btn_shopping).setVisibility(View.GONE);
		findViewById(R.id.btn_gift).setVisibility(View.GONE);

		lvStoreItems = (HorizontalListView) findViewById(R.id.lv_store);

		btnCharge = (Button) findViewById(R.id.btn_charging);
//		btnCharge.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(StoreActivity.this,
//						ChargingActivity.class);
//				startActivity(intent);
//			}
//		});

		rgStoreCategoryTabs = (RadioGroup) findViewById(R.id.rg_store_category);
		rgStoreCategoryTabs
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rb_store_gold:
							BusinessRequester.getInstance().getShopItems();
							StoreActivity.this.checkedId = checkedId;
							showLoading();
							break;
						case R.id.rb_store_item:
							BusinessRequester.getInstance().getCashShopItems();
							StoreActivity.this.checkedId = checkedId;
							showLoading();
							break;
						case R.id.rb_store_sale:
							Toast.makeText(StoreActivity.this,
									R.string.text_developing,
									Toast.LENGTH_SHORT).show();
							group.setOnCheckedChangeListener(null);
							group.check(StoreActivity.this.checkedId);
							group.setOnCheckedChangeListener(this);
							break;
						default:
							break;
						}
					}
				});

		txtTitle = getString(R.string.activity_store_title);
		isVisibleBtnSettings = true;

		initTopBar();
		initBottomBar();

		BusinessRequester.getInstance().getShopItems();
		showLoading();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (MessageService.INTENT_GET_SHOP_ITEMS.equalsIgnoreCase(intent
					.getAction())) {

				hideLoading();
				if (status) {
					ArrayList<ShopItemData> storeItems = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					lvStoreItems.setAdapter(new StoreAdapter(
							StoreActivity.this, storeItems));
					lvStoreItems.invalidate();
					Log.d("Content", "content: " + storeItems);
				} else {

					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						alert(msg);
					} else {
						alert(getString(R.string.has_errors));
					}
				}

			} else if (MessageService.INTENT_PURCHASE_SHOP_ITEM
					.equalsIgnoreCase(intent.getAction())
					|| MessageService.INTENT_PURCHASE_CASH_SHOP_ITEM
							.equalsIgnoreCase(intent.getAction())) {

				hideLoading();

				if (status) {
					BusinessRequester.getInstance().getUserInfo(
							GameData.shareData().getMyself().id);
					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					alert(msg);
				} else {
					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						alert(msg);
					} else {
						alert(getString(R.string.has_errors));
					}
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_CASH_SHOP_ITEMS)) {
				hideLoading();

				if (status) {
					ArrayList<CashShopItemData> storeItems = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					lvStoreItems.setAdapter(new CashStoreAdapter(storeItems));
					lvStoreItems.invalidate();
				} else {

					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						alert(msg);
					} else {
						alert(getString(R.string.has_errors));
					}
				}
			}

		}
	};

	@Override
	protected void registerReceiveNotification() {
		super.registerReceiveNotification();
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_SHOP_ITEMS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_PURCHASE_SHOP_ITEM));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_CASH_SHOP_ITEMS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_PURCHASE_CASH_SHOP_ITEM));
	}

	@Override
	protected void unregisterReceiveNotification() {
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
		super.unregisterReceiveNotification();
	}

	@Override
	protected void needReconnection() {

	}

	static class StoreAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<ShopItemData> data;
		private int[] drawables = { R.drawable.icon_gold_pack1,
				R.drawable.icon_gold_pack2, R.drawable.icon_gold_pack3,
				R.drawable.icon_gold_pack4, R.drawable.icon_gold_pack5,
				R.drawable.icon_gold_pack6, R.drawable.icon_gold_pack7,
				R.drawable.icon_gold_pack8, R.drawable.icon_gold_pack9,
				R.drawable.icon_gold_pack10 };

		public StoreAdapter(Context context) {
			this.context = context;
			this.data = new ArrayList<ShopItemData>();
		}

		public StoreAdapter(Context context, ArrayList<ShopItemData> data) {
			this.context = context;
			this.data = data;
		}

		public void setData(ArrayList<ShopItemData> data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ShopItemData shopItem = data.get(position);
			if (convertView == null) {
				convertView = ((LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.activity_store_item, parent, false);
				StoreAdapterViewsHolder holder = new StoreAdapterViewsHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.tv_item_title);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.iv_item_image);
				holder.price = (TextView) convertView
						.findViewById(R.id.tv_item_price);
				holder.btnBuy = (Button) convertView
						.findViewById(R.id.btn_buy_now);
				holder.bonus = (TextView) convertView
						.findViewById(R.id.tv_bonus);

				convertView.setTag(holder);
			}
			if (shopItem != null) {
				StoreAdapterViewsHolder holder = (StoreAdapterViewsHolder) convertView
						.getTag();

				holder.title
						.setText(CommonUtils.formatGold(shopItem.getValue()));
				holder.itemImage.setImageResource(drawables[position > 9 ? 9
						: position]);
				holder.price.setText("" + (int) shopItem.getPrice());
				holder.btnBuy.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final StoreActivity activity = (StoreActivity) context;

						String code = "<br /><br /><p>Bạn có chắc chắn mua  "
								+ CommonUtils.formatGold(shopItem.getValue())
								+ " <img src = 'gold' > <br /> bằng "
								+ (int) shopItem.getPrice()
								+ " <img src = 'xeeng' > không?</p>";
						Spanned spanned = Html.fromHtml(code, activity, null);
						final BasicDialog dialog = activity.confirm(spanned);
						dialog.setPositiveOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								long userId = GameData.shareData().getMyself().id;
								String shopItemId = shopItem.getId();

								BusinessRequester.getInstance()
										.purchaseShopItem(userId, shopItemId);
								activity.showLoading();
								dialog.dismiss();
							}
						});
					}
				});

				holder.bonus.setText(String.format("+%2.0f%%",
						shopItem.getBonus()));
				if (shopItem.getBonus() > 0) {
					holder.bonus.setVisibility(View.VISIBLE);

					Animation anim = new RotateAnimation(0f, -15f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					anim.setFillAfter(true);
					anim.setDuration(0);
					holder.bonus.startAnimation(anim);
				} else {
					holder.bonus.setVisibility(View.INVISIBLE);
				}
			}
			return convertView;
		}
	}

	static class StoreAdapterViewsHolder {
		TextView title;
		ImageView itemImage;
		TextView price;
		Button btnBuy;
		TextView bonus;
	}

	class CashStoreAdapter extends BaseAdapter {
		private ArrayList<CashShopItemData> data;

		public CashStoreAdapter(ArrayList<CashShopItemData> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
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
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.activity_store_item, parent, false);

				StoreAdapterViewsHolder holder = new StoreAdapterViewsHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.tv_item_title);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.iv_item_image);
				holder.price = (TextView) convertView
						.findViewById(R.id.tv_item_price);
				holder.btnBuy = (Button) convertView
						.findViewById(R.id.btn_buy_now);
				holder.bonus = (TextView) convertView
						.findViewById(R.id.tv_bonus);

				convertView.setTag(holder);
			}
			StoreAdapterViewsHolder holder = (StoreAdapterViewsHolder) convertView
					.getTag();

			final CashShopItemData item = data.get(position);

			holder.title.setText(item.getName());
			holder.title.setCompoundDrawables(null, null, null, null);

			holder.price.setText(CommonUtils.formatCash(item.getPrice()));

			int resId = R.drawable.icon_coin_xeeng;
			if (item.getType().equalsIgnoreCase(CashShopItemData.TYPE_GOLD)) {
				resId = R.drawable.icon_coin_gold;
			}
			Drawable icon = parent.getContext().getResources()
					.getDrawable(resId);
			icon.setBounds(0, 0, icon.getIntrinsicWidth(),
					icon.getIntrinsicHeight());

			holder.price.setCompoundDrawables(null, null, icon, null);

			holder.itemImage.setImageResource(item.getResId());
			holder.itemImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CashShopItemInfoDialog dialog = new CashShopItemInfoDialog(
							parent.getContext(), item, null);
					dialog.show();
				}
			});

			holder.btnBuy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String icon = "'xeeng'";
					if (item.getType().equalsIgnoreCase(
							CashShopItemData.TYPE_GOLD)) {
						icon = "'gold'";
					}
					String code = "<br /><br /><p>Bạn có chắc chắn mua  "
							+ item.getName() + " bằng " + (int) item.getPrice()
							+ " <img src = " + icon + " > không?</p>";
					Spanned spanned = Html.fromHtml(code, StoreActivity.this,
							null);
					final BasicDialog dialog = StoreActivity.this
							.confirm(spanned);
					dialog.setPositiveOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							StoreActivity.this.showLoading();
							BusinessRequester.getInstance()
									.purchaseCashShopItem(item.getCode());
						}
					});
				}
			});
			holder.bonus.setVisibility(View.INVISIBLE);

			return convertView;
		}
	}
}
