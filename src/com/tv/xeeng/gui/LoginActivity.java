package com.tv.xeeng.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
//import com.facebook.android.AsyncFacebookRunner;
//import com.facebook.android.AsyncFacebookRunner.RequestListener;
//import com.facebook.android.DialogError;
//import com.facebook.android.Facebook;
//import com.facebook.android.Facebook.DialogListener;
//import com.facebook.android.FacebookError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.R;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.CustomProgressDialog;
import com.tv.xeeng.gui.customview.ForgotPasswordDialog;
import com.tv.xeeng.manager.Logger;
import com.tv.xeeng.manager.UserPreference;

public class LoginActivity extends BaseXeengActivity {
	private static final String PATH = Environment.getExternalStorageDirectory() + "/download/xeeng/";
	private static final String FILENAME = "Xeeng.apk";
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	// GOOGLE API
	String SENDER_ID = "905263882679";
	GoogleCloudMessaging gcm;
	String regId;// 0.9.3

	// FACEBOOK API
	private static String APP_ID = "396780797115228";
//	private Facebook facebook = new Facebook(APP_ID);
	String user_ID;
	String profileName;
	String mLink;
//	private AsyncFacebookRunner mAsyncRunner;

	// Widgets
	private EditText edtUsername, edtPassword;
	private Button btnLogin, btnRegister, btnForgot, btnPlayNow;

	private boolean isSendingRequest = false;

	// private static final String GA_PROPERTY_ID = "UA-43963796-1";
	private static final String SCREEN_LABLE = "Login screen";

	private static final String CAMPAIGN_SOURCE_PARAM = "utm_source";
	Tracker mTracker;

	private TextView appVersion;
	private Handler handler;
	private Runnable runnableAnim;

	private View imvGlow2;

	private String rejoinMsg;
	private long rejoinMatchId;
	private int rejoinGameId;
	private boolean isRejoining;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppsFlyerLib.setAppsFlyerKey(getString(R.string.appsflyer_dev_id));
		AppsFlyerLib.sendTracking(getApplicationContext());
		
		mTracker = ((CustomApplication) getApplication()).getTracker();

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {

			Logger.getInstance().warn(this, "Google Play Services APK found.");
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = UserPreference.sharePreference().getRegistrationId(this);

			if (regId.length() == 0) {
				registerInBackground();
			} else {
				Logger.getInstance().info(this, "REG_ID: " + regId);
			}

		} else {

			Logger.getInstance().error(this,
					"No valid Google Play Services APK found.");
			if (!UserPreference.sharePreference().getBackendStatus()) {

				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						sendRegistrationIdToBackend();
						return null;
					}

				}.execute(null, null, null);
			}
		}

		mTracker.send(new HitBuilders.AppViewBuilder().build());

		// set content view & update reference
		initLayout();

		imvGlow2 = findViewById(R.id.imv_glow2);

		final TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT,
				2, Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 0);
		anim.setDuration(5000);

		// handler = new Handler();
		// runnableAnim = new Runnable() {
		//
		// @Override
		// public void run() {
		// imvGlow2.startAnimation(anim);
		//
		// handler.postDelayed(this, new Random().nextLong() % 3000 + 4000);
		// }
		// };
	}

	@Override
	protected void onResume() {
		super.onResume();

		// remember username and password
		String[] userAcc = UserPreference.sharePreference().getUser();
		if (userAcc != null) {
			edtUsername.setText(userAcc[0]);
			edtPassword.setText(userAcc[1]);
		}

		// runnableAnim.run();

		CommonUtils.clearCache();

		// TODO: Should we also close GameSocket as well???
		GameSocket.shareSocket().close();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// handler.removeCallbacks(runnableAnim);
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	@Override
	protected void needReconnection() {
		if (isSendingRequest) {
			WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = manager.getConnectionInfo();
			String macAddress = wifiInfo.getMacAddress();

			BusinessRequester.getInstance().login(
					edtUsername.getText().toString(),
					edtPassword.getText().toString(), macAddress);
		}
	}

	;

	@Override
	protected void registerReceiveNotification() {
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_FAST_LOGIN));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_LOGIN));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_USER_INFO));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_RESET_PASSWORD));
	}

	@Override
	protected void unregisterReceiveNotification() {
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	private OnClickListener clickHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (btnLogin.getId() == v.getId()) {

				showLoading("Đang đăng nhập...");
				isSendingRequest = true;

				// check if user want remember account
				UserPreference.sharePreference().rememberUser(
						edtUsername.getText().toString(),
						edtPassword.getText().toString());

				GameData.initialize();
				GameSocket.shareSocket().open();

				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = manager.getConnectionInfo();
				String macAddress = wifiInfo.getMacAddress();

				BusinessRequester.getInstance().login(
						edtUsername.getText().toString(),
						edtPassword.getText().toString(), macAddress);

			} else if (btnRegister.getId() == v.getId()) {

				Intent i = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(i);

			} else if (btnForgot.getId() == v.getId()) {

				final ForgotPasswordDialog dialog = new ForgotPasswordDialog(
						LoginActivity.this);
				dialog.show();
			} else if (v == btnPlayNow) {

				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = manager.getConnectionInfo();
				String MACAddress = wifiInfo.getMacAddress();
				if (MACAddress != null) {

					showLoading("Đang đăng nhập...");
					GameData.initialize();
					GameSocket.shareSocket().open();

					try {
						BusinessRequester.getInstance().sendFastLogin(
								MACAddress);
					} catch (Exception e) {
						hideLoading();
						alert(getString(R.string.has_errors));
					}
				} else {

					Toast.makeText(LoginActivity.this,
							"Thiết bị này không hỗ trợ", Toast.LENGTH_SHORT)
							.show();
				}

			}
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			isSendingRequest = false;

			if (MessageService.INTENT_LOGIN
					.equalsIgnoreCase(intent.getAction())) {

				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, true);
				if (status) {

					if (GameData.shareData().isNeedUpdate) {
						BusinessRequester.getInstance().logout();
						hideLoading();
						showConfirmUpdateDialog();
					} else {
						long userId = GameData.shareData().getMyself().id;
						UserPreference.sharePreference().storeNewbieStatus();
						BusinessRequester.getInstance().getUserInfo(userId);

						isRejoining = false;
						String rejoinInfo = intent
								.getStringExtra(NetworkUtils.MESSAGE_INFO_2);
						parseRejoinInfo(rejoinInfo);
					}

				} else {

					if (intent.getBooleanExtra("NeedActive", false)) {
						// TODO: ???
						// activeAccountIfNeed(this.getClass());
					} else {
						hideLoading();
						String msg = intent
								.getStringExtra(NetworkUtils.MESSAGE_INFO);
						if (msg != null) {
							alert(msg);
						} else {
							alert(getString(R.string.has_errors));
						}
					}
				}

			} else if (MessageService.INTENT_FAST_LOGIN.equalsIgnoreCase(intent
					.getAction())) {

				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, true);
				if (status) {

					if (GameData.shareData().isNeedUpdate) {
						BusinessRequester.getInstance().logout();
						hideLoading();
						showConfirmUpdateDialog();
					} else {
						isRejoining = false;
						UserPreference.sharePreference().storeNewbieStatus();
						String rejoinInfo = intent
								.getStringExtra(NetworkUtils.MESSAGE_INFO_2);
						parseRejoinInfo(rejoinInfo);

						int regCount = CommonUtils.getRegisterCount();
						UserPreference.sharePreference().rememberRegCount(
								regCount + 1);

						startUserHomeActivity();
					}
				} else {

					if (intent.getBooleanExtra("NeedActive", false)) {
						// TODO: ???
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

			} else if (MessageService.INTENT_GET_USER_INFO
					.equalsIgnoreCase(intent.getAction())) {

				hideLoading();
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, true);
				if (status) {

					Player player = intent
							.getParcelableExtra(NetworkUtils.MESSAGE_INFO);
					GameData.shareData().setMyself(player);

					String username = edtUsername.getText().toString();
					String password = edtPassword.getText().toString();

					// remember user account
					UserPreference.sharePreference().rememberUser(username,
							password);

					startUserHomeActivity();
					finish();
				} else {

					String msg = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						alert(msg);
					} else {
						alert(getString(R.string.has_errors));
					}
				}

			} else if (MessageService.INTENT_RESET_PASSWORD
					.equalsIgnoreCase(intent.getAction())) {
				Toast.makeText(LoginActivity.this,
						intent.getStringExtra(NetworkUtils.MESSAGE_INFO),
						Toast.LENGTH_SHORT).show();
				hideLoading();
			}

		}
	};

	private void showConfirmUpdateDialog() {
		final BasicDialog dialog = confirm(GameData.shareData().updateDescription);
		dialog.setPositiveText("Nâng cấp");
		dialog.setPositiveOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.GOOGLE_PLAY_VER) {
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("market://details?id="
										+ getPackageName())));
					} catch (ActivityNotFoundException ex) {
						startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://play.google.com/store/apps/details?id="
										+ getPackageName())));
						ex.printStackTrace();
					}
				} else {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							new AutoUpdateAsynctask().execute();
						}
					});
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri
//							.parse(GameData.shareData().updateLink)));
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void parseRejoinInfo(String rejoinInfo) {
		if (!TextUtils.isEmpty(rejoinInfo)) {
			isRejoining = true;
			String[] infos = NetworkUtils.stringSplit(rejoinInfo,
					NetworkUtils.ELEMENT_SEPERATOR);

			rejoinMsg = infos[0];
			rejoinGameId = Integer.parseInt(infos[1]);
			rejoinMatchId = Long.parseLong(infos[2]);
		}
	}

	private void initLayout() {
		setContentView(R.layout.activity_login);

		edtUsername = (EditText) findViewById(R.id.edt_username);
		edtPassword = (EditText) findViewById(R.id.edt_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnRegister = (Button) findViewById(R.id.btn_register);
		btnForgot = (Button) findViewById(R.id.btn_forgot_password);
		btnPlayNow = (Button) findViewById(R.id.btn_play_now);

		// set event to button
		btnLogin.setOnClickListener(clickHandler);
		btnRegister.setOnClickListener(clickHandler);
		btnForgot.setOnClickListener(clickHandler);
		btnPlayNow.setOnClickListener(clickHandler);

//		appVersion = (TextView) findViewById(R.id.tv_version);

//		appVersion.setText("v" + CommonUtils.getAppVersion());
	}

	private void startUserHomeActivity() {
		Intent i = new Intent(getBaseContext(), UserHomeActivity.class);
		i.putExtra(UserHomeActivity.REJOINING, isRejoining);
		i.putExtra(UserHomeActivity.REJOIN_MSG, rejoinMsg);
		i.putExtra(UserHomeActivity.REJOIN_GAME_ID, rejoinGameId);
		i.putExtra(UserHomeActivity.REJOIN_MATCH_ID, rejoinMatchId);
		startActivity(i);
	}

	@SuppressWarnings("deprecation")
	protected void loginFaceBook() {
		// new Request(
		// facebook.getSession(),
		// "/me",
		// null,
		// HttpMethod.GET,
		// new Request.Callback() {
		// public void onCompleted(Response response) {
		//
		// }
		// }
		// ).executeAsync();
		String access_token = UserPreference.sharePreference()
				.getFbAccessToken(this);
		long expires = UserPreference.sharePreference().getFbExpires(this);
		/*Log.e("old Access_token" + access_token,
				"checking new:" + facebook.getAccessToken());
		if (access_token != null) {
			facebook.setAccessToken(access_token);
			mAsyncRunner.request("me", new RequestListener() {

				@Override
				public void onMalformedURLException(MalformedURLException e,
						Object state) {

				}

				@Override
				public void onIOException(IOException e, Object state) {

				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e,
						Object state) {

				}

				@Override
				public void onFacebookError(FacebookError e, Object state) {

				}

				@Override
				public void onComplete(String response, Object state) {
					try {
						JSONObject profile = new JSONObject(response);
						String userName;
						userName = profile.getString("username");
						GameData.initialize();
						GameSocket.shareSocket().open();
						BusinessRequester.getInstance().sendFaceBookLogin(
								userName);
					} catch (JSONException e) {
						showFaceBookLoginDialog();
						e.printStackTrace();

					}
				}
			});
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
			// logoutFromFacebook();
		}

		if (!facebook.isSessionValid()) {
			showFaceBookLoginDialog();
		}*/

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p/>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {

				String msg = "";
				try {

					if (gcm == null) {

						gcm = GoogleCloudMessaging
								.getInstance(LoginActivity.this);
					}
					regId = gcm.register(SENDER_ID);

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					UserPreference.sharePreference().storeRegistrationId(
							LoginActivity.this, regId);
				} catch (IOException ex) {

					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}

	private void sendRegistrationIdToBackend() {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://123.30.187.51:8080/gcm/register");

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("regId", regId));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {

				Logger.getInstance().info(this, "store backend status to true");
				UserPreference.sharePreference().storeBackendStatus(true);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {

			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public void getFaceBookUserName() {
		/*mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {

			}

			@Override
			public void onIOException(IOException e, Object state) {

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {

			}

			@Override
			public void onComplete(String response, Object state) {
				String json = response;
				try {
					JSONObject profile = new JSONObject(json);

					final String username = profile.getString("username");

					final String email = profile.getString("email");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Name: " + username + "\nEmail: " + email,
									Toast.LENGTH_LONG).show();
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@SuppressWarnings("deprecation")
	public void logoutFromFacebook() {
		mAsyncRunner.logout(this, new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				Log.d("Logout from Facebook", response);
				if (Boolean.parseBoolean(response) == true) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
						}

					});

				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void showFaceBookLoginDialog() {
		facebook.authorize(this, new String[] { "email", "publish_stream" },
				new DialogListener() {

					@Override
					public void onCancel() {

					}

					@Override
					public void onComplete(Bundle values) {
						UserPreference.sharePreference().storeFbAccessToken(
								facebook.getAccessToken());
						UserPreference.sharePreference().storeFbExpires(
								facebook.getAccessExpires());
						loginFaceBook();
					}

					@Override
					public void onError(DialogError error) {

					}

					@Override
					public void onFacebookError(FacebookError fberror) {

					}

				});*/

	}
	
	private class AutoUpdateAsynctask extends AsyncTask<Void, Integer, Boolean> {
		private CustomProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new CustomProgressDialog(LoginActivity.this);
			progressDialog.setTitle("Cập nhật");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setProgressNumberFormat("%2d / %2d KB");
			progressDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					AutoUpdateAsynctask.this.cancel(true);
				}
			});
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
//				GameData.shareData().updateLink = "http://dl.xeengvn.com/Xeeng.apk";
				File parentDir = new File(PATH);
				parentDir.mkdirs();
				File file = new File(PATH + FILENAME);
				URL url = new URL(GameData.shareData().updateLink);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStream is = conn.getInputStream();
				OutputStream os = new FileOutputStream(file);

				int length = conn.getContentLength();
				int downloaded = 0, len = 0;
				byte[] buffer = new byte[1024];
				
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
					downloaded += len;
					publishProgress(downloaded, length);
				}
				
				is.close();
				
				os.flush();
				os.close();
				
				return true;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			progressDialog.setMax(values[1] / 1024);
			progressDialog.setProgress(values[0] / 1024);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			progressDialog.dismiss();
			
			if (result) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.setDataAndType(Uri.fromFile(new File(PATH + FILENAME)), "application/vnd.android.package-archive");
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			} else {
				alert("Có lỗi xảy ra!");
			}
		}
	}
}
