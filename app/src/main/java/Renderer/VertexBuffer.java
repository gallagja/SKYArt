package Renderer;

import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by ajluntz on 2/8/17.
 *
 * Static Vertex buffer
 */

public class VertexBuffer  {
    int mBufferName;
    int mAttribLoc;
    int mMode;
    int mSize;
    int mVerts;

    int mFloatsPerVert;


    /*
    create();
    setAttribute();
    send();
    setMode();

    while (rendering) {
        state.use();
        ...
        draw();
        ...
    }

    delete();
    */

    VertexBuffer() {
        mFloatsPerVert = 3;
        mBufferName = -1;
        mAttribLoc = -1;
        mMode = GLES20.GL_TRIANGLES;
    }

    VertexBuffer(int floatsPerElement) {
        mFloatsPerVert = floatsPerElement;
        mBufferName = -1;
        mAttribLoc = -1;
        mMode = GLES20.GL_TRIANGLES;
    }

    public boolean isValid() {
        return (mBufferName != -1 && mAttribLoc != -1);
    }

    public void create() {
        mBufferName = VertexBuffer.GenBuffers(1)[0];
    }

    public void delete() {
        DeleteBuffers(mBufferName);
        mBufferName = -1;
        mAttribLoc = -1;
    }

    public void setAttribute(State state, String name) {
        if (state == null || !state.isValid()) {
            throw new RuntimeException("VertexBuffer.setAttribute(): invalid state");
        }

        mAttribLoc = GetAttribLocation(state.getProgram(), name);
    }

    public void send(FloatBuffer buffer, int numVerts) {
        if (isValid()) {
            Log.d("VertexBuffer.send()", "Sending buffer...");
            mVerts = numVerts;
            mSize = numVerts * mFloatsPerVert * 4/*bytes in a float*/;
            VertexBuffer.BufferData(mBufferName, mSize, buffer);
            VertexBuffer.BindBuffer(0);
        } else {
            Log.e("VertexBuffer.send()", "Invalid VertexBuffer, please ensure create() and setAttribute() have been called.");
        }
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setFloatsPerVert(int floats) {
        mFloatsPerVert = floats;
    }

    public void draw() {
        if (isValid()) {
            VertexBuffer.DrawArrays(mMode, mBufferName, mAttribLoc, mFloatsPerVert, mSize);
            VertexBuffer.BindBuffer(0);
        } else {
            Log.e("VertexBuffer.draw()", "Invalid VertexBuffer, please ensure create() and setAttribute() have been called.");
        }
    }


    private static int[] GenBuffers(int count) {
        int buffers[] = new int[count];
        GLES20.glGenBuffers(count, buffers, 0);
        GLErrors.checkErrors("VertexBuffer.GenBuffers()");
        return buffers;
    }

    private static void BindBuffer(int buffer) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer);
        GLErrors.checkErrors("VertexBuffer.BindBuffer()");
    }

    private static int GetAttribLocation(int program, String name) {
        int loc = GLES20.glGetAttribLocation(program, name);
        GLErrors.checkErrors("VertexBuffer.DeleteBuffers()");
        return loc;
    }

    private static void BufferData(int name, int size, java.nio.Buffer buffer) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, name);
        GLErrors.checkErrors("VertexBuffer.glBindBuffer(" + name + ")");

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                size,
                buffer,
                GLES20.GL_STATIC_DRAW);
        GLErrors.checkErrors("VertexBuffer.BufferData()");
    }

    public static void DrawArrays(int mode, int name, int attrib, int elems, int size) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, name);
        GLErrors.checkErrors("VertexBuffer.DrawArrays().glBindBuffer(" + name + ")");

        GLES20.glEnableVertexAttribArray(attrib);
        GLErrors.checkErrors("VertexBuffer.DrawArrays().glEnableVertexAttribArray(" + attrib + ")");

        GLES20.glVertexAttribPointer(attrib, elems, GLES20.GL_FLOAT, false, 0, 0);
        GLErrors.checkErrors("VertexBuffer.DrawArrays().glVertexAttribPointer()");

        GLES20.glDrawArrays(mode, 0, size);
        GLErrors.checkErrors("VertexBuffer.DrawArrays().glDrawArrays()");
    }

    private static void DeleteBuffers(int buffer) {
        int [] buffers = new int [1];
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLErrors.checkErrors("VertexBuffer.DeleteBuffers()");
    }

    private static void DeleteBuffers(int [] buffers) {
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLErrors.checkErrors("VertexBuffer.DeleteBuffers()");
    }
}