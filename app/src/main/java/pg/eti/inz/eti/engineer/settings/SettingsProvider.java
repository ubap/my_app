package pg.eti.inz.eti.engineer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pg.eti.inz.eti.engineer.utils.Constants;

/**
 * Klasa pozwalająca na łatwy dostęp do ustawień aplikacji
 */
public class SettingsProvider {

    public static Boolean getUseNetworkLocation (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(Constants.SETTINGS_GPS_USE_NETWORK_KEY, false);
    }

    //Klasa użytkowa, nie powinna być inicjalizowana
    private SettingsProvider () {}
}
