package skyart.skyffti.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Coltan on 2/16/2017.
 * Simple class to check permissions.
 */

public class Permissions {

    public static void check(Activity activity, String permission, int close_on_fail){
        // do we have permission?


            ActivityCompat.requestPermissions(activity,
                    new String[]{permission}, close_on_fail);

    }

}
