package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.gui.BaseGeneralXeengActivity;
import com.tv.xeeng.gui.BaseXeengActivity;
import com.tv.xeeng.R;

public class ForgotPasswordDialog extends BasicDialog {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private EditText edtUsername;
	private EditText edtPersonalId;
	private EditText edtPhone;
	private EditText edtPassword;
	private EditText edtRepassword;

	// ===========================================================
	// Constructors
	// ===========================================================
	public ForgotPasswordDialog(Context context) {
		super(context, "LẤY LẠI MẬT KHẨU", "", context
				.getString(R.string.cancel), context
				.getString(R.string.confirm));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		setContentView(R.layout.dialog_forgot_password);

		txtTitle = (TextView) findViewById(R.id.txt_title);

		edtUsername = (EditText) findViewById(R.id.edt_username);
		edtPersonalId = (EditText) findViewById(R.id.edt_personal_id);
		edtPhone = (EditText) findViewById(R.id.edt_phone);
		edtPassword = (EditText) findViewById(R.id.edt_password);
		edtRepassword = (EditText) findViewById(R.id.edt_repassword);

		btnPositive = (Button) findViewById(R.id.btn_positive);
		btnNegative = (Button) findViewById(R.id.btn_negative);
		btnClose = (Button) findViewById(R.id.btn_close);

		onClickListenerPositive = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (TextUtils.isEmpty(getUsername())
						|| TextUtils.isEmpty(getPersonalId())
						|| TextUtils.isEmpty(getPhone())
						|| TextUtils.isEmpty(getPassword())) {
					Toast.makeText(context, "Hãy nhập đầy đủ thông tin",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (getUsername().length() < 6) {
					Toast.makeText(context,
							"Tên tài khoản phải có tối thiểu 6 kí tự",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (!getPassword().equals(getRepassword())) {
					Toast.makeText(context, "Mật khẩu nhập lại không đúng",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (getPassword().length() < 6) {
					Toast.makeText(context,
							"Mật khẩu phải có tối thiểu 6 kí tự",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (!(context instanceof BaseGeneralXeengActivity)) {
					GameSocket.shareSocket().open();
				}

				if (context instanceof BaseXeengActivity) {
					((BaseXeengActivity) context).showLoading();
				}

				BusinessRequester.getInstance().resetPassword(getUsername(),
						getPersonalId(), getPhone(), getPassword());
				dismiss();
			}
		};
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public String getUsername() {
		return edtUsername.getText().toString();
	}

	public String getPersonalId() {
		return edtPersonalId.getText().toString();
	}

	public String getPhone() {
		return edtPhone.getText().toString();
	}

	public String getPassword() {
		return edtPassword.getText().toString();
	}

	public String getRepassword() {
		return edtRepassword.getText().toString();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
