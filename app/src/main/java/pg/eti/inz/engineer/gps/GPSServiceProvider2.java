package pg.eti.inz.engineer.gps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.LocationSource;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.utils.Log;

/**
 * https://developer.android.com/reference/android/location/LocationManager.html
 */

public class GPSServiceProvider2 {

    private static GPSServiceProvider2 instance;

    private CoreService.GPSBinder service;
    private AtomicBoolean isConnected;
    @Setter
    private Handler onConnectedHandler;

    public static GPSServiceProvider2 getInstance() {
        if (instance == null) {
            instance = new GPSServiceProvider2();
        }
        return instance;
    }

    GPSServiceProvider2() {
        isConnected = new AtomicBoolean(false);
        onConnectedHandler = null;
    }

    public void init(Activity context) {
        Intent intent = new Intent(context, CoreService.class);
        context.bindService(intent, mConnection, 0);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d();
            GPSServiceProvider2.this.service = (CoreService.GPSBinder) service;
            isConnected.set(true);

            onConnectedHandler.sendEmptyMessage(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isConnected.set(false);
        }
    };

    public Location getLocation() {
        if (isConnected.get()) {
            return service.getLocation();
        } else {
            return null;
        }
    }

    public Location getLastKnownLocation() { return service.getLastKnownLocation(); }

    public void startTracking(Trip trip) {
        service.startTracking(trip);
    }

    public Trip stopTracking() {
        return service.stopTracking();
    }

    public boolean isTracking() {
        return service.isTracking();
    }

    public Trip getTrip() {
        return service.getTrip();
    }

    public LocationSource getLocationSource() {
        return service.getLocationSource();
    }

    public CoreService.GPSStatus getGPSStatus() {
        return service.getGPSStatus();
    }
}
