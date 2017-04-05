/**
 * Created by Coltan on 3/30/2017.
 *
 * MainBrain is the componet to that controls markers, radiuses, artwork, etc
 *
 *
 *------------------\
 *                   \   Ping ring: Grabs large list of art from server
 *--------------\
 *               \ Radar ring: Places pens on map
 *-----------\
 *            \ Pen Ring: User is close enough to show artwork
 *
 */

package Brain;

import android.location.Location;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import skyart.skyffti.Fragments.Fragment_Maps;
import skyart.skyffti.Fragments.Fragment_Camera;
import skyart.skyffti.R;
import skyart.skyffti.Utils.ResourceLoader;



public class MainBrain {

    private static boolean pingRingCollision; //When the RadarRadius hits the pingRadius

    //Radiuses in Meters
    float dropPenRadius = 3;
    float dropPenTimer = 300; //Minimum time require before next check
    float radarRadius = 30;
    float radarTimer = 500;   //Minimum time require before next check
    float pingRadius= 60;
    float pingTimer = 30000;   //Minimum time require before next check

    String ArtNaming = "artPiece"; //Naming convention for the artPieces. NOT USED
    int ArtNamingID = 0;   //increments as more Art comes in
    int currentID = -1;    // used to know the viewing bitmap
    double currentDist = -1;

    Location pingRingLocation; //Location of the last ping ring.

    static float lastUpdate; //The Timestamp of the last update

    List<Artwork> ArtList;   //Dis is the master list of all the art within the ping radius

    private static MainBrain instance;
    private Context mContext;

    final Bitmap defaultBitmap;

    //FOR TESTING ONLY
    public void loadTestData(){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.splash, options);
        Location loc = new Location("GPS");
        loc.setLatitude(28.53109);
        loc.setLongitude(-81.0509);
        //Fragment_Maps.setArtMarker(loc, "ArtWork1");
        ArtList.add(new Artwork(bitmap, loc, ArtNamingID));
        ArtNamingID++;
        final Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.art1, options);
        Location loc2 = new Location("GPS");
        loc2.setLatitude(28.53109);
        loc2.setLongitude(-81.0508);
        // Fragment_Maps.setArtMarker(loc, "ArtWork2");
        ArtList.add(new Artwork(bitmap2, loc2, ArtNamingID));
        ArtNamingID++;



    }

    public MainBrain(Context m){
        ArtList = new ArrayList<Artwork>();
        instance = this;
        mContext = m;
        loadTestData();

        dropPenRadius = ResourceLoader.readFloatFromResource(mContext, R.raw.pen_radius);
        radarRadius = ResourceLoader.readFloatFromResource(mContext, R.raw.radar_radius);
        pingRadius = ResourceLoader.readFloatFromResource(mContext, R.raw.ping_radius);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.art, options);
    }

    /*
    Calcuates the Distances between two Locations
     */
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;//3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return (dist*1000);
    }

    public static double distFrom(Location myLoc, Location artLoc){
        return (distFrom(myLoc.getLatitude(), myLoc.getLongitude(), artLoc.getLatitude(), artLoc.getLongitude()));
    }

    /*
    Ping Radius is the outer ring.
    Only Updates if the Radar Radius hits the Ping Radius or after Timer or user updates;
     */
    public void checkPingRadius(Location myLocation){

        //TODO Call server

    }


    public void checkRadarRadius(Location myLocation){
        for(Artwork a : ArtList) {
            double dist = distFrom(myLocation, a.getLoc());
            if (dist < radarRadius) {
                if (!a.hasMarker) {
                    a.setMarker();
                }
            }else if(a.hasMarker){
                a.removeMarker();
            }
        }
    }
    public void checkPenDropRadius(Location myLocation){
        for(Artwork a : ArtList) {
            double dist = distFrom(myLocation, a.getLoc());
            if (dist < dropPenRadius) {
                if (a.getArtID() != currentID && (dist < currentDist || currentDist == -1)) {
                    Fragment_Camera.loadArt(a);
                    currentID = a.getArtID();
                    currentDist = dist;
                }
            }
            if(a.getArtID() == currentID && dist > dropPenRadius){
                currentID = -1;
                currentDist = -1;
            }

        }
    }

    public static void LocationUpdate(Location location) {

        float timeDiff = System.currentTimeMillis() - lastUpdate;

        //Ping

        instance.checkPingRadius(location);
        instance.checkPenDropRadius(location);
        instance.checkRadarRadius(location);
        lastUpdate = System.currentTimeMillis();
        pingRingCollision = false;


    }

    public static void create(Context mContext) {
        if(instance == null){
            instance = new MainBrain(mContext);
        }
    }

    public static void createArtwork(Bitmap bitmap, Location loc){
        Artwork art = new Artwork(bitmap, loc, instance.ArtNamingID);
        instance.ArtList.add(art);

    }
    boolean fromDrawing = false;
    Bitmap drawing;
    public static void setBitmap(Bitmap bitmap){
        instance.fromDrawing = true;
        instance.drawing = bitmap;
    }
    public static Bitmap getCurrent() {
        if (instance.ArtList.size() > 0 && instance.currentID != -1)
            return instance.ArtList.get(instance.currentID).getBitmap();
        else
            return null;
    }
}
