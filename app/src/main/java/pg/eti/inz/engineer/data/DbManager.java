package pg.eti.inz.engineer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class DbManager {
    private static DbHelper dbHelper;

    public DbManager(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
    }

    // todo: make this async
    public void saveTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbHelper.TripContract.Trip.COLUMN_NAME_START_TIME, trip.getStartTime().getTime());
        values.put(DbHelper.TripContract.Trip.COLUMN_NAME_FINISH_TIME, trip.getFinishTime().getTime());
        values.put(DbHelper.TripContract.Trip.COLUMN_NAME_DISTANCE, trip.getDistance());
        values.put(DbHelper.TripContract.Trip.COLUMN_NAME_AVG_SPEED, trip.getAvgSpeed());

        List<MeasurePoint> path = trip.getPath();

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(path);
            values.put(DbHelper.TripContract.Trip.COLUMN_NAME_TRIP_DATA, bos.toByteArray());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DbHelper.TripContract.Trip.TABLE_NAME, null, values);
    }

    public Cursor getTripsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                DbHelper.TripContract.Trip._ID,
                DbHelper.TripContract.Trip.COLUMN_NAME_START_TIME,
                DbHelper.TripContract.Trip.COLUMN_NAME_FINISH_TIME,
                DbHelper.TripContract.Trip.COLUMN_NAME_DISTANCE,
                DbHelper.TripContract.Trip.COLUMN_NAME_AVG_SPEED,
                DbHelper.TripContract.Trip.COLUMN_NAME_TRIP_DATA
        };

// Filter results WHERE "title" = 'My Title'
      //  String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
      //  String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                DbHelper.TripContract.Trip.COLUMN_NAME_START_TIME + " DESC";

        Cursor c = db.query(
                DbHelper.TripContract.Trip.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return c;
    }

    public void closeConnection() {
        dbHelper.close();
    }
}
