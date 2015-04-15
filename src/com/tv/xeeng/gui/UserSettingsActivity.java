package com.tv.xeeng.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.ForgotPasswordDialog;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class UserSettingsActivity extends BaseLayoutXeengActivity {
	TextView tvLogout, tvRegister, tvChangePassword;
	Button btnLogout, btnRegister, btnChangePassword;
	ToggleButton toggleSoundBgMusic, toggleViberate, toggleAutoDeny,
			toggleSoundEffect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isVisibleTvVersion = true;
		txtTitle = getString(R.string.settings_uc);

		initLayout();
	}

	@Override
	protected void needReconnection() {

	}

	private OnClickListener clickHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_logout:
				final BasicDialog dialog = confirm("Bạn có chắc chắn muốn đăng xuất tài khoản?");

				dialog.setPositiveOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						logout();
					}
				});
				break;

			case R.id.btn_register:
				Intent i = new Intent(UserSettingsActivity.this,
						RegisterActivity.class);
				startActivity(i);
				break;

			case R.id.btn_change_password:
				ForgotPasswordDialog changePasswordDialog = new ForgotPasswordDialog(
						UserSettingsActivity.this);
				changePasswordDialog.setTitleText("ĐỔI MẬT KHẨU");
				changePasswordDialog.show();
				break;
			}
		}
	};

	private OnCheckedChangeListener configChangeHandler = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.toggle_sound_bg_music:
				UserPreference.sharePreference().setSoundBgMusicEnable(
						isChecked);
				if (isChecked) {
					playBackgroundMusic();
				} else {
					pauseBackgroundMusic();
				}
				break;
			case R.id.toggle_viberate:
				UserPreference.sharePreference().setVibrationEnable(isChecked);
				break;
			case R.id.toggle_autodeny:
				UserPreference.sharePreference().setAutoDenyInvitation(
						isChecked);
				break;
			case R.id.toggle_sound_effect:
				UserPreference.sharePreference()
						.setSoundEffectEnable(isChecked);
				break;
			default:
				break;
			}
		}
	};

	protected void initLayout() {
		setContentView(R.layout.activity_settings);
		initTopBar();

		tvLogout = (TextView) findViewById(R.id.tv_logout);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvChangePassword = (TextView) findViewById(R.id.tv_change_password);
		btnLogout = (Button) findViewById(R.id.btn_logout);
		btnRegister = (Button) findViewById(R.id.btn_register);
		btnChangePassword = (Button) findViewById(R.id.btn_change_password);
		toggleSoundBgMusic = (ToggleButton) findViewById(R.id.toggle_sound_bg_music);
		toggleSoundEffect = (ToggleButton) findViewById(R.id.toggle_sound_effect);
		toggleViberate = (ToggleButton) findViewById(R.id.toggle_viberate);
		toggleAutoDeny = (ToggleButton) findViewById(R.id.toggle_autodeny);

		btnLogout.setOnClickListener(clickHandler);
		btnRegister.setOnClickListener(clickHandler);
		btnChangePassword.setOnClickListener(clickHandler);

		toggleSoundBgMusic.setChecked(UserPreference.sharePreference()
				.isSoundBgMusicOn());
		toggleSoundEffect.setChecked(UserPreference.sharePreference()
				.isSoundEffectOn());
		toggleAutoDeny.setChecked(UserPreference.sharePreference()
				.isAutoDenyInvitation());
		toggleViberate.setChecked(UserPreference.sharePreference()
				.isVibrationOn());

		toggleSoundBgMusic.setOnCheckedChangeListener(configChangeHandler);
		toggleSoundEffect.setOnCheckedChangeListener(configChangeHandler);
		toggleViberate.setOnCheckedChangeListener(configChangeHandler);
		toggleAutoDeny.setOnCheckedChangeListener(configChangeHandler);

		if (GameData.shareData().isGuest()) {
			tvChangePassword.setVisibility(View.GONE);
			btnChangePassword.setVisibility(View.GONE);

			tvRegister.setVisibility(View.VISIBLE);
			btnRegister.setVisibility(View.VISIBLE);
		} else {
			tvChangePassword.setVisibility(View.GONE);
			btnChangePassword.setVisibility(View.GONE);

			tvRegister.setVisibility(View.GONE);
			btnRegister.setVisibility(View.GONE);
		}

	}
}
