package com.tv.xeeng.game.elements.common;

import android.graphics.*;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.manager.Logger;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.region.ITextureRegion;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressTimer extends Entity {
	protected static final int FREQ = 10;

	protected enum ProgressState {

		INITIALIZED, RUNNING, STOP
	}

	protected float strokeWidth = 6; // default
	protected final long stepInLong = 100l;
	protected int maxCount;
	protected int mtimeOut;
	protected int mcounter;
	protected float stepAngle;
	protected boolean isReverse;
	protected ProgressState mstate;
	protected float msweepAngle;
	protected OnProgressTimerListener mTimerListener;

	protected int mwidth, mheight;
	protected int mtopInset, mbottomInset, mleftInset, mrightInset;

	protected Paint paint;
	protected BitmapTextureAtlas marcBitmap;
	protected TextureManager mtextureManager;
	protected ITextureRegion marcTR;
	protected BaseXeengGame mgameActivity;
	protected Sprite mcircleSprite;

	protected Timer mTimer = new Timer();
	protected TimerTask mTask;

	private int lowTimeTrigger;

	public int getLowTimeTrigger() {
		return lowTimeTrigger;
	}

	public void setLowTimeTrigger(int lowTimeTrigger) {
		this.lowTimeTrigger = lowTimeTrigger * FREQ;
	}

	private boolean onLowTimeCalled;

	public ProgressTimer(float x, float y, int _width, int _height,
			int timeout, BaseXeengGame _gactivity) {

		super(x, y);
		mwidth = (int) (_width / BaseXeengGame.INSTANCE.textScale);
		mheight = (int) (_height / BaseXeengGame.INSTANCE.textScale);
		isReverse = false;
		mcounter = 0;
		mtimeOut = timeout;
		maxCount = (int) (timeout * FREQ);
		stepAngle = 360.0f / maxCount;
		mgameActivity = _gactivity;
		mtextureManager = mgameActivity.getTextureManager();

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeCap(Cap.ROUND);
		paint.setAlpha(255);

		strokeWidth = strokeWidth / BaseXeengGame.INSTANCE.textScale;
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Style.FILL);

		// gradient = new Gradient(0, mheight / 2, mwidth, mheight,
		// _gactivity.getVertexBufferObjectManager());
		// gradient.setScale(BaseXeengGame.INSTANCE.textScale);
		// gradient.setAnchorCenterX(0);

		/*
		 * compute bound rect of canvas
		 */
		updateRectInset();

		marcBitmap = new BitmapTextureAtlas(mtextureManager,
				(int) (_width / BaseXeengGame.INSTANCE.textScale),
				(int) (_height / BaseXeengGame.INSTANCE.textScale));
		updateProgressBitmap();

		mcircleSprite = new Sprite(0, 0, marcTR,
				mgameActivity.getVertexBufferObjectManager());
		attachChild(mcircleSprite);
		mcircleSprite.setSize(mwidth, mheight);
		mcircleSprite.setScale(BaseXeengGame.INSTANCE.textScale);

		// mcircleSprite.attachChild(gradient);

		mstate = ProgressState.INITIALIZED;

	}

	public void start() {

		if (mstate == ProgressState.RUNNING)
			return;

		setIgnoreUpdate(false);
		setVisible(true);

		onLowTimeCalled = false;

		mcounter = 0;
		mstate = ProgressState.RUNNING;
		mTask = new TimerTask() {

			@Override
			public void run() {
				mcounter++;
				if (mcounter < maxCount) {
					if (lowTimeTrigger != 0
							&& mcounter > maxCount - lowTimeTrigger) {
						if (mTimerListener != null
								&& mstate == ProgressState.RUNNING
								&& !onLowTimeCalled) {
							mTimerListener.onLowTime();
							onLowTimeCalled = true;
						}
					}
				} else {
					Logger.getInstance().warn(ProgressTimer.this, "time out");
					if (mTimerListener != null
							&& mstate == ProgressState.RUNNING) {
						mTimerListener.onTimeout();
					}
					stop();
				}
				float progress = (float) mcounter / maxCount;

				// from 88, 1, 1
				// to 255, 0, 0

				int toColor = Color.argb(255, (int) (33f + (88f - 33f)
						* progress), (int) (1 + (8 - 1) * (1 - progress)), 1);
				int fromColor = Color.argb(255, (int) (255 * progress),
						(int) (255 * (1 - progress)),
						(int) (23f * (1 - progress)));

				paint.setShader(new LinearGradient(0, 0, 0, mheight, fromColor,
						toColor, TileMode.CLAMP));

				marcBitmap.clearTextureAtlasSources();
				updateProgressBitmap();
			}
		};
		mTimer = new Timer();
		try {
			mTimer.schedule(mTask, 0, stepInLong);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		mstate = ProgressState.STOP;
		setVisible(false);
		setIgnoreUpdate(true);

		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	public void pause() {

	}

	public void setOnProgressTimerListener(
			OnProgressTimerListener pTimerListener) {

		mTimerListener = pTimerListener;
	}

	public void setReverse(boolean _isReverse) {

		if (isReverse == _isReverse)
			return;
		isReverse = _isReverse;
		// mcircleSprite.setFlippedHorizontal(isReverse);
	}

	public void setStrokeWidth(int _stroke) {

		strokeWidth = _stroke / BaseXeengGame.INSTANCE.textScale;
		paint.setStrokeWidth(strokeWidth);
		updateRectInset();
	}

	public void setTimeOut(int timeOut) {
		this.mtimeOut = timeOut;
		maxCount = (int) (this.mtimeOut * FREQ);
	}

	@Override
	public boolean detachSelf() {

		stop();
		return super.detachSelf();
	}

	@Override
	public void dispose() {

		if (mTask != null) {

			mTask.cancel();
			mTask = null;
		}

		if (mTimer != null) {

			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
		super.dispose();
	}

	private void updateRectInset() {

		mtopInset = (int) (strokeWidth / 2) + 1;
		mleftInset = (int) (strokeWidth / 2) + 1;
		mrightInset = (int) (strokeWidth / 2) + 1;
		mbottomInset = (int) (strokeWidth / 2) + 1;
	}

	protected void updateProgressBitmap() {
		final IBitmapTextureAtlasSource baseTextureSource = new EmptyBitmapTextureAtlasSource(
				mwidth, mheight);

		final IBitmapTextureAtlasSource decor = new BaseBitmapTextureAtlasSourceDecorator(
				baseTextureSource) {
			@Override
			protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
				drawRoundedCorners(pCanvas);
			}

			@Override
			public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
				return null;
			}
		};

		marcTR = BitmapTextureAtlasTextureRegionFactory.createFromSource(
				marcBitmap, decor, 0, 0);
		marcBitmap.load();
	}

	protected void drawRoundedCorners(Canvas pCanvas) {
		int w = mwidth * mcounter / maxCount;
		int radius = 4;

		pCanvas.drawRoundRect(new RectF(0, 0, mwidth - w, mheight + 4), radius,
				radius, paint);

		if (w >= radius && w <= mwidth - radius) {
			pCanvas.drawRect(new RectF(new Rect(radius, 0, mwidth - w, 6)),
					paint);
		} else if (w < radius) {
			int r1 = radius - w;
			pCanvas.drawRoundRect(
					new RectF(new Rect(radius, 0, mwidth - w, 6)), r1, r1,
					paint);
		}
	}

	public interface OnProgressTimerListener {
		public void onTimeout();

		public void onLowTime();
	}
}
