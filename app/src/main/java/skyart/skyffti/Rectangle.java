package skyart.skyffti;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Coltan on 2/3/2017.
 */

public class Rectangle {

    private FloatBuffer vertexBuffer;   // buffer holding the vertices
    private float vertices[] = {
            -1.3f, -1.3f,  0.0f,  // 0. left-bottom
            1.3f, -1.3f,  0.0f,  // 1. right-bottom
            -1.3f,  1.3f,  0.0f,  // 2. left-top
            1.3f,  1.3f,  0.0f   // 3. right-top
    };

    public Rectangle() {
        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());

        // allocates the memory from the byte buffer
        vertexBuffer = vertexByteBuffer.asFloatBuffer();

        // fill the vertexBuffer with the vertices
        vertexBuffer.put(vertices);

        // set the cursor position to the beginning of the buffer
        vertexBuffer.position(0);

    }


    /** The draw method for the square with the GL context */
    public void draw(javax.microedition.khronos.opengles.GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // set the colour for the square
        gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);  //red

        // Point to our vertex buffer
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        // Draw the vertices as triangle strip
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

}