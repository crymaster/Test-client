package com.tv.xeeng.game.hud;

import android.util.Log;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.util.adt.list.SmartList;

public class BaseGameDialog extends Entity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private Camera camera;
	private Scene scene;

	private SmartList<ITouchArea> touchAreas;
	private SmartList<ITouchArea> hudTouchAreas;
	private SmartList<ITouchArea> underlaySceneTouchAreas;

	// ===========================================================
	// Constructors
	// ===========================================================
	public BaseGameDialog(Scene scene, Camera camera) {
		super();
		this.camera = camera;
		this.scene = scene;

		touchAreas = new SmartList<ITouchArea>();
		hudTouchAreas = new SmartList<ITouchArea>();
		underlaySceneTouchAreas = new SmartList<ITouchArea>();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public final void onAttached() {
		super.onAttached();
		Log.d("OnAttached", "OnAttched");
		hudTouchAreas.clear();
		hudTouchAreas.addAll(camera.getHUD().getTouchAreas());
		camera.getHUD().clearTouchAreas();

		underlaySceneTouchAreas.clear();
		underlaySceneTouchAreas.addAll(scene.getTouchAreas());
		underlaySceneTouchAreas.removeAll(touchAreas);
		for (ITouchArea area : underlaySceneTouchAreas) {
			scene.unregisterTouchArea(area);
		}

		for (ITouchArea area : touchAreas) {
			scene.registerTouchArea(area);
		}
	}

	@Override
	public final void onDetached() {
		super.onDetached();
		Log.d("OnDettached", "OnDettached");
		for (ITouchArea touchArea : hudTouchAreas) {
			camera.getHUD().registerTouchArea(touchArea);
		}
		for (ITouchArea area : underlaySceneTouchAreas) {
			scene.registerTouchArea(area);
		}
		for (ITouchArea touchArea : touchAreas) {
			scene.unregisterTouchArea(touchArea);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	protected final void setTouchAreaBindingOnActionMoveEnabled(boolean enable) {
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
	}

	protected final void setTouchAreaBindingOnActionDownEnabled(boolean enable) {
		scene.setTouchAreaBindingOnActionDownEnabled(enable);
	}

	protected final void registerTouchArea(ITouchArea touchArea) {
		touchAreas.add(touchArea);
	}

	protected final void unregisterTouchArea(ITouchArea touchArea) {
		scene.unregisterTouchArea(touchArea);
		touchAreas.remove(touchArea);
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
