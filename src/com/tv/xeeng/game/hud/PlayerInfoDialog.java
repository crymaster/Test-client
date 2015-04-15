package com.tv.xeeng.game.hud;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.game.BaseGame.GameState;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.R;

public class PlayerInfoDialog extends BasicDialog {

	private TextView mnameTv, mcashTv, mexpTv;
	private Button maddFriendBtn, mcashGiftBtn, mkickBtn;
	private long mplayerId;
	private String mName;
	private OnPlayerInfoClickListener mListener;

	public PlayerInfoDialog(Context context, OnPlayerInfoClickListener pListener) {
		super(context);
		mListener = pListener;
		initLayout();
	}

	public void update(long pPlayerId) {

		mplayerId = pPlayerId;
		String pPlayerName = "";
		long pCash = -1;
		long pExp = 0;
		boolean pIsKickable = false;
		Player pd = GameData.shareData().getGame().getPlayer(mplayerId);
		if (pd != null) {
			mName = pd.character;
			pPlayerName = pd.character;
			pCash = pd.cash;
		}
		pIsKickable = GameData.shareData().getGame().getMainPlayer().isTableMaster;
		mnameTv.setText(pPlayerName);
		mcashTv.setText(CommonUtils.formatCash(pCash));
		mexpTv.setText(CommonUtils.formatExp(pExp));

		if (pPlayerId == GameData.shareData().getMyself().id) {

			mkickBtn.setVisibility(View.INVISIBLE);
			maddFriendBtn.setVisibility(View.INVISIBLE);
			mcashGiftBtn.setVisibility(View.INVISIBLE);
		} else {
			if (pIsKickable
					&& GameData.shareData().getGame().getState() != GameState.PLAYING) {
				// Workaround for API < 11. Using an alpha animation instead
				AlphaAnimation anim = new AlphaAnimation(1.0f, 1.0f);
				anim.setDuration(0);
				anim.setFillAfter(true);
				mkickBtn.startAnimation(anim);
				mkickBtn.setEnabled(true);
			} else {
				AlphaAnimation anim = new AlphaAnimation(0.5f, 0.5f);
				anim.setDuration(0);
				anim.setFillAfter(true);
				mkickBtn.startAnimation(anim);
				mkickBtn.setEnabled(false);
			}
		}
	}

	private void initLayout() {

		setContentView(R.layout.dialog_player_info_game);
		mnameTv = (TextView) findViewById(R.id.tvUsername);
		mcashTv = (TextView) findViewById(R.id.tvGoldValue);
		mexpTv = (TextView) findViewById(R.id.txtExpValue);
		maddFriendBtn = (Button) findViewById(R.id.btn_dialog_player_info_game_add_friend);
		mcashGiftBtn = (Button) findViewById(R.id.btn_dialog_player_info_game_cash_gift);
		mkickBtn = (Button) findViewById(R.id.btn_dialog_player_info_game_kick);
		maddFriendBtn.setOnClickListener(btnClickListener);
		mcashGiftBtn.setOnClickListener(btnClickListener);
		mkickBtn.setOnClickListener(btnClickListener);
		mcashGiftBtn.setEnabled(true);
		final View view = findViewById(R.id.rl_dialog_player_info_game_root);

		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float dialogWidth = getContext().getResources().getDimension(
						R.dimen.dialog_player_info_width);
				float dialogHeight = getContext().getResources().getDimension(
						R.dimen.dialog_player_info_height);
				float originX = (view.getWidth() - dialogWidth) / 2;
				float originY = (view.getHeight() - dialogHeight) / 2;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if ((event.getX() < originX || event.getX() > originX
							+ dialogWidth)
							|| (event.getY() < originY || event.getY() > originY
									+ dialogHeight)) {
						dismiss();
						return true;
					}
				}
				return false;
			}
		});
	}

	private android.view.View.OnClickListener btnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == maddFriendBtn) {

				if (mListener != null) {

					mListener.onClickAddFriendBtn(mplayerId);
				}
				dismiss();
			} else if (v == mcashGiftBtn) {

				SendGiftMoney dialog = new SendGiftMoney(v.getContext(),
						GameData.shareData().getMyself().cash, mName);
				dialog.show();
			} else if (v == mkickBtn) {

				if (mListener != null) {

					mListener.onClickKickBtn(mplayerId);
				}
				dismiss();
			}
		}
	};

	public interface OnPlayerInfoClickListener {

		public void onClickAddFriendBtn(long pPlayerId);

		public void onClickKickBtn(long pPlayerId);
	}

}
