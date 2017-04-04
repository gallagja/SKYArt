package Renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.nio.IntBuffer;

/**
 * Created by ajluntz on 4/4/17.
 */

public class BMPTexture {
    Texture2D   mTex;
    Bitmap      mImg;

    boolean mDirty;

    public BMPTexture() {
        mTex = null;
        mImg = null;
        mDirty = false;
    }

    public BMPTexture(Bitmap bitmap) {
        mTex = null;
        mImg = null;
        mDirty = false;

        this.setImage(bitmap);
    }

    public BMPTexture(Texture2D texture) {
        mTex = null;
        mImg = null;
        mDirty = false;

        this.setTexture(texture);
    }

    public BMPTexture(Texture2D texture, Bitmap bitmap) {
        mTex = null;
        mImg = null;
        mDirty = false;

        this.setTexture(texture);
        this.setImage(bitmap);
    }

    public void ensure() {
        if (mTex == null) {
            mTex = new Texture2D();
            mTex.create();
        }
        if (mDirty &&  mImg != null) {
            mTex.send(mImg);
        }
    }

    public void ensure(boolean sendToCard) {
        if (mTex == null) {
            mTex = new Texture2D();
            mTex.create();
        }
        if (sendToCard && mDirty &&  mImg != null) {
            mTex.send(mImg);
        }
    }

    public void activate(int slot) {
        ensure();
        mTex.activate(slot);
    }

    public void dirty() {
        mDirty = true;
    }

    public boolean isDirty() {
        return mDirty;
    }

    public Bitmap loadToBMP(int x, int y, int w, int h) {
        ensure(false);
        mTex.bind();
        return BMPTexture.SavePixels(x, y, h, w);
    }

    public Bitmap loadToBMP(int w, int h) {
        ensure(false);
        mTex.bind();
        return BMPTexture.SavePixels(0, 0, h, w);
    }

    public Bitmap setImage(Bitmap bitmap) {
        if (bitmap != mImg)
            mDirty = true;

        Bitmap old = mImg;
        mImg = bitmap;
        return old;
    }

    public Bitmap getImage() {
        return mImg;
    }

    public Texture2D setTexture(Texture2D texture) {
        if (mTex != texture)
            mDirty = true;

        Texture2D old = mTex;
        mTex = texture;
        return old;
    }

    public void delete() {
        if (mTex != null) {
            mTex.delete();
        }
        if (mImg != null) {
            mImg.recycle();
        }
        mDirty = false;
    }




    private static Bitmap SavePixels(int x, int y, int w, int h){
        int b[]=new int[w*(y+h)];
        int bt[]=new int[w*h];
        IntBuffer ib = IntBuffer.wrap(b);
        ib.position(0);
        GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);

        for(int i=0, k=0; i<h; i++, k++)
        {//remember, that OpenGL bitmap is incompatible with Android bitmap
            //and so, some correction need.
            for(int j=0; j<w; j++)
            {
                int pix=b[i*w+j];
                int pb=(pix>>16)&0xff;
                int pr=(pix<<16)&0x00ff0000;
                int pix1=(pix&0xff00ff00) | pr | pb;
                bt[(h-k-1)*w+j]=pix1;
            }
        }

        Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;
    }
}
