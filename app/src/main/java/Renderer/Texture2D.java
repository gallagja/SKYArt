package Renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import org.w3c.dom.Text;

import skyart.skyffti.R;

/**
 * Created by ajluntz on 2/10/17.
 */

public class Texture2D {
    int mHandle;
    int mResource;
    int mSize[];

    public int[] getSize() {
        return mSize;
    }

    public Texture2D(int resource) {
        mResource = resource;
        mHandle = -1;
        mSize = new int [2];
    }

    public void setResource(int resource) {
        mResource = resource;
    }

    public void create() {
        mHandle = Texture2D.loadTexture(Entity.getContext(), mResource, mSize);
    }

    public void delete() {
        Texture2D.DeleteTextures(mHandle);
    }

    public int activate(int index) {
        Texture2D.BindTexture(mHandle);
        Texture2D.ActiveTexture(index);
        return index;
    }

    private static int loadTexture(final Context context, final int resourceId, int size[])
    {
        int textureHandle = Texture2D.GenTextures();

        if (textureHandle != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            String resourceName = String.valueOf(resourceId);
            int checkExistence = context.getResources().getIdentifier(resourceName , "drawable", context.getPackageName());

            Bitmap bitmap;
            if (checkExistence != 0 && resourceId > -1) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            } else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.notfound, options);
            }

            if (bitmap == null) {
                Texture2D.DeleteTextures(textureHandle);
                return -1;
            }

            size[0] = bitmap.getWidth();
            size[1] = bitmap.getHeight();

            // Bind to the texture in OpenGL
            Texture2D.BindTexture(textureHandle);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLErrors.checkErrors("Texture2D.loadTexture().texImage2D()");

            // Unbind our texture
            Texture2D.BindTexture(0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle;
    }

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

    private static void ActiveTexture(int index) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLErrors.checkErrors("Texture2D.ActiveTexture()");
    }

    private static void TexImage2D(int format, int width, int height, java.nio.Buffer pixels) {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexParameteri()");

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexParameteri()");

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
                0/*we don't do levels, maybe we can talk later about doing some sort of LODing*/,
                format,
                width, height,
                0/*borders are stupid*/,
                GLES20.GL_RGBA/*no matter what it was, we want RGBA*/,
                GLES20.GL_UNSIGNED_BYTE/*again just guessing*/,
                pixels);
        GLErrors.checkErrors("Texture2D.TexImage2D().glTexImage2D()");
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
