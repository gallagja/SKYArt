package skyart.skyffti;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import skyart.skyffti.Utils.Permissions;

/**
 * Created by Coltan on 2/17/2017.
 */

public class Splash extends Activity {


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public boolean close_on_fail = true;
    public int locationEnabled;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);
        Permissions.check(this, Manifest.permission.CAMERA, 0);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        locationEnabled);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {

                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                            Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                            Splash.this.startActivity(mainIntent);
                            Splash.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);


                break;


            }
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                            Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                            Splash.this.startActivity(mainIntent);
                            Splash.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);

                }else{
                    finish();
                }
                break;

        }
    }

}
