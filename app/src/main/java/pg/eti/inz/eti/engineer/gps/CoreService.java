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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.util.LinkedList;
import java.util.List;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.data.Trip;
import pg.eti.inz.eti.engineer.utils.Log;

/**
 * Created by ubap on 2016-10-15.
 */

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
    LocationSettingsRequest.Builder builder;

    private final IBinder mGPSBinder = new GPSBinder();


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

        public PendingResult<LocationSettingsResult> checkLocationSettings() {
            return LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        Log.d();
    }

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

        Log.d(location.toString());
        if(tracking) {
            currTrip.addLocation(location);
        }
        this.location = location;
    }
}
