package pg.eti.inz.engineer.rest.util;

import pg.eti.inz.engineer.utils.Constants;

/**
 * Klasa odpowiedzialna za komunikację między serwisem REST, a aplikacją mobilną
 */

public class RestAddressHelper {

    public static String getUserEndpointAdress() {
        return Constants.REST_SERVICE_ADDRESS + "user";
    }

    public static String getRoutesEndpointAddress() {
        return Constants.REST_SERVICE_ADDRESS + "routes";
    }
}
