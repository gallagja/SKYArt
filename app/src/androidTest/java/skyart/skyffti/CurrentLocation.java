package skyart.skyffti;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
/**
 * Created by jt10g_000 on 2/11/2017.
 * this class shoudld also keep the current location updated
 * C:\Users\jt10g
 */

public  class CurrentLocation implements LocationListener{

   // LocationManager locationManager = (LocationManager) this.getResource(Context.LOCATION_SERVICE);
    // Register the listener with the Location Manager to receive location updates

   // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    Location current;
    public CurrentLocation(){
        current=new Location("User Location");
        current.setLatitude(20);
        current.setLongitude(20);
    }

    public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
          //  makeUseOfNewLocation(location);
        // update location
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }
    public double getY(){
        return current.getLatitude();
    }
    public double getX(){
        return current.getLongitude();
    }
    public Location getCurrent(){
        return current;
    }



}
