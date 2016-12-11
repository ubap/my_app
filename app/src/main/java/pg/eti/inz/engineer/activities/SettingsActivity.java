package pg.eti.inz.engineer.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.DbManager;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.rest.account.AccountManager;
import pg.eti.inz.engineer.rest.route.RouteManager;
import pg.eti.inz.engineer.rest.util.RestAddressHelper;
import pg.eti.inz.engineer.utils.Constants;

/**
 * Activity zawierające ustawienia aplikacji
 * https://developer.android.com/guide/topics/ui/settings.html
 * https://developer.android.com/reference/android/preference/PreferenceActivity.html
 */
public class SettingsActivity extends PreferenceActivity {

    private AccountManager accountManager;
    private DbManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_layout);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.CREDENTIALS_SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        accountManager = new AccountManager(sharedPreferences);
        dbManager = new DbManager(this);

        Preference logoutButton = getPreferenceManager().findPreference(getResources().
                getString(R.string.settings_user_account_logout_key));
        Preference deleteUserButton = getPreferenceManager().findPreference(getResources().
                getString(R.string.settings_user_account_delete_key));
        Preference logInUserButton = getPreferenceManager().findPreference(getResources().
                getString(R.string.settings_user_account_login_key));
        Preference changePasswordPreference = getPreferenceManager().findPreference(getResources().
                getString(R.string.settings_user_account_changePassword_key));
        Preference synchronizePreference = getPreferenceManager().findPreference(getResources().
                getString(R.string.settings_user_account_synchronize_key));

        if (accountManager.isUserLogged()) {

            logoutButton.setEnabled(true);
            deleteUserButton.setEnabled(true);
            changePasswordPreference.setEnabled(true);
            logInUserButton.setEnabled(false);
            synchronizePreference.setEnabled(true);

            logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    logoutUser();
                    navigateToLogin();

                    return true;
                }
            });

            deleteUserButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle(R.string.settings_user_account_delete)
                            .setMessage(R.string.settings_user_account_delete_confirmation_summary)
                            .setPositiveButton(R.string.gps_alert_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteUser();
                                }
                            })
                            .setNegativeButton(R.string.gps_alert_cancel, null)
                            .show();
                    return true;
                }
            });

            changePasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    changeUserPassword();
                    return true;
                }
            });

            synchronizePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    synchronizeTrips();
                    return true;
                }
            });
        } else {
            logoutButton.setEnabled(false);
            deleteUserButton.setEnabled(false);
            changePasswordPreference.setEnabled(false);
            synchronizePreference.setEnabled(false);
            logInUserButton.setEnabled(true);

            logInUserButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    navigateToLogin();
                    return true;
                }
            });
        }

    }

    private void navigateToLogin() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.CREDENTIALS_SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        accountManager = new AccountManager(sharedPreferences);

        accountManager.logout();
    }

    private void deleteUser() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.setBasicAuth(accountManager.getUsername(), accountManager.getUserPassword());

        client.delete(RestAddressHelper.getUserEndpointAdress(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                logoutUser();
                navigateToLogin();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(SettingsActivity.this, R.string.toast_deleteFailed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeUserPassword() {
        Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
        startActivity(changePasswordIntent);
    }

    private void synchronizeTrips() {
        RouteManager routeManager = new RouteManager(this, accountManager);
        List<Trip> unsynchronizedTrips = dbManager.getUnsynchronizedTrips();

        routeManager.synchronizeTripsFromService(dbManager);
        routeManager.synchronizeTripsWithService(unsynchronizedTrips, dbManager);
    }
}
