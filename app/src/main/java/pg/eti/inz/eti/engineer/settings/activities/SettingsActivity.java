package pg.eti.inz.eti.engineer.settings.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.activities.EngineersActivity;
import pg.eti.inz.eti.engineer.settings.Option;
import pg.eti.inz.eti.engineer.settings.listeners.LanguageOnItemSelectedListener;
import pg.eti.inz.eti.engineer.settings.providers.SettingsDatabaseProvider;
import pg.eti.inz.eti.engineer.settings.utils.OptionType;
import pg.eti.inz.eti.engineer.settings.utils.SettingsConstants;
import pg.eti.inz.eti.engineer.settings.utils.locale.LocaleManager;

public class SettingsActivity extends EngineersActivity {

    SettingsDatabaseProvider databaseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        LocaleManager localeManager = new LocaleManager();
        Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        String savedLanguage = null;

        databaseProvider = new SettingsDatabaseProvider(this);
        Map<String, Option> options = databaseProvider.getOptions();

        savedLanguage = localeManager.recognizeLanguageNameByLanguageCode(options.get(
                SettingsConstants.OPTIONS_DB_LOCALE).getValue());

        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this, R.array.settingsLanguages,
                R.layout.support_simple_spinner_dropdown_item);
        languageAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(new LanguageOnItemSelectedListener());

        languageSpinner.setSelection(languageAdapter.getPosition(savedLanguage));
    }

    public void saveSettings(View view) {
        SettingsDatabaseProvider databaseProvider = new SettingsDatabaseProvider(this);
        List<Option> options = new ArrayList<>();
        LocaleManager localeManager = new LocaleManager();
        Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);

        options.add(new Option(SettingsConstants.OPTIONS_DB_LOCALE,
                localeManager.recognizeLanguageCodeByLanguageName(languageSpinner.getSelectedItem().toString()),
                OptionType.STRING));

        databaseProvider.updateOptions(options);

        databaseProvider.close();
        finish();
    }
}
