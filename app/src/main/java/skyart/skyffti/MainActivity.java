package skyart.skyffti;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;

import javax.microedition.khronos.opengles.GL10;

import Painter.ARCanvas;
import Renderer.ARSurfaceView;
import Renderer.GLRenderer;
import Renderer.State;


public class MainActivity extends Activity{


    private static final int SPLASH_DISPLAY_LENGTH = 100;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        CameraHandler ch = new CameraHandler(this, (TextureView) this.findViewById(R.id.textureView));
        ARSurfaceView arView = new ARSurfaceView(this);
        addContentView(arView, this.findViewById(R.id.textureView).getLayoutParams());


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
