package com.tv.xeeng.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

//import com.appota.payment.AppotaPayment;
import com.devsmart.android.ui.HorizontalListView;
import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.billing.utils.IabException;
import com.tv.xeeng.billing.utils.IabHelper;
import com.tv.xeeng.billing.utils.IabHelper.OnConsumeFinishedListener;
import com.tv.xeeng.billing.utils.IabHelper.OnIabPurchaseFinishedListener;
import com.tv.xeeng.billing.utils.IabHelper.OnIabSetupFinishedListener;
import com.tv.xeeng.billing.utils.IabResult;
import com.tv.xeeng.billing.utils.Inventory;
import com.tv.xeeng.billing.utils.Purchase;
import com.tv.xeeng.billing.utils.SkuDetails;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.itemdata.ChargingItemData;
import com.tv.xeeng.gui.itemdata.ChargingItemData.ChargingItemType;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class ChargingActivity extends BaseLayoutXeengActivity {
	private static final String SKU_XEENG_PACKAGE_1 = "xeeng_package_1";
	private static final String SKU_XEENG_PACKAGE_2 = "xeeng_package_2";
	private static final String SKU_XEENG_PACKAGE_3 = "xeeng_package_3";
	private static final String SKU_XEENG_PACKAGE_4 = "xeeng_package_4";
	private static final String SKU_XEENG_PACKAGE_5 = "xeeng_package_5";

	private static final String INTENT_SMS_STATUS_SENT = "sms_sent";
	private static final String INTENT_SMS_STATUS_DELIVERY = "sms_delivery";

	// Appota constants
	private static final String NOTICEURL = "http://pg.xeengvn.com/AppotaPaygateService/webresources/AppotaCallback";
	private static final String APIKEY = "K-A164665-U00000-7L8LNY-6B54FEBCEF7F6DAC";
	private static final String SANDBOXAPIKEY = "SK-A164665-U00000-PXP9GK-B706DC088348BEBB";

	private static final Map<String, String> CARD_COURIER_NAME;
	static {
		CARD_COURIER_NAME = new HashMap<String, String>();

		CARD_COURIER_NAME.put("viettel", "VIETTEL");
		CARD_COURIER_NAME.put("mobifone", "MOBIFONE");
		CARD_COURIER_NAME.put("vinaphone", "VINAPHONE");
		CARD_COURIER_NAME.put("fpt", "FPT");
		CARD_COURIER_NAME.put("vnpt", "VNPTEPAY");
	}

	private HorizontalListView chargingItemListView;
	private ArrayList<ChargingItemData> chargingItems;
	private ChargingAdapter chargingAdapter;

	private TextView noticeText;

	private IabHelper billingHelper;
	private Inventory inventory;

	private boolean isPurchasing = false;

//	private AppotaPayment ap;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (billingHelper != null && requestCode == 1337) {
			isPurchasing = true;
			billingHelper.handleActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_charging);
//		ap = AppotaPayment.getInstance();
//		ap.setContext(ChargingActivity.this, APIKEY, SANDBOXAPIKEY, true);

		if (CommonUtils.GOOGLE_PLAY_VER) {
			String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqtxTVUM+tl1RHCHJ4hWgsgc/A6lJfcTJfhos1lCAQN4HcoD204pt+K8ZM7YdQLRI+8LzY6R235UHl5XeAGr4LrFSy6e7eX3ae/BawcmKJk5nhDCHEpAMLxCofTPUhBDuPiRGkCbo/Jc+vi/zZpJTzmGP1fYTWdxgx6qbvuufrdPqR4hLm0BxusmMH8z5t+qse6ELYgrfUvuYLg9JtQ5arzKh2ppk2d+sPvQP95UOFFsmbLcrMtUkbbrq2stelq88HCFmmZeJAA+zfUOL13BmwRZitFcXT62OK/m+cv5HmCCaWtrY23g936yNe92W4yL3zsBuogLjprrathvEiOodHwIDAQAB";

			Log.d("Charging Activity", "About to intitiate google play billing");
			billingHelper = new IabHelper(this, publicKey);
			billingHelper.enableDebugLogging(true, "Google Play Billing");
			billingHelper.startSetup(new OnIabSetupFinishedListener() {

				@Override
				public void onIabSetupFinished(IabResult result) {
					if (!result.isSuccess()) {
						Log.d("CharingActivity",
								"Google Play Billing Error: "
										+ result.getMessage() + "("
										+ result.getResponse() + ")");
						return;
					}
					if (billingHelper == null) {
						return;
					}

					Log.d("Charging Acitivty",
							"Successfully set up google play billing");
					List<String> skusList = new ArrayList<String>();
					skusList.add(SKU_XEENG_PACKAGE_1);
					skusList.add(SKU_XEENG_PACKAGE_2);
					skusList.add(SKU_XEENG_PACKAGE_3);
					skusList.add(SKU_XEENG_PACKAGE_4);
					skusList.add(SKU_XEENG_PACKAGE_5);

					try {
						inventory = billingHelper
								.queryInventory(true, skusList);
						Log.d("Charging Acitivty",
								"Successfully query google play inventory");
						addGooglePlayItems();
					} catch (IabException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

		chargingItems = new ArrayList<ChargingItemData>();
		chargingAdapter = new ChargingAdapter(chargingItems);
		chargingAdapter.setBtnBuyOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();

				ChargingItemData item = chargingItems.get(position);
				Log.d("ljdhfljsdhflsdhfsd", item.getType().toString());
				if (item.getType() == ChargingItemType.SMS) {
					if (CommonUtils.GOOGLE_PLAY_VER) {
						launchSMSApp(item, GameData.shareData().getMyself().id);
					} else {
						confirmSendSms(item);
					}
				} else if (item.getType() == ChargingItemType.CARD) {
					Intent intent = new Intent(ChargingActivity.this,
							CardChargingActivity.class);
					intent.putExtra(CardChargingActivity.KEY_LOGO,
							item.getLogoDrawable());
					String cardCourier = CommonUtils.APPOTA_VER ? CARD_COURIER_NAME
							.get(item.getProvider()) : item.getProvider();
					intent.putExtra(CardChargingActivity.KEY_COURIER,
							cardCourier);

					startActivity(intent);
				} else if (item.getType() == ChargingItemType.VISA) {
					String payload = GameData.shareData().getMyself().id + "";
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showLoading(false);
						}
					});
					billingHelper.launchPurchaseFlow(ChargingActivity.this,
							item.getContent(), 1337,
							new OnIabPurchaseFinishedListener() {
								@Override
								public void onIabPurchaseFinished(
										IabResult result, Purchase purchase) {
									if (result == null || purchase == null
											|| result.isFailure()) {
										isPurchasing = false;
										hideLoading();
										return;
									}
									isPurchasing = true;
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											showLoading(false);
										}
									});
									billingHelper.consumeAsync(purchase,
											new OnConsumeFinishedListener() {
												@Override
												public void onConsumeFinished(
														Purchase purchase,
														IabResult result) {
													confirmGooglePlayPurchase(
															result, purchase);
												}
											});
								}
							}, payload);
				}
			}
		});

		chargingItemListView = (HorizontalListView) findViewById(R.id.lv_charging);
		chargingItemListView.setAdapter(chargingAdapter);

		SpannableString s = new SpannableString(getString(R.string.notice_text));
		Drawable icon = getResources().getDrawable(R.drawable.icon_coin_xeeng);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(),
				icon.getIntrinsicHeight());

		s.setSpan(new ImageSpan(icon, ImageSpan.ALIGN_BASELINE), s.toString()
				.indexOf("icon2"),
				s.toString().indexOf("icon2") + "icon2".length(),
				SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

		noticeText = (TextView) findViewById(R.id.tv_notice);
		noticeText.setText(s, BufferType.SPANNABLE);
		noticeText.setTypeface(noticeText.getTypeface(), Typeface.ITALIC);

		showLoading("Loading");

		txtTitle = getString(R.string.activity_charging_title);
		isVisibleBtnSettings = true;

		initTopBar();
		initBottomBar();

		registerReceiver(smsReceiver, new IntentFilter(INTENT_SMS_STATUS_SENT));
		registerReceiver(smsReceiver, new IntentFilter(
				INTENT_SMS_STATUS_DELIVERY));
	}

	private void confirmSendSms(final ChargingItemData item) {
		final BasicDialog dialog = confirm("Bạn có muốn nạp " + item.getValue()
				+ " Xeeng bằng tin nhắn? (" + item.getPrice() + "/ 1 tin)",
				"Có");
		dialog.setNegativeText("Không");
		dialog.setPositiveOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

				try {
					sendSms(item);
				} catch (SecurityException ex) {
					ex.printStackTrace();
					launchSMSApp(item, GameData.shareData().getMyself().id);
				}
			}
		});
	}

	private void sendSms(ChargingItemData item) throws SecurityException {
		if (!CommonUtils.APPOTA_VER) {
			SmsManager manager = SmsManager.getDefault();
			long playerId = GameData.shareData().getMyself().id;
			String content = new StringBuffer(item.getSmsTemplate())
					.append(" ").append(playerId).toString();

			PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0,
					new Intent(INTENT_SMS_STATUS_SENT), 0);
			PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0,
					new Intent(INTENT_SMS_STATUS_DELIVERY), 0);

			manager.sendTextMessage(item.getProvider(), null, content,
					sentIntent, deliveryIntent);
		} else {
			String username = GameData.shareData().isGuest() ? GameData
					.shareData().getMyself().character : UserPreference
					.sharePreference().readLoginUsername();

			/*ap.makeDirectSMSPayment(item.getPrice(), "",
					String.valueOf(GameData.shareData().getMyself().id) + " "
							+ username, NOTICEURL, "Thanh toan qua SMS", "");*/

			Log.d("ChargingActivity", "target username" + ": " + username);
		}
	}

	private void launchSMSApp(ChargingItemData payment, long playerId) {

		String content = new StringBuffer(payment.getSmsTemplate()).append(" ")
				.append(playerId).toString();
		Intent launchsms = new Intent(Intent.ACTION_VIEW);
		launchsms.setType("vnd.android-dir/mms-sms");
		launchsms.putExtra("address", payment.getProvider());
		launchsms.putExtra("sms_body", content);

		try {
			startActivity(launchsms);
		} catch (ActivityNotFoundException anfe) {

			AlertDialog.Builder b;
			b = new AlertDialog.Builder(this);
			b.setTitle("Thông báo");
			b.setMessage("Thiết bị của bạn không có ứng dụng gửi tin nhắn. Vui lòng soạn tin nhắn theo cú pháp sau để kích hoạt "
					+ content
					+ " rồi gửi tới đầu số "
					+ payment.getProvider()
					+ " để kích hoạt");
			b.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			b.show();
		}
	}

	/**
	 * Send purchase information to server to confirm the request.
	 * 
	 * @param result
	 *            {@link IabResult} from GooglePlayService
	 * @param info
	 *            {@link Purchase} from GooglePlayService
	 */
	private void confirmGooglePlayPurchase(final IabResult result,
			final Purchase info) {
		Log.d("ChargingActivity",
				"UserId: " + GameData.shareData().getMyself().id
						+ ", productId: " + info.getSku() + ", transactionId: "
						+ info.getToken());
		Log.d("ChargingActivity",
				"Original jsons, info: " + info.getOriginalJson());

		if (result.isFailure()) {
			isPurchasing = false;
			return;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showLoading(false);
			}
		});

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String url = "http://pg.xeengvn.com/GooglePaygateService/webresources/ConfirmPurchase/UserId/"
						+ GameData.shareData().getMyself().id
						+ "/ProductId/"
						+ info.getSku() + "/TransactionId/" + info.getToken();

				try {
					URLConnection con = (new URL(url)).openConnection();
					InputStream is = con.getInputStream();

					byte[] buffer = new byte[1024];
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					int length = 0;
					while ((length = is.read(buffer)) != -1) {
						os.write(buffer, 0, length);
					}

					String response = os.toString();
					Log.d("ChargingActivity", "Response from server: "
							+ response);
					final JSONObject json = new JSONObject(response);

					runOnUiThread(new Runnable() {
						public void run() {
							try {
								if (json.get("code").toString()
										.equalsIgnoreCase("0")) {
									// Success
									new Handler().postDelayed(new Runnable() {

										@Override
										public void run() {
											BusinessRequester
													.getInstance()
													.getUserInfo(
															GameData.shareData()
																	.getMyself().id);
										}
									}, 500);
								}
								alert(json.get("message").toString());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isPurchasing = false;
				hideLoading();
			}
		};

		BackgroundThreadManager.post(runnable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalBroadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_GET_SMS_CHARGING_LIST));
		if (!isPurchasing) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					BusinessRequester.getInstance().getChargingSMSList();
				}
			}, 100);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(smsReceiver);
		if (billingHelper != null) {
			billingHelper.dispose();
			billingHelper = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalBroadcastManager.unregisterReceiver(receiver);
	}

	private void addGooglePlayItems() {
		if (inventory != null && chargingItems != null) {
			List<String> skusList = new ArrayList<String>();
			skusList.add(SKU_XEENG_PACKAGE_1);
			skusList.add(SKU_XEENG_PACKAGE_2);
			skusList.add(SKU_XEENG_PACKAGE_3);
			skusList.add(SKU_XEENG_PACKAGE_4);
			skusList.add(SKU_XEENG_PACKAGE_5);

			int i = 0;
			for (String str : skusList) {
				SkuDetails skuDetails = inventory.getSkuDetails(str);
				if (skuDetails != null) {
					chargingItems.add(i++, new ChargingItemData(
							ChargingItemType.VISA, skuDetails.getPrice(), str));
				}
			}
		}
	}

	private class ChargingAdapter extends BaseAdapter {
		private ArrayList<ChargingItemData> data;
		private OnClickListener btnBuyOnClickListener;

		public void setData(ArrayList<ChargingItemData> data) {
			this.data = new ArrayList<ChargingItemData>();
			for (ChargingItemData itemData : data) {
				if (itemData.getType() != ChargingItemType.SMS) {
					this.data.add(itemData);
				}
			}
		}

		public ChargingAdapter(ArrayList<ChargingItemData> data) {
			setData(data);
		}

		public void setBtnBuyOnClickListener(
				OnClickListener btnBuyOnClickListener) {
			this.btnBuyOnClickListener = btnBuyOnClickListener;
		}

		class ChargingAdapterViewsHolder {
			TextView title;
			TextView value;
			ImageView itemImage;
			TextView price;
			Button btnBuy;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.activity_charging_item, parent, false);
				ChargingAdapterViewsHolder holder = new ChargingAdapterViewsHolder();

				holder.title = (TextView) convertView
						.findViewById(R.id.tv_item_title);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.iv_item_image);
				holder.price = (TextView) convertView
						.findViewById(R.id.tv_item_price);
				holder.btnBuy = (Button) convertView
						.findViewById(R.id.btn_buy_now);
				holder.value = (TextView) convertView
						.findViewById(R.id.tv_item_value);

				convertView.setTag(holder);
			}
			convertView.setVisibility(View.VISIBLE);
			ChargingItemData payment = data.get(position);
			if (payment != null) {
				ChargingAdapterViewsHolder holder = (ChargingAdapterViewsHolder) convertView
						.getTag();
				holder.title.setText(payment.getType().toString());
				if (payment.getType() == ChargingItemType.CARD) {
					holder.price.setText(CommonUtils.capitalize(payment
							.getProvider()));
					holder.value.setVisibility(View.GONE);
				} else if (payment.getType() == ChargingItemType.SMS) {
					holder.price.setText(payment.getContent());
					holder.value.setVisibility(View.GONE);
				} else if (payment.getType() == ChargingItemType.VISA) {
					SkuDetails skuDetails = inventory.getSkuDetails(payment
							.getContent());
					holder.title.setText(skuDetails.getTitle());
					holder.price.setText(payment.getProvider());
					holder.value.setVisibility(View.GONE);
				}
				holder.itemImage.setImageResource(payment.getLogoDrawable());

				holder.btnBuy.setTag(position);
				holder.btnBuy.setOnClickListener(btnBuyOnClickListener);
			}
			return convertView;
		}
	}

	private BroadcastReceiver smsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case RESULT_OK:
				if (intent.getAction().equalsIgnoreCase(INTENT_SMS_STATUS_SENT)) {
					Toast.makeText(ChargingActivity.this, "Đang gửi tin nhắn",
							Toast.LENGTH_SHORT).show();
				} else if (intent.getAction().equalsIgnoreCase(
						INTENT_SMS_STATUS_DELIVERY)) {
					Toast.makeText(ChargingActivity.this,
							"Đã gửi tin nhắn thành công", Toast.LENGTH_SHORT)
							.show();
				}
				BusinessRequester.getInstance().getUserInfo(
						GameData.shareData().getMyself().id);
				break;
			default:
				Toast.makeText(ChargingActivity.this,
						"Gửi tin nhắn không thành công", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MessageService.INTENT_GET_SMS_CHARGING_LIST
					.equalsIgnoreCase(intent.getAction())) {
				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (status) {
					chargingItems = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);

					if (CommonUtils.GOOGLE_PLAY_VER) {
						addGooglePlayItems();
					}

					chargingAdapter.setData(chargingItems);
					chargingAdapter.notifyDataSetChanged();
				} else {
					Log.d("Content",
							"False Content: "
									+ intent.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO));
				}
			}
		}
	};
}
