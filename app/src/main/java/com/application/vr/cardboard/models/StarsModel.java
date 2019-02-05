package com.application.vr.cardboard.models;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.application.vr.cardboard.R;
import com.application.vr.cardboard.file_utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform4f;

public class StarsModel {
    private FloatBuffer vertexData;

    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float[] mModelMatrix = new float[16];
    private Random random = new Random();
    private int stars_amount = 2000;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public StarsModel(Context context) {
        prepareData();
        // Prepare shaders and OpenGL program.
        int vertexShaderId = ShaderUtils.createShader(context, GLES30.GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GLES30.GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        // Create empty OpenGL Program.
        mProgram = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     */
    public void draw(float[] mVPMatrix) {
        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Enable vertex array
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
        // We can transform, rotate or scale the mModelMatrix set it as an identity matrix.

        //Drawing of the model
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 0, vertexData);
        drawModel();
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mModelMatrix, 0);
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    private void prepareData() {
        float[] vertex = new float[stars_amount*3];

        for (int i=0; i<stars_amount; i+=3) {
            float x = (random.nextFloat());
            float y = (random.nextFloat());
            float z = (random.nextFloat());
            float r = (float) Math.sqrt(x*x + y*y + z*z);

            x /= r;
            y /= r;
            z /= r;

            boolean isNeg1 = random.nextBoolean();
            if (isNeg1) vertex[i] = x*-1000;
            else vertex[i] = x*1000;
            boolean isNeg2 = random.nextBoolean();
            if (isNeg2) vertex[i+1] = y*-1000;
            else vertex[i+1] = y*1000;
            boolean isNeg3 = random.nextBoolean();
            if (isNeg3) vertex[i+2] = z*-1000;
            else vertex[i+2] = z*1000;
        }

        vertexData = ByteBuffer
                .allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertex);
        vertexData.position(0);
    }

    private void drawModel() {
        glLineWidth(1);
        glUniform4f(mColorHandle, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 0, stars_amount);
    }
}
