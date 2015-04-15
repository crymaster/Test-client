package com.tv.xeeng.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.R;

public class SplashScreenActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 2000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					}
					;
					startActivity(new Intent(SplashScreenActivity.this,
							LoginActivity.class));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					finish();
				}
			}
		};

		logoTimer.start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("SplashScreenActivity", "Installation ID: " + CommonUtils.getDeviceUniqueId());
	}
}