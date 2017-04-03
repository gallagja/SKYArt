package Painter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import Renderer.Drawable;
import Renderer.State;
import Renderer.VertexBuffer;
import skyart.skyffti.R;
import skyart.skyffti.Utils.ResourceLoader;

/**
 * Created by ajluntz on 3/28/17.
 */

public class viewerDrawable extends Drawable {
    public class CanvasShaderData {
        public int colorLoc;
        public int a_Position;
        public int a_TexCorrdinate;
        public int a_Normal;

    }

    private VertexBuffer mVertexBuffer;
    private CanvasShaderData mShaderData;

    final float[] cubeTextureCoordinateData =
            {
                    // Front face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
            };


    /** Store our model data in a float buffer. */
    private final FloatBuffer mCubeTextureCoordinates;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;



    public viewerDrawable() {
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        viewerDrawable.setup();

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

        GLES10.glEnable(GL10.GL_TEXTURE_2D);


        mTextureDataHandle = loadTexture();
        

        mShaderData.colorLoc = GLES20.glGetUniformLocation(this.getState().getProgram(), "vColor");
        mShaderData.a_Position = GLES20.glGetUniformLocation(this.getState().getProgram(), "a_Position");
        mShaderData.a_Normal = GLES20.glGetUniformLocation(this.getState().getProgram(), "a_Normal");
        mShaderData.a_TexCorrdinate = GLES20.glGetUniformLocation(this.getState().getProgram(), "a_TexCorrdinate");

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
        mTextureUniformHandle = GLES20.glGetUniformLocation(this.getState().getProgram(), "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(this.getState().getProgram(), "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        final float [] vColor = {1.0f, 0.0f, 0.0f, 0.25f};
        GLES20.glUniform4fv(mShaderData.colorLoc, 1, vColor, 0);

        mVertexBuffer.draw();
// Pass in the texture coordinate information
        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);
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

    public static int loadTexture()
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.art, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


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
