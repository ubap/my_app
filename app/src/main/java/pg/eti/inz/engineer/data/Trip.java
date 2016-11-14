package pg.eti.inz.engineer.data;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class Trip implements java.io.Serializable {
    // TODO: Czy service działa w innym wątku? czy może nastąpić wyścig w dostępie do tej klasy?
    // TODO: niech ta klasa sama sie zarzadza w trip containerze poprzez db Manager
    // w db managerze stworzyc unfinished trips i finished trips i mozna potem dodac jakies ozyskiwanie w razie faila (POTEM)
    @Getter
    private List<MeasurePoint> path = new LinkedList<>();
    private MeasurePoint lastMeasure;
    @Getter
    private float distance = 0.0f;
    @Getter
    private Time startTime;
    @Getter
    private Time finishTime;
    private TripStatus status = TripStatus.NOT_STARTED;

    public enum TripStatus { NOT_STARTED, STARTED, PAUSED, FINISHED }

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

    public void addMeasurePoint(MeasurePoint measurePoint) {
        if (this.lastMeasure != null) {
            distance = distance + lastMeasure.getLocation().distanceTo(measurePoint.getLocation());
        }
        path.add(measurePoint);
        lastMeasure = measurePoint;
    }

    public String toString(){ return "trip"; }
}
