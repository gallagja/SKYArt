package skyart.skyffti.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Artwork.Artwork;
import Painter.CanvasDrawable;
import Painter.SprayerDrawable;
import Renderer.ARSurfaceView;
import Renderer.Camera;
import Renderer.SensorEntityController;
import skyart.skyffti.CameraHandler;
import skyart.skyffti.R;
import skyart.skyffti.Utils.SensorControl;

/**
 * Created by Coltan on 3/28/2017.
 *
 * This controls the camera UI and all related stuff
 */

public class Fragment_Camera extends Fragment {


    //Camera Junk
    public static Context context;
    private static CameraHandler mCameraHandler;
    private static ARSurfaceView arView;
    private static Camera mCamera;
    private static Fragment_Camera instance;
    CanvasDrawable canvas;

    //Touch Control Stuff
    private int lastTouchX;
    private int lastTouchY;
    private int touchDistanceY = 0;
    private boolean touchDown = false; /// for your mom

    //Viewer vs Painter mode
    private boolean painterMode = true;

    //The UI Components
    TextView textView;
    ImageView imageView;

    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter;

    public Fragment_Camera() {
        instance = this;
    }

    /**
     * Returns a new instance of this fragment
     */
    public static Fragment_Camera newInstance(int sectionNumber) {
        Fragment_Camera fragment = new Fragment_Camera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    //Creates the layout and everything here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //rootView is our Main View
        View rootView = inflater.inflate(R.layout.cam_frag, container, false);



        mCameraHandler = new CameraHandler(rootView.getContext(), (TextureView) rootView.findViewById(R.id.textureView));
        arView = (ARSurfaceView) rootView.findViewById(R.id.arView);
        //arView = new ARSurfaceView(rootView.getContext());
        //this.getActivity().addContentView(arView, rootView.findViewById(R.id.textureView).getLayoutParams());

        mCamera = arView.getmRenderer().getCamera();
        SensorControl.initInstance(this.getActivity());
        SensorEntityController camController = new SensorEntityController();
        mCamera.setController(camController);


        ///What Shall We Render???
        //Square.setContext(rootView.getContext());
        //Square canvas = new Square();
        CanvasDrawable.setContext(rootView.getContext());
        canvas = new CanvasDrawable();
        canvas.enableViewer(true);
        SprayerDrawable.setContext(rootView.getContext());
        SprayerDrawable sprayerDrawable = new SprayerDrawable();
        //PainterTest.setContext(rootView.getContext());
        //final PainterTest painter = new PainterTest();


        //Color Picker Dialog
        final Dialog d = new Dialog(this.getContext());
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Fragment_Color fc = Fragment_Color.newInstance(2);
        d.setContentView(fc.onCreateView(inflater, null, savedInstanceState));
        FloatingActionButton picker = (FloatingActionButton) rootView.findViewById(R.id.floatingColorPicker);
        picker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                d.show();
                return false;
            }
        });

        //Viewer vs Painter switch
        FloatingActionButton viewSwitchButton = (FloatingActionButton) rootView.findViewById(R.id.floatingViewSwitchButton);
        viewSwitchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        canvas.enableViewer(painterMode);
                        painterMode = !painterMode;
                        break;
                }

                return false;
            }
        });


        arView.getmRenderer().addEntity("CanvasDrawable", canvas);
//        arView.getmRenderer().addEntity("SprayerDrawable", sprayerDrawable);
//        arView.getmRenderer().addEntity("Painter", painter);
        rootView.setOnTouchListener(touchMe);


        textView = (TextView) rootView.findViewById(R.id.textGPSView);
        //imageView = (ImageView) rootView.findViewById(R.id.imageView);
        return rootView;
    }


    //The Touch listener
    private View.OnTouchListener touchMe = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX(); //gets the touch x
            int y = (int) event.getY(); //gets the touch y
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("TAG", "touched down");
                    touchDown = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchDistanceY = lastTouchY - y;

                    lastTouchX = x;
                    lastTouchY = y;

                   // Log.i("TAG", "moving: (" + touchDistanceY + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    touchDown = false;
                    touchDistanceY = 0;
                    //Log.i("TAG", "touched up");
                    break;
            }

            //This is a swipe that does nothing
            if (touchDistanceY > 30 && touchDown) {
                //SensorEntityController.setRot();
                touchDown = false;
            }

            return true;
        }
    };
    public static void updateGPS(Location location){

        //instance.textView.setText(location.getLatitude() + " : " + location.getLongitude());
    }

    public static void loadArt(Artwork art){
        instance.canvas.getTexture().send(art.getBitmap());

    }

    public static void sendBitmap(Bitmap bitmap){
        instance.canvas.getTexture().send(bitmap);
    }

}





