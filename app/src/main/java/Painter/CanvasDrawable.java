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
import java.nio.IntBuffer;

import Brain.Artwork;
import Brain.MainBrain;
import Renderer.Drawable;
import Renderer.GLErrors;
import Renderer.State;
import Renderer.Texture2D;
import Renderer.VertexBuffer;
import skyart.skyffti.Fragments.Fragment_Color;
import skyart.skyffti.MainActivity;
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
    private Texture2D mTexture;
    private CanvasShaderData mShaderData;
    private boolean mViewerMode;

    public CanvasDrawable() {
        CanvasDrawable.setup();

        mShaderData = new CanvasShaderData();
        mVertexBuffer = new VertexBuffer(3);
        mIndexBuffer = new VertexBuffer(2);

        mTexture = new Texture2D();

        mViewerMode = false;
    }

    public void enableViewer(boolean enabled) {
        mViewerMode = enabled;
    }

    public Texture2D getTexture() {
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
        mTexture.create();
        mTexture.bind();
        mTexture.send(bitmap);
        bitmap.recycle();

         fbo = new FrameBuffer( 1024, 1024 );
        final int[] texIds = new int[1];
        texid = createBlankTextures( texIds, 1024, 1024 );

    }
    FrameBuffer fbo;
    int texid;
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





        fbo.bind( texid, 1024, 1024 );

//        if(!mViewerMode)
//            mTexture.send(SavePixels(0, 1, 1024, 1024));
//texture should turn blue here since in the bind() function I placed a glClear with blue
        fbo.unbind();


        Bitmap art = MainBrain.getCurrent();
        if (art != null)
            mTexture.send(art);
//
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

    public static Bitmap SavePixels(int x, int y, int w, int h)
    {
        int b[]=new int[w*(y+h)];
        int bt[]=new int[w*h];
        IntBuffer ib=IntBuffer.wrap(b);
        ib.position(0);
        GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);

        for(int i=0, k=0; i<h; i++, k++)
        {
            for(int j=0; j<w; j++)
            {
                int pix=b[i*w+j];
                int pb=(pix>>16)&0xff;
                int pr=(pix<<16)&0x00ff0000;
                int pix1=(pix&0xff00ff00) | pr | pb;
                bt[(h-k-1)*w+j]=pix1;
            }
        }


        Bitmap sb=Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;
    }

    public int createBlankTextures(int[] texIds, int width, int height ){
        GLES20.glGenTextures( texIds.length, texIds, 0 );
        for( int i=0; i<texIds.length; i++ ){
            GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, texIds[i] );
            GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
            GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );
            GLES20.glTexImage2D( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null );
            GLES20.glGenerateMipmap( GLES20.GL_TEXTURE_2D );
        }
        return texIds[0];
    }

    public static class FrameBuffer{

        private final int width;
        private final int height;
        private final int[] fboId = new int[1];
        private final int[] renId = new int[1];

        public FrameBuffer( int Width, int Height ){

            width = Width;
            height = Height;

            GLES20.glGenFramebuffers( 1, fboId, 0 );
            GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, fboId[0] );
            GLES20.glFramebufferRenderbuffer( GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renId[0] );

            GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0);
        }

        public void bind( int texId, int texWidth, int texHeight ){
            GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, fboId[0] );
            GLES20.glFramebufferTexture2D( GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texId, 0 );
        }

        public void unbind(){
            GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
        }
    }
}
