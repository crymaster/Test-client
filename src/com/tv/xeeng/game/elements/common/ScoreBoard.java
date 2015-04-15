package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScoreBoard extends Entity {
	private Sprite background;
	private XeengText txtScore;

	public ScoreBoard(float x, float y, String pointStr) {
		this(x, y);
		setPoint(pointStr);
	}

	public ScoreBoard(float x, float y) {
		super(x, y);

		VertexBufferObjectManager vbom = BaseXeengGame.INSTANCE
				.getVertexBufferObjectManager();

		background = new Sprite(0, 0, BaseCardGameActivity.scoreBgTR, vbom);
		txtScore = new XeengText(background.getWidth() / 2,
				background.getHeight() / 2, BaseXeengGame.smallBoldFont,
				"0123456789abcdefghijklmnopqrstuvwxyz", vbom);
		txtScore.setColor(238f / 255f, 209f / 255f, 56f / 255f);

		background.attachChild(txtScore);

		attachChild(background);
	}

	/*
	 * public PhomScoreBoard(float x, float y, int point) { super(x, y);
	 * 
	 * VertexBufferObjectManager vbom = BaseXeengGame.INSTANCE
	 * .getVertexBufferObjectManager();
	 * 
	 * background = new Sprite(0, 0, PhomGameActivity.scoreBoardTR, vbom);
	 * String pointStr = point + " điểm"; txtScore = new
	 * XeengText(background.getWidth() / 2, background.getHeight() / 2,
	 * BaseXeengGame.smallRegularFont, pointStr, "30 điểm".length(), vbom);
	 * txtScore.setColor(238f / 255f, 209f / 255f, 56f / 255f);
	 * 
	 * background.attachChild(txtScore);
	 * 
	 * attachChild(background); }
	 */

	public void setPoint(String pointStr) {
		txtScore.setText(pointStr);
	}

	public void setPoint(int point) {
		txtScore.setText(point + " điểm");
	}

}
