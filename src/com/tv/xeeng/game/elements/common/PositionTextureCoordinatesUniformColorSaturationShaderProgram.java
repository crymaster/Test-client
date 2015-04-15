package com.tv.xeeng.game.elements.common;

import android.opengl.GLES20;
import org.andengine.opengl.shader.PositionTextureCoordinatesUniformColorShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

/**
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @author Michal Stawinski <michal.stawinski@gmail.com>
 * @since 19:20:20 - 30.05.2013
 */
public class PositionTextureCoordinatesUniformColorSaturationShaderProgram
		extends ShaderProgram {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String UNIFORM_SATURATION = "u_saturation";
	private static PositionTextureCoordinatesUniformColorSaturationShaderProgram INSTANCE;

	public static final String VERTEXSHADER = "uniform mat4 "
			+ ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n"
			+ "attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION
			+ ";\n" + "attribute vec2 "
			+ ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n"
			+ "varying vec2 "
			+ ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n"
			+ "void main() {\n" + "	"
			+ ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = "
			+ ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n"
			+ "	gl_Position = "
			+ ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * "
			+ ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" + "}";

	public static final String FRAGMENTSHADER = "precision lowp float;\n"
			+ "uniform sampler2D "
			+ ShaderProgramConstants.UNIFORM_TEXTURE_0
			+ ";\n"
			+ "uniform vec4 "
			+ ShaderProgramConstants.UNIFORM_COLOR
			+ ";\n"
			+ "uniform float "
			+ UNIFORM_SATURATION
			+ ";\n"
			+ "varying mediump vec2 "
			+ ShaderProgramConstants.VARYING_TEXTURECOORDINATES
			+ ";\n"
			+ "void main() {\n"
			+ "	vec4 colorized = "
			+ ShaderProgramConstants.UNIFORM_COLOR
			+ " * texture2D("
			+ ShaderProgramConstants.UNIFORM_TEXTURE_0
			+ ", "
			+ ShaderProgramConstants.VARYING_TEXTURECOORDINATES
			+ ");\n"
			+ "	vec4 gray = vec4(vec3(dot(colorized.rgb, vec3(.222, .707, .071)) ), colorized.a);\n"
			+ "	gl_FragColor = mix(gray, colorized, "
			+ UNIFORM_SATURATION
			+ ");\n" + "}";

	// ===========================================================
	// Fields
	// ===========================================================

	public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
	public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
	public static int sUniformSaturationLocation = ShaderProgramConstants.LOCATION_INVALID;

	// ===========================================================
	// Constructors
	// ===========================================================

	private PositionTextureCoordinatesUniformColorSaturationShaderProgram() {
		super(
				PositionTextureCoordinatesUniformColorSaturationShaderProgram.VERTEXSHADER,
				PositionTextureCoordinatesUniformColorSaturationShaderProgram.FRAGMENTSHADER);
	}

	public static PositionTextureCoordinatesUniformColorSaturationShaderProgram getInstance() {
		if (PositionTextureCoordinatesUniformColorSaturationShaderProgram.INSTANCE == null) {
			PositionTextureCoordinatesUniformColorSaturationShaderProgram.INSTANCE = new PositionTextureCoordinatesUniformColorSaturationShaderProgram();
		}
		return PositionTextureCoordinatesUniformColorSaturationShaderProgram.INSTANCE;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void link(final GLState pGLState)
			throws ShaderProgramLinkException {
		GLES20.glBindAttribLocation(this.mProgramID,
				ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_POSITION);
		GLES20.glBindAttribLocation(this.mProgramID,
				ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

		super.link(pGLState);

		PositionTextureCoordinatesUniformColorSaturationShaderProgram.sUniformModelViewPositionMatrixLocation = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		PositionTextureCoordinatesUniformColorSaturationShaderProgram.sUniformTexture0Location = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
		PositionTextureCoordinatesUniformColorShaderProgram.sUniformColorLocation = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_COLOR);
		PositionTextureCoordinatesUniformColorSaturationShaderProgram.sUniformSaturationLocation = this
				.getUniformLocation(UNIFORM_SATURATION);
	}

	@Override
	public void bind(final GLState pGLState,
			final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
		GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(
				PositionTextureCoordinatesUniformColorSaturationShaderProgram.sUniformModelViewPositionMatrixLocation,
				1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(
				PositionTextureCoordinatesUniformColorSaturationShaderProgram.sUniformTexture0Location,
				0);
	}

	@Override
	public void unbind(final GLState pGLState) {
		GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

		super.unbind(pGLState);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
