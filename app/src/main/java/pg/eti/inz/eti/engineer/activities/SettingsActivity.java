package pg.eti.inz.eti.engineer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import pg.eti.inz.eti.engineer.R;

/**
 * Created by jakub on 13.09.16.
 * https://developer.android.com/guide/topics/ui/settings.html
 * https://developer.android.com/reference/android/preference/PreferenceActivity.html
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SetLanguageSummary();
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

    private static String languageKey = "language";
    private static String errMsg = "Err";

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(languageKey)) {
            SetLanguageSummary();
        }
    }

    private void SetLanguageSummary() {
        Preference languagePref = findPreference(languageKey);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        languagePref.setSummary(sharedPreferences.getString(languageKey, errMsg));
    }

}