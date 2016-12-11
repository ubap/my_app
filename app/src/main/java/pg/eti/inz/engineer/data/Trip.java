package pg.eti.inz.engineer.data;

import android.database.Cursor;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pg.eti.inz.engineer.utils.Log;

public class Trip implements java.io.Serializable {

    // TODO: Czy service działa w innym wątku? czy może nastąpić wyścig w dostępie do tej klasy?
    // TODO: niech ta klasa sama sie zarzadza w trip containerze poprzez db Manager
    // w db managerze stworzyc unfinished trips i finished trips i mozna potem dodac jakies odzyskiwanie w razie faila (POTEM)
    @Getter
    private List<MeasurePoint> path = new LinkedList<>();
    private MeasurePoint lastMeasure;

    @Getter @Setter
    private int id;
    /*
    * Przejechany dystans w metrach.
    * */
    @Getter
    private float distance = 0.0f;
    @Getter @Setter
    private double avgSpeed = 0.0;
    @Getter
    private Time startTime;
    @Getter
    private Time finishTime;
    @Getter @Setter
    private Integer isSynchronized;
    @Getter @Setter
    private Integer remoteId;

    private TripStatus status = TripStatus.NOT_STARTED;

    public enum TripStatus { NOT_STARTED, STARTED, PAUSED, FINISHED }

    public Trip() {
    }

    public Trip(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip._ID));
        startTime = new Time(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_START_TIME)));
        finishTime = new Time(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_FINISH_TIME)));
        distance = cursor.getFloat(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_DISTANCE));
        avgSpeed = cursor.getFloat(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_AVG_SPEED));
        isSynchronized = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_TRIP_SYNCHRONIZED));
        remoteId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_TRIP_REMOTE_ID));

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

    public Trip(JSONObject jsonObject) {
        Date startDate = null;
        Date finishDate = null;
        path = new ArrayList<>();
        try {
            id = jsonObject.getInt("id");
            distance = (float) jsonObject.getDouble("distance");
            avgSpeed = jsonObject.getDouble("averageSpeed");

            startDate = new Date(jsonObject.getString("startDate"));
            startTime = new Time(startDate.getTime());

            finishDate = new Date(jsonObject.getString("finishDate"));
            finishTime = new Time(finishDate.getTime());

            JSONArray locations = jsonObject.getJSONArray("locations");

            for (int i = 0; i < locations.length(); i++) {
                JSONObject jsonLocation = locations.getJSONObject(i);
                MeasurePoint measurePoint = new MeasurePoint();
                Date measureTime = new Date(jsonLocation.getString("timestamp"));

                measurePoint.setLatitude(jsonLocation.getDouble("latitude"));
                measurePoint.setLongitude(jsonLocation.getDouble("longitude"));
                measurePoint.setSpeed((float) jsonLocation.getDouble("speed"));
                measurePoint.setTime(new Time(measureTime.getTime()));

                path.add(measurePoint);
            }

        } catch (JSONException e) {
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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = new Date(this.startTime.getTime());
            Date finishDate = new Date(this.finishTime.getTime());
            JSONArray locations = new JSONArray();

            jsonObject.put("distance", this.distance);
            jsonObject.put("averageSpeed", this.avgSpeed);
            jsonObject.put("startDate", dateFormat.format(startDate));
            jsonObject.put("finishDate", dateFormat.format(finishDate));

            for (MeasurePoint measurePoint : path) {
                JSONObject location = new JSONObject();

                location.put("latitude", measurePoint.getLatitude());
                location.put("longitude", measurePoint.getLongitude());
                location.put("speed", measurePoint.getSpeed());

                if (measurePoint.getTime() != null) {
                    location.put("timestamp", dateFormat.format(new Date(measurePoint.getTime().getTime())));
                }

                locations.put(location);
            }

            jsonObject.put("locations", locations);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
