package Brain;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
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


    private Location location; //location of the art work
    private Bitmap imageHD; //High Res
    private Bitmap image;   //Low Res
    private String title;   // Maybe?

    public boolean hasMarker = false;
    Marker marker;

    private int ID; //Its ID according to the brain

    public Artwork(Bitmap bm, Location loc, int id){
        image = bm;
        location = loc;
        ID = id;
    }

    public Artwork(int id, Location loc) {
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

    // Setters & Getters
    public Bitmap getBitmap() { return image; }
    public void setBitmap(Bitmap img) { this.image = img; }

    public Bitmap getBitmapHD(){ return imageHD; }
    public void setBitmapHD(Bitmap img) { this.imageHD = img; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Location getLoc(){ return location; }
    public void setLoc(Location loc) { this.location = loc; }

    public int getArtID(){ return ID; }
    public void setArtID(int artID) { this.ID = artID; }

}
