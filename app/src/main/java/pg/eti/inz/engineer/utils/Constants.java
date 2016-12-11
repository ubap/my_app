package pg.eti.inz.engineer.utils;

/**
 * Utility class with global application constants
 */
public class Constants {

    //Database name
    public static final String DATABASE_NAME = "Engineer";

    //SDK version 23
    public static final Integer SDK_VERSION_23 = 23;

    //Settings keys
    public static final String SETTINGS_GPS_USE_NETWORK_KEY = "useNetwork";

    // SERVICE
    public static final String BROADCAST_ACTION_GPS_STATUS_SET = "BROADCAST_ACTION_GPS_STATUS_SET";

    // DASHBOARD COMPONENTS
    public static final int SINGLE_STEP_SIZE_RESIZE_MOVE_DP = 32;
    public static final double SCALE_FACTOR_DETECT_SCALE = .1f;

    public static final boolean DEBUG = true;

    //Login
    public static final String CREDENTIALS_SHARED_PREFERENCES_NAME = "currentLoggedUser";

    //REST service address
    public static final String REST_SERVICE_ADDRESS = "http://81.2.239.28:8080/RestEngineerService/";
    public static final String REST_SERVICE_USERNAME_SHARED_PREFERENCES_KEY = "username";
    public static final String REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY = "password";

    private Constants() {}
}
