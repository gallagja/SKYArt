package Renderer;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by ajluntz on 2/8/17.
 */

public class GLErrors {

    public static void checkErrors(String context) {
        int err = GLES20.glGetError();
        if (err == GLES20.GL_NO_ERROR)
            return;

        Log.e("GLError", "Error in " + context + ": " + GLErrors.toString(err));
    }

    public static String toString(int err) {
        switch (err) {
            default:
            case GLES20.GL_NO_ERROR:
                return "no error";

            case GLES20.GL_INVALID_ENUM:
                return "invalid enum";
            case GLES20.GL_INVALID_VALUE:
                return "invalid value";
            case GLES20.GL_INVALID_OPERATION:
                return "invalid value";
//            case GLES20.GL_STACK_OVERFLOW:
//            case GLES20.GL_STACK_UNDERFLOW:
            case GLES20.GL_OUT_OF_MEMORY:
                return "out of memory";
            case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
                return "invalid framebuffer operation";
//            case GLES20.GL_CONTEXT_LOST:
//            case GLES20.GL_TABLE_TOO_LARGE1:
        }
    }
}