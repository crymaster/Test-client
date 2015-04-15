package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.R;

public class GiftCodeDialog extends BasicDialog {
	private TextView tvReceiveGoldHelp;

	public GiftCodeDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_giftcode);

		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		edtInput = (EditText) findViewById(R.id.edt_input);
		btnPositive = (Button) findViewById(R.id.btn_positive);
		btnNegative = (Button) findViewById(R.id.btn_negative);
		btnClose = (Button) findViewById(R.id.btn_close);
		tvReceiveGoldHelp = (TextView) findViewById(R.id.tv_receive_gold_help);

		tvReceiveGoldHelp
				.setText(Html
						.fromHtml("<a style='color: #FF9899' href='http://"
								+ getContext().getResources().getString(
										R.string.domain_name)
								+ "/huong-dan/huong-dan-nguoi-choi-moi/cac-cach-de-co-nhieu-gold-khi-tham-gia-xeeng-online/'>Hướng dẫn nhận quà</a>"));
		tvReceiveGoldHelp.setMovementMethod(LinkMovementMethod.getInstance());

		title = "QUÀ TẶNG";
		message = "Nhận Gold hỗ trợ";
		btnPositiveTitle = "Nhận quà";
		btnNegativeTitle = "Nhận gold";
		isEnableInput = true;
		isCancelable = true;

		onClickListenerPositive = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BusinessRequester.getInstance().submitGiftCode(getInputValue());
			}
		};
	}

	@Override
	public void show() {
		super.show();

		btnNegative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

				if (GameData.shareData().getMyself().cash < 4000) {
					BusinessRequester.getInstance().receiveFreeGold();
				} else {
					Toast.makeText(getContext(),
							"Bạn không đủ điều kiện nhận gold miễn phí.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
