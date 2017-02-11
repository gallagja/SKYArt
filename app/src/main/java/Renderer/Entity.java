package Renderer;

import android.content.Context;
import android.opengl.Matrix;

/**
 * Created by ajluntz on 2/8/17.
 */

public class Entity {
    private float[] mTransform;
    private float[] mPos;

    private EntityController mController;

    private static Context mContext;
    public static void setContext(Context context) {
        mContext = context;
    }
    public static Context getContext() {
        return mContext;
    }

    Entity() {
        mTransform = new float[16];
        mPos = new float[3];

        Matrix.setIdentityM(mTransform , 0);
    }

    public void update() {
        if (mController != null) {
            mController.apply(this);
        }
    }

    public void setController(EntityController controller) {
        mController = controller;
    }
    public EntityController getController() {
        return mController;
    }

    public float[] getTransform() {
        return mTransform;
    } // Please do not modify
    public float[] getPosition() {
        return mPos;
    } // Please do not modify

    public void scale(float [] scale) {
        if (scale.length < 3)
            throw new ArrayIndexOutOfBoundsException("Setting the position of Entity fails, scale.length >= 3");

        Matrix.scaleM(mTransform, 0, scale[0], scale[1], scale[2]);
    }

    public void setPosition(float [] pos) {
        if (pos.length < 3)
            throw new ArrayIndexOutOfBoundsException("Setting the position of Entity fails, pos.length >= 3");

        mPos[0] = pos[0];
        mPos[1] = pos[1];
        mPos[2] = pos[2];
        for (int i = 0; i < mPos.length; ++i) {
            mTransform[12 + i] = mPos[i];
        }
    }

    public void translate(float [] offset) {
        if (offset.length < 3)
            throw new ArrayIndexOutOfBoundsException("Translating the position of Entity fails, offset.length >= 3");

        Matrix.translateM(mTransform, 0, offset[0], offset[1], offset[2]);
        for (int i = 0; i < mPos.length; ++i) {
            mPos[i] = mTransform[12 + i];
        }
    }

    public void setRotation(float [] hpr) {
        if (hpr.length < 3)
            throw new ArrayIndexOutOfBoundsException("Setting the rotation of Entity fails, hpr.length >= 3");

        Matrix.setRotateM(mTransform, 0, hpr[0], 0.0f, 0.0f, 1.0f); // heading
        Matrix.setRotateM(mTransform, 0, hpr[1], 0.0f, 1.0f, 0.0f); // pitch
        Matrix.setRotateM(mTransform, 0, hpr[2], 1.0f, 0.0f, 0.0f); // roll
    }

    public void rotate(float [] hpr) {
        if (hpr.length < 3)
            throw new ArrayIndexOutOfBoundsException("Rotating the Drawable fails, hpr.length >= 3");

        Matrix.rotateM(mTransform, 0, hpr[0], 0.0f, 1.0f, 0.0f); // heading
        Matrix.rotateM(mTransform, 0, hpr[1], 1.0f, 0.0f, 0.0f); // pitch
        Matrix.rotateM(mTransform, 0, hpr[2], 0.0f, 0.0f, 1.0f); // roll
    }

    public void lookAt(float [] xyz) {
        if (xyz.length < 3)
            throw new ArrayIndexOutOfBoundsException("lookAt fails, xyz.length >= 3");

        Matrix.setLookAtM(mTransform, 0,
                mPos[0], mPos[1], mPos[2],
                xyz[0], xyz[1], xyz[2],
                // The transform should look like this in cols
                // LEFT UP FORWARD TRANSLATION
                // The last row we should not touch
                mTransform[4], mTransform[5], mTransform[6]);

        for (int i = 0; i < mPos.length; ++i) {
            mPos[i] = mTransform[12 + i];
        }
    }
}