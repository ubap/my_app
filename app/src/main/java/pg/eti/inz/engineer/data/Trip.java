package pg.eti.inz.engineer.data;

import android.database.Cursor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pg.eti.inz.engineer.utils.Log;

public class Trip implements java.io.Serializable {

    // TODO: Czy service działa w innym wątku? czy może nastąpić wyścig w dostępie do tej klasy?
    // TODO: niech ta klasa sama sie zarzadza w trip containerze poprzez db Manager
    // w db managerze stworzyc unfinished trips i finished trips i mozna potem dodac jakies ozyskiwanie w razie faila (POTEM)
    @Getter
    private List<MeasurePoint> path = new LinkedList<>();
    private MeasurePoint lastMeasure;
    @Getter
    private float distance = 0.0f;
    @Getter @Setter
    private double avgSpeed = 0.0f;
    @Getter
    private Time startTime;
    @Getter
    private Time finishTime;

    private TripStatus status = TripStatus.NOT_STARTED;

    public enum TripStatus { NOT_STARTED, STARTED, PAUSED, FINISHED }

    public Trip() {
    }

    public Trip(Cursor cursor) {
        startTime = new Time(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_START_TIME)));
        finishTime = new Time(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_FINISH_TIME)));
        distance = cursor.getFloat(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_DISTANCE));


        byte[] tripBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_TRIP_DATA));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tripBytes);
        try {
            ObjectInput objectInput = new ObjectInputStream(byteArrayInputStream);

            path = (List<MeasurePoint>) objectInput.readObject();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.d("blad przy odczytywaniu MeasurePoint, prawdopodobnie zostala zmieniona struktura measure point?");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
        avgSpeed = (avgSpeed * path.size() + measurePoint.getSpeed()) / (path.size() + 1);
        if (this.lastMeasure != null) {
            distance = distance + lastMeasure.getLocation().distanceTo(measurePoint.getLocation());
        }
        path.add(measurePoint);
        lastMeasure = measurePoint;
    }

    public String toString(){ return "trip"; }
}
