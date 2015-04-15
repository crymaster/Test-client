package com.tv.xeeng.gui.customview;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.itemdata.UserLevelItemData;
import com.tv.xeeng.R;

public class UserProfileDialog extends Dialog {
	private Handler uiHandler;
	private Context context;

	// Widgets
	private TextView txtUsername, txtGold, txtGender, txtLevel, txtWin,
			txtLose, txtVipLevel;
	private Button btnAddFriend, btnMessage, btnClose;

	private ImageView imvProfileAvatar, imvStatus;

	private Button btnKickOrInvitePlay;
	private Player player;

	private boolean inGame;

	private LocalBroadcastManager localBroadcastManager;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);
			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_USER_INFO)) {
				if (status) {
					Player player = intent
							.getParcelableExtra(NetworkUtils.MESSAGE_INFO);

					if (txtGender != null) {
						txtGender.setText(player.sex ? "Nam" : "Nữ");
					}
				}
			}
		}
	};

	public UserProfileDialog(Context context, Player player) {
		this(context, player, false);
	}

	public UserProfileDialog(Context context, Player player, boolean inGame) {
		super(context);
		this.context = context;
		this.player = player;
		this.inGame = inGame;

		this.localBroadcastManager = LocalBroadcastManager
				.getInstance(getContext());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		setContentView(R.layout.dialog_user_profile);

		imvProfileAvatar = (ImageView) this
				.findViewById(R.id.imv_profile_avatar);
		imvStatus = (ImageView) this.findViewById(R.id.imv_status);
		txtUsername = (TextView) this.findViewById(R.id.txt_username);
		txtGold = (TextView) this.findViewById(R.id.txt_gold);
		txtGender = (TextView) this.findViewById(R.id.txt_gender);
		txtLevel = (TextView) this.findViewById(R.id.txt_level);
		txtVipLevel = (TextView) this.findViewById(R.id.txt_viplevel);
		btnAddFriend = (Button) this.findViewById(R.id.btn_private_chat);
		btnMessage = (Button) this.findViewById(R.id.btn_message);
		btnKickOrInvitePlay = (Button) this
				.findViewById(R.id.btn_kick_or_invite);
		btnClose = (Button) this.findViewById(R.id.btn_close);

		btnAddFriend.setOnClickListener(clickHandler);
		btnMessage.setOnClickListener(clickHandler);
		btnKickOrInvitePlay.setOnClickListener(clickHandler);
		btnClose.setOnClickListener(clickHandler);

		uiHandler = new Handler();
	}

	@Override
	public void show() {
		super.show();

		setCancelable(true);

		txtUsername.setText(player.character);

		StringBuilder goldValue = new StringBuilder();
		goldValue.append(CommonUtils.formatCash(player.cash));
		txtGold.setText(goldValue.toString());
		txtGender.setText(player.sex ? "Nam" : "Nữ");
		setPlayerLevel();
		if (player.vipId == 0) {
			txtVipLevel.setText("");
		} else {
			txtVipLevel.setText("Vip " + player.vipId);
		}

		if (player.isOnline || inGame) {
			imvStatus.setImageResource(R.drawable.icon_online);
		} else {
			// ColorMatrix matrix = new ColorMatrix();
			// matrix.setSaturation(0);
			//
			// ColorMatrixColorFilter filter = new
			// ColorMatrixColorFilter(matrix);
			// imvProfileAvatar.setColorFilter(filter);

			imvStatus.setImageResource(R.drawable.icon_offline);
		}

		int buttonsVisibility = player.id != GameData.shareData().getMyself().id ? View.VISIBLE
				: View.INVISIBLE;

		btnAddFriend.setVisibility(buttonsVisibility);
		btnMessage.setVisibility(buttonsVisibility);
		btnKickOrInvitePlay.setVisibility(buttonsVisibility);

		if (inGame) {
			btnKickOrInvitePlay
					.setBackgroundResource(R.drawable.btn_kick_player);
			btnKickOrInvitePlay.setVisibility(GameData.shareData().getGame()
					.getMainPlayer().isTableMaster ? buttonsVisibility
					: View.INVISIBLE);
		} else {
			btnKickOrInvitePlay
					.setBackgroundResource(R.drawable.btn_invite_play);
		}

		localBroadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_GET_USER_INFO));
		BusinessRequester.getInstance().getUserInfo(player.id);
		setAvatar();
	}

	private void setPlayerLevel() {
		if (GameData.shareData().allUserLevels == null) {
			// Unlikely to happen at this state but who knows...
			return;
		}
		for (UserLevelItemData level : GameData.shareData().allUserLevels) {
			if (player.cash > level.getMinGold()) {
				player.level = level;
			}
		}
		txtLevel.setText(player.level.getName());
	}

	private View.OnClickListener clickHandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_private_chat:
				BusinessRequester.getInstance().addFriend(player.id, true);
				UserProfileDialog.this.dismiss();
				break;
			case R.id.btn_message: {
				final SendMessageDialog dialog = new SendMessageDialog(context);
				dialog.setPositiveText("Gửi tin nhắn");
				dialog.setTitleText(txtUsername.getText());
				dialog.setPositiveOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						BusinessRequester.getInstance().sendPrivateMessage(
								player.id,
								GameData.shareData().getMyself().character,
								dialog.getInputValue());
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
			}
			case R.id.btn_kick_or_invite: {
				UserProfileDialog.this.dismiss();
				if (inGame) {
					BusinessRequester.getInstance().kickOut(
							GameData.shareData().getGame().getMatchId(),
							player.id);
				} else {
					Toast.makeText(context, "Tính năng đang phát triển",
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case R.id.btn_close:
				dismiss();
				break;
			}
		}
	};

	private void setAvatar() {
		if (imvProfileAvatar != null) {
			imvProfileAvatar.setImageResource(R.drawable.avatar_default);
			Bitmap bitmap = CommonUtils.getBitmapFromMemCache(player.id);
			if (bitmap != null) {
				imvProfileAvatar.setImageBitmap(bitmap);
			}
		} else {
			// Try get it from server
			BackgroundThreadManager.post(new Runnable() {

				@Override
				public void run() {
					if (BusinessRequester.getInstance().getUserAvatar(
							GameData.shareData().getMyself().id)) {
						uiHandler.post(new Runnable() {

							@Override
							public void run() {
								setAvatar();
							}
						});
					}
				}
			});
		}
	}
}
