package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.CardGameTable;
import com.tv.xeeng.R;

public class CreateTableDialog extends BasicDialog {

	private SeekBar seekBar;

	private long[] availableBets;
	private int[] textColors;

	private int interval;
	private int maxNumberOfPlayer;

	private RelativeLayout layoutSeekbarLabels;
	private RadioGroup numberOfPlayer;

	private RadioButton radioBtnTypeFull;

	public CreateTableDialog(Context context) {
		super(context);
		title = "TẠO BÀN";
		btnPositiveTitle = context.getString(R.string.confirm);
		btnNegativeTitle = null;
		isEnableInput = true;

		setCancelable(true);

		onClickListenerPositive = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int maxPlayers = getMaxNumberOfPlayer();
				String tableName = getInputValue();

				if (TextUtils.isEmpty(tableName)) {
					Toast.makeText(CreateTableDialog.this.context,
							"Tên bàn không thể để trống!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				BusinessRequester.getInstance().createNewTable(
						GameData.shareData().gameId,
						GameData.shareData().currentRoomLevel.getId(),
						getCurrentBet(), maxPlayers, tableName);

				CardGameTable table = new CardGameTable(0);
				table.update(0, 1, maxPlayers, getCurrentBet(), getCurrentBet());

				GameData.shareData().getGame().setCurrentTable(table);

				dismiss();
			}
		};
	}

	public long[] getAvailableBets() {
		return availableBets;
	}

	public CreateTableDialog setAvailableBets(long[] availableBets) {
		this.availableBets = availableBets;
		return this;
	}

	public long getCurrentBet() {
		return availableBets[(seekBar.getProgress() / interval)];
	}

	public int getMaxNumberOfPlayer() {
		if (numberOfPlayer == null) {
			return 2;
		}
		if (numberOfPlayer.getCheckedRadioButtonId() == R.id.btn_type_solo) {
			return 2;
		}
		return maxNumberOfPlayer;
	}

	public CreateTableDialog setMaxNumberOfPlayer(int maxNumberOfPlayer) {
		this.maxNumberOfPlayer = maxNumberOfPlayer;
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_create_table);

		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		edtInput = (EditText) findViewById(R.id.edt_input);
		btnPositive = (Button) findViewById(R.id.btn_positive);
		btnNegative = (Button) findViewById(R.id.btn_negative);
		btnClose = (Button) findViewById(R.id.btn_close);

		seekBar = (SeekBar) findViewById(R.id.sb_min_bet);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			boolean isRelease = false;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				isRelease = true;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isRelease = false;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (!fromUser && !isRelease)
					return;

				for (int i = 0; i < availableBets.length - 1; i++) {
					if (progress >= (i + 0.5) * interval) {
						seekBar.setProgress((i + 1) * interval);
					}
				}
				if (progress < 0.5 * interval) {
					seekBar.setProgress(0);
				}
			}
		});
		layoutSeekbarLabels = (RelativeLayout) findViewById(R.id.rl_seek_bar_labels);
		numberOfPlayer = (RadioGroup) findViewById(R.id.rg_table_size);

		radioBtnTypeFull = (RadioButton) numberOfPlayer
				.findViewById(R.id.btn_type_full);

		textColors = getContext().getResources().getIntArray(
				R.array.create_table_text_colors);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			initSeekbarLabels();
		}
	}

	private void initSeekbarLabels() {
		interval = 100 / ((availableBets.length - 1) > 0 ? availableBets.length - 1
				: 1);

		if (layoutSeekbarLabels.getChildCount() > 1) {
			layoutSeekbarLabels.removeViews(1,
					layoutSeekbarLabels.getChildCount() - 1);
		}

		int seekBarWidth = seekBar.getWidth() - seekBar.getPaddingLeft()
				- seekBar.getPaddingRight();

		for (int i = 0; i < availableBets.length; i++) {
			TextView label = new TextView(getContext());
			label.setGravity(Gravity.LEFT);
			label.setSingleLine(true);
			label.setTextColor(textColors[i > textColors.length ? textColors.length - 1
					: i]);
			label.setId(i * 1000 + 100);
			label.setText(CommonUtils.formatGold(availableBets[i]));

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			params.addRule(RelativeLayout.ABOVE, R.id.sb_min_bet);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			if (i > 0) {
				params.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 1000 + 100);
				params.setMargins(seekBarWidth / (availableBets.length - 1) * i
						- getTextViewWidth(label) / 2, 0, 0, 0);
			}

			layoutSeekbarLabels.addView(label, params);
		}

		seekBar.setProgress(0);
	}

	private String getRandomRoomName() {
		String[] roomNames = { "Đại gia thì vào", "Không thử sao biết",
				"Cao thủ vào đây chiến hết", "A đù lại thắng",
				"Làm giàu không khó", "Không dành cho gà", "Chơi tới bến",
				"Đấu với Thần Bài", "Buồn phiền vì nhiều tiền",
				"Tiền hết tình tan",
				"Tiền không thiếu nhưng nhiều thì không có" };

		return roomNames[(int) Math.floor(Math.random() * roomNames.length)];
	}

	private int getTextViewWidth(TextView textView) {
		textView.measure(0, 0);
		return textView.getMeasuredWidth();
	}

	@Override
	public void show() {
		super.show();
		radioBtnTypeFull.setText(maxNumberOfPlayer + " người");
		numberOfPlayer.check(radioBtnTypeFull.getId());
		edtInput.setText(getRandomRoomName());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
