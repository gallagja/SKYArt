package skyart.skyffti.Utils;

import android.content.Context;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ajluntz on 2/8/17.
 */

public class ResourceLoader {
    public static String readTextFileFromRawResource(Context context,
                                                     int resourceId)
    {
        InputStream inputStream =
                context.getResources().openRawResource( resourceId );

        InputStreamReader inputStreamReader =
                new InputStreamReader( inputStream );

        BufferedReader bufferedReader =
                new BufferedReader( inputStreamReader );

        String line;
        String contents = new String();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                contents += line + '\n';
            }
        } catch (IOException e) {
            return null;
        }

        return contents;
    }

    public static float readFloatFromResource(Context context,
                                              int resourceId) {
        TypedValue tv = new TypedValue();
        context.getResources().getValue(resourceId, tv, true);
        return tv.getFloat();
    }

    public static int readIntFromResource(Context context,
                                              int resourceId) {
        return context.getResources().getInteger(resourceId);
    }
}