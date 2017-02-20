package Renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ajluntz on 2/6/17.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    Map<String, Drawable> mDrawables;
    List<Drawable> mInitQueue;
    List<Drawable> mDeleteQueue;


    Context mContext;

    static Camera mCam;

    public GLRenderer(@NonNull Context context) {
        super();
        mCam = new Camera();

        mContext = context;

        mDrawables = new HashMap<>();
        mInitQueue = new ArrayList<>();
        mDeleteQueue = new ArrayList<>();

    }

    public static Camera getCamera() {
        return mCam;
    }

    public void addEntity(String name, Drawable entity) {
        if (name == null || entity == null) {
            Log.w("GLRenderer", "addEntity attempt failed on bad name or entity");
            return;
        }

        if (mDrawables.get(name) == null) {
            mDrawables.put(name, entity);
            mInitQueue.add(entity);

            Log.i("GLRenderer", "Entity: " + name + " added.");
        } else {
            Log.w("GLRenderer", "addEntity(" + name + ") attempt failed, entity exists");
        }
    }

    public void removeEntity(String name) {
        if (name == null) {
            Log.w("GLRenderer", "removeEntity(" + name + ") attempt failed, bad name");
            return;
        }

        Drawable d = mDrawables.get(name);
        mDrawables.remove(name);
        mDeleteQueue.add(d);
    }

    public void initGL() {
        // Unfortunately if this gets hit, we kinda need to redo all the GL stuff...... at least it seems like it
        for (Map.Entry<String, Drawable> entry : mDrawables.entrySet()) {
            Drawable entity = entry.getValue();
            mDeleteQueue.add(entity);
            mInitQueue.add(entity);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        Log.i("GLRenderer", "Surface created");

        initGL();
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        for (Drawable d : mDeleteQueue) {
            d.delete();
        }
        for (Drawable d : mInitQueue) {
            d.init();
        }
        mDeleteQueue.clear();
        mInitQueue.clear();

        mCam.update();

        for (Map.Entry<String, Drawable> entry : mDrawables.entrySet())
        {
            Drawable entity = entry.getValue();
            if (entity != null) {
                entity.update();

                entity.draw();
            }
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        mCam.setProjection(45.0f, width, height);
        initGL();
    }
}