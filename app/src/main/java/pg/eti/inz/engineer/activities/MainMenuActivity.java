package pg.eti.inz.engineer.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.gps.CoreService;
import pg.eti.inz.engineer.gps.GPSServiceProvider;
import pg.eti.inz.engineer.utils.Log;

/**
 * Główne menu aplikacji
 */
public class MainMenuActivity extends Activity implements
        ResultCallback<LocationSettingsResult> {

    // this is invoked when the connection to the service is established
    // checks if the gps is enabled, if not then the user is prompted to enable, navigate to settings
    class CheckGPSHandler extends Handler {
        Context context;
        CheckGPSHandler(Context context) { this.context = context; }
        public void handleMessage(Message msg)
        {
            GPSServiceProvider.getInstance().setOnGPSStatusHandler(null);
            if (GPSServiceProvider.getInstance().getGPSStatus() == CoreService.GPSStatus.DISABLED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.gps_alert_message).setTitle(R.string.gps_alert_title);

                builder.setPositiveButton(R.string.gps_alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
                });
                builder.setNegativeButton(R.string.gps_alert_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

//            // switch to the last enabled activity
//            SharedPreferences sharedPreferences = getSharedPreferences("lastActivity", MODE_PRIVATE);
//            // by default switch to maps activity
//            String lastActivityName = sharedPreferences.getString("name", "MapsActivity");
//            switch (lastActivityName) {
//                case "MapsActivity":
//                    navigateToMap(null);
//                    break;
//                default:
//                    break;
//            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        Intent intent = new Intent(this, CoreService.class);
        startService(intent);

        GPSServiceProvider.getInstance().init(this);
        GPSServiceProvider.getInstance().setOnGPSStatusHandler(new CheckGPSHandler(this));
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
