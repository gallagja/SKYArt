package Renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by ajluntz on 2/6/17.
 */

public class Drawable extends Entity {
    private State mState;
    private float mViewMatrix[];

    private int mModelViewHandle;
    private int mProjectionHandle;

    public Drawable() {
        super();
        mState = null;
        mViewMatrix = new float[16];

        mModelViewHandle = -1;
        mProjectionHandle = -1;
    }

    // A safe place to initialize everything GL
    // THIS NEEDS A STATE
    public void init() {
        assert mState != null;
        if (mModelViewHandle == -1 || mProjectionHandle == -1) {
            mModelViewHandle = GLES20.glGetUniformLocation(this.getState().getProgram(), "vModelView");
            GLErrors.checkErrors("Drawable.draw().glGetUniformLocation(vModelView)");

            mProjectionHandle = GLES20.glGetUniformLocation(this.getState().getProgram(), "vProjection");
            GLErrors.checkErrors("Drawable.draw().glGetUniformLocation(vProjection)");
        }
    }
    // A safe place to delete GL resources
    public void delete() {
        if (mState != null) {
            mState.delete();
        }
    }

    public void draw() {
        if (mState == null || !mState.isValid()) {
            Log.e("Drawable.draw()", "Bad state, cannot draw");
            return;
        }

        mState.use();

        final Camera camera = GLRenderer.getCamera();

        Matrix.invertM(mViewMatrix, 0, camera.getTransform(), 0);

        float [] modelView = new float [16];
        Matrix.multiplyMM(modelView, 0, this.getTransform(), 0 , mViewMatrix, 0);

        GLES20.glUniformMatrix4fv(mModelViewHandle, 1, true, modelView, 0);
        GLErrors.checkErrors("Drawable.draw().glUniformMatrix4fv(mModelViewHandle)");

        GLES20.glUniformMatrix4fv(mProjectionHandle, 1, true, camera.getProjection(), 0);
        GLErrors.checkErrors("Drawable.draw().glUniformMatrix4fv(mProjectionHandle)");
    }

    public void setState(State state) {
        mState = state;
    }

    public State getState() {
        return mState;
    }
}