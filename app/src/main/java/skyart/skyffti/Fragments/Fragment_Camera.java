package skyart.skyffti.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.zip.Inflater;

import Painter.CanvasDrawable;
import Painter.PainterTest;
import Painter.SprayerDrawable;
import Painter.viewerDrawable;
import Renderer.ARSurfaceView;
import Renderer.Camera;
import Renderer.SensorEntityController;
import skyart.skyffti.CameraHandler;
import skyart.skyffti.R;
import skyart.skyffti.Utils.SensorControl;

/**
 * Created by Coltan on 3/28/2017.
 */

public class Fragment_Camera extends FragmentActivity {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    //Camera Junk
    public static Context context;
    private static CameraHandler mCameraHandler;
    private static ARSurfaceView arView;
    private static Camera mCamera;
    private static Fragment_Camera instance;

    private static int lastTouchX;
    private static int lastTouchY;

    public Fragment_Camera() {

        instance = this;
    }


    /**
     * Returns a new instance of this fragment
     */
    public static class PlaceholderFragment extends Fragment {

        public static PlaceholderFragment instance;
        public PlaceholderFragment() {

        }
        /**
         * Returns a new instance of this fragment
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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


            //viewerDrawable.setContext(rootView.getContext());
            //viewerDrawable canvas = new viewerDrawable();
            CanvasDrawable.setContext(rootView.getContext());
            CanvasDrawable canvas = new CanvasDrawable();
            SprayerDrawable.setContext(rootView.getContext());
            SprayerDrawable sprayerDrawable = new SprayerDrawable();
            PainterTest.setContext(rootView.getContext());
            PainterTest painter = new PainterTest();


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



            arView.getmRenderer().addEntity("CanvasDrawable", canvas);
            arView.getmRenderer().addEntity("SprayerDrawable", sprayerDrawable);
            arView.getmRenderer().addEntity("Painter", painter);
            rootView.setOnTouchListener(touchMe);

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
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lastTouchX = x;
                        lastTouchY = y;
                        //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                        break;
                    case MotionEvent.ACTION_UP:
                        //Log.i("TAG", "touched up");
                        break;
                }

                if(lastTouchY < y){
                    SensorEntityController.setRot();
                }

                return true;
            }
        };
    }




}
