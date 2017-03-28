package skyart.skyffti;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Toast;

import Painter.CanvasDrawable;
import Renderer.ARSurfaceView;
import Renderer.Camera;
import Renderer.SensorEntityController;


public class MainActivity extends Activity{


    private static final int SPLASH_DISPLAY_LENGTH = 100;
    private Camera mCamera;
    private CameraHandler mCameraHandler;
    private ARSurfaceView arView;

    private static MainActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
    }

    private void init(){

        mCameraHandler = new CameraHandler(this, (TextureView) this.findViewById(R.id.textureView));
        arView = new ARSurfaceView(this);
        addContentView(arView, this.findViewById(R.id.textureView).getLayoutParams());

        mCamera = arView.getmRenderer().getCamera();
        SensorControl.initInstance(this);
        SensorEntityController camController = new SensorEntityController();
        mCamera.setController(camController);

        CanvasDrawable.setContext(this);
        CanvasDrawable canvas = new CanvasDrawable();

        arView.getmRenderer().addEntity("CanvasDrawable", canvas);

        instance = this;
    }

    public static void makeToast(String text){
        Toast.makeText(instance, text, Toast.LENGTH_SHORT);
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
