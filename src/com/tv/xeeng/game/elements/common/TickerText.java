package com.tv.xeeng.game.elements.common;

import android.opengl.GLES20;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TickerText extends XeengText {

	private float mCharacterPerMiliSeconds;
	private int mCharacterVisible;
	private float mTimeElapsed;
	private boolean isAutoReset;
	private CustomeTickerTextOptions mTickerTextOptions;

	public TickerText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum,
			CustomeTickerTextOptions pTickerTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY, pFont, pText, pCharactersMaximum, pTickerTextOptions,
				pVertexBufferObjectManager);
		mTimeElapsed = 0;
		isAutoReset = true;
		mTickerTextOptions = pTickerTextOptions;
		mCharacterVisible = mTickerTextOptions.getCharecterVisible();
		mCharacterPerMiliSeconds = mTickerTextOptions
				.getCharacterPerMiliseconds();
	}

	public void setText(String pText, int pCharactorVisible) {

		setText(pText);
		mTickerTextOptions.setCharecterVisible(pCharactorVisible);
		reset();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {

		super.onManagedUpdate(pSecondsElapsed);

		if (mCharacterVisible < mCharactersToDraw) {

			mTimeElapsed += pSecondsElapsed;
			if (mTimeElapsed > mCharacterPerMiliSeconds) {

				mTimeElapsed = 0;
				mCharacterVisible++;
			}
		} else if (isAutoReset) {

			mTimeElapsed += pSecondsElapsed;
			if (mTimeElapsed > mCharacterPerMiliSeconds) {

				mTimeElapsed = 0;
				reset();
			}
		}
	}

	@Override
	public void reset() {

		mCharacterVisible = mTickerTextOptions.getCharecterVisible();
		mTimeElapsed = 0;
	}

	public boolean isAutoReset() {
		return isAutoReset;
	}

	public void setAutoReset(boolean isAutoReset) {
		this.isAutoReset = isAutoReset;
	}

	@Override
	protected void draw(GLState pGLState, Camera pCamera) {

		this.mTextVertexBufferObject.draw(GLES20.GL_TRIANGLES,
				this.mCharacterVisible * Text.VERTICES_PER_LETTER);
	}
}
