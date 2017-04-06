package Painter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import Renderer.BMPTexture;
import Renderer.Drawable;
import Renderer.State;
import Renderer.Texture2D;
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
        public int texLoc;

        public int viewerModeLoc;
    }

    private VertexBuffer mVertexBuffer;
    private VertexBuffer mIndexBuffer;
    private BMPTexture mTexture;
    private CanvasShaderData mShaderData;
    private boolean mViewerMode;

    public CanvasDrawable() {
        CanvasDrawable.setup();

        mShaderData = new CanvasShaderData();
        mVertexBuffer = new VertexBuffer(3);
        mIndexBuffer = new VertexBuffer(2);

        mTexture = new BMPTexture();

        mViewerMode = false;
    }

    public void enableViewer(boolean enabled) {
        mViewerMode = enabled;
    }

    public BMPTexture getTexture() {
        return mTexture;
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
        mShaderData.texLoc = GLES20.glGetUniformLocation(this.getState().getProgram(), "texture");

        mShaderData.viewerModeLoc = GLES20.glGetUniformLocation(this.getState().getProgram(), "viewerMode");

        mVertexBuffer.create();
        mVertexBuffer.setAttribute(this.getState(), "vertexPosition");
        mVertexBuffer.setMode(GLES20.GL_TRIANGLE_STRIP);
        mVertexBuffer.send(mVertices, mVertCount);

        mIndexBuffer.create();
        mIndexBuffer.setAttribute(this.getState(), "texCoord");
        mIndexBuffer.send(mIndices, mVertCount);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.art, options);
        mTexture.setImage(bitmap);
        mTexture.ensure();
    }

    public void delete() {
        mVertexBuffer.delete();
        mIndexBuffer.delete();
        mTexture.delete();
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

        GLES20.glUniform1i(mShaderData.viewerModeLoc, mViewerMode ? 1 : 0);

        int textureSlot = ResourceLoader.readIntFromResource(mContext, R.raw.canvas_texture_slot);
        mTexture.activate(textureSlot);
        GLES20.glUniform1i(mShaderData.texLoc, textureSlot);

        mIndexBuffer.activate();

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
    private static FloatBuffer mIndices;
    private static int mVertCount;

    static private float [] createCylinder(float height, float radius, int slices, float [] coords) {
        float halfHeight = height * 0.5f;

        int floatsPerSlice = 3/*floats per vert*/*2/*verts per rod*/;
        float[] cylinder = new float[floatsPerSlice * (1+slices)];

        for (int i = 0; i <= slices; i++) {
            float theta = (float) (((double) i / (double) slices) * 2.0 * Math.PI + .5 * Math.PI) ;

            int slice = 0;
            float x, y;

            if (coords != null && coords.length >= slices) {
                int l = 0, j = (i*2*2);

                coords[j + l++] = (float)i / (float)slices;
                coords[j + l++] = 1.0f;

                coords[j + l++] = (float)i / (float)slices;
                coords[j + l++] = 0.0f;
            }

            x = (float) (radius * Math.cos(theta));
            y = (float) (radius * Math.sin(theta));

            int at = i*floatsPerSlice;
            cylinder[at + (slice++)] = x;
            cylinder[at + (slice++)] = halfHeight;
            cylinder[at + (slice++)] = y;

            cylinder[at + (slice++)] = x;
            cylinder[at + (slice++)] = -halfHeight;
            cylinder[at + (slice++)] = y;
        }
        return cylinder;
    }

    static private void loadCylinder() {
        float height = ResourceLoader.readFloatFromResource(mContext, R.raw.canvas_height);
        float radius = ResourceLoader.readFloatFromResource(mContext, R.raw.canvas_radius);
        int slices = ResourceLoader.readIntFromResource  (mContext, R.raw.canvas_cylinder_slices);

        float [] coords = new float [(1+slices) * 2*2];
        float [] verts = createCylinder(height, radius, slices, coords);
        mVertCount = verts.length / 3;

        ByteBuffer vb = ByteBuffer.allocateDirect(verts.length * 4 /*bytes per float*/);
        vb.order(ByteOrder.nativeOrder());
        mVertices = vb.asFloatBuffer();
        mVertices.position(0);
        mVertices.put(verts);
        mVertices.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(coords.length * 4 /*bytes per float*/);
        ib.order(ByteOrder.nativeOrder());
        mIndices = ib.asFloatBuffer();
        mIndices.position(0);
        mIndices.put(coords);
        mIndices.position(0);

        Log.d("CanvasDrawable", "Height: " + height);
        Log.d("CanvasDrawable", "Radius: " + radius);
        Log.d("CanvasDrawable", "Slices: " + slices);
    }
}