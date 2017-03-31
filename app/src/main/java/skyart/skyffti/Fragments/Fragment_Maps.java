package skyart.skyffti.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import skyart.skyffti.R;


public class Fragment_Maps extends Fragment implements OnMapReadyCallback, LocationListener {


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static Fragment instance;
    private static GoogleMap mMap;
    private LocationManager mLm;
    private LocationListener mLocationListener;
    private double lat, lon;


    public void init() {


    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Maps newInstance(int sectionNumber) {
        Fragment_Maps fragment = new Fragment_Maps();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.theColorPicker);
        //setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mLm = (LocationManager) this.getActivity().getSystemService(this.getActivity().LOCATION_SERVICE);
        if (mLm != null && mLocationListener != null) {
            mLm.removeUpdates(mLocationListener);
        }
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mLm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);



        return rootView;
    }

    /*
    SetMapStyle
    1 = dark
    2 = light
    3 = night
     */
    public static void setMapStyle(int i){
        switch (i){
            case 0:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(instance.getContext(), R.raw.map_style1));
                break;
            case 1:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(instance.getContext(), R.raw.map_style2));
                break;
            case 2:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(instance.getContext(), R.raw.map_style3));
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.getContext(), R.raw.map_style1));
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));



       // onStart();
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onLocationChanged(Location location) {
//when location changed change text
        lon = location.getLongitude();
        lat = location.getLatitude();
        LatLng latLng = new LatLng(lat, lon);
        if (mMap != null) {
//            mMap.addMarker(new MarkerOptions().position(latLng).title("Your current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}



