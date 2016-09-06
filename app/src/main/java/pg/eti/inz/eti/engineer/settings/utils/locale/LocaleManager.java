package pg.eti.inz.eti.engineer.settings.utils.locale;

import java.util.Locale;
import java.util.logging.Logger;

import pg.eti.inz.eti.engineer.settings.utils.SettingsConstants;

/**
 * Class using to changing Locale of app.
 */
public class LocaleManager {

    private static final Logger LOGGER = Logger.getLogger(LocaleManager.class.getName());

    public Locale recognizeLocaleByLanguageName(String languageName) {
        Locale locale = null;

        switch (languageName) {
            case SettingsConstants.POLISH:
                locale = new Locale(SettingsConstants.POLISH_LOCALE);
                LOGGER.info("Locale for " + languageName + " recognized as: " + SettingsConstants.POLISH_LOCALE);
                break;
            case SettingsConstants.ENGLISH:
                locale = Locale.ENGLISH;
                LOGGER.info("Locale for " + languageName + " recognized as: " + SettingsConstants.ENGLISH_LOCALE);
                break;
        }

        return locale;
    }

    public String recognizeLanguageCodeByLanguageName(String languageName) {
        String recognizedCode = null;

        switch (languageName) {
            case SettingsConstants.POLISH:
                recognizedCode = SettingsConstants.POLISH_LOCALE;
                LOGGER.info("Locale for " + languageName + " recognized as: " + SettingsConstants.POLISH_LOCALE);
                break;
            case SettingsConstants.ENGLISH:
                recognizedCode = SettingsConstants.ENGLISH_LOCALE;
                LOGGER.info("Locale for " + languageName + " recognized as: " + SettingsConstants.ENGLISH_LOCALE);
                break;
        }
        return recognizedCode;
    }

    public String recognizeLanguageNameByLanguageCode(String languageCode) {
        String recognizedLanguage = null;

        switch (languageCode) {
            case SettingsConstants.POLISH_LOCALE:
                recognizedLanguage = SettingsConstants.POLISH;
                LOGGER.info("Locale for " + languageCode + " recognized as: " + SettingsConstants.POLISH);
                break;
            case SettingsConstants.ENGLISH:
                recognizedLanguage = SettingsConstants.ENGLISH;
                LOGGER.info("Locale for " + languageCode + " recognized as: " + SettingsConstants.ENGLISH);
                break;
        }
        return recognizedLanguage;
    }
}
