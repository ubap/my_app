package pg.eti.inz.engineer.utils;

import pg.eti.inz.engineer.R;

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

    private Constants() {}
}
