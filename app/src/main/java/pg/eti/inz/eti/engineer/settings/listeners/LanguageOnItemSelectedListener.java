package pg.eti.inz.eti.engineer.settings.listeners;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;

import java.util.Locale;

import pg.eti.inz.eti.engineer.settings.utils.locale.ActiveLocale;
import pg.eti.inz.eti.engineer.settings.utils.locale.LocaleManager;

/**
 * Listener for language spinner in settings
 */
public class LanguageOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public LanguageOnItemSelectedListener() {}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        LocaleManager localeManager = new LocaleManager();
        Locale locale = localeManager.recognizeLocaleByLanguageName(parent.getItemAtPosition(pos).toString());

        ActiveLocale.getInstance(parent.getContext()).setActiveLocale(locale);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
