package com.tv.xeeng.gui.itemdata;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.gamedata.GameData;

public class FacebookWebViewClient extends WebViewClient {

	public final static String LIKE_URL = "m.facebook.com/a/profile.php?fan&id=";
	public final static String UNLIKE_URL = "m.facebook.com/a/profile.php?unfan&id=";
	public String pageUrl = "http://m.facebook.com/xeeng";

	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		// TODO Auto-generated method stub
		super.onFormResubmission(view, dontResend, resend);

		Log.d("FacebookWebViewClient", "onFormResubmission");
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		// TODO Auto-generated method stub
		super.onLoadResource(view, url);

		Log.d("FacebookWebViewClient", "--- onLoadResource ---");
		Log.d("FacebookWebViewClient",
				new StringBuffer("load url: ").append(url).toString());
		if (url.indexOf(LIKE_URL) > -1) {
			Log.d("FacebookWebViewClient", "Like page");
			BusinessRequester.getInstance().getRewardCash(
					Long.toString(GameData.shareData().getMyself().id), 1);
		} else if (url.indexOf(UNLIKE_URL) > -1) {
			Log.d("FacebookWebViewClient", "Unlike page");
		} else {

		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		Log.e("FacebookWebViewClient", "onPageFinished");
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		Log.e("FacebookWebViewClient", "onPageStarted");
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		Log.e("FacebookWebViewClient", "onReceivedError");
	}

	@Override
	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		super.onUnhandledKeyEvent(view, event);
		Log.e("FacebookWebViewClient", "onUnhandledKeyEvent");
	}

	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		Log.e("FacebookWebViewClient", "shouldOverrideKeyEvent");
		return super.shouldOverrideKeyEvent(view, event);

	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.e("FacebookWebViewClient", url);
		return super.shouldOverrideUrlLoading(view, url);

	}
}
