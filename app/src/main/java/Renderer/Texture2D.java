package Renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;

import org.w3c.dom.Text;

/**
 * Created by ajluntz on 4/2/17.
 */

public class Texture2D {
    int mHandle;

    public final static int NumTextureSlots = (GLES20.GL_TEXTURE31 - GLES20.GL_TEXTURE0) + 1;

    public Texture2D() {
        mHandle = -1;
    }

    public boolean isValid() {
        return (mHandle > -1);
    }

    public int handle() {
        return mHandle;
    }

    public void create() {
        mHandle = Texture2D.GenTextures();
    }

    public void delete() {
        if (mHandle > 0) {
            Texture2D.DeleteTextures(mHandle);
            mHandle = -1;
        }
    }

    public void send(int format, int width, int height, java.nio.Buffer pixels) {
        Texture2D.TexImage2D(format, width, height, pixels);
    }

    public void send(@NonNull Bitmap image) {
        this.bind();
        Texture2D.TexImage2D(image);
    }

    public void bind() {
        Texture2D.BindTexture(mHandle);
    }

    public void activate(int slot) {
        Texture2D.ActiveTexture(slot, mHandle);
        this.bind();
    }





    private static int activeTextures[];

    private static int GenTextures() {
        int [] handle = new int[1];
        GLES20.glGenTextures(1, handle, 0);
        GLErrors.checkErrors("Texture2D.GenTextures()");
        return handle[0];
    }

    private static void BindTexture(int handle) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
        GLErrors.checkErrors("Texture2D.BindTexture()");
    }

    private static void ActiveTexture(int index, int handle) {
        if (Texture2D.NumTextureSlots > index) {
            if (Texture2D.activeTextures == null) {
                Texture2D.activeTextures = new int [Texture2D.NumTextureSlots];
                for (int i = 0; i < Texture2D.NumTextureSlots; ++i)
                    Texture2D.activeTextures[i] = -1;
            }

//            if (Texture2D.activeTextures[index] != handle) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
                GLErrors.checkErrors("Texture2D.ActiveTexture()");
                Texture2D.activeTextures[index] = handle;
//            }
        }
        else {
            throw new RuntimeException("Texture2D.ActivateTexture(): " + index + " is over the max of " + (Texture2D.NumTextureSlots-1));
        }
    }

    private static void TexImage2D(int format, int width, int height, java.nio.Buffer pixels) {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexParameteri()");

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexParameteri()");

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
                0/*we don't do levels, maybe we can talk later about doing some sort of LODing*/,
                GLES20.GL_RGBA/*no matter what it was, we want RGBA*/,
                width, height,
                0/*borders are stupid*/,
                format,
                GLES20.GL_UNSIGNED_BYTE/*again just guessing*/,
                pixels);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexImage2D()");

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLErrors.checkErrors("Texture2D.TexImage2D().glGenerateMipmap()");
    }

    private static void TexImage2D(Bitmap bitmap) {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexParameteri()");

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexParameteri()");

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLErrors.checkErrors("Texture2D.TexImage2D().glGenerateMipmap()");
    }

    private static void GenerateMipMap() {
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLErrors.checkErrors("Texture2D.GenerateMipMap()");
    }

    private static void DeleteTextures(int handle) {
        int [] handles = new int [1];
        handles [0] = handle;
        GLES20.glDeleteTextures(1, handles, 1);
        GLErrors.checkErrors("Texture2D.DeleteTextures()");
    }
}
