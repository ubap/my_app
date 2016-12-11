package pg.eti.inz.engineer.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.LocationSource;

import java.util.LinkedList;
import java.util.List;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.MeasurePoint;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.utils.Constants;
import pg.eti.inz.engineer.utils.Log;

public class CoreService extends Service implements LocationListener,
        GpsStatus.Listener {

    private static int NOTIFICATION_SLOT_ID = 1;

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
    private double avgSpeed;
    private LocationManager locationManager;
    private CoreService instance;

    private final IBinder mGPSBinder = new GPSBinder();

    protected void startLocationUpdates() {
    }

    public class GPSBinder extends Binder {
        public Location getLocation() { return location; }
        public Location getLastKnownLocation() {
            SharedPreferences sharedPreferences = getSharedPreferences("lastKnownLocation", MODE_PRIVATE);
            double latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0.0"));
            double longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0.0"));
            Location lastKnownLocation = new Location("SharedPreferences");
            lastKnownLocation.setLatitude(latitude);
            lastKnownLocation.setLongitude(longitude);
            return lastKnownLocation;
        }
        public boolean isTracking() { return tracking; }
        public Trip getTrip() { return currTrip; }
        public LocationSource getLocationSource() { return locationSource; }
        public GPSStatus getGPSStatus() { return statusGPS; }

        public void startTracking(Trip trip) {
            if (tracking || !trip.start()) {
                return;
            }
            tracking = true;
            avgSpeed = 0.0f;
            currTrip = trip;
            // make this service as a foreground one
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.apple_safari)
                            .setContentTitle(getString(R.string.appName))
                            .setContentText(getString(R.string.gps_core_notification_tracking));
            instance.startForeground(NOTIFICATION_SLOT_ID, notificationBuilder.build());
        }

        public Trip stopTracking() {
            if (!tracking || !currTrip.finish())
                return null;
            tracking = false;
            instance.stopForeground(true);
            return currTrip;
        }
    }

    // Service
    @Override
    public void onCreate() {
        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        instance = this;
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
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.addGpsStatusListener(this);
        }
        catch (SecurityException e) { e.printStackTrace(); }
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

        if (tracking) {
            MeasurePoint measurePoint = new MeasurePoint();
            measurePoint.setLocation(location);
            currTrip.addMeasurePoint(measurePoint);
            // update avg speed
            int measureCount = currTrip.getPath().size();
            avgSpeed = ( (avgSpeed * (measureCount-1)) + location.getSpeed()) / measureCount;
            currTrip.setAvgSpeed(avgSpeed);
        }
        this.location = location;

        // notify registered listeners...
        for (LocationSource.OnLocationChangedListener listener : onLocationChangedListeners) {
            listener.onLocationChanged(location);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("lastKnownLocation", MODE_PRIVATE);
        SharedPreferences.Editor lastKnownLocationEditor = sharedPreferences.edit();
        lastKnownLocationEditor.putString("latitude", Double.toString(location.getLatitude()));
        lastKnownLocationEditor.putString("longitude", Double.toString(location.getLongitude()));
        lastKnownLocationEditor.commit();
    }

    // LocationListener
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d();
    }

    // LocationListener
    @Override
    public void onProviderEnabled(String provider) {
        statusGPS = GPSStatus.NOT_FIXED;
        broadcastGpsStatusChanged();
    }

    // LocationListener
    @Override
    public void onProviderDisabled(String provider) {
        statusGPS = GPSStatus.DISABLED;
        broadcastGpsStatusChanged();
    }

    // android.location.GpsStatus.Listener
    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                statusGPS = GPSStatus.NOT_FIXED;
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                statusGPS = GPSStatus.FIXED;
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // count satelites?
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                statusGPS = GPSStatus.DISABLED;
                break;
            default:
                Log.d("error: onGpsStatusChanged unhandled status");
                break;
        }
        broadcastGpsStatusChanged();
    }

    private void broadcastGpsStatusChanged() {
        Intent intent = new Intent();
        intent.putExtra("GpsStatus", statusGPS);
        intent.setAction(Constants.BROADCAST_ACTION_GPS_STATUS_SET);
        sendBroadcast(intent);
    }
}