package skyart.skyffti;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Toast;

import Renderer.ARSurfaceView;


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
