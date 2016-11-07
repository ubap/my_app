package pg.eti.inz.engineer.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.LocationSource;

import java.util.LinkedList;
import java.util.List;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.utils.Log;

public class CoreService extends Service implements LocationListener {

//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            handler.postDelayed(this, 1000);
//            // PROPOZYCJA: WRZUCAC DANE DO TRIPA CO 1S JAK BEDA ODCZYTY Z INNYCH CZUJNIKÓW
//        }
//    };

    private List<LocationSource.OnLocationChangedListener> onLocationChangedListeners
            = new LinkedList<LocationSource.OnLocationChangedListener>();

    private LocationSource locationSource = new LocationSource() {
        OnLocationChangedListener onLocationChangedListener;
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            Log.d();
            this.onLocationChangedListener = onLocationChangedListener;
            onLocationChangedListeners.add(onLocationChangedListener);
        }

        @Override
        public void deactivate() {
            onLocationChangedListeners.remove(onLocationChangedListener);
        }
    };

    public enum GPSStatus {
        DISABLED, NOT_FIXED, FIXED
    };

    private GPSStatus statusGPS = GPSStatus.DISABLED;
    private boolean tracking = false;
    private Trip currTrip;
    private Location location;

    private final IBinder mGPSBinder = new GPSBinder();

    protected void startLocationUpdates() {
    }

    public class GPSBinder extends Binder {
        public Location getLocation() { return location; }
        public boolean isTracking() { return tracking; }
        public Trip getTrip() { return currTrip; }
        public LocationSource getLocationSource() { return locationSource; }
        public GPSStatus getGPSStatus() { return statusGPS; }

        // TODO: make this function boolean, check if can start tracking, return if started;
        public void startTracking(Trip trip) {
            if (tracking || !trip.start()) {
                return;
            }
            tracking = true;
            currTrip = trip;
        }

        public Trip stopTracking() {
            if (!tracking || !currTrip.finish())
                return null;
            tracking = false;
            return currTrip;
        }
    }

    // Service
    @Override
    public void onCreate() {
    }

    // Service
    @Override
    public void onDestroy() {
        Log.d();
    }

    // Service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d();

        // start gps listener
        // TODO: Check if registering a location listener for second time won't break anything and is OK
        LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) { e.printStackTrace(); }


        // make this service as a foreground one
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.apple_safari)
                        .setContentTitle("Trackowanie drogi")
                        .setContentText("Trwa");

        this.startForeground(0, notificationBuilder.build());

        return START_STICKY;
    }

    // Service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mGPSBinder;
    }

    //  LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d(location.toString());
        statusGPS = GPSStatus.FIXED;

        if(tracking) {
            currTrip.addLocation(location);
        }
        this.location = location;

        // notify registered listeners...
        for (LocationSource.OnLocationChangedListener listener : onLocationChangedListeners) {
            listener.onLocationChanged(location);
        }
    }

    // LocationListener
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d();
    }

    // LocationListener
    @Override
    public void onProviderEnabled(String provider) {
        Log.d();
        statusGPS = GPSStatus.NOT_FIXED;
    }

    // LocationListener
    @Override
    public void onProviderDisabled(String provider) {
        Log.d();
        statusGPS = GPSStatus.DISABLED;
    }
}