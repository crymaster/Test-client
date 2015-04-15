package com.tv.xeeng.game.hud;

import android.content.Context;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.customview.UserProfileDialog;

public class UserProfileIngameDialog extends UserProfileDialog {

	public UserProfileIngameDialog(Context context, Player player) {
		super(context, player);
	}
	// private OnPlayerInfoClickListener listener;
	//
	// public UserProfileIngameDialog(Context context, Player player,
	// OnPlayerInfoClickListener listener) {
	// super(context, player);
	// this.listener = listener;
	// }
	//
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// boolean kickable =
	// GameData.shareData().getGame().getMainPlayer().isTableMaster;
	//
	// btnInvitePlay.setText("Kick");
	// btnInvitePlay.setVisibility(kickable ? View.VISIBLE : View.INVISIBLE);
	// btnInvitePlay.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// listener.onClickKickBtn(player.id);
	// }
	// });
	// }
}
