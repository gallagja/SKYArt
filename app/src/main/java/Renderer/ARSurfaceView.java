package Renderer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;

/**
 * Created by ajluntz on 2/6/17.
 */

public class ARSurfaceView extends GLSurfaceView {
    GLRenderer mRenderer;

    public ARSurfaceView(@NonNull Context context) {
        super(context);

        super.setEGLContextClientVersion(2);
        super.setEGLConfigChooser(8,8,8,8,16,0);
        super.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        super.setZOrderOnTop(true);

        mRenderer = new GLRenderer(context);
        super.setRenderer(mRenderer);
    }
}