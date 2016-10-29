package pg.eti.inz.engineer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.utils.Constants;

/**
 * Activity zawierające ustawienia aplikacji
 * https://developer.android.com/guide/topics/ui/settings.html
 * https://developer.android.com/reference/android/preference/PreferenceActivity.html
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.SETTINGS_GPS_USE_NETWORK_KEY)) {

        }
    }
}