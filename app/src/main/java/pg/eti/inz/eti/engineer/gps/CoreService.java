package pg.eti.inz.eti.engineer.gps;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.data.Trip;
import pg.eti.inz.eti.engineer.utils.Log;

public class CoreService extends Service implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks {

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

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private final IBinder mGPSBinder = new GPSBinder();

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d();
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                location = mLastLocation;
                Log.d(location.toString());
            }
        } catch (SecurityException e) { e.printStackTrace(); }
        startLocationUpdates();
    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch(SecurityException e) { e.printStackTrace(); }
    }

    public class GPSBinder extends Binder {
        public Location getLocation() { return location; }
        public boolean isTracking() { return tracking; }
        public Trip getTrip() { return currTrip; }

        // TODO: make this function boolean, check if can start tracking, return if started;
        public void startTracking(Trip trip) {
            if (tracking) {
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

        mLocationRequest = intent.getParcelableExtra("LocationRequest");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

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

    //  com.google.android.gms.location.LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d(location.toString());
        if(tracking) {
            currTrip.addLocation(location);
        }
        this.location = location;
    }
}