package Brain;

import android.location.Location;

import java.util.List;

/**
 * Created by Coltan on 3/30/2017.
 *
 *
 *
 *
 *
 *
 */

public class MainBrain {

    private static boolean pingRingCollision; //When the RadarRadius hits the pingRadius

    //Radiuses in Meters
    float dropPenRadius = 5;
    float raderRadius = 15;
    float pingRadius= 30;

    Location pingRingLocation; //Location of the last ping ring.

    static float lastUpdate;
    List<Artwork> ArtList;

    private static MainBrain instance;

    public MainBrain(){
        instance = this;
    }

    /*
    Calcuates the Distances between two Locations
     */
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    public void checkPingRadius(Location myLocation){

    }
    public void checkRadarRadius(Location myLocation){

    }
    public void checkPenDropRadius(Location myLocation){

    }

    public static void LocationUpdate(Location location) {

        float timeDiff = System.currentTimeMillis() - lastUpdate;

        //if 10 mintues or new ping
        if(timeDiff >= 600000 || pingRingCollision) {
            instance.checkPenDropRadius(location);
            instance.checkPingRadius(location);
            instance.checkRadarRadius(location);
            pingRingCollision = false;
        }
    }
}
