package pg.eti.inz.eti.engineer.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * https://developer.android.com/reference/android/location/LocationManager.html
 */

public class GPSServiceProvider2 implements LocationListener {

    private static GPSServiceProvider2 instance;
    private Location location;

    public static GPSServiceProvider2 getInstance() {
        if (instance == null) {
            instance = new GPSServiceProvider2();
        }
        return instance;
    }

    public void init(Activity context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // TODO: check permission
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) { e.printStackTrace(); }
    }

    public Location getLocation() {
        return location;
    }

    // LocationListener
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider
        Log.d("MyApp","GPSServiceProvider2::onLocationChanged, Location:"+location.toString());
        this.location = location;
    }
    // LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    // LocationListener
    public void onProviderEnabled(String provider) {}
    // LocationListener
    public void onProviderDisabled(String provider) {}
}
