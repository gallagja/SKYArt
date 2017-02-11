package Renderer;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by ajluntz on 2/6/17.
 *
 * I called this "State" but really it's just a program
 */

public class State {
    private int mProgram;

    /*
    create
    loadShader
    finalize
    delete
     */

    State() {
        mProgram = -1;
    }

    public int getProgram() {
        return mProgram;
    }

    public boolean isValid() {
        return (mProgram >= 0);
    }

    public void create() {
        if (isValid()) {
            this.delete();
        }
        mProgram = State.CreateProgram();
    }
    public void delete() {
        if (!isValid())
            return;
        State.DeleteProgram(mProgram);
        mProgram = -1;
    }

    public void use() {
        if (!isValid()) {
            Log.e("State.use()", "Bad program, cannot use");
            return;
        }
        State.UseProgram(mProgram);
    }

    public void loadShader(int type, String shaderCode){
        if (mProgram < 0) {
            Log.e("State.loadShader", "Bad program, cannot load shader");
            return;
        }
        int shader = State.CreateShader(type);
        if (State.CompileShaderSource(shader, shaderCode)) {
            this.attach(shader);
        } else {
            Log.e("State.loadShader()", "Cannot load shader");
        }
    }

    public void attach(int shader) {
        if (!isValid()) {
            Log.e("State.attach()", "Bad program, cannot attach shader");
            return;
        }
        State.AttachShader(mProgram, shader);
    }

    public void finalize() {
        if (mProgram < 0) {
            Log.e("State.finalize()", "Bad program, cannot link program");
            return;
        }
        if (State.LinkProgram(mProgram)) {
            Log.i("State.finalize()", "Finalized program: " + mProgram);
        } else {
            mProgram = -1;
        }
    }




    private static int CreateProgram() {
        int program = GLES20.glCreateProgram();
        GLErrors.checkErrors("State.CreateProgram()");
        return program;
    }

    private static void UseProgram(int program) {
        GLES20.glUseProgram(program);
        GLErrors.checkErrors("State.UseProgram()");
    }

    private static int CreateShader(int type) {
        int shader = GLES20.glCreateShader(type);
        GLErrors.checkErrors("State.CreateShader()");
        return shader;
    }

    private static boolean CompileShaderSource(int shader, String source) {
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.e("State.loadShader()", GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return false;
        } else {
            return true;
        }
    }

    private static void AttachShader(int program, int shader) {
        GLES20.glAttachShader(program, shader);
        GLErrors.checkErrors("State.AttachShader()");
    }

    private static boolean LinkProgram(int program) {
        GLES20.glLinkProgram(program);
        GLErrors.checkErrors("State.LinkProgram()");

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == 0) {
            Log.e("State.finalize()", "Failed to link program(" + program + "): " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            return false;
        } else {
            return true;
        }
    }

    private static void DeleteProgram(int program) {
        GLES20.glDeleteProgram(program);
        GLErrors.checkErrors("State.DeleteProgram()");
    }


}