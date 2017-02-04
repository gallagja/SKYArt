package skyart.skyffti;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajluntz on 1/27/17.
 */

public class CameraHandler implements TextureView.SurfaceTextureListener
{
    final private MainActivity mActivity;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private CameraDevice mCameraDevice;
    private TextureView mTextureView;

    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    CameraHandler(MainActivity activity, @NonNull TextureView textureView) {
        mActivity = activity;
        mTextureView = textureView;

        mTextureView.setSurfaceTextureListener(this);
    }


    private void initPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                mActivity.showToast("Surface Texture was found to be null");
                mActivity.finish();
            }

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

    private Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                   int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();

        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.

//            mActivity.showToast("Could not find optimal preview size");
            return choices[0];

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mBackgroundHandler = new BackgroundHandler();
        mBackgroundHandler.start();

        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics =
                        manager.getCameraCharacteristics(cameraId);

                Integer facing =
                        characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == null || facing != CameraCharacteristics.LENS_FACING_BACK)
                    continue;

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null)
                    continue;

                Display display = mActivity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                Size optSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        mTextureView.getWidth(), mTextureView.getHeight(),
                        size.x, size.y,
                        new Size(mTextureView.getWidth(), mTextureView.getHeight()));

                float scaleToHeight;
                float aspectRatio = (float) optSize.getHeight() / optSize.getWidth();
                int xoff=0, yoff=0;

                if (optSize.getHeight() > mTextureView.getHeight()) {
                    scaleToHeight = (aspectRatio*optSize.getHeight()) / mTextureView.getHeight();
                    xoff = (int) (mTextureView.getWidth() - optSize.getWidth()) / 2 + mTextureView.getWidth();
                    yoff = 0; //(int)(aspectRatio*(optSize.getHeight() - mTextureView.getHeight())) / 2;
                }
                else {
                    scaleToHeight = (float) mTextureView.getHeight() / optSize.getHeight();
                }
                mActivity.showToast("xOff(" + xoff + ")");

                Matrix xform = new Matrix();
                mTextureView.getTransform(xform);
                xform.setScale(
                        scaleToHeight * (1.0f/(aspectRatio)),
                        scaleToHeight * aspectRatio
                );
                xform.postTranslate(xoff, yoff);
                mTextureView.setTransform(xform);

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
            mActivity.showToast("CameraAPI2 not supported!!!");
            mActivity.finish();
        }
        catch (SecurityException e) {
            mActivity.showToast("Application needs camera permissions!");
            mActivity.finish();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mBackgroundHandler.stop();
        mBackgroundHandler = null;
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
                    mActivity.showToast("Camera device had error");
                    mActivity.finish();
                }
            };
}
