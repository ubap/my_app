package pg.eti.inz.eti.engineer.settings.utils;

/**
 * Util class with local settings constants
 */
public class SettingsConstants {

    //Tabele w bazie danych
    public static final String OPTIONS_DATABASE_TABLE = "settings";

    //Nazwy kolumn w tabeli z ustawieniami
    public static final String OPTIONS_DB_NAME_COLUMN = "name";
    public static final String OPTIONS_DB_VALUE_COLUMN = "value";
    public static final String OPTIONS_DB_TYPE_COLUMN = "type";
    
    //Nazwy pojedynczych ustawien
    public static final String OPTIONS_DB_LOCALE = "lang";
    public static final String OPTIONS_DB_SERVER_ADDRESS = "server_address";

    //Wartosci dla jezykow
    public static final String POLISH = "Polski";
    public static final String ENGLISH = "English";
    public static final String POLISH_LOCALE = "pl";
    public static final String ENGLISH_LOCALE = "en";
    
    private SettingsConstants(){}
    
}
