package skyart.skyffti;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Timer;

import Renderer.ARSurfaceView;
import Renderer.Camera;
import Renderer.SensorEntityController;
import skyart.skyffti.Fragments.SectionsPagerAdapter;


public class MainActivity extends FragmentActivity{

    //Tab View stuff
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static MainActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(2); //sets it to the Camera View

        mViewPager.setOffscreenPageLimit(3);  //So no fragments get deleted from unuse

        instance = this;

    }

    /*
    Can make a toast from any class
     */
    public static void makeToast(String text){
        Toast.makeText(instance, text, Toast.LENGTH_SHORT);
    }
}