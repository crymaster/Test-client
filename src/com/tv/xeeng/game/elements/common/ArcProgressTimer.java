package com.tv.xeeng.game.elements.common;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;

public class ArcProgressTimer extends ProgressTimer {

	public ArcProgressTimer(float x, float y, int _width, int _height,
			int timeout, BaseXeengGame _gactivity) {
		super(x, y, _width, _height, timeout, _gactivity);
		// paint.setStyle(Style.STROKE);
	}

	protected void updateProgressBitmap() {

		final IBitmapTextureAtlasSource baseTextureSource = new EmptyBitmapTextureAtlasSource(
				mwidth, mheight);
		final IBitmapTextureAtlasSource decor = new BaseBitmapTextureAtlasSourceDecorator(
				baseTextureSource) {

			@Override
			protected void onDecorateBitmap(Canvas pCanvas) throws Exception {

				if (isReverse) {

					msweepAngle = 360 - stepAngle * mcounter;
				} else {

					msweepAngle = stepAngle * mcounter;
				}

				pCanvas.drawArc(new RectF(new Rect(mleftInset, mtopInset,
						mwidth - mrightInset, mheight - mbottomInset)), -90,
						msweepAngle, false, mPaint);
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

}
