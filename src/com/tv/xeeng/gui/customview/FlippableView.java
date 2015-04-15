package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.tv.xeeng.R;

public class FlippableView extends FrameLayout {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final int FLIP_DURATION = 500;
	
	// ===========================================================
	// Fields
	// ===========================================================
	private View frontView;
	private View backView;

	private boolean isShowingFrontView;

	public boolean isShowingFrontView() {
		return isShowingFrontView;
	}

	// ===========================================================
	// Constructors
	// ===========================================================
	public FlippableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		readAttributeSet(attrs);
		initLayout();
	}

	public FlippableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		readAttributeSet(attrs);
		initLayout();
	}

	public FlippableView(Context context) {
		super(context);
		initLayout();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void setFrontView(View view) {
		this.removeView(frontView);
		this.frontView = view;
		this.addView(frontView);

		this.showCorrectView();
		this.invalidate();
	}

	public void setBackView(View view) {
		this.removeView(backView);
		this.backView = view;
		this.addView(frontView);

		this.showCorrectView();
		this.invalidate();
	}

	public View getFrontView() {
		return frontView;
	}

	public View getBackView() {
		return backView;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	private void readAttributeSet(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.FlippableView);

		frontView = inflate(getContext(), typedArray.getResourceId(
				R.styleable.FlippableView_frontViewId,
				R.layout.flippable_view_front), null);
		backView = inflate(getContext(), typedArray.getResourceId(
				R.styleable.FlippableView_backViewId,
				R.layout.flippable_view_back), null);
		isShowingFrontView = typedArray.getBoolean(
				R.styleable.FlippableView_isShowingFrontView, false);

		typedArray.recycle();
	}

	private void initLayout() {
		if (frontView == null) {
			frontView = new View(getContext());
		}
		if (backView == null) {
			backView = new View(getContext());
		}

		addView(frontView);
		addView(backView);

		showCorrectView();
	}

	private void showCorrectView() {
		if (isShowingFrontView) {
			frontView.setVisibility(View.VISIBLE);
			backView.setVisibility(View.INVISIBLE);
		} else {
			frontView.setVisibility(View.INVISIBLE);
			backView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Play the "flip" animation on this view with the listener
	 * 
	 * @param listener
	 *            Can be null
	 */
	public void flipUp(AnimationListener listener) {
		Animation anim = new FlipAnimation(backView, frontView);
		anim.setAnimationListener(listener);

		startAnimation(anim);

		isShowingFrontView = true;
	}

	/**
	 * Play the "flip" animation on this view with the listener
	 * 
	 * @param listener
	 *            Can be null
	 */
	public void flipDown(AnimationListener listener) {
		Animation anim = new FlipAnimation(frontView, backView);
		anim.setAnimationListener(listener);

		startAnimation(anim);

		isShowingFrontView = false;
	}

	public void flip(AnimationListener listener) {
		if (isShowingFrontView) {
			flipDown(listener);
		} else {
			flipUp(listener);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	public static class FlipAnimation extends Animation {
		private Camera camera;

		private View fromView;
		private View toView;

		private float centerX;
		private float centerY;

		private boolean forward = true;

		/**
		 * Creates a 3D flip animation between two views.
		 * 
		 * @param fromView
		 *            First view in the transition.
		 * @param toView
		 *            Second view in the transition.
		 */
		public FlipAnimation(View fromView, View toView) {
			this.fromView = fromView;
			this.toView = toView;

			setDuration(FLIP_DURATION);
			setFillAfter(false);
			setInterpolator(new AccelerateDecelerateInterpolator());
		}

		public void reverse() {
			forward = false;
			View switchView = toView;
			toView = fromView;
			fromView = switchView;
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			centerX = width / 2;
			centerY = height / 2;
			camera = new Camera();
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			// Angle around the y-axis of the rotation at the given time
			// calculated both in radians and degrees.
			final double radians = Math.PI * interpolatedTime;
			float degrees = (float) (180.0 * radians / Math.PI);

			// Once we reach the midpoint in the animation, we need to hide the
			// source view and show the destination view. We also need to change
			// the angle by 180 degrees so that the destination does not come in
			// flipped around
			if (interpolatedTime >= 0.5f) {
				degrees -= 180.f;
				fromView.setVisibility(View.GONE);
				toView.setVisibility(View.VISIBLE);
			}

			if (forward)
				degrees = -degrees; // determines direction of rotation when
									// flip begins

			final Matrix matrix = t.getMatrix();
			camera.save();
			camera.rotateY(degrees);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centerX, -centerY);
			matrix.postTranslate(centerX, centerY);
		}
	}
}
