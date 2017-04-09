/**
 * Created by Coltan on 3/30/2017.
 *
 * ArtworkController is the componet to that controls markers, radiuses, artwork, etc
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

package Artwork;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import skyart.skyffti.Fragments.Fragment_Camera;
import skyart.skyffti.MainActivity;
import skyart.skyffti.R;
import skyart.skyffti.Utils.ResourceLoader;
import skyart.skyffti.database.databaseUtils;


public class ArtworkController {

    private static boolean pingRingCollision; //When the RadarRadius hits the pingRadius

    //Radiuses in Meters
    private float dropPenRadius = 3;
    private float dropPenTimer = 300; //Minimum time require before next check
    private float radarRadius = 30;
    private float radarTimer = 500;   //Minimum time require before next check
    private float pingRadius= 6000;
    private float pingTimer = 60000;   //Minimum time require before next check

    private String ArtNaming = "artPiece"; //Naming convention for the artPieces. NOT USED
    private int ArtNamingID = 0;   //increments as more Art comes in
    private int currentID = -1;    // used to know the viewing bitmap
    private double currentDist = -1;

    private Location pingRingLocation; //Location of the last ping ring.

    private int lastUpdate; //The Timestamp of the last update

    private List<Artwork> ArtList;   //Dis is the master list of all the art within the ping radius

    private static ArtworkController instance;
    private Context mContext;

    private final Bitmap defaultBitmap;

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

    public ArtworkController(Context m){
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
        lastUpdate = (int)System.currentTimeMillis();
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

        for(Artwork a : ArtList) {
            if(a.hasMarker)
            a.removeMarker();
            a = null;
        }

        ArtList = databaseUtils.getNearby(""+myLocation.getLatitude(), ""+myLocation.getLongitude(), (int)pingRadius);
    }


    public void checkRadarRadius(Location myLocation){
        for(Artwork a : ArtList) {
            double dist = distFrom(myLocation, a.location);
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
            double dist = distFrom(myLocation, a.location);
            if (dist < dropPenRadius) {
                if (a.ID != currentID && (dist < currentDist || currentDist == -1)) {
                    Fragment_Camera.loadArt(a);
                    currentID = a.ID;
                    currentDist = dist;
                }
            }
            if(a.ID == currentID && dist > dropPenRadius){
                currentID = -1;
                currentDist = -1;
            }

        }
    }

    public static void LocationUpdate(Location location) {

        int sysTime = (int)System.currentTimeMillis(); //For some reason currentMillisecond wasn't updating
        int timeDiff =(int) (sysTime - instance.lastUpdate);

            if(timeDiff >= instance.pingTimer) {
                instance.checkPingRadius(location);
                instance.lastUpdate = (int)System.currentTimeMillis();
                MainActivity.makeToast("hey");
            }
            instance.checkPenDropRadius(location);
            instance.checkRadarRadius(location);

            pingRingCollision = false;


    }

    public static void create(Context mContext) {
        if(instance == null){
            instance = new ArtworkController(mContext);
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
