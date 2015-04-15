package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.tv.xeeng.R;

public class SendMessageDialog extends BasicDialog {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public SendMessageDialog(Context context) {
		super(context);
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

		setContentView(R.layout.dialog_send_message);

		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		edtInput = (EditText) findViewById(R.id.edt_input);
		btnPositive = (Button) findViewById(R.id.btn_positive);
		btnNegative = (Button) findViewById(R.id.btn_negative);
		btnClose = (Button) findViewById(R.id.btn_close);

		setEnableInput(true);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void show() {
		super.show();
		edtInput.setText("");
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
