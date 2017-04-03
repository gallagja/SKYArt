package Renderer;

import android.opengl.Matrix;

/**
 * Created by ajluntz on 2/8/17.
 */

public class Camera extends Entity {
    private float[] mProjection;
    private static Camera instance;

    Camera() {
        super();
            instance = this;
        mProjection = new float[16];
    }

    public float[] getProjection() {
        return mProjection;
    }

    public void setProjection(float fov, int width, int height) {
        float aspect = ((float)width) / ((float)height);
        float nearz = 1.0f;
        float farz = 10000.0f;

        Matrix.perspectiveM(mProjection, 0,
                fov, aspect, nearz, farz);
    }

    public static Camera getInstance() {
        return instance;
    }
}