package com.tv.xeeng.gui.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.tv.xeeng.R;

public class BasicDialog extends Dialog {

	protected Context context;

	protected CharSequence title;
	protected CharSequence message;
	protected CharSequence btnPositiveTitle;
	protected CharSequence btnNegativeTitle;
	protected boolean isEnableInput;
	protected boolean isCancelable;

	protected TextView txtTitle;
	protected TextView txtMessage;
	protected EditText edtInput;
	protected Button btnPositive;
	protected Button btnNegative;
	protected Button btnClose;

	protected View.OnClickListener onClickListenerPositive = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	protected View.OnClickListener onClickListenerNegative = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	protected View.OnClickListener onClickListenerClose = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	public BasicDialog(Context context) {
		this(context, null, null, null, null);
	}

	public BasicDialog(Context context, String title, String message,
			String btnCancel, String btnOk) {
		super(context);
		this.context = context;
		this.title = title;
		this.message = message;
		this.btnPositiveTitle = btnOk;
		this.btnNegativeTitle = btnCancel;
		this.isCancelable = true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		setContentView(R.layout.dialog_basic);

		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		edtInput = (EditText) findViewById(R.id.edt_input);
		btnPositive = (Button) findViewById(R.id.btn_positive);
		btnNegative = (Button) findViewById(R.id.btn_negative);
		btnClose = (Button) findViewById(R.id.btn_close);
	}

	@Override
	public void show() {
		// while (isShowing()) {
		// dismiss();
		//
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		super.show();

		setCancelable(isCancelable);

		if (isCancelable) {
			btnClose.setVisibility(View.VISIBLE);
		} else {
			btnClose.setVisibility(View.INVISIBLE);
		}

		if (txtTitle != null) {
			txtTitle.setText(title, BufferType.SPANNABLE);
		}
		if (txtMessage != null) {
			txtMessage.setText(message);
		}

		if (edtInput != null) {
			if (isEnableInput) {
				edtInput.setVisibility(View.VISIBLE);
			} else {
				edtInput.setVisibility(View.GONE);
			}
		}

		if (onClickListenerClose == null) {
			btnClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}

		if (onClickListenerPositive == null) {
			onClickListenerPositive = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			};
		}

		if (onClickListenerNegative == null) {
			onClickListenerNegative = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			};
		}

		btnPositive.setVisibility(View.VISIBLE);
		btnPositive.setText(btnPositiveTitle);
		btnPositive.setOnClickListener(onClickListenerPositive);

		btnNegative.setVisibility(View.VISIBLE);
		btnNegative.setText(btnNegativeTitle);
		btnNegative.setOnClickListener(onClickListenerNegative);

		btnClose.setOnClickListener(onClickListenerClose);

		if (TextUtils.isEmpty(btnPositiveTitle)) {
			btnPositive.setVisibility(View.GONE);

			btnNegative.setText(btnNegativeTitle);
			btnNegative.setOnClickListener(onClickListenerNegative);
		}
		if (TextUtils.isEmpty(btnNegativeTitle)) {
			btnPositive.setText(btnPositiveTitle);
			btnPositive.setOnClickListener(onClickListenerPositive);

			btnNegative.setVisibility(View.GONE);
		}

		if (TextUtils.isEmpty(btnNegativeTitle)
				&& onClickListenerPositive == null) {
			btnPositive
					.setOnClickListener(new android.view.View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dismiss();
						}
					});
		}

		this.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (onClickListenerNegative != null) {
					onClickListenerNegative.onClick(null);
				}
			}
		});
	}

	public CharSequence getTitleText() {
		return title;
	}

	public BasicDialog setTitleText(CharSequence title) {
		this.title = title;
		return this;
	}

	public CharSequence getMessageText() {
		return message;
	}

	public BasicDialog setMessageText(CharSequence message) {
		this.message = message;
		return this;
	}

	public CharSequence getPositiveText() {
		return btnPositiveTitle;
	}

	public BasicDialog setPositiveText(CharSequence btnPositiveTitle) {
		this.btnPositiveTitle = btnPositiveTitle;
		return this;
	}

	public CharSequence getNegativeText() {
		return btnNegativeTitle;
	}

	public BasicDialog setNegativeText(CharSequence btnNegativeTitle) {
		this.btnNegativeTitle = btnNegativeTitle;
		return this;
	}

	public View.OnClickListener getPositiveOnClickListener() {
		return onClickListenerPositive;
	}

	public BasicDialog setPositiveOnClickListener(
			View.OnClickListener onClickListenerPositive) {
		this.onClickListenerPositive = onClickListenerPositive;
		if (btnPositive != null) {
			btnPositive.setOnClickListener(onClickListenerPositive);
		}
		return this;
	}

	public View.OnClickListener getNegativeOnClickListener() {
		return onClickListenerNegative;
	}

	public BasicDialog setNegativeOnClickListener(
			View.OnClickListener onClickListenerNegative) {
		this.onClickListenerNegative = onClickListenerNegative;
		if (btnNegative != null) {
			btnNegative.setOnClickListener(onClickListenerNegative);
		}
		return this;
	}

	public BasicDialog setCloseOnClickListener(
			View.OnClickListener onClickListenerClose) {
		this.onClickListenerClose = onClickListenerClose;
		if (btnClose != null) {
			btnClose.setOnClickListener(onClickListenerClose);
		}
		return this;
	}

	public boolean isEnableInput() {
		return isEnableInput;
	}

	public BasicDialog setEnableInput(boolean isEnableInput) {
		this.isEnableInput = isEnableInput;
		return this;
	}

	public boolean isCancelable() {
		return isCancelable;
	}

	@Override
	public void setCancelable(boolean isCancelable) {
		this.isCancelable = isCancelable;
		super.setCancelable(isCancelable);
	}

	public String getInputValue() {
		return edtInput.getText().toString();
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		setTitleText(title);
	}

	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		setTitleText(context.getString(titleId));
	}

}
