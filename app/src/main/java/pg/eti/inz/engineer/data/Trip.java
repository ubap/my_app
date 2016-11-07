package pg.eti.inz.engineer.data;

import android.location.Location;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class Trip /*implements Serializable*/ {
    // TODO: Czy service działa w innym wątku? czy może nastąpić wyścig w dostępie do tej klasy?
    // TODO: niech ta klasa sama sie zarzadza w trip containerze poprzez db Manager
    // w db managerze stworzyc unfinished trips i finished trips i mozna potem dodac jakies ozyskiwanie w razie faila (POTEM)
    @Getter
    private List<Location> path = new LinkedList<>();
    private Location lastLocation;
    @Getter
    private float distance = 0.0f;
    @Getter
    private Time startTime;
    @Getter
    private Time finishTime;

    public enum TripStatus {
        NOT_STARTED, STARTED, PAUSED, FINISHED
    };
    private TripStatus status = TripStatus.NOT_STARTED;

    public Trip() {
    }

    public boolean start() {
        if (status == TripStatus.NOT_STARTED) {
            startTime = new Time(System.currentTimeMillis());
            status = TripStatus.STARTED;
            return true;
        }
        return false;
    }

    public boolean finish() {
        if (status == TripStatus.STARTED || status == TripStatus.PAUSED ) {
            finishTime = new Time(System.currentTimeMillis());
            status = TripStatus.FINISHED;
            return true;
        }
        return false;
    }

    public void addLocation(Location location) {
        if (this.lastLocation != null) {
            distance = distance + lastLocation.distanceTo(location);
        }
        path.add(location);
        lastLocation = location;
    }

    public String toString(){ return "trip"; }
}
