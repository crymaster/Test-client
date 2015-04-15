package com.tv.xeeng.gui.fragments;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tv.xeeng.R;
import com.tv.xeeng.gamedata.GameData;

public class ChatDialogFragment extends DialogFragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final int UPDATE_INTERVAL = 5000; // 5s
	public static final String SHOUT_BOX_HOST = "http://123.30.179.204:8580/ShoutBox";

	private static final String SALT = "abc123";

	private static final String KEY_USER = "user";
	private static final String KEY_CONTENT = "content";

	private boolean textViewShouldScrollBottom = true;

	// ===========================================================
	// Fields
	// ===========================================================
	private RadioGroup layoutChatRooms;
	private TextView content;
	private EditText chatMessage;

	private ImageButton btnEmoticon;
	private ImageButton btnClose;
	private ImageButton btnSend;

	private Handler handler;
	private Runnable runnable;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static ChatDialogFragment newInstance() {
		ChatDialogFragment fragment = new ChatDialogFragment();

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_chat, null);

		layoutChatRooms = (RadioGroup) view
				.findViewById(R.id.layout_chat_rooms);
		layoutChatRooms
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.btn_chat_world:

							break;
						case R.id.btn_chat_current_room:
							Toast.makeText(getActivity(),
									"Tính năng đang phát triển",
									Toast.LENGTH_SHORT).show();
							group.check(R.id.btn_chat_world);
							break;
						default:
							break;
						}
					}
				});
		content = (TextView) view.findViewById(R.id.tv_event_description);

		chatMessage = (EditText) view.findViewById(R.id.edt_chat_message);
		chatMessage.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (validateInputs()) {
					sendMessage(v.getText().toString());
					v.setText("");
				}
				return true;
			}
		});

		btnEmoticon = (ImageButton) view.findViewById(R.id.btn_emoticon);
		btnClose = (ImageButton) view.findViewById(R.id.btn_close);
		btnSend = (ImageButton) view.findViewById(R.id.btn_send);

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (validateInputs()) {
					sendMessage(chatMessage.getText().toString());
					chatMessage.setText("");
				}
			}
		});

		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				new PullContentTask(content, textViewShouldScrollBottom)
						.execute(SHOUT_BOX_HOST);
				textViewShouldScrollBottom = false;
				handler.postDelayed(this, UPDATE_INTERVAL);
			}
		};

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);
	}

	@Override
	public void onResume() {
		super.onResume();
		runnable.run();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void sendMessage(String content) {
		if (!TextUtils.isEmpty(content)) {
			Log.d("Chat content", content);
			new PushContentTask().execute(SHOUT_BOX_HOST, content);
		}
	}

	private boolean validateInputs() {
		String chatMes = chatMessage.getText().toString().trim();
		if (TextUtils.isEmpty(chatMes)) {
			return false;
		}
		if (chatMes.length() > 120) {
			Toast.makeText(getActivity(),
					"Độ dài tin nhắn không được vượt quá 120 kí tự",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class PushContentTask extends AsyncTask<String, Void, HttpResponse> {

		@Override
		protected HttpResponse doInBackground(String... params) {
			try {
				String url = params[0] + "/data?a=commit";
				String content = params[1];

				// Authentication. Temporary disabled
				// String user = "nguyen";
				// String expiration = (System.currentTimeMillis() / 1000L + 60)
				// + "";
				// String challenge = "";
				//
				// challenge = ShoutBox.shoutbox_gen_hash(SALT, user, content);
				//
				// url += "&u=" + user + "&e=" + expiration + "&st=" +
				// challenge;

				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				request.setHeader("Content-type",
						"application/json; charset=UTF-8");

				JSONObject json = new JSONObject();
				json.put(KEY_USER, GameData.shareData().getMyself().character);
				json.put(KEY_CONTENT, content);

				StringEntity se = new StringEntity(json.toString(), "UTF-8");
				se.setContentType("application/json; charset=UTF-8");

				request.setEntity(se);
				HttpResponse response = client.execute(request);

				return response;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			super.onPostExecute(result);
			if (result != null) {
				textViewShouldScrollBottom = true;
				handler.removeCallbacks(runnable);
				runnable.run();
			}
		}

	}

	public static class PullContentTask extends
			AsyncTask<String, Void, SpannableStringBuilder> {
		private TextView content;
		private boolean shouldScrollToBottom;

		public PullContentTask(TextView content, boolean scroll) {
			this.content = content;
			shouldScrollToBottom = scroll;
		}

		@Override
		protected SpannableStringBuilder doInBackground(String... params) {
			try {
				String url = params[0] + "/data?a=getcomment";

				// Authentication. Temporary disabled
				// String user = "nguyen";
				// String expiration = (System.currentTimeMillis() / 1000L + 60)
				// + "";
				// String challenge = ShoutBox.shoutbox_gen_hash(SALT, user,
				// expiration + "000");
				//
				// Log.d(getClass().getName(), challenge);

				// url += "&u=" + user + "&e=" + expiration + "&st=" +
				// challenge;

				DocumentBuilder docBuilder = DocumentBuilderFactory
						.newInstance().newDocumentBuilder();
				Document doc = docBuilder.parse(new InputSource(new URL(url)
						.openStream()));
				doc.getDocumentElement().normalize();

				NodeList nodeList = doc.getElementsByTagName("row");
				SpannableStringBuilder comments = new SpannableStringBuilder("");

				for (int i = nodeList.getLength() - 1; i >= 0; i--) {
					int start = comments.length();
					int end = start
							+ nodeList.item(i).getTextContent().indexOf(":")
							+ 4;

					comments.append("\n" + nodeList.item(i).getTextContent());
					comments.setSpan(new ForegroundColorSpan(Color.YELLOW),
							start, end + 1,
							SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
				}

				return comments;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new SpannableStringBuilder("");
		}

		@Override
		protected void onPostExecute(SpannableStringBuilder result) {
			super.onPostExecute(result);
			if (content != null) {
				content.setText(result);
				if (shouldScrollToBottom) {
					if (content.getParent() instanceof ScrollView) {
						((ScrollView) content.getParent()).postDelayed(
								new Runnable() {

									@Override
									public void run() {
										((ScrollView) content.getParent())
												.fullScroll(ScrollView.FOCUS_DOWN);
									}
								}, 200);
					}
				}
			}
		}
	}
}
