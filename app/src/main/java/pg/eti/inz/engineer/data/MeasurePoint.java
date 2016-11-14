package pg.eti.inz.engineer.data;

import android.location.Location;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ubap on 2016-11-14.
 */

public class MeasurePoint implements java.io.Serializable {
    @Getter
    private double latitude;
    @Getter
    private double longitude;
    private float speed;

    public void setLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
    }

    public Location getLocation() {
        Location location = new Location("MeasurePoint");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setSpeed(speed);

        return location;
    }
}
