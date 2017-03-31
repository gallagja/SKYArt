package Painter;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import Renderer.Drawable;
import Renderer.State;
import Renderer.VertexBuffer;
import skyart.skyffti.Fragments.Fragment_Color;
import skyart.skyffti.R;
import skyart.skyffti.Utils.ResourceLoader;

/**
 * Created by ajluntz on 3/28/17.
 */

public class CanvasDrawable extends Drawable {
    public class CanvasShaderData {
        public int sprayerHalfAngle;
        public int sprayerColor;
        public int colorLoc;
    }

    private VertexBuffer mVertexBuffer;
    private CanvasShaderData mShaderData;

    public CanvasDrawable() {
        CanvasDrawable.setup();

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

        mShaderData.sprayerHalfAngle = GLES20.glGetUniformLocation(this.getState().getProgram(), "vSprayerHalfAngle");
        mShaderData.sprayerColor = GLES20.glGetUniformLocation(this.getState().getProgram(), "vSprayerColor");
        mShaderData.colorLoc = GLES20.glGetUniformLocation(this.getState().getProgram(), "vColor");

        mVertexBuffer.create();
        mVertexBuffer.setAttribute(this.getState(), "vertexPosition");
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
        final float [] vColor = {.0f, .0f, .0f, .5f};
        GLES20.glUniform4fv(mShaderData.colorLoc, 1, vColor, 0);

        final float [] vSprayerColor = {
                (float) Fragment_Color.instance.getRed()/255.0f,
                (float) Fragment_Color.instance.getGreen()/255.0f,
                (float) Fragment_Color.instance.getBlue()/255.0f,
                1.0f};
        GLES20.glUniform4fv(mShaderData.sprayerColor, 1, vSprayerColor, 0);

        float [] halfAngle = new float[]{ResourceLoader.readFloatFromResource(mContext, R.raw.sprayer_halfangle)};
        GLES20.glUniform1fv(mShaderData.sprayerHalfAngle, 1, halfAngle, 0);

        mVertexBuffer.draw();
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
                        R.raw.canvas_vert // TODO: needs new shader with textures and shit
                );
        mFragCode =
                ResourceLoader.readTextFileFromRawResource(
                        mContext,
                        R.raw.canvas_frag // TODO: needs new shader with textures and shit
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
            cylinder[i*bitesPerSlice + (slice++)] = halfHeight;
            cylinder[i*bitesPerSlice + (slice++)] = y;

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = -halfHeight;
            cylinder[i*bitesPerSlice + (slice++)] = y;

            x = (float) (radius * Math.cos(nextTheta));
            y = (float) (radius * Math.sin(nextTheta));

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = halfHeight;
            cylinder[i*bitesPerSlice + (slice++)] = y;

            cylinder[i*bitesPerSlice + (slice++)] = x;
            cylinder[i*bitesPerSlice + (slice++)] = -halfHeight;
            cylinder[i*bitesPerSlice + (slice++)] = y;
        }
        return cylinder;
    }

    static private void loadCylinder() {
        float height = ResourceLoader.readFloatFromResource(mContext, R.raw.canvas_height);
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
