package skyart.skyffti;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajluntz on 1/27/17.
 */

public class CameraHandler implements TextureView.SurfaceTextureListener
{
    final private Context mContext;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private CameraDevice mCameraDevice;
    private TextureView mTextureView;

    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    public CameraHandler(@NonNull Context context, @NonNull TextureView textureView) {
        mContext = context;
        mTextureView = textureView;

        mTextureView.setSurfaceTextureListener(this);
    }

    private void initPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();

            // Output surface using the texture from the view handed to us
            Surface surface = new Surface(texture);

            // Set up the request with the target surface created from the texture
            mPreviewRequestBuilder =
                    mCameraDevice.createCaptureRequest(
                            CameraDevice.TEMPLATE_PREVIEW
                    );
            mPreviewRequestBuilder.addTarget(surface);

            // Create session
            mCameraDevice.createCaptureSession(
                    Arrays.asList(surface),
                    mCaptureSessionStateCallback,
                    null
            );
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private class BackgroundHandler {
        private HandlerThread mBackgroundThread;
        private Handler mBackgroundHandler;

        Handler getHandler() {
            return mBackgroundHandler;
        }

        public BackgroundHandler() {
            mBackgroundHandler = null;
            mBackgroundThread = null;
        }

        public void start() {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
        public void stop() {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private BackgroundHandler mBackgroundHandler;

    private Size chooseSize(Size[] choices, int maxWidth, int maxHeight) {
        Size curr = new Size(0,0);

        for (Size option : choices) {
            // Small enough
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight) {
                if (curr.getWidth() == 0 || curr.getHeight() == 0) {
                    curr = option;
                }
                // Choose the biggest and badest
                else if (curr.getHeight() < option.getHeight() &&
                        curr.getWidth()  < option.getWidth()) {
                    curr = option;
                }
            }
        }

        // If the currently picked size is good, then go with it,
        //  otherwise just pick the first size. Something is better
        //  than nothing?
        if (curr.getWidth() == 0 || curr.getHeight() == 0) {
            return choices[0];
        } else {
            return curr;
        }
    }

    private void configureTextureView(Size cameraSize) {
        int xoff = 0;
        int yoff = 0;
        float scaleToWidth;
        float scaleToHeight;

        float camHeight = (float)cameraSize.getHeight();
        float camWidth  = (float)cameraSize.getHeight();
        float texHeight = (float)mTextureView.getHeight();
        float texWidth  = (float)mTextureView.getWidth();

        // We need to keep the camera stream aspect, otherwise things will look weird...
        float aspect = camWidth / camHeight;
        float texAspect = texWidth / texHeight;

        // This gets a little funky, so we want to figure out exactly what stretching
        //  is needed to make the streaming camera look right.
        scaleToWidth = Math.max(1.0f / texAspect, 1.0f / aspect);
        if (mTextureView.getHeight() > mTextureView.getWidth()) {
            scaleToHeight = 1.0f;

            float max = Math.max(texWidth*scaleToWidth, texWidth);
            float min = Math.min(texWidth*scaleToWidth, texWidth);
            xoff = (int) ((min - max) / 2.0f);
        } else {
            scaleToHeight = Math.max(texAspect, aspect);

            float max = Math.max(texHeight*scaleToHeight, texHeight);
            float min = Math.min(texHeight*scaleToHeight, texHeight);
            yoff = (int) ((min - max) / 2.0f);
        }

        Log.d("CameraHandler", "texture aspect: " + texAspect);
        Log.d("CameraHandler", "camera  aspect: " + aspect);

        Log.d("CameraHandler", "setScale("+ scaleToWidth + ", " + scaleToHeight +")");
        Log.d("CameraHandler", "translate(" + xoff + ", " + yoff + ")");

        Matrix xform = new Matrix();
        mTextureView.getTransform(xform);
        xform.setScale(
                scaleToWidth,
                scaleToHeight
        );
        xform.postTranslate(xoff, yoff);
        mTextureView.setTransform(xform);
    }

    private Size mCamSize;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // TODO: We need to request the permissions, I don't think it matters where this happens but I'm a fan of containment.
            Log.e("CameraHandler", "Needs camera permissions");
            return;
        }

        mBackgroundHandler = new BackgroundHandler();
        mBackgroundHandler.start();

        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics =
                        manager.getCameraCharacteristics(cameraId);

                Integer facing =
                        characteristics.get(CameraCharacteristics.LENS_FACING);

                // If it's a bad facing or when the lens is not facing back we should continue
                //  looking through our cameras
                if (facing == null || facing != CameraCharacteristics.LENS_FACING_BACK)
                    continue;

                // So here we can get the sizes of our camera streams
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null)
                    continue;

                Size optSize = chooseSize(map.getOutputSizes(SurfaceTexture.class), 10000, 10000);

                mCamSize = optSize;
                this.configureTextureView(mCamSize);

                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Time out waiting to lock camera opening");
                }
                manager.openCamera(
                        cameraId,
                        mCameraStateCallback,
                        mBackgroundHandler.getHandler()
                );
                break;
            }
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
//            mContext.showToast("CameraAPI2 not supported!!!");
//            mContext.finish();
        }
        catch (SecurityException e) {
            e.printStackTrace();
//            mActivity.showToast("Application needs camera permissions!");
//            mContext.finish();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mCamSize == null) {
            this.configureTextureView(new Size(width, height));
        } else {
            this.configureTextureView(mCamSize);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.stop();
            mBackgroundHandler = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    private CameraCaptureSession.StateCallback mCaptureSessionStateCallback =
            new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // The camera closed
                    if (mCameraDevice == null)
                        return;

                    mCaptureSession = cameraCaptureSession;
                    try {
                        // Continuous auto focus
                        mPreviewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        );

                        // Start displaying the camera preview
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        mCaptureSession.setRepeatingRequest(
                                mPreviewRequest,
                                null,
                                mBackgroundHandler.getHandler()
                        );
                    }
                    catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }

            };

    private final CameraDevice.StateCallback mCameraStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraOpenCloseLock.release();
                    mCameraDevice = camera;
                    initPreviewSession();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    mCameraOpenCloseLock.release();
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    mCameraOpenCloseLock.release();
                    camera.close();
                    mCameraDevice = null;
//                    mActivity.showToast("Camera device had error");
//                    mActivity.finish();
                }
            };
}