package Artwork;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import skyart.skyffti.Fragments.Fragment_Maps;

/**
 * Created by Coltan on 3/30/2017.
 * This is the arkwork.
 * it has all the inforamtion needed for the art
 * has all controls for the art
 *
 */

public class Artwork {


    Location location; //location of the art work
    Bitmap imageHD; //High Res
    Bitmap image;   //Low Res
    String Title;   // Maybe?

    public boolean hasMarker = false;
    Marker marker;

    public int ID; //Its ID according to the brain

    public Artwork(Bitmap bm, Location loc, int id){
        image = bm;
        location = loc;
        ID = id;
    }

    public Artwork(Location loc, int id){
        image = null;
        location = loc;
        ID = id;
    }

    //region Marker controls
    public Marker getMarker() {
        return marker;
    }

    public void setMarker(){
        MarkerOptions temp = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Art: " + ID)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker = Fragment_Maps.setArtMarker(temp);
        hasMarker = true;
    }

    public void removeMarker() {
        hasMarker = false;
        Fragment_Maps.removeArtMarker(marker);
        marker = null;
    }
    //endregion

    public Bitmap getBitmap() {
        return image;
    }

    public String getTitle() {
        return Title;
    }


}
