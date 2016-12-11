package pg.eti.inz.engineer.data;

import android.location.Location;

import java.io.IOException;
import java.sql.Time;

import lombok.Getter;
import pg.eti.inz.engineer.utils.Log;

/**
 * Created by ubap on 2016-11-14.
 */

public class MeasurePoint implements java.io.Serializable {
    // !!! NIE ZMIENIAC !!!
    static final long serialVersionUID = 1L;
    // !!! Przy zmianie struktury danych zmienic INTERNAL_VERSION_ID i dodac nowy sposob deserializacji w metodzie readObject(); !!!
    static final int INTERNAL_VERSION_ID = 1;

    @Getter
    private double latitude;
    @Getter
    private double longitude;
    @Getter
    private float speed;
    @Getter
    private Time time;

    public void setLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
        time = new Time(location.getTime());
    }

    public Location getLocation() {
        Location location = new Location("MeasurePoint");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setSpeed(speed);
        location.setTime(time.getTime());
        return location;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(INTERNAL_VERSION_ID);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeFloat(speed);
        out.writeObject(time);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            latitude = in.readDouble();
            longitude = in.readDouble();
            speed = in.readFloat();
        } else {
            Log.d("nierozpoznana wersja MeasurePoint");
        }
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
