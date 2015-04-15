package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.manager.Logger;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

public class TimerText extends Sprite {

	private Text text;
	private int mtimeout;
	private TimerType mtype;
	private int mcurrent;
	private boolean isReset;
	private OnTimerTextListener mListener;

	public enum TimerType {

		OVERLAY, FOCUS,
	}

	public TimerText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				Color.WHITE, BaseXeengGame.timerTextBgTR,
				pVertexBufferObjectManager);
	}

	public TimerText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			ITextureRegion backgroundTR,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				Color.WHITE, backgroundTR, pVertexBufferObjectManager);
	}

	public TimerText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions, Color textColor,
			ITextureRegion backgroundTR,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, backgroundTR, pVertexBufferObjectManager);
		text = new Text(getWidth() / 2, getHeight() / 2, pFont, pText,
				pCharactersMaximum, pTextOptions, pVertexBufferObjectManager);
		text.setColor(textColor);
		attachChild(text);
	}

	public void setText(CharSequence cs) {
		this.text.setText(cs);
	}

	public void setupTimer(int pTimeout) {

		mcurrent = pTimeout + 1;
		mtimeout = pTimeout;
		mtype = TimerType.FOCUS;
		reset();
	}

	public void setOnTimerListener(OnTimerTextListener pListener) {

		mListener = pListener;
	}

	public void start() {

		reset();
		this.registerUpdateHandler(timerHandler);
		Logger.getInstance().warn(this, "Did start timer");
	}

	public void stop() {

		isReset = false;
		this.setVisible(false);
		this.clearUpdateHandlers();
		Logger.getInstance().warn(this, "Did stop timer");
	}

	public void seek(int bySecond) {

		mcurrent += bySecond;
	}

	public void pause() {

		isReset = false;
	}

	public void reset() {
		mcurrent = mtimeout;
		this.setText("" + mtimeout);
		isReset = true;
		this.clearUpdateHandlers();
		timerHandler.reset();
	}

	public void setType(TimerType pType) {

		if (mtype == pType)
			return;

		mtype = pType;
	}

	public int getCurrentTime() {

		return mcurrent;
	}

	private TimerHandler timerHandler = new TimerHandler(1,
			new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {

					mcurrent--;
					if (mcurrent > 0 && isReset) {

						pTimerHandler.reset();
					} else {

						stop();
						if (mListener != null) {

							mListener.onTimeOut();
						}
					}

					// update text
					TimerText.this.setText("" + mcurrent);

					switch (mtype) {
					case OVERLAY:

						break;

					case FOCUS:
						text.registerEntityModifier(new ParallelEntityModifier(
								new ScaleAtModifier(0.2f, 2, 1, 0.5f, 0.5f),
								new AlphaModifier(0.2f, 0.1f, 1)));
						break;
					default:
						break;
					}
				}
			});

	public interface OnTimerTextListener {
		public void onTimeOut();
	}
}
