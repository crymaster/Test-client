package com.tv.xeeng.gui;

import android.os.Bundle;
import android.view.View;
import com.tv.xeeng.R;

public class GameSettingsActivity extends UserSettingsActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isVisibleTvVersion = true;
		txtTitle = getString(R.string.settings_uc);

		initLayout();

		tvLogout.setVisibility(View.GONE);
		tvRegister.setVisibility(View.GONE);
		tvChangePassword.setVisibility(View.GONE);
		btnLogout.setVisibility(View.GONE);
		btnRegister.setVisibility(View.GONE);
		btnChangePassword.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		pauseBackgroundMusic();
	}
}
