package skyart.skyffti.database;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;

/**
 * Created by Coltan on 3/30/2017.
 */

public class databaseUtils {

    //Base URL for server
    public static String BASE_URL = "http://skyffiti.cf/76ee3de97a1b8b903319b7c013d8c877/img.php";

    private static void imageUpload(final String imagePath) {

        //SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, BASE_URL,
        //        new Response.Listener<String>() {
        //            @Override
        //            public void onResponse(String response) {
        //                Log.d("Response", response);
        //                try {
        //                    JSONObject jObj = new JSONObject(response);
        //                    String message = jObj.getString("message");
//
        //                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//
        //                } catch (JSONException e) {
        //                    // JSON error
        //                    e.printStackTrace();
        //                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        //                }
        //            }
        //        }, new Response.ErrorListener() {
        //    @Override
        //    public void onErrorResponse(VolleyError error) {
        //        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        //    }
        //});
//
        //smr.addFile("file", imagePath);
        //MyApplication.getInstance().addToRequestQueue(smr);
//
    }
}
