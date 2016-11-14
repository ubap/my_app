package pg.eti.inz.engineer.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.gps.CoreService;
import pg.eti.inz.engineer.gps.GPSServiceProvider2;
import pg.eti.inz.engineer.utils.Log;

/**
 * Main menu of application
 */
public class MainMenuActivity extends Activity implements
        ResultCallback<LocationSettingsResult> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        GPSServiceProvider2.getInstance().init(this);

        Intent intent = new Intent(this, CoreService.class);
        startService(intent);

        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    public void navigateToSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void navigateToMap(View view) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }

    public void navigateToDashboard (View view) {
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
    }

    public void navigateToTrips(View view) {
        Intent tripIntent = new Intent(this, TripsActivity.class);
        startActivity(tripIntent);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates LocationSettingsStatesCodes = locationSettingsResult.getLocationSettingsStates();

        Boolean gps = LocationSettingsStatesCodes.isGpsUsable();
        Boolean network = LocationSettingsStatesCodes.isNetworkLocationUsable();
        Boolean location = LocationSettingsStatesCodes.isLocationUsable();
        Boolean ble = LocationSettingsStatesCodes.isBleUsable();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d("LocationSettingsStatusCodes.SUCCESS");
                // All location settings are satisfied. The client can
                // initialize location requests here.
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d("LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            MainMenuActivity.this,
                            0x1);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d("LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.
                break;
        }
    }
}
