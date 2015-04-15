package com.tv.xeeng.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class RegisterActivity extends BaseXeengActivity {

	// Topbar widgets
	Button btnBack;
	TextView tvTitle;

	// Widgets
	EditText edtUsername, edtPassword, edtRePassword, edtCharacter;
	EditText edtPhone, edtPersonalId;
	RadioGroup radGender;
	RadioButton radGenderMale, radGenderFemale;
	Button btnRegister;

	//
	String username, password;
	boolean isSendingRequest = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();
	}

	@Override
	protected void registerReceiveNotification() {
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_REGISTER));
	}

	@Override
	protected void unregisterReceiveNotification() {
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	@Override
	protected void needReconnection() {
		if (isSendingRequest) {
			WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = manager.getConnectionInfo();
			String MACAddress = wifiInfo.getMacAddress();
			BusinessRequester.getInstance().register(username, password,
					MACAddress, radGenderMale.isChecked(),
					edtCharacter.getText().toString(),
					edtPersonalId.getText().toString(),
					edtPhone.getText().toString());
		}
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			isSendingRequest = false;

			if (MessageService.INTENT_REGISTER.equalsIgnoreCase(intent
					.getAction())) {

				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, true);
				if (status) {

					int regCount = UserPreference.sharePreference()
							.readRegCount();
					regCount++;
					UserPreference.sharePreference().rememberRegCount(regCount);
					UserPreference.sharePreference().rememberUser(username,
							password);

					final BasicDialog dialog = alert("Chúc mừng bạn đã đăng ký thành công.");
					dialog.setPositiveOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (GameData.shareData().getMyself() != null) {
								BusinessRequester.getInstance().logout();
								GameData.shareData().clean();

								hideLoading();
								// Intent i = new Intent(RegisterActivity.this,
								// LoginActivity.class);
								// ComponentName cn = i.getComponent();
								// Intent loginIntent =
								// IntentCompat.makeRestartActivityTask(cn);
								// startActivity(loginIntent);
								// finish();
							}

							dialog.dismiss();
							finish();
						}
					});

				} else {

					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						alert(msg);
					}
				}

			}
		}
	};

	OnClickListener clickHandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_back:
				onBackPressed();
				break;

			case R.id.btn_register:
				String msg = validateInput();
				if (msg == null) {

					showLoading("Đang đăng ký...");
					isSendingRequest = true;

					WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = manager.getConnectionInfo();
					String MACAddress = wifiInfo.getMacAddress();

					GameSocket.shareSocket().open();

					username = edtUsername.getText().toString();
					password = edtPassword.getText().toString();

					BusinessRequester.getInstance().register(username,
							password, MACAddress, radGenderMale.isChecked(),
							edtCharacter.getText().toString(),
							edtPersonalId.getText().toString(),
							edtPhone.getText().toString());
				} else {

					alert(msg);
				}
				break;
			}
		}
	};

	private void initLayout() {
		setContentView(R.layout.activity_register);

		btnBack = (Button) findViewById(R.id.btn_back);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		edtUsername = (EditText) findViewById(R.id.edt_username);
		edtPassword = (EditText) findViewById(R.id.edt_password);
		edtRePassword = (EditText) findViewById(R.id.edt_repassword);
		edtCharacter = (EditText) findViewById(R.id.edt_character);
		edtPersonalId = (EditText) findViewById(R.id.edt_personal_id);
		edtPhone = (EditText) findViewById(R.id.edt_phone);
		radGender = (RadioGroup) findViewById(R.id.rad_gender);
		radGenderMale = (RadioButton) findViewById(R.id.rad_gender_male);
		radGenderFemale = (RadioButton) findViewById(R.id.rad_gender_female);
		btnRegister = (Button) findViewById(R.id.btn_register);

		btnBack.setOnClickListener(clickHandler);
		btnRegister.setOnClickListener(clickHandler);

		tvTitle.setText("ĐĂNG KÍ TÀI KHOẢN");
	}

	private String validateInput() {
		if (edtUsername.length() < 6) {
			return "Tài khoản phải có ít nhất 6 kí tự";
		}

		if (edtUsername.length() > 20) {
			return "Tài khoản phải ngắn hơn hoặc bằng 20 ký tự";
		}

		if (edtPassword.length() < 6) {
			return "Mật khẩu phải có ít nhất 6 ký tự";
		}

		if (!edtPassword.getText().toString()
				.equals(edtRePassword.getText().toString())) {
			return "Mật khẩu nhập lại không đúng";
		}

		if (edtCharacter.length() < 3) {
			return "Tên nhân vật phải có ít nhất 3 kí tự";
		}

		if (edtCharacter.length() > 18) {
			return "Tên nhân vật không vượt quá 18 ký tự";
		}

		if (edtPersonalId.length() <= 0) {
			return "Chưa nhập số CMND";
		}

		if (edtPhone.length() <= 0) {
			return "Chưa nhập số điện thoại";
		}

		if (!radGenderMale.isChecked() && !radGenderFemale.isChecked()) {
			return "Chưa chọn giới tính";
		}

		return null;
	}

}
