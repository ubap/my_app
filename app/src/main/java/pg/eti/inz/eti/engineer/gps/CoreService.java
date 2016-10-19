package pg.eti.inz.eti.engineer.gps;

import android.app.NotificationManager;
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
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.data.Trip;

/**
 * Created by ubap on 2016-10-15.
 */

public class CoreService extends Service implements LocationListener{

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
            // PROPOZYCJA: WRZUCAC DANE DO TRIPA CO 1S JAK BEDA ODCZYTY Z INNYCH CZUJNIKÓW
        }
    };

    private boolean tracking = false;
    private Trip currTrip;
    private Location location;

    private final IBinder mGPSBinder = new GPSBinder();

    public class GPSBinder extends Binder {
        public Location getLocation() { return location; }
        public boolean isTracking() { return tracking; }
        public Trip getTrip() { return currTrip; }

        // TODO: make this function boolean, check if can start tracking, return if started;
        public void startTracking(Trip trip) {
            if(tracking) {
                return;
            }
            tracking = true;
            currTrip = trip;
        }

        public Trip stopTracking() {
            tracking = false;
            return currTrip;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        Log.d("myApp", "GPSServiceProvider2::GPSService::onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("myApp", "GPSServiceProvider2::GPSService::onStartCommand");
        // start gps listener
        // TODO: Check if registering a location listener for second time won't break anything and is OK
        LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) { e.printStackTrace(); }

        // make this service as a foreground one
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.apple_safari).setContentTitle("Trackowanie drogi").setContentText("Trwa");

        this.startForeground(0, mBuilder.build());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // other bind purposes
        return mGPSBinder;
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d("myApp", "GPSServiceProvider2::GPSService::LocationListener::onLocationChanged");
        if(tracking) {
//            if (this.location != null) {
//                pathLength = pathLength + location.distanceTo(location);
//            }
            currTrip.getPath().add(location);
        }

        this.location = location;
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
