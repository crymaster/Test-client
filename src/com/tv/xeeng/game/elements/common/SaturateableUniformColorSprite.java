package com.tv.xeeng.game.elements.common;

import android.opengl.GLES20;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.UniformColorSprite;
import org.andengine.entity.sprite.vbo.HighPerformanceUniformColorSpriteVertexBufferObject;
import org.andengine.entity.sprite.vbo.IUniformColorSpriteVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributesBuilder;

public class SaturateableUniformColorSprite extends UniformColorSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int VERTEX_INDEX_X = 0;
	public static final int VERTEX_INDEX_Y = SaturateableUniformColorSprite.VERTEX_INDEX_X + 1;
	public static final int TEXTURECOORDINATES_INDEX_U = SaturateableUniformColorSprite.VERTEX_INDEX_Y + 1;
	public static final int TEXTURECOORDINATES_INDEX_V = SaturateableUniformColorSprite.TEXTURECOORDINATES_INDEX_U + 1;

	public static final int VERTEX_SIZE = 2 + 2;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int SPRITE_SIZE = SaturateableUniformColorSprite.VERTEX_SIZE
			* SaturateableUniformColorSprite.VERTICES_PER_SPRITE;

	public static final VertexBufferObjectAttributes VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT = new VertexBufferObjectAttributesBuilder(
			2)
			.add(ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION,
					ShaderProgramConstants.ATTRIBUTE_POSITION, 2,
					GLES20.GL_FLOAT, false)
			.add(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION,
					ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES, 2,
					GLES20.GL_FLOAT, false).build();

	// ===========================================================
	// Fields
	// ===========================================================
	private float mSaturation = 1.0f;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(),
				pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShaderProgram pShaderProgram) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(),
				pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC,
				pShaderProgram);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager,
			final DrawType pDrawType) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(),
				pTextureRegion, pVertexBufferObjectManager, pDrawType);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager,
			final DrawType pDrawType, final ShaderProgram pShaderProgram) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(),
				pTextureRegion, pVertexBufferObjectManager, pDrawType,
				pShaderProgram);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final ITextureRegion pTextureRegion,
			final IUniformColorSpriteVertexBufferObject pVertexBufferObject) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(),
				pTextureRegion, pVertexBufferObject);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final ITextureRegion pTextureRegion,
			final IUniformColorSpriteVertexBufferObject pVertexBufferObject,
			final ShaderProgram pShaderProgram) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(),
				pTextureRegion, pVertexBufferObject, pShaderProgram);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final float pWidth, final float pHeight,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pWidth, pHeight, pTextureRegion,
				pVertexBufferObjectManager, DrawType.STATIC);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final float pWidth, final float pHeight,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShaderProgram pShaderProgram) {
		this(pX, pY, pWidth, pHeight, pTextureRegion,
				pVertexBufferObjectManager, DrawType.STATIC, pShaderProgram);
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final float pWidth, final float pHeight,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager,
			final DrawType pDrawType) {
		this(pX, pY, pWidth, pHeight, pTextureRegion,
				pVertexBufferObjectManager, pDrawType,
				PositionTextureCoordinatesUniformColorSaturationShaderProgram
						.getInstance());
	}

	public SaturateableUniformColorSprite(final float pX, final float pY,
			final float pWidth, final float pHeight,
			final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager,
			final DrawType pDrawType, final ShaderProgram pShaderProgram) {
		this(
				pX,
				pY,
				pWidth,
				pHeight,
				pTextureRegion,
				new HighPerformanceUniformColorSpriteVertexBufferObject(
						pVertexBufferObjectManager,
						SaturateableUniformColorSprite.SPRITE_SIZE,
						pDrawType,
						true,
						SaturateableUniformColorSprite.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT),
				pShaderProgram);
	}

	public SaturateableUniformColorSprite(
			final float pX,
			final float pY,
			final float pWidth,
			final float pHeight,
			final ITextureRegion pTextureRegion,
			final IUniformColorSpriteVertexBufferObject pSaturateableUniformColorSpriteVertexBufferObject) {
		this(pX, pY, pWidth, pHeight, pTextureRegion,
				pSaturateableUniformColorSpriteVertexBufferObject,
				PositionTextureCoordinatesUniformColorSaturationShaderProgram
						.getInstance());
	}

	public SaturateableUniformColorSprite(
			final float pX,
			final float pY,
			final float pWidth,
			final float pHeight,
			final ITextureRegion pTextureRegion,
			final IUniformColorSpriteVertexBufferObject pSaturateableUniformColorSpriteVertexBufferObject,
			final ShaderProgram pShaderProgram) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,
				pSaturateableUniformColorSpriteVertexBufferObject,
				pShaderProgram);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public float getSaturation() {
		return this.mSaturation;
	}

	public void setSaturation(float pSaturation) {
		this.mSaturation = pSaturation;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void preDraw(final GLState pGLState, final Camera pCamera) {
		super.preDraw(pGLState, pCamera);

		GLES20.glUniform1f(
				PositionTextureCoordinatesUniformColorSaturationShaderProgram.sUniformSaturationLocation,
				this.mSaturation);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
