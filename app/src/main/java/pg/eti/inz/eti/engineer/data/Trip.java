package pg.eti.inz.eti.engineer.data;

import android.location.Location;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Trip /*implements Serializable*/ {
    // TODO: Czy service działa w innym wątku? czy może nastąpić wyścig w dostępie do tej klasy?
    // TODO: niech ta klasa sama sie zarzadza w trip containerze poprzez db Manager
    // w db managerze stworzyc unfinished trips i finished trips i mozna potem dodac jakies ozyskiwanie w razie faila (POTEM)
    private List<Location> path = new LinkedList<>();
    private Location lastLocation;
    private float distance = 0.0f;

    private Date date;
    private boolean finished = false;

    public Trip() {
    }

    public List<Location> getPath() {
        return path;
    }

    public void addLocation(Location location) {
        if (this.lastLocation != null) {
            distance = distance + lastLocation.distanceTo(location);
        }
        path.add(location);
        lastLocation = location;
    }
}
