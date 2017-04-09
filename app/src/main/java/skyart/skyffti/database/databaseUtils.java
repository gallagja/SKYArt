package skyart.skyffti.database;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import android.location.Location;
import android.util.Log;

import Artwork.Artwork;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class databaseUtils {
    /**
     * Class Variables
     */
    //Base URL for server
    private final static String URL_Base = "http://162.243.60.44/";
    private final static String URL_User = URL_Base + "dbc/read/user.php";
    private final static String URL_Nearby = URL_Base + "dbc/read/getNearby.php";
    /**
     *
     * @param username: String
     * @param password: String
     * @return email: String
     */
    public static String check_user(String username, String password) {
        // Input: POST params
        ArrayList<String> paramsList = new ArrayList<>();
        // Output: String array of parsed JSON
        ArrayList<String> dataList = new ArrayList<>();

        // POST params
        paramsList.add(URL_User);
        paramsList.add("username");
        paramsList.add(username);
        paramsList.add("password");
        paramsList.add(password);
        String[] params = new String[ paramsList.size() ];
        params = paramsList.toArray(params);

        // Execute HTTPRequest && Get json raw string
        String jsonStr = postRequest( params );

        // Parse JSON String
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String success = jsonObj.getString("success");
                if (success.equals("1")) {

                    return jsonObj.getString("email");

                } else {
                    return jsonObj.getString("status");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     *
     * @param loc_lat: Strign
     * @param loc_lng: String
     * @param distance: int
     * @return List of Art: ArrayList<Artwork>
     */
    public static ArrayList<Artwork> getNearby(String loc_lat, String loc_lng, int distance) {
        // Input: POST params
        ArrayList<String> paramsList = new ArrayList<String>();
        // Output: String array of parsed JSON
        ArrayList<Artwork> artworkList = new ArrayList<Artwork>();

        // POST params
        paramsList.add(URL_Nearby);
        paramsList.add("username");
        paramsList.add("user");
        paramsList.add("loc_lat");
        paramsList.add(loc_lat);
        paramsList.add("loc_lng");
        paramsList.add(loc_lng);
        paramsList.add("distance");
        paramsList.add(Integer.toString(distance));
        String[] params = new String[ paramsList.size() ];
        params = paramsList.toArray(params);

        // Execute HTTPRequest && Get json raw string
        String jsonStr = postRequest( params );

        // Parse JSON String
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String success = jsonObj.getString("success");
                if (success.equals("1")){

                    JSONArray jsonArray = jsonObj.getJSONArray("locations");
                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject locObj = jsonArray.getJSONObject(i);

                        // Create new location for new Art
                        String art_ID = locObj.getString("art_id");
                        Location loc = new Location( art_ID );
                        loc.setLatitude(Double.parseDouble( locObj.getString("loc_lat") ));
                        loc.setLongitude(Double.parseDouble( locObj.getString("loc_lng") ));

                        // Add to artwork List
                        Artwork newArt = new Artwork(null, loc, Integer.parseInt(art_ID));
                        artworkList.add( newArt );
                    }

                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        Log.d("DatabaseUtils", "getNearby: Arrary size:" + artworkList.size());
        return artworkList;
    }

    public static String postRequest(String... params) {
        String jsonStr = null;
        OkHttpPostHandler handler = new OkHttpPostHandler();
        try {
            jsonStr = handler.execute(params).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonStr;
    }

    // HTTP POST REQUEST HANDLER (background thread)
    public static class OkHttpPostHandler extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        public static final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {

            // Initialize Builder (not RequestBody)
            FormBody.Builder builder = new FormBody.Builder();

            // Add POST Params
            for(int i=1; i < params.length-1; i++)
                builder.add( params[i], params[i+1] );

            RequestBody body = builder.build();

            Request request = new Request.Builder()
                    .url(params[0])
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch( Exception e ){
                return "HTTP Request Error";
            }
        }
    }


    private static void imageUpload(final String imagePath) {

        //SimpleMultiPartRequest smr = new SimpleMultiPartRequest(volley.Request.Method.POST, BASE_URL,
        //        new volley.Response.Listener<String>() {
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
        //                    Toast.msakeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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