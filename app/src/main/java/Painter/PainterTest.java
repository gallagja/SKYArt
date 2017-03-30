package Painter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import Renderer.Camera;
import Renderer.Drawable;
import Renderer.Entity;
import Renderer.State;
import Renderer.VertexBuffer;
import skyart.skyffti.R;
import skyart.skyffti.Utils.ResourceLoader;

/**
 * Created by ajluntz on 3/28/17.
 */

public class PainterTest extends Drawable {
    public class CanvasShaderData {
        public int colorLoc;
    }

    private VertexBuffer mVertexBuffer;
    private CanvasShaderData mShaderData;
//    private SprayerEntityController sprayerEntityController;

    public void setTransform(Entity e)
    {
        if (e == null) return;

        super.mTransform = e.getTransform();
        super.mPos = e.getPosition();
    }

    public PainterTest() {
        PainterTest.setup();

        //setController(new SprayerEntityController(this, Camera.getInstance()));
        mShaderData = new CanvasShaderData();
        mVertexBuffer = new VertexBuffer(3);
    }

    @Override
    public void init() {
        State program = new State();
        program.create();
        program.loadShader(GLES20.GL_VERTEX_SHADER, mVertexCode);
        program.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragCode);
        program.finalize();

        super.setState(program);
        super.init();
//        sprayerEntityController = new SprayerEntityController();
//        setController(sprayerEntityController);

        mShaderData.colorLoc = GLES20.glGetUniformLocation(this.getState().getProgram(), "vColor");

        Log.d("PAinter", "init: get state ");
        mVertexBuffer.create();
        mVertexBuffer.setAttribute(this.getState(), "a_Position");
        mVertexBuffer.setAttribute(this.getState(), "a_Color");
        mVertexBuffer.setAttribute(this.getState(), "a_Normal");
        mVertexBuffer.setMode(GLES20.GL_TRIANGLE_STRIP);
        mVertexBuffer.send(mVertices, mVertCount);
    }

    public void delete() {
        mVertexBuffer.delete();
        super.delete();
    }

    public void draw() {
        super.draw();

        // TODO: Load textures
        final float [] vColor = {1.0f, 0.0f, 0.0f, 0.95f};
        GLES20.glUniform4fv(mShaderData.colorLoc, 1, vColor, 0);

        mVertexBuffer.draw();
        drawLight();

    }

    private float[] mMVPMatrix = new float[16];
    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(this.getState().getProgram(), "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(this.getState().getProgram(), "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, 0, 0, 0);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, Camera.getInstance().getProjection(), 0,Camera.getInstance().getTransform(), 0);
        Matrix.multiplyMM(mMVPMatrix, 0, Camera.getInstance().getProjection(), 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }


    public static void setup() {
        if (mVertexCode == null || mFragCode == null)
            loadCode();
        if (mVertices == null)
            loadCylinder();
    }

    private static Context mContext;

    private static String mVertexCode;
    private static String mFragCode;

    public static void setContext(Context context) {
        mContext = context;
    }


    private static void loadCode() {
        if (mContext == null)
            return;

        if (mVertexCode != null &&
            mFragCode   != null )
            return;

        mVertexCode =
                ResourceLoader.readTextFileFromRawResource(
                        mContext,
                        R.raw.light_vert // TODO: needs new shader with textures and shit
                );

        mFragCode =
                ResourceLoader.readTextFileFromRawResource(
                        mContext,
                        R.raw.light_frag // TODO: needs new shader with textures and shit
                );
    }

    private static FloatBuffer mVertices;
    private static int mVertCount;

    static private float [] createCylinder(float height, float radius, int slices) {
        float halfHeight = height * 0.5f;

        int bitesPerSlice = 4*3;
        float[] cylinder = new float[bitesPerSlice * slices];

        for (int i = 0; i < slices; i++) {
            float theta = (float) (((double) i / (double) slices) * 2.0 * Math.PI + .5 * Math.PI) ;
            float nextTheta = (float) (((double) (i + 1) / (double) slices) * 2.0 * Math.PI + .5 * Math.PI);

            int slice = 0;
            float x, y;

            x = (float) (radius * Math.cos(theta));
            y = (float) (radius * Math.sin(theta));

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = y;
            cylinder[i*bitesPerSlice + (slice++)] = -height;

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = y;
            cylinder[i*bitesPerSlice + (slice++)] = height;

            x = (float) (radius * Math.cos(nextTheta));
            y = (float) (radius * Math.sin(nextTheta));

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = y;
            cylinder[i*bitesPerSlice + (slice++)] = -height;

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = y;
            cylinder[i*bitesPerSlice + (slice++)] = height;
        }
        return cylinder;
    }

    static private void loadCylinder() {
        float height = ResourceLoader.readFloatFromResource(mContext, R.raw.sprayer_height);
        float radius = ResourceLoader.readFloatFromResource(mContext, R.raw.canvas_radius);
        int slices = ResourceLoader.readIntFromResource  (mContext, R.raw.canvas_cylinder_slices);

        float [] verts = createCylinder(height, radius, slices);
        mVertCount = verts.length / 3;

        ByteBuffer vb = ByteBuffer.allocateDirect(verts.length * 4 /*bytes per float*/);
        vb.order(ByteOrder.nativeOrder());
        mVertices = vb.asFloatBuffer();
        mVertices.position(0);
        mVertices.put(verts);
        mVertices.position(0);

        Log.d("CanvasDrawable", "Height: " + height);
        Log.d("CanvasDrawable", "Radius: " + radius);
        Log.d("CanvasDrawable", "Slices: " + slices);
    }


}
