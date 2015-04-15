package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

//import com.appota.payment.AppotaPayment;
//import com.appota.payment.callback.CardPaymentCallback;
//import com.appota.payment.core.AppotaPaymentException;
//import com.appota.payment.model.TransactionResult;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class CardChargingActivity extends BaseLayoutXeengActivity {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String KEY_LOGO = "logo";
	public static final String KEY_COURIER = "courier";
	// ===========================================================
	// Appota constants
	private static final String NOTICEURL = "http://pg.xeengvn.com/AppotaPaygateService/webresources/AppotaCallback";
	private static final String APIKEY = "K-A164665-U00000-7L8LNY-6B54FEBCEF7F6DAC";
	private static final String SANDBOXAPIKEY = "SK-A164665-U00000-PXP9GK-B706DC088348BEBB";

	//
	// ===========================================================
	// Fields
	// ===========================================================
	private EditText edtCode;
	private EditText edtSerial;

	private Button btnAccept;
	// =============================================================
	// Appota
//	private AppotaPayment ap;
	// =============================================================

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_CARD_CHARGING_RESULT)) {
				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);

				hideLoading();
				if (status) {
					String contentString = intent.getExtras().getString(
							NetworkUtils.MESSAGE_INFO);

					// contentString +=
					// "\nHệ thống đang xử lý. Đăng nhập lại để cập nhật.";
					final BasicDialog dialog = new BasicDialog(
							CardChargingActivity.this, "Thông báo",
							contentString, "Nạp tiếp", "Mua Gold");

					dialog.setPositiveOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									CardChargingActivity.this,
									StoreActivity.class);
							startActivity(intent);
							dialog.dismiss();
							finish();
						}
					});

					dialog.setNegativeOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.setCloseOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							finish();
						}
					});
					dialog.show();
				} else {
					BasicDialog dialog = new BasicDialog(
							CardChargingActivity.this,
							getString(R.string.default_dialog_title),
							getResources().getString(R.string.card_error_msg),
							getString(R.string.ok), "");
					dialog.show();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_card_charging);

		int logoDrawable = getIntent().getIntExtra(KEY_LOGO,
				R.drawable.icon_sms_1);
		final String carrierName = getIntent().getStringExtra(KEY_COURIER);

		((ImageView) findViewById(R.id.iv_logo)).setImageResource(logoDrawable);

		// ============================================================
//		ap = AppotaPayment.getInstance();
//		ap.setContext(CardChargingActivity.this, APIKEY, SANDBOXAPIKEY, true);

		// ============================================================

		edtCode = (EditText) findViewById(R.id.edt_code);
		edtSerial = (EditText) findViewById(R.id.edt_serial);

		btnAccept = (Button) findViewById(R.id.btn_accept);
		btnAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (TextUtils.isEmpty(edtCode.getText())
						|| TextUtils.isEmpty(edtSerial.getText())) {
					BasicDialog dialog = new BasicDialog(
							CardChargingActivity.this,
							getString(R.string.default_dialog_title),
							"Thẻ không hợp lệ. Xin vui lòng kiểm tra lại mã thẻ và số serial",
							getString(R.string.ok), "");
					dialog.show();
					return;
				}
				if (CommonUtils.APPOTA_VER) {
					/*try {
						String username = GameData.shareData().isGuest() ? GameData
								.shareData().getMyself().character
								: UserPreference.sharePreference()
										.readLoginUsername();

						ap.makeCardPayment(edtSerial.getText().toString(),
								edtCode.getText().toString(),
								carrierName.toUpperCase(), NOTICEURL, "",
								GameData.shareData().getMyself().id + " "
										+ username, new CardPaymentCallback() {

									@Override
									public void onCardPaymentSuccess(
											TransactionResult arg0) {
										// TODO Auto-generated method stub
										String msg = "Nạp Xeng thành công";
										try {
											msg = "Bạn đã nạp thành công "
													+ Integer.parseInt(arg0
															.getAmount()) * 100
													+ " Xeng.";
										} catch (NumberFormatException ex) {
											ex.printStackTrace();
										}
										final BasicDialog dialog = new BasicDialog(
												CardChargingActivity.this,
												"Thông báo", msg, "Nạp tiếp",
												"Mua Gold");

										dialog.setPositiveOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												Intent intent = new Intent(
														CardChargingActivity.this,
														StoreActivity.class);
												startActivity(intent);
												dialog.dismiss();
												finish();
											}
										});

										dialog.setNegativeOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												dialog.dismiss();
											}
										});
										dialog.setCloseOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												dialog.dismiss();
												finish();
											}
										});
										dialog.show();
									}

									@Override
									public void onCardPaymentError(String arg0) {
										// TODO Auto-generated method stub
										BasicDialog dialog = new BasicDialog(
												CardChargingActivity.this,
												getString(R.string.default_dialog_title),
												"Có lỗi xảy ra trong quá trình giao dịch, vui lòng thử lại",
												getString(R.string.close), "");
										dialog.show();
										return;
									}
								});
					} catch (AppotaPaymentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				} else {
					BusinessRequester.getInstance().chargingCard(
							edtCode.getText().toString(),
							edtSerial.getText().toString(),
							carrierName.toLowerCase());

					showLoading("Đang xử lý...");
				}
			}
		});

		isVisibleBtnHelp = false;
		isVisibleBtnSettings = false;
		txtTitle = getString(R.string.activity_card_charging_title);

		initTopBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalBroadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_CARD_CHARGING_RESULT));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalBroadcastManager.unregisterReceiver(receiver);
	}

	@Override
	public void onBackPressed() {
		// Log.d("BackPressed", "BackPressed");
		finish();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ============================================================
	// Appota

}
