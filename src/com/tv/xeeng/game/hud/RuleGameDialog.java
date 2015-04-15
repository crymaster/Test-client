package com.tv.xeeng.game.hud;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.tv.xeeng.R;

public class RuleGameDialog extends Dialog {
	private TextView ruleView;
	private TextView titleView;
	private Button closeButton;

	private String ruleContent;
	private String titleContent;

	public RuleGameDialog(Context context) {

		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout(getContext());
	}

	private void initLayout(Context context) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		setContentView(R.layout.dialog_games_rule);
		ruleView = (TextView) findViewById(R.id.rule_in_game);
		titleView = (TextView) findViewById(R.id.rule_in_game_title);
		closeButton = (Button) findViewById(R.id.btn_close);

		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	@Override
	public void show() {
		super.show();

		if (ruleView != null) {
			ruleView.setText(ruleContent);
		}
		if (titleView != null) {
			titleView.setText("LUẬT CHƠI - " + titleContent);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	public void update(String string, String title) {
		ruleContent = string;
		titleContent = title;
	}

	public interface OnDismissRuleDialogListener {

	}

	public void setDismissListener(OnDismissRuleDialogListener dismissListener) {
	}
}
