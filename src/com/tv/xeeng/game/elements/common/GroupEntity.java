package com.tv.xeeng.game.elements.common;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class GroupEntity extends TouchableEntity {

	public GroupEntity(float pX, float pY,
			VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY, pVertexBufferObjectManager);
	}

	@Override
	public void setIgnoreUpdate(boolean pIgnoreUpdate) {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setIgnoreUpdate(pIgnoreUpdate);
		}
		super.setIgnoreUpdate(pIgnoreUpdate);
	}

	@Override
	public void setVisible(boolean pVisible) {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setVisible(pVisible);
		}
		super.setVisible(pVisible);
	}
}
