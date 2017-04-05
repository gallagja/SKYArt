package skyart.skyffti;

import Brain.Artwork;
import skyart.skyffti.database.databaseUtils;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;

import Brain.MainBrain;
import Renderer.ARSurfaceView;
import Renderer.Camera;
import Renderer.SensorEntityController;
import skyart.skyffti.Fragments.SectionsPagerAdapter;
import android.location.Location;


public class MainActivity extends FragmentActivity{

    //Tab View stuff
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static MainActivity instance;

    // DB Test
    private ArrayList<Artwork> artworkList;
    private String output = "Empty";
    private TextView outputBox;

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

        MainBrain.create(getApplication().getApplicationContext());
        instance = this;


        // DATABASE TESTER (making Toasts)
        final Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                String loc_lng = "-81.1654281616";
                String loc_lat = "28.5639324188";
                int distance = Integer.MAX_VALUE; //meters

                artworkList = databaseUtils.getNearby(loc_lng, loc_lat, distance);
                String email = databaseUtils.check_user("user", "password");

                String output = "[\n";
                for(int i=0; i<artworkList.size(); i++){
                    output += "{ "
                            + artworkList.get(i).getArtID() + "; "
                            + artworkList.get(i).getLoc().getLatitude() + ", "
                            + artworkList.get(i).getLoc().getLongitude() + " }\n";
                }
                output += "]";
                makeToast( email + " : " + output );

            }
        });


    }

    /*
    Can make a toast from any class
     */
    public static void makeToast(String text){
        Toast.makeText(instance, text, Toast.LENGTH_SHORT).show();

    }
}