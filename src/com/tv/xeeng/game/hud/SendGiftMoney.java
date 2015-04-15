package com.tv.xeeng.game.hud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.R;

public class SendGiftMoney extends BasicDialog {
	public long mCash;
	public long cashTransfer = 0;
	public String mDesName;
	protected LocalBroadcastManager mLocalBroadcastManager;

	public SendGiftMoney(Context context, long cash, String desName) {
		super(context);
		mDesName = desName;
		mCash = cash;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(getContext());
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_TRANSFER_CASH_MESSAGE));
		super.onCreate(savedInstanceState);
		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);
		setContentView(R.layout.dialog_send_gift_money);
		SeekBar seek = (SeekBar) findViewById(R.id.seekbar);
		seek.setMax((int) mCash);
		Button btnAgree = (Button) findViewById(R.id.agree);
		btnAgree.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				BusinessRequester.getInstance().transferCash(cashTransfer,
						mDesName);
				dismiss();
			}
		});

		final TextView txtInfo = (TextView) findViewById(R.id.txtInfo);
		txtInfo.setText("Gửi Tặng xu cho bạn " + mDesName);
		final TextView cashValue = (TextView) findViewById(R.id.cashValue);
		seek.setBackgroundResource(R.drawable.bg_popup_bottom);
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				cashValue.setText("" + progress + " Xu");
				cashTransfer = (long) progress;
			}
		});
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("checking action receiver", "value: " + intent.getAction());
			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_TRANSFER_CASH_MESSAGE)) {
				String txtMessage = intent
						.getStringExtra(NetworkUtils.MESSAGE_INFO_2);
				showDialogMessage(txtMessage);
				Log.e("show dialogbox", "true");
			}
		}
	};
	BasicDialog dialog;

	protected void showDialogMessage(String txtMessage) {
		if (dialog != null) {

		} else {
			dialog = new BasicDialog(this.getContext(), "Thông Báo",
					txtMessage, "Đóng", null);
			dialog.show();

			Button btnNegative = (Button) dialog
					.findViewById(R.id.btn_negative);
			btnNegative.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mLocalBroadcastManager.unregisterReceiver(mReceiver);
					dialog.dismiss();
					dialog = null;
					mLocalBroadcastManager.unregisterReceiver(mReceiver);
				}
			});
		}
	}
}
