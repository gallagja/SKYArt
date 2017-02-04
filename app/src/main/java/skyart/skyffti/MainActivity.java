package skyart.skyffti;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.camera2.*;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;

import javax.microedition.khronos.opengles.GL10;


public class MainActivity extends Activity{

    private myGLView glView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SurfaceView cameraView = new SurfaceView(this);

        //SimpleRender sr = new SimpleRender();
        GLSurfaceView glView = (GLSurfaceView) this.findViewById(R.id.glView2);           // Allocate a GLSurfaceView
        CameraHandler ch = new CameraHandler(this, (TextureView)this.findViewById(R.id.textureView));

        //Need this crap to make it transparent
        glView.setZOrderOnTop(true);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.RGBA_8888);
        glView.setRenderer(new MyGLRenderer(this)); // Use a custom renderer
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);



    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public void showToast(String s) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }
}
