package pg.eti.inz.eti.engineer.settings.utils.locale;

import android.content.Context;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import pg.eti.inz.eti.engineer.settings.Option;
import pg.eti.inz.eti.engineer.settings.providers.SettingsDatabaseProvider;
import pg.eti.inz.eti.engineer.settings.utils.SettingsConstants;

/**
 * Singleton which is containing active locale
 */
public class ActiveLocale {
    private static final Logger LOGGER = Logger.getLogger(ActiveLocale.class.getName());

    private static Locale activeLocale;
    private static ActiveLocale instance = null;

    protected ActiveLocale(Context context){
        SettingsDatabaseProvider databaseProvider = new SettingsDatabaseProvider(context);
        Map<String, Option> options = databaseProvider.getOptions();

        this.activeLocale = new Locale(options.get(SettingsConstants.OPTIONS_DB_LOCALE).getValue());

        databaseProvider.close();
    }

    public static ActiveLocale getInstance(Context context) {
        if (instance == null) {
            LOGGER.info(ActiveLocale.class.getName() + ": Getting locale from database");
            instance = new ActiveLocale(context);
        }
        return instance;
    }

    public static Locale getActiveLocale() {
        return activeLocale;
    }

    public static void setActiveLocale(Locale activeLocale) {
        ActiveLocale.activeLocale = activeLocale;
    }
}
