package pg.eti.inz.engineer.gps;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.settings.SettingsProvider;
import pg.eti.inz.engineer.utils.Constants;

/**
 * Klasa odpowiadająca za pobieranie danych z GPS.
 */
public class GPSServiceProvider extends Service implements LocationListener {

    // Minimalny pokonany dystans potrzebny do aktualizacji lokalizacji w metrach
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    // Minimalny czas pomiedzy aktualizacjami lokalizacji w milisekundach
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 10; // 10 sekund

    private final Context parentContext;

    private Boolean isGPSEnabled;
    private Boolean isNetworkEnabled;
    private Boolean canGetLocation;
    private Location location;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;

    public GPSServiceProvider(Context context) {
        parentContext = context;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showGPSDisabledAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentContext);

        alertDialogBuilder.setTitle(getResources().getString(R.string.gps_alert_title));
        alertDialogBuilder.setMessage(getResources().getString(R.string.gps_alert_message));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.settings),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        parentContext.startActivity(settingsIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialogBuilder.show();
    }

    public void turnGPSOff() {
        if (Build.VERSION.SDK_INT >= Constants.SDK_VERSION_23 &&
                parentContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public Location getLocation() {
        if (Build.VERSION.SDK_INT >= Constants.SDK_VERSION_23 &&
                parentContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try {
            locationManager = (LocationManager) parentContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && SettingsProvider.getUseNetworkLocation(this);

            canGetLocation = isGPSEnabled || isNetworkEnabled;

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            if (isNetworkEnabled && SettingsProvider.getUseNetworkLocation(this)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this);
                Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (isGPSEnabled && location != null) {
                    location.setLongitude((location.getLongitude() + networkLocation.getLongitude()) / 2);
                    location.setLatitude((location.getLatitude() + networkLocation.getLatitude()) / 2);
                } else {
                    location = networkLocation;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public Double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public Double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public Boolean getGPSEnabled() {
        return isGPSEnabled;
    }

    public Boolean getCanGetLocation() {
        return canGetLocation;
    }
}
