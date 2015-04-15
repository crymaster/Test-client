package com.tv.xeeng.gui;

import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html.ImageGetter;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.ProgressHUD;
import com.tv.xeeng.R;
import org.json.JSONException;

public abstract class BaseXeengActivity extends FragmentActivity implements
		ImageGetter {

	protected LocalBroadcastManager mLocalBroadcastManager;
	protected Tracker tracker;

	private ProgressHUD mProgressHUD;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().getAttributes().format = PixelFormat.RGBA_8888;
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

		mProgressHUD = new ProgressHUD(this, R.style.ProgressHUD);
		mProgressHUD.setTitle("");
		mProgressHUD.setContentView(R.layout.hud_progress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_NETWORK_DEVICE_PROBLEM));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_NEED_RECONNECTION));
		registerReceiveNotification();
	}

	;

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiveNotification();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
		hideLoading();
	}

	;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MessageService.INTENT_NETWORK_DEVICE_PROBLEM
					.equalsIgnoreCase(intent.getAction())) {

				// Logger.getInstance().error(BaseXeengActivity.this,
				// "NETWORK OF DEVICE has a PROBLEM");
				hideLoading();
				showNetworkError();
			} else if (MessageService.INTENT_NEED_RECONNECTION
					.equalsIgnoreCase(intent.getAction())) {

				needReconnection();
			}
		}
	};

	protected abstract void registerReceiveNotification();

	protected abstract void unregisterReceiveNotification();

	protected abstract void needReconnection();

	protected BasicDialog networkDialog;

	protected void showNetworkError() {
		if (networkDialog != null && networkDialog.isShowing()) {
			networkDialog.dismiss();
		}

		networkDialog = confirm("Kết nối bị lỗi. Vui lòng kiểm tra lại kết nối trên thiết bị và thử lại sau");
		networkDialog.setPositiveOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLoading();
				networkDialog.dismiss();
				GameSocket.shareSocket().reconnect();
				try {
					BusinessRequester.getInstance().reconnect(1, false);
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		networkDialog.setNegativeOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				networkDialog.dismiss();
				if (BaseXeengActivity.this instanceof BaseGeneralXeengActivity) {
					BaseGeneralXeengActivity activity = (BaseGeneralXeengActivity) BaseXeengActivity.this;
					activity.logout();
				}
			}
		});
	}

	protected void showFatalError() {
		if (networkDialog != null && networkDialog.isShowing()) {
			networkDialog.dismiss();
		}

		networkDialog = alert("Có lỗi trong quá trình xử lí. Vui lòng thử lại sau");
		networkDialog.setPositiveText("Logout");
		networkDialog.setPositiveOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				networkDialog.dismiss();
				if (BaseXeengActivity.this instanceof BaseGeneralXeengActivity) {
					BaseGeneralXeengActivity activity = (BaseGeneralXeengActivity) BaseXeengActivity.this;
					activity.logout();
				}
			}
		});
	}

	public void showLoading() {
		showLoading(getString(R.string.loading), true);
	}

	public void showLoading(String message) {
		showLoading(message, true);
	}

	public void showLoading(boolean isCancelable) {
		showLoading(getString(R.string.loading), isCancelable);
	}

	/**
	 * Show loading for a specified amount of time
	 * 
	 * @param timeOut
	 */
	public void showLoading(String message, final long timeOut) {
		showLoading(message, true);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						hideLoading();
					}
				}, timeOut);
			}
		});
	}

	public void showLoading(long timeOut) {
		showLoading(getString(R.string.loading), timeOut);
	}

	public void showLoading(String message, boolean isCancelable) {
		showLoading(message, new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// Do nothing
			}
		}, isCancelable);
	}

	public void showLoading(String message, OnCancelListener onCancelListener, boolean isCancelable) {
		if (!isFinishing()) {
			mProgressHUD.setCancelable(isCancelable);
			mProgressHUD.setOnCancelListener(onCancelListener);
			mProgressHUD.setMessage(message);
			mProgressHUD.show();
		}
	}

	public void hideLoading() {
		mProgressHUD.dismiss();
	}

	public BasicDialog alert(CharSequence message) {
		BasicDialog dialog = new BasicDialog(this)
				.setTitleText(getString(R.string.default_dialog_title_uc))
				.setMessageText(message)
				.setPositiveText(getString(R.string.close))
				.setNegativeText(null);
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}

	public BasicDialog confirm(CharSequence message) {
		return confirm(message, getString(R.string.ok));
	}

	public BasicDialog confirm(CharSequence message, CharSequence positiveTitle) {
		BasicDialog dialog = new BasicDialog(this)
				.setTitleText(getString(R.string.default_dialog_title_uc))
				.setMessageText(message).setPositiveText(positiveTitle)
				.setNegativeText(getString(R.string.cancel));
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}

	public BasicDialog promt(CharSequence message) {
		BasicDialog dialog = new BasicDialog(this)
				.setTitleText(getString(R.string.default_dialog_title_uc))
				.setMessageText(message)
				.setPositiveText(getString(R.string.ok)).setEnableInput(true);
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}

	@Override
	public Drawable getDrawable(String source) {
		int id = 0;

		if (source.equals("xeeng")) {
			id = R.drawable.icon_coin_xeeng_padding;
		} else if (source.equals("gold")) {
			id = R.drawable.icon_coin_gold_padding;
		} else {
			return null;
		}

		LevelListDrawable d = new LevelListDrawable();
		Drawable empty = getResources().getDrawable(id);
		d.addLevel(0, 0, empty);
		d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

		return d;
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
}
