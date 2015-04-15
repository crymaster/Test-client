package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.tv.xeeng.R;

public class EventRewardDialog extends BasicDialog {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private long gold;
	private String message;

	private TextView tvGoldReward;

	// ===========================================================
	// Constructors
	// ===========================================================
	public EventRewardDialog(Context context, long gold, String message) {
		super(context, "Thông báo", "Chúc mừng Baybaythe.\n Bạn đã nhận được",
				"Trở lại", null);
		this.gold = gold;
		this.message = message;
	}

	public EventRewardDialog(Context context, String message) {
		super(context, "Thông báo", "Chúc mừng Baybaythe.\n Bạn đã nhận được",
				"Trở lại", null);
		this.message = message;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_event_reward);

		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		edtInput = (EditText) findViewById(R.id.edt_input);
		btnPositive = (Button) findViewById(R.id.btn_positive);
		btnNegative = (Button) findViewById(R.id.btn_negative);
		btnClose = (Button) findViewById(R.id.btn_close);

		tvGoldReward = (TextView) findViewById(R.id.tv_gold_reward);
		// tvGoldReward.setText(CommonUtils.formatCash(gold));
		tvGoldReward.setText(message);
	}

	@Override
	public void show() {
		super.show();

		// SpannableString ss = new SpannableString("Chúc mừng "
		// + GameData.shareData().getMyself().character
		// + "\nBạn đã nhận được");
		//
		// int start = 0;
		// int end = 9;
		// ss.setSpan(new ForegroundColorSpan(Color.rgb(255, 255, 255)), start,
		// end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		//
		// start = 10;
		// end = start + GameData.shareData().getMyself().character.length();
		// ss.setSpan(new ForegroundColorSpan(Color.rgb(255, 226, 96)), start,
		// end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		//
		// start = end + 1;
		// end = ss.length() - 1;
		// ss.setSpan(new ForegroundColorSpan(Color.rgb(213, 211, 212)), start,
		// end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		txtMessage.setText(message);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
