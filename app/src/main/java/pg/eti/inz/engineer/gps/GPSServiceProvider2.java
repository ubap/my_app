package pg.eti.inz.engineer.gps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.maps.LocationSource;

import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.utils.Log;

/**
 * https://developer.android.com/reference/android/location/LocationManager.html
 */

public class GPSServiceProvider2 {

    private static GPSServiceProvider2 instance;

    private CoreService.GPSBinder mService;
    private Boolean mBound = false;

    public static GPSServiceProvider2 getInstance() {
        if (instance == null) {
            instance = new GPSServiceProvider2();
        }
        return instance;
    }

    public void init(Activity context) {
        Intent intent = new Intent(context, CoreService.class);
        context.bindService(intent, mConnection, 0);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d();
            mService = (CoreService.GPSBinder) service;
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public Location getLocation() {
        if (mBound) {
            return mService.getLocation();
        } else {
            return null;
        }
    }

    public void startTracking(Trip trip) {
        mService.startTracking(trip);
    }

    public Trip stopTracking() {
        return mService.stopTracking();
    }

    public boolean isTracking() {
        return mService.isTracking();
    }

    public Trip getTrip() {
        return mService.getTrip();
    }

    public LocationSource getLocationSource() {
        return mService.getLocationSource();
    }

    public CoreService.GPSStatus getGPSStatus() {
        return mService.getGPSStatus();
    }
}
