package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.text.exception.OutOfCharactersException;
import org.andengine.entity.text.vbo.ITextVertexBufferObject;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class XeengText extends Text {

	private float mWidth, mHeight;

	{
		setScale(1);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			VertexBufferObjectManager pVertexBufferObjectManager,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager, pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager, pDrawType);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pCharactersMaximum,
				pVertexBufferObjectManager);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager, pDrawType,
				pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager,
				pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager,
				pDrawType);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum,
				pVertexBufferObjectManager, pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType) {
		super(pX, pY, pFont, pText, pCharactersMaximum,
				pVertexBufferObjectManager, pDrawType);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				pVertexBufferObjectManager);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			ITextVertexBufferObject pTextVertexBufferObject) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				pTextVertexBufferObject);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager,
				pDrawType, pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum,
				pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				pVertexBufferObjectManager, pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				pVertexBufferObjectManager, pDrawType);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			ITextVertexBufferObject pTextVertexBufferObject,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				pTextVertexBufferObject, pShaderProgram);
	}

	public XeengText(float pX, float pY, IFont pFont, CharSequence pText,
			int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions,
				pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	@Override
	public void setScale(float pScale) {
		setScale(pScale, pScale);
	}

	@Override
	public void setScale(float pScaleX, float pScaleY) {

		super.setScale(pScaleX * BaseXeengGame.INSTANCE.textScale, pScaleY
				* BaseXeengGame.INSTANCE.textScale);
	}

	@Override
	public void setScaleX(float pScaleX) {

		super.setScaleX(pScaleX * BaseXeengGame.INSTANCE.textScale);
	}

	@Override
	public void setScaleY(float pScaleY) {

		super.setScaleY(pScaleY * BaseXeengGame.INSTANCE.textScale);
	}

	@Override
	public void setText(CharSequence pText) throws OutOfCharactersException {

		super.setText(pText);
		updateSize();
	}

	public float getBiengWidth() {

		return mWidth;
	}

	public float getBiengHeight() {

		return mHeight;
	}

	private void updateSize() {

		// mWidth = getWidth()*BaseXeengGame.INSTANCE.textScale;
		// mHeight = getHeight()*BaseXeengGame.INSTANCE.textScale;
	}
}
